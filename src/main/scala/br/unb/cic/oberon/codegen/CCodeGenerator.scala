package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ast.{UserDefinedType, _}
import br.unb.cic.oberon.transformations.CoreChecker
import org.typelevel.paiges._

class NotOberonCoreException(s:String) extends Exception(s){}

abstract class CCodeGenerator extends CodeGenerator {}

case class PaigesBasedGenerator(lineSpaces: Int = 4) extends CCodeGenerator {


  override def generateCode(module: OberonModule): String = {

    if (module.stmt.isDefined && !CoreChecker.isModuleCore(module))
      throw new NotOberonCoreException("Não podemos compilar módulo C que não seja OberonCore")

    val mainHeader = Doc.text("#include <stdio.h>") + Doc.line + Doc.text(
      "#include <stdbool.h>"
    ) + Doc.line + Doc.line
    val procedureDocs = module.procedures.map(procedure => generateProcedure(procedure, lineSpaces))
    val mainProcedures =
      if (procedureDocs.nonEmpty)
        Doc.intercalate(
          Doc.line + Doc.line,
          procedureDocs
        ) + Doc.line + Doc.line
      else Doc.empty
    val mainDefines = generateConstants(module.constants)
    val mainDeclarations = generateDeclarations(module.variables, module.userTypes, lineSpaces)
    val userDefinedTypes = generateUserDefinedTypes(module.userTypes, lineSpaces)
    val mainBody = module.stmt match {
      case Some(stmt) =>
        Doc.text("int main() ") + Doc.char(
          '{'
        ) + Doc.line + mainDeclarations + Doc.line + generateStatement(
          stmt,
          lineSpaces,
          lineSpaces
        ) + Doc.char('}')
      case None => Doc.text("int main() {}")
    }
    val main = mainHeader + userDefinedTypes + mainDefines + mainProcedures + Doc.line + mainBody
    main.render(60)
  }

  def generateProcedure(procedure: Procedure, lineSpaces: Int = 2): Doc = {
    val returnType = procedure.returnType match {
      case Some(varType) => generateType(varType)
      case None          => Doc.text("void")
    }
    val args = procedure.args.map {
      case parameterByValue =>
        val argumentType = generateType(parameterByValue.argumentType)
        argumentType + Doc.space + Doc.text(parameterByValue.name)
      case parameterByReference =>
        val argumentType = generateType(parameterByReference.argumentType)
        argumentType + Doc.space + Doc.text(parameterByReference.name)
    }

    val procedureDeclarations = generateDeclarations(procedure.variables, List())
    val procedureArgs = Doc.intercalate(Doc.char(',') + Doc.space, args)
    val procedureName =
      returnType + Doc.space + Doc.text(procedure.name) + Doc.char('(')
    val procedureHeader =
      procedureArgs.tightBracketBy(procedureName, Doc.char(')'))
    val procedureBody =
      generateStatement(procedure.stmt, lineSpaces, lineSpaces)

    procedureHeader + Doc.space + Doc.char(
      '{'
    ) + Doc.line + procedureDeclarations + procedureBody +
      Doc.char('}')
  }

  def generateDeclarations(
      variables: List[VariableDeclaration],
      userTypes: List[UserDefinedType],
      lineSpaces: Int = 2
  ): Doc = {
    val intVariables = variables.filter(_.variableType == IntegerType).map(intVar => Doc.text(intVar.name))
    val intDeclaration =
      if (intVariables.nonEmpty)
        formatLine(lineSpaces) + Doc.text("int ") + Doc.intercalate(
          Doc.comma + Doc.space,
          intVariables
        ) + Doc.char(';') + Doc.line
      else Doc.empty

    val boolVariables = variables.filter(_.variableType == BooleanType).map(boolVar => Doc.text(boolVar.name))
    val boolDeclaration =
      if (boolVariables.nonEmpty)
        formatLine(lineSpaces) + Doc.text("bool ") + Doc.intercalate(
          Doc.comma + Doc.space,
          boolVariables
        ) + Doc.char(';') + Doc.line
      else Doc.empty

    var userVariablesDoc = Doc.empty

    for (variable <- variables){
      variable.variableType match {
        case ReferenceToUserDefinedType(name) =>
          val (userType, userTypeName) = stringToType(name, userTypes)

          userType match {

            case ArrayType(length, innerType) =>
              val variableType: String = getCType(innerType, userTypes)
              userVariablesDoc += formatLine(lineSpaces) + Doc.text(s"$variableType ${variable.name}[$length];") + Doc.line

            case RecordType(_) =>
              userVariablesDoc += formatLine(lineSpaces) + Doc.text(s"struct $userTypeName ${variable.name};") + Doc.line
          }

        case _ => ()
      }
    }

    intDeclaration + boolDeclaration + userVariablesDoc
  }


  def generateUserDefinedTypes(userTypes:List[UserDefinedType] , lineSpaces:Int): Doc = {

    var text:Doc = Doc.empty

    for (userType <- userTypes) {
      text += (userType.baseType match {
        case ArrayType(length, innerType) =>
          val variableType: String = getCType(innerType, userTypes)
          Doc.text(s"$variableType ${userType.name}[$length];") + Doc.line
        case RecordType(variables) =>
          Doc.text(s"struct ${userType.name} {") +
            Doc.line + generateDeclarations(variables, userTypes, lineSpaces) +
            Doc.text("};") + Doc.line
      })
    }
    text
  }

  def stringToType(typeAsString:String, userTypes:List[UserDefinedType]):Type  = {
    for (userType <- userTypes){
      if (userType.name == typeAsString){
        return userType.baseType
      }
    }
     throw new Exception("Type not Found.")
  }

  def getCType(variableType:Type, userTypes:List[UserDefinedType]):String= {
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
  // É possível refatorar isso aqui.
  def generateType(varType: Type): Doc = {
    varType match {
      case IntegerType => Doc.text("int")
      case BooleanType => Doc.text("bool")
      case _           => Doc.text("undefined")
    }
  }

  def generateConstants(constants: List[Constant]): Doc = {
    val constantsDeclaration = constants.map {
      constant =>
        Doc.text("#define ") + Doc.text(
          constant.name
        ) + Doc.space + Doc.text(genExp(constant.exp))
    }
    if (constantsDeclaration.nonEmpty)
      Doc.intercalate(Doc.line, constantsDeclaration) + Doc.line + Doc.line
    else
      Doc.empty
  }

  def generateStatement(
      statement: Statement,
      startSpaces: Int = 2,
      padSpaces: Int = 2
  ): Doc = {
    statement match {
      case AssignmentStmt(varName, expression) =>
        formatLine(startSpaces) + Doc.text(varName) + Doc.space + Doc.char(
          '='
        ) + Doc.space + Doc.text(genExp(expression)) + Doc.char(
          ';'
        ) + Doc.line
      case SequenceStmt(stmts) =>
        val multipleStmts = stmts.map(stmt => generateStatement(stmt, startSpaces, padSpaces))
        Doc.intercalate(Doc.empty, multipleStmts)
      case ReadIntStmt(varName) =>
        formatLine(startSpaces) + Doc.text("scanf(\"%d\", &") + Doc.text(
          varName
        ) + Doc.text(
          ");"
        ) + Doc.line
      case WriteStmt(expression) =>
        formatLine(startSpaces) + Doc.text(
          "printf(\"%d\\n\", "
        ) + Doc.text(genExp(expression)) + Doc.text(");") + Doc.line
      case ProcedureCallStmt(name, args) =>
        val expressions = args.map(arg => Doc.text(genExp(arg)))
        val functionArgs = Doc.intercalate(Doc.char(',') + Doc.space, expressions)
        functionArgs.tightBracketBy(
          formatLine(startSpaces) + Doc.text(name) + Doc.char('('),
          Doc.text(");")
        ) + Doc.line
      case IfElseStmt(condition, thenStmt, elseStmt) =>
        val ifCond =
          formatLine(startSpaces) + Doc.text("if (") + Doc.text(genExp(condition)) + Doc.text(
            ")"
          ) + Doc.text(" {") + Doc.line + generateStatement(
            thenStmt,
            startSpaces + padSpaces,
            padSpaces
          ) + formatLine(startSpaces) + Doc.char('}')
        val elseCond = elseStmt match {
          case Some(stmt) =>
            Doc.text(" else {") + Doc.line + generateStatement(
              stmt,
              startSpaces + padSpaces,
              padSpaces
            ) + formatLine(startSpaces) + Doc.char('}') + Doc.line
          case None => Doc.line
        }
        ifCond + elseCond
      case WhileStmt(condition, stmt) =>
        formatLine(startSpaces) + Doc.text("while (") + Doc.text(genExp(condition)) + Doc.text(")") +
          Doc.text(" {") + Doc.line + generateStatement(
          stmt,
          startSpaces + padSpaces,
          padSpaces
        ) + formatLine(startSpaces) + Doc.char('}') + Doc.line
      case ReturnStmt(exp) =>
        formatLine(startSpaces) + Doc.text("return ") + Doc.text(genExp(exp)) + Doc
          .char(';') + Doc.line

      case EAssignmentStmt(designator, exp) =>
        designator match {
          case ArrayAssignment(array, elem) =>
            array match {
              case VarExpression(name) =>
                val index = genExp(elem)
                formatLine(startSpaces) + Doc.text(s"$name[$index] = ${genExp(exp)};") + Doc.line
            }
          case RecordAssignment(record, atrib) =>
            formatLine(startSpaces) + Doc.text(s"${genExp(record)}.$atrib = ${genExp(exp)};") + Doc.line
        }

      case ExitStmt() =>
        formatLine(startSpaces) + Doc.text("break; ") + Doc.line

      case _ => Doc.empty
    }
  }

  def genExp(exp: Expression) : String = {

    exp match {
      case IntValue(v) => v.toString
      case Brackets(exp) => s"( ${genExp(exp)} )"
      case BoolValue(v) => if (v) "true" else "false"
      case Undef() => "undefined"

      case VarExpression(name) => name
      case FunctionCallExpression(name, args) =>
        val expressions = args.map(arg => Doc.text(genExp(arg)))
        val functionArgs = Doc.intercalate(Doc.char(',') + Doc.space, expressions)
        functionArgs.tightBracketBy(
          Doc.text(name) + Doc.char('('),
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
      case OrExpression(left, right) => s"${genExp(left)} | ${genExp(right)}"
      case AndExpression(left, right) => s"${genExp(left)} & ${genExp(right)}"
      case FieldAccessExpression(exp, name) => s"${genExp(exp)}.${name}"
      case _ => throw new Exception("expression not found")
    }
  }


  def generateBinExpression(
      left: Expression,
      right: Expression,
      sign: String
  ): Doc =
    Doc.text(genExp(left)) + Doc.space + Doc.text(
      sign
    ) + Doc.space + Doc.text(genExp(right))




  def formatLine(spaces: Int): Doc =
    Doc.intercalate(
      Doc.empty,
      List.fill(spaces)(Doc.space)
    ) // adiciona a indentação na quantidade de espaços definidos

}

class PPrintBasedGenerator extends CCodeGenerator {
  override def generateCode(module: OberonModule): String = ???
}
