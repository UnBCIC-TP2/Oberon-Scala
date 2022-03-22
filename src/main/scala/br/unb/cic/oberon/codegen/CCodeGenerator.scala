package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.transformations.CoreChecker
import org.typelevel.paiges._

class NotOberonCoreException(s:String) extends Exception(s){}

abstract class CCodeGenerator extends CodeGenerator {}

case class PaigesBasedGenerator(lineSpaces: Int = 4) extends CCodeGenerator {
  override def generateCode(module: OberonModule): String = {

    if (!CoreChecker.isModuleCore(module))
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
    val mainDeclarations = generateDeclarations(module.variables, lineSpaces)
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
    val main = mainHeader + mainDefines + mainProcedures + mainBody
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

    val procedureDeclarations = generateDeclarations(procedure.variables)
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

    intDeclaration + boolDeclaration
  }

  def generateConstants(constants: List[Constant]): Doc = {
    val constantsDeclaration = constants.map {
      constant =>
        Doc.text("#define ") + Doc.text(
          constant.name
        ) + Doc.space + generateExpression(constant.exp)
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
        ) + Doc.space + generateExpression(expression) + Doc.char(
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
        ) + generateExpression(
          expression
        ) + Doc.text(");") + Doc.line
      case ProcedureCallStmt(name, args) =>
        val expressions = args.map(arg => generateExpression(arg))
        val functionArgs = Doc.intercalate(Doc.char(',') + Doc.space, expressions)
        functionArgs.tightBracketBy(
          formatLine(startSpaces) + Doc.text(name) + Doc.char('('),
          Doc.text(");")
        ) + Doc.line
      case IfElseStmt(condition, thenStmt, elseStmt) =>
        val ifCond =
          formatLine(startSpaces) + Doc.text("if (") + generateExpression(
            condition
          ) + Doc.text(
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
        formatLine(startSpaces) + Doc.text("while (") + generateExpression(
          condition
        ) + Doc.text(")") +
          Doc.text(" {") + Doc.line + generateStatement(
          stmt,
          startSpaces + padSpaces,
          padSpaces
        ) + formatLine(startSpaces) + Doc.char('}') + Doc.line
      case ReturnStmt(exp) =>
        formatLine(startSpaces) + Doc.text("return ") + generateExpression(
          exp
        ) + Doc
          .char(';') + Doc.line

      case ExitStmt() =>
        formatLine(startSpaces) + Doc.text("break; ") + Doc.line

      case _ => Doc.empty
    }
  }

  def generateExpression(expression: Expression): Doc = {
    expression match {
      case Brackets(exp) =>
        Doc.char('(') + generateExpression(exp) + Doc
          .char(')')
      case IntValue(v)         => Doc.text(v.toString)
      case BoolValue(v)        => Doc.text(if (v) "true" else "false")
      case Undef()             => Doc.text("undefined")
      case VarExpression(name) => Doc.text(name)
      case FunctionCallExpression(name, args) =>
        val expressions = args.map(arg => generateExpression(arg))
        val functionArgs = Doc.intercalate(Doc.char(',') + Doc.space, expressions)
        functionArgs.tightBracketBy(
          Doc.text(name) + Doc.char('('),
          Doc.char(')')
        )
      case EQExpression(left, right) => generateBinExpression(left, right, "==")
      case NEQExpression(left, right) =>
        generateBinExpression(left, right, "!=")
      case GTExpression(left, right) => generateBinExpression(left, right, ">")
      case LTExpression(left, right) => generateBinExpression(left, right, "<")
      case GTEExpression(left, right) =>
        generateBinExpression(left, right, ">=")
      case LTEExpression(left, right) =>
        generateBinExpression(left, right, "<=")
      case AddExpression(left, right) => generateBinExpression(left, right, "+")
      case SubExpression(left, right) => generateBinExpression(left, right, "-")
      case MultExpression(left, right) =>
        generateBinExpression(left, right, "*")
      case DivExpression(left, right) => generateBinExpression(left, right, "/")
      case OrExpression(left, right)  => generateBinExpression(left, right, "|")
      case AndExpression(left, right) => generateBinExpression(left, right, "&")

      case _ => Doc.text("expression not found")
    }
  }

  def generateBinExpression(
      left: Expression,
      right: Expression,
      sign: String
  ): Doc =
    generateExpression(left) + Doc.space + Doc.text(
      sign
    ) + Doc.space + generateExpression(right)

  def generateType(varType: Type): Doc = {
    varType match {
      case IntegerType => Doc.text("int")
      case BooleanType => Doc.text("bool")
      case _           => Doc.text("undefined")
    }
  }

  def formatLine(spaces: Int): Doc =
    Doc.intercalate(
      Doc.empty,
      List.fill(spaces)(Doc.space)
    ) // adiciona a indentação na quantidade de espaços definidos

}

class PPrintBasedGenerator extends CCodeGenerator {
  override def generateCode(module: OberonModule): String = ???
}
