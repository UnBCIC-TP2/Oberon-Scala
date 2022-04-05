package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.transformations.CoreChecker
import org.typelevel.paiges.Doc
import org.typelevel.paiges.Doc._

class NotOberonCoreException(s: String) extends Exception(s) {}

abstract class CCodeGenerator extends CodeGenerator {}

case class PaigesBasedGenerator() extends CCodeGenerator {
  val indentSize: Int = 4
  val twoLines: Doc = line * 2

  override def generateCode(module: OberonModule): String = {

    if (module.stmt.isDefined && !CoreChecker.isModuleCore(module))
      throw new NotOberonCoreException("Não podemos compilar módulo C que não seja OberonCore")

    val mainHeader = text("#include <stdio.h>") / text("#include <stdbool.h>") + twoLines
    val procedureDocs = module.procedures.map(procedure => generateProcedure(procedure, module.userTypes))
    val mainProcedures =
      if (procedureDocs.nonEmpty) intercalate(twoLines, procedureDocs) + twoLines
      else empty
    val mainDefines = generateConstants(module.constants)
    val mainDeclarations = generateDeclarations(module.variables, module.userTypes)
    val userDefinedTypes = generateUserDefinedTypes(module.userTypes)
    val mainBody = module.stmt match {
      case Some(stmt) =>
        text("int main() {") / mainDeclarations / generateStmt(
          stmt,
          indentSize
        ) + Doc.char('}')
      case None => text("int main() {}")
    }
    val main = mainHeader + userDefinedTypes + mainDefines + mainProcedures / mainBody
    main.render(60)
  }

  def generateProcedure(procedure: Procedure, userTypes: List[UserDefinedType]): Doc = {
    val returnType = procedure.returnType match {
      case Some(varType) => getCType(varType, userTypes)
      case None => "void"
    }

    val args = procedure.args.map {
      case parameterByValue =>
        val argumentType = getCType(parameterByValue.argumentType, userTypes)
        text(s"$argumentType ${parameterByValue.name}")
      case parameterByReference =>
        val argumentType = getCType(parameterByReference.argumentType, userTypes)
        text(s"$argumentType ${parameterByReference.name}")
    }

    val procedureDeclarations = generateDeclarations(procedure.variables, List())
    val procedureArgs = intercalate(Doc.char(',') + space, args)
    val procedureName = text(s"$returnType ${procedure.name}(")
    val procedureHeader = procedureArgs.tightBracketBy(procedureName, Doc.char(')'))
    val procedureBody = generateStmt(procedure.stmt)

    procedureHeader + space + Doc.char(
      '{'
    ) / procedureDeclarations + procedureBody +
      Doc.char('}')
  }

  def generateDeclarations(variables: List[VariableDeclaration], userTypes: List[UserDefinedType]): Doc = {

    var basicVariablesDoc = empty
    for (varType <- List(IntegerType, BooleanType)) {
      val variablesOfType = variables.filter(_.variableType == varType).map(variable => variable.name)
      if (variablesOfType.nonEmpty) {
        val CVarType = getCType(varType, userTypes)
        val varNames = variablesOfType.mkString(", ")
        val newDeclarationLine = textln(indentSize, s"$CVarType $varNames;")
        basicVariablesDoc = basicVariablesDoc + newDeclarationLine
      }
    }

    var userVariablesDoc = empty

    for (variable <- variables) {
      variable.variableType match {
        case ReferenceToUserDefinedType(userTypeName) =>
          val userType = stringToType(userTypeName, userTypes)

          userType match {

            case ArrayType(length, innerType) =>
              val variableType: String = getCType(innerType, userTypes)
              userVariablesDoc += textln(indentSize,s"$variableType ${variable.name}[$length];")

            case RecordType(_) =>
              userVariablesDoc += textln(indentSize,s"struct $userTypeName ${variable.name};")
          }

        case _ => ()
      }
    }

    basicVariablesDoc + userVariablesDoc
  }

  def generateUserDefinedTypes(userTypes: List[UserDefinedType]): Doc = {

    var generatedDoc: Doc = empty

    for (userType <- userTypes) {
      generatedDoc += (userType.baseType match {
        case ArrayType(length, innerType) =>
          val variableType: String = getCType(innerType, userTypes)
          textln(s"$variableType ${userType.name}[$length];")
        case RecordType(variables) =>
          val structName = userType.name
          text(s"struct $structName {") /
            generateDeclarations(variables, userTypes) +
            textln("};")
      })
    }
    generatedDoc
  }

  def stringToType(typeAsString: String, userTypes: List[UserDefinedType]): Type = {
    for (userType <- userTypes) {
      if (userType.name == typeAsString) {
        return userType.baseType
      }
    }
    throw new Exception("Type not defined.")
  }

  def getCType(variableType: Type, userTypes: List[UserDefinedType]): String = {
    variableType match {
      case IntegerType => "int"
      case BooleanType => "bool"
      case CharacterType => "char"
      case RealType => "float"
      case ReferenceToUserDefinedType(name) =>
        val userType = stringToType(name, userTypes)
        userType match {
          case RecordType(_) => s"struct $name"
        }
    }
  }

  def generateConstants(constants: List[Constant]): Doc = {
    val constantsDeclaration = constants.map {
      constant => text(s"#define ${constant.name} ${genExp(constant.exp)}")
    }
    if (constantsDeclaration.nonEmpty)
      intercalate(line, constantsDeclaration) + twoLines
    else
      empty
  }

  def generateStmt(statement: Statement, indent: Int = indentSize): Doc = {

    statement match {
      case AssignmentStmt(varName, exp) =>
        textln(indent,s"$varName = ${genExp(exp)};")
      case SequenceStmt(stmts) =>
        val multipleStmts = stmts.map(stmt => generateStmt(stmt, indent))
        intercalate(empty, multipleStmts)
      case ReadIntStmt(varName) =>
        textln(indent,s"""scanf("%d", &$varName);""")
      case WriteStmt(expression) =>
        textln(indent,s"""printf("%d\\n", ${genExp(expression)});""")
      case ProcedureCallStmt(name, args) =>
        val expressions = args.map(arg => text(genExp(arg)))
        val functionArgs = intercalate(Doc.char(',') + space, expressions)
        functionArgs.tightBracketBy(
          indentation(indent) + text(name + '('),
          text(");")
        ) + line
      case IfElseStmt(condition, thenStmt, elseStmt) =>
        val ifCond =
          textln(indent, s"if (${genExp(condition)}) {") +
            generateStmt(thenStmt, indent + indentSize) +
            indentation(indent) + text("}")
        val elseCond = elseStmt match {
          case Some(stmt) =>
            textln(" else {") + generateStmt(
              stmt,
              indent + indentSize
            ) + textln(indent,"}")
          case None => line
        }
        ifCond + elseCond
      case WhileStmt(condition, stmt) =>
        textln(indent,s"while (${genExp(condition)}) {") +
          generateStmt(stmt, indent + indentSize) +
          textln(indent,"}")
      case ReturnStmt(exp) =>
        textln(indent,s"return ${genExp(exp)};")

      case EAssignmentStmt(designator, exp) =>
        designator match {
          case ArrayAssignment(array, elem) =>
            val varName = genExp(array)
            val index = genExp(elem)
            val value = genExp(exp)
            textln(indent,s"$varName[$index] = $value;")

          case RecordAssignment(record, atrib) =>
            val structName = genExp(record)
            val value = genExp(exp)
            textln(indent,s"$structName.$atrib = $value;")
        }

      case ExitStmt() =>
       textln(indent,"break; ")

      case _ => empty
    }
  }

  def genExp(exp: Expression): String = {
    exp match {
      case IntValue(v) => v.toString
      case Brackets(exp) => s"( ${genExp(exp)} )"
      case BoolValue(v) => if (v) "true" else "false"
      case Undef() => "undefined"

      case VarExpression(name) => name
      case FunctionCallExpression(name, args) =>
        val expressions = args.map(arg => text(genExp(arg)))
        val functionArgs = intercalate(Doc.char(',') + space, expressions)
        functionArgs.tightBracketBy(
          text(name + '('),
          Doc.char(')')
        ).render(10000)
      case EQExpression(left, right) => s"${genExp(left)} == ${genExp(right)}"
      case NEQExpression(left, right) => s"${genExp(left)} != ${genExp(right)}"
      case GTExpression(left, right) => s"${genExp(left)} > ${genExp(right)}"
      case LTExpression(left, right) => s"${genExp(left)} < ${genExp(right)}"
      case GTEExpression(left, right) => s"${genExp(left)} >= ${genExp(right)}"
      case LTEExpression(left, right) => s"${genExp(left)} <= ${genExp(right)}"
      case AddExpression(left, right) => s"${genExp(left)} + ${genExp(right)}"
      case SubExpression(left, right) => s"${genExp(left)} - ${genExp(right)}"
      case MultExpression(left, right) => s"${genExp(left)} * ${genExp(right)}"
      case DivExpression(left, right) => s"${genExp(left)} / ${genExp(right)}"
      case OrExpression(left, right) => s"${genExp(left)} || ${genExp(right)}"
      case AndExpression(left, right) => s"${genExp(left)} && ${genExp(right)}"
      case FieldAccessExpression(exp, name) => s"${genExp(exp)}.$name"
      case ArraySubscript(arrayBase, index) =>
        val arrayName = genExp(arrayBase)
        val arrayIndex = genExp(index)
        s"$arrayName[$arrayIndex]"
      case _ => throw new Exception("expression not found")
    }
  }

  def textln(str: String): Doc = text(str) + line
  def textln(indentSize: Int, str: String): Doc = indentation(indentSize) + text(str) + line

  def indentation(size: Int = indentSize): Doc = intercalate(empty, List.fill(size)(space))

}

class PPrintBasedGenerator extends CCodeGenerator {
  override def generateCode(module: OberonModule): String = ???
}
