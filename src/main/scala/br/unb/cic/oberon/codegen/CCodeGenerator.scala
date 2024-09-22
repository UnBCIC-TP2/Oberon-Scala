package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.ir.common._
import br.unb.cic.oberon.transformations.CoreChecker
import org.typelevel.paiges.Doc
import org.typelevel.paiges.Doc._

class NotOberonCoreException(s: String) extends Exception(s) {}

abstract class CCodeGenerator extends CodeGenerator[String] {}

case class PaigesBasedGenerator() extends CCodeGenerator {
  val indentSize: Int = 4
  val twoLines: Doc = line * 2

  override def generateCode(module: OberonModule): String = {

    if (module.stmt.isDefined && !CoreChecker.checkModule(module))
      throw new NotOberonCoreException(
        "Não podemos compilar módulo que não seja OberonCore"
      )

    val mainHeader =
      text("#include <stdio.h>") / text("#include <stdbool.h>") + twoLines
    val procedureDocs = module.procedures.map(procedure =>
      generateProcedure(procedure, module.userTypes)
    )
    val mainProcedures =
      if (procedureDocs.nonEmpty)
        intercalate(twoLines, procedureDocs) + twoLines
      else empty
    val mainDefines = generateConstants(module.constants)
    val userDefinedTypes = generateUserDefinedTypes(module.userTypes)
    val globalVars = declareVars(module.variables, module.userTypes, 0)
    val mainBody = module.stmt match {
      case Some(stmt) =>
        text("int main() {") / generateStmt(
          stmt,
          indentSize
        ) + Doc.char('}')
      case None => text("int main() {}")
    }

    val CCode =
      mainHeader + userDefinedTypes / globalVars / mainDefines + mainProcedures / mainBody
    CCode.render(60)
  }

  def generateProcedure(
      procedure: Procedure[Statement],
      userTypes: List[UserDefinedType]
  ): Doc = {
    val returnType = procedure.returnType match {
      case Some(varType) => getCType(varType, userTypes)
      case None          => "void"
    }

    val args = procedure.args.map {
      case ParameterByValue(name, argumentType) =>
        text(convertVariable(name, argumentType, userTypes, isArgument = true))
      case ParameterByReference(name, argumentType) =>
        text(convertVariable(name, argumentType, userTypes, isArgument = true))
    }

    val procedureDeclarations =
      declareVars(procedure.variables, List(), indentSize)
    val procedureArgs = intercalate(Doc.char(',') + space, args)
    val procedureName = text(s"$returnType ${procedure.name}(")
    val procedureHeader =
      procedureArgs.tightBracketBy(procedureName, Doc.char(')'))
    val procedureBody = generateStmt(procedure.stmt)

    procedureHeader + space + Doc.char(
      '{'
    ) / procedureDeclarations + procedureBody +
      Doc.char('}')
  }

  def declareVars(
      variables: List[VariableDeclaration],
      userTypes: List[UserDefinedType],
      localIndent: Int
  ): Doc = {

    var basicVariablesDoc = empty
    for (varType <- List(IntegerType, RealType, BooleanType, StringType)) {
      val variablesOfType = variables
        .filter(_.variableType == varType)
        .map(variable => variable.name)
      if (variablesOfType.nonEmpty) {
        val CVarType = getCType(varType, userTypes)
        val varNames = variablesOfType.mkString(", ")
        val newDeclarationLine = textln(localIndent, s"$CVarType $varNames;")
        basicVariablesDoc = basicVariablesDoc + newDeclarationLine
      }
    }

    var userVariablesDoc = empty

    for (variable <- variables) {
      variable.variableType match {
        case ReferenceToUserDefinedType(userTypeName) =>
          userVariablesDoc += textln(
            localIndent,
            s"$userTypeName ${variable.name};"
          )

        case ArrayType(length, innerType) =>
          val variableType: String = getCType(innerType, userTypes)
          userVariablesDoc += textln(
            localIndent,
            s"$variableType ${variable.name}[$length];"
          )

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
          val variableType: String = innerType match {
            case ReferenceToUserDefinedType(name) => name
            case _ => getCType(innerType, userTypes)
          }

          textln(s"typedef $variableType ${userType.name}[$length];")
        case RecordType(variables) =>
          val structName = userType.name
          text(s"struct ${structName}_struct {") /
            declareVars(variables, userTypes, indentSize) +
            textln("};") +
            textln(s"typedef struct ${structName}_struct $structName;")
        case _ => throw new Exception("Non-exhaustive match in case statement.")
      })
    }
    generatedDoc
  }

  def stringToType(
      typeAsString: String,
      userTypes: List[UserDefinedType]
  ): Type = {
    for (userType <- userTypes) {
      if (userType.name == typeAsString) {
        return userType.baseType
      }
    }
    throw new Exception(s"Type not defined: $typeAsString")
  }

  def getCType(variableType: Type, userTypes: List[UserDefinedType]): String = {
    variableType match {
      case IntegerType   => "int"
      case BooleanType   => "bool"
      case CharacterType => "char"
      case RealType      => "float"
      case StringType    => "char*"

      case ReferenceToUserDefinedType(name) =>
        val userType = stringToType(name, userTypes)
        userType match {
          case RecordType(_) => s"struct $name"
          case _ =>
            throw new Exception("Non-exhaustive match in case statement.")
        }
      case _ => throw new Exception("Non-exhaustive match in case statement.")
    }
  }

  def convertVariable(
      name: String,
      varType: Type,
      userTypes: List[UserDefinedType],
      isArgument: Boolean = false
  ): String = {
    varType match {
      case ArrayType(length, arrayVarType) =>
        val innerCType = getCType(arrayVarType, userTypes)
        if (isArgument) {
          s"$innerCType $name[]"
        } else {
          s"$innerCType $name[$length]"
        }

      case _ =>
        val CType = getCType(varType, userTypes)
        s"$CType $name"
    }
  }

  def generateConstants(constants: List[Constant]): Doc = {
    val constantsDeclaration = constants.map { constant =>
      text(s"#define ${constant.name} ${genExp(constant.exp)}")
    }
    if (constantsDeclaration.nonEmpty)
      intercalate(line, constantsDeclaration) + twoLines
    else
      empty
  }

  def generateStmt(statement: Statement, indent: Int = indentSize): Doc = {

    statement match {
      case SequenceStmt(stmts) =>
        val multipleStmts = stmts.map(stmt => generateStmt(stmt, indent))
        intercalate(empty, multipleStmts)
      case ReadIntStmt(varName) =>
        textln(indent, s"""scanf("%d", &$varName);""")
      case WriteStmt(expression) =>
        textln(indent, s"""printf("%d\\n", ${genExp(expression)});""")
      case ProcedureCallStmt(name, args) =>
        genProcedureCallStmt(name, args, indent)
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
            ) + textln(indent, "}")
          case None => line
        }
        ifCond + elseCond
      case WhileStmt(condition, stmt) =>
        textln(indent, s"while (${genExp(condition)}) {") +
          generateStmt(stmt, indent + indentSize) +
          textln(indent, "}")
      case ReturnStmt(exp) =>
        textln(indent, s"return ${genExp(exp)};")
      case ForStmt(init, condition, stmt) =>
        textln(indent, s"for ($init; ${genExp(condition)}; $stmt) {") +
          generateStmt(stmt, indent + indentSize) +
          textln(indent, "}")

      case AssignmentStmt(designator, exp) =>
        designator match {
          case VarAssignment(varName) =>
            textln(indent, s"$varName = ${genExp(exp)};")
          case ArrayAssignment(array, elem) =>
            val varName = genExp(array)
            val index = genExp(elem)
            val value = genExp(exp)
            textln(indent, s"$varName[$index] = $value;")
          case RecordAssignment(record, atrib) =>
            val structName = genExp(record)
            val value = genExp(exp)
            textln(indent, s"$structName.$atrib = $value;")
          case _ =>
            throw new Exception("Non-exhaustive match in case statement.")
        }

      case ExitStmt() =>
        textln(indent, "break;")

      case _ => empty
    }
  }

  def genExp(exp: Expression): String = {
    exp match {
      case IntValue(v)    => v.toString
      case RealValue(v)   => v.toString
      case Brackets(exp)  => s"( ${genExp(exp)} )"
      case BoolValue(v)   => if (v) "true" else "false"
      case StringValue(v) => s""""${v}""""
      case Undef()        => "undefined"

      case VarExpression(name) => name
      case FunctionCallExpression(name, args) =>
        name match {
          case "ODD" =>
            s"${genExp(args.head)} % 2 == 1"
          case _ =>
            val expressions = args.map(arg => text(genExp(arg)))
            val functionArgs = intercalate(Doc.char(',') + space, expressions)
            functionArgs
              .tightBracketBy(
                text(name + '('),
                Doc.char(')')
              )
              .render(10000)
        }
      case EQExpression(left, right)   => s"${genExp(left)} == ${genExp(right)}"
      case NEQExpression(left, right)  => s"${genExp(left)} != ${genExp(right)}"
      case GTExpression(left, right)   => s"${genExp(left)} > ${genExp(right)}"
      case LTExpression(left, right)   => s"${genExp(left)} < ${genExp(right)}"
      case GTEExpression(left, right)  => s"${genExp(left)} >= ${genExp(right)}"
      case LTEExpression(left, right)  => s"${genExp(left)} <= ${genExp(right)}"
      case AddExpression(left, right)  => s"${genExp(left)} + ${genExp(right)}"
      case SubExpression(left, right)  => s"${genExp(left)} - ${genExp(right)}"
      case MultExpression(left, right) => s"${genExp(left)} * ${genExp(right)}"
      case DivExpression(left, right)  => s"${genExp(left)} / ${genExp(right)}"
      case OrExpression(left, right)   => s"${genExp(left)} || ${genExp(right)}"
      case AndExpression(left, right)  => s"${genExp(left)} && ${genExp(right)}"
      case ModExpression(left, right)  => s"${genExp(left)} % ${genExp(right)}"
      case FieldAccessExpression(exp, name) => s"${genExp(exp)}.$name"
      case ArraySubscript(arrayBase, index) =>
        val arrayName = genExp(arrayBase)
        val arrayIndex = genExp(index)
        s"$arrayName[$arrayIndex]"
      case _ => throw new Exception("expression not found")
    }
  }

  def genProcedureCallStmt(
      name: String,
      args: List[Expression],
      indent: Int
  ): Doc = {
    name match {
      case "INC" =>
        genInc(args, "+", indent)
      case "DEC" =>
        genInc(args, "-", indent)
      case _ =>
        val expressions = args.map(arg => text(genExp(arg)))
        val functionArgs = intercalate(Doc.char(',') + space, expressions)
        functionArgs.tightBracketBy(
          indentation(indent) + text(name + '('),
          text(");")
        ) + line
    }
  }

  def genInc(args: List[Expression], signal: String, indent: Int): Doc = {
    if (args.length == 1) {
      indentation(indent) + textln(
        s"${genExp(args.head)} = ${genExp(args.head)} ${signal} 1;"
      )
    } else {
      indentation(indent) + textln(
        s"${genExp(args.head)} = ${genExp(args.head)} ${signal} ${genExp(args(1))};"
      )
    }
  }

  def textln(str: String): Doc = text(str) + line

  def textln(indentSize: Int, str: String): Doc =
    indentation(indentSize) + text(str) + line

  def indentation(size: Int = indentSize): Doc =
    intercalate(empty, List.fill(size)(space))

}

class PPrintBasedGenerator extends CCodeGenerator {
  override def generateCode(module: OberonModule): String = ???
}
