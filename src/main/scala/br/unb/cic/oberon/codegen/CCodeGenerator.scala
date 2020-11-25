package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ast._
import scala.annotation.tailrec
import org.typelevel.paiges._

abstract class CCodeGenerator extends CodeGenerator {}

case class PaigesBasedGenerator(lineSpaces: Int = 2) extends CCodeGenerator {
  override def generateCode(module: OberonModule): String = {
    val mainHeader = Doc.text("#include <stdio.h>") + Doc.line + Doc.text(
      "#include <stdbool.h>"
    ) + Doc.line + Doc.line
    val procedureDocs = module.procedures.map {
      case (procedure) => generateProcedure(procedure, lineSpaces)
    }
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
        Doc.text("void main() ") + Doc.char(
          '{'
        ) + Doc.line + mainDeclarations + Doc.line + generateStatement(
          stmt,
          lineSpaces,
          lineSpaces
        ) + Doc.char('}')
      case None => Doc.text("void main() {}")
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
      case (arg) =>
        val argumentType = generateType(arg.argumentType)
        argumentType + Doc.space + Doc.text(arg.name)
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
    val intVariables = variables.filter(_.variableType == IntegerType).map {
      case (intVar) => Doc.text(intVar.name)
    }
    val intDeclaration =
      if (intVariables.nonEmpty)
        formatLine(lineSpaces) + Doc.text("int ") + Doc.intercalate(
          Doc.comma + Doc.space,
          intVariables
        ) + Doc.char(';') + Doc.line
      else Doc.empty

    val boolVariables = variables.filter(_.variableType == BooleanType).map {
      case (boolVar) => Doc.text(boolVar.name)
    }
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
      case (constant) =>
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
      case SequenceStmt(stmts) => {
        val multipleStmts = stmts.map {
          case (stmt) => generateStatement(stmt, startSpaces, padSpaces)
        }
        Doc.intercalate(Doc.empty, multipleStmts)
      }
      case ReadIntStmt(varName) =>
        formatLine(startSpaces) + Doc.text("scanf(\"%d\", &") + Doc.text(
          varName
        ) + Doc.text(
          ");"
        ) + Doc.line
      case WriteStmt(expression) => {
        formatLine(startSpaces) + Doc.text(
          "printf(\"%d\\n\", "
        ) + generateExpression(
          expression
        ) + Doc.text(");") + Doc.line
      }
      case ProcedureCallStmt(name, args) => {
        val expressions = args.map {
          case (arg) =>
            generateExpression(arg)
        }
        val functionArgs =
          Doc.intercalate(Doc.char(',') + Doc.space, expressions)
        functionArgs.tightBracketBy(
          formatLine(startSpaces) + Doc.text(name) + Doc.char('('),
          Doc.text(");")
        ) + Doc.line
      }
      case IfElseStmt(condition, thenStmt, elseStmt) => {
        val ifCond =
          formatLine(startSpaces) + Doc.text("if (") + generateExpression(
            condition
          ) + Doc.text(
            ")"
          ) + Doc.text(" {") + Doc.line + generateStatement(
            thenStmt,
            startSpaces + padSpaces,
            padSpaces
          ) + formatLine(startSpaces) + Doc.char('}') + Doc.line
        val elseCond = elseStmt match {
          case Some(stmt) => {
            formatLine(startSpaces) + Doc.text("else") +
              Doc.text(" {") + Doc.line + generateStatement(
              stmt,
              startSpaces + padSpaces,
              padSpaces
            ) + Doc.char('}') + Doc.line
          }
          case None => Doc.empty
        }
        ifCond + elseCond
      }
      case WhileStmt(condition, stmt) => {
        formatLine(startSpaces) + Doc.text("while (") + generateExpression(
          condition
        ) + Doc.text(")") +
          Doc.text(" {") + Doc.line + generateStatement(
          stmt,
          startSpaces + padSpaces,
          padSpaces
        ) + formatLine(startSpaces) + Doc.char('}') + Doc.line
      }
      case RepeatUntilStmt(condition, stmt) => {
        formatLine(startSpaces) + Doc.text("do {") + Doc.line +
          generateStatement(stmt, startSpaces + padSpaces, padSpaces) +
          formatLine(startSpaces) + Doc.text("} while (!(") +
          generateExpression(condition) + Doc.text("));") + Doc.line
      }
      case ForStmt(init: Statement, condition, stmt) => {
        init match {
          case AssignmentStmt(varName, expression) => {
            formatLine(startSpaces) + Doc.text("for (") + Doc.text(
              varName
            ) + Doc.space + Doc.char('=') + Doc.space +
              generateExpression(expression) + Doc.text(
              "; "
            ) + generateExpression(condition) + Doc.text("; ") +
              Doc.text(varName) + Doc.text("++") + Doc.text(
              ") {"
            ) + Doc.line + generateStatement(
              stmt,
              startSpaces + padSpaces,
              padSpaces
            ) + formatLine(
              startSpaces
            ) +
              Doc.text("}") + Doc.line
          }
          case _ => Doc.empty
        }
      }

      case ReturnStmt(exp) =>
        formatLine(startSpaces) + Doc.text("return ") + generateExpression(
          exp
        ) + Doc
          .char(';') + Doc.line

      case CaseStmt(exp, cases, elseStmt) => {
        val caseStmts = cases.map {
          case SimpleCase(condition, stmt) =>
            formatLine(startSpaces) + Doc.text("case ") + generateExpression(
              condition
            ) + Doc.char(':') + Doc.line + generateStatement(
              stmt,
              startSpaces + padSpaces,
              padSpaces
            ) + formatLine(startSpaces + padSpaces) + Doc.text("break;")
          case RangeCase(min, max, stmt) =>
            formatLine(startSpaces) + Doc.text("case ") + generateExpression(
              min
            ) + Doc.text(" ... ") + generateExpression(max) + Doc.char(
              ':'
            ) + Doc.line + generateStatement(
              stmt,
              startSpaces + padSpaces,
              padSpaces
            ) + formatLine(startSpaces + padSpaces) + Doc.text("break;")
        }
        val stmtDefault = elseStmt match {
          case Some(stmt) =>
            formatLine(startSpaces) + Doc.text(
              "default:"
            ) + Doc.line + generateStatement(
              stmt,
              startSpaces + padSpaces,
              padSpaces
            ) + formatLine(startSpaces + padSpaces) + Doc.text(
              "break;"
            ) + Doc.line
          case None => Doc.empty
        }
        val stmts = Doc.intercalate(Doc.line, caseStmts :+ stmtDefault)

        formatLine(startSpaces) + Doc.text("switch ") + Doc.char(
          '('
        ) + generateExpression(exp) + Doc.text(
          ") {"
        ) + Doc.line + stmts + formatLine(startSpaces) + Doc.char(
          '}'
        ) + Doc.line
      }

      case _ => Doc.empty
    }
  }

  def generateExpression(expression: Expression): Doc = {
    expression match {
      case Brackets(exp) =>
        Doc.char('(') + generateExpression(exp) + Doc
          .char(')')
      case IntValue(v)         => Doc.text(v.toString())
      case BoolValue(v)        => Doc.text(if (v) "true" else "false")
      case Undef()             => Doc.text("undefined")
      case VarExpression(name) => Doc.text(name)
      case FunctionCallExpression(name, args) => {
        val expressions = args.map {
          case (arg) =>
            generateExpression(arg)
        }
        val functionArgs =
          Doc.intercalate(Doc.char(',') + Doc.space, expressions)
        functionArgs.tightBracketBy(
          Doc.text(name) + Doc.char('('),
          Doc.char(')')
        )
      }
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
