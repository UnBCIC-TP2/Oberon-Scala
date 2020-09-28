package br.unb.cic.oberon.codegen
import br.unb.cic.oberon.ast._
import scala.annotation.tailrec
import org.typelevel.paiges._

abstract class CCodeGenerator extends CodeGenerator {}

case class PaigesBasedGenerator() extends CCodeGenerator {
  override def generateCode(module: OberonModule): String = {
    for (procedure <- module.procedures) {
      println(generateProcedure(procedure).render(60))
    }
    val main = module.stmt match {
      case Some(stmt) => Doc.text("void main() ") + Doc.line + generateStatement(stmt).tightBracketBy(Doc.char('{'), Doc.char('}'))
      case None => Doc.text("void main() {}")
    }
    println(main.render(60))
    ""
  }

  def generateProcedure(procedure: Procedure): Doc = {
    var returnType = Doc.empty;
    procedure.returnType match {
      case Some(IntegerType) => returnType = Doc.text("int")
      case Some(BooleanType) => returnType = Doc.text("bool")
      case None              => returnType = Doc.text("void")
      case _                 => returnType = Doc.text("undefined")
    }
    val args = procedure.args.map {
      case (arg) =>
        var argumentType = Doc.empty;
        arg.argumentType match {
          case IntegerType => argumentType = Doc.text("int")
          case BooleanType => argumentType = Doc.text("bool")
          case _           => argumentType = Doc.text("undefined")
        }
        argumentType + Doc.space + Doc.text(arg.name)
    }
    val procedureArgs = Doc.intercalate(Doc.char(',') + Doc.space, args)
    val procedureName =
      returnType + Doc.space + Doc.text(procedure.name) + Doc.char('(')
    val procedureHeader =
      procedureArgs.tightBracketBy(procedureName, Doc.char(')'))
    val procedureBody = generateStatement(procedure.stmt)
    procedureHeader + Doc.space + Doc.char('{') + Doc.line + procedureBody + Doc
      .char('}')
  }

  def generateStatement(statement: Statement): Doc = {
    statement match {
      case AssignmentStmt(varName, expression) =>
        Doc.space + Doc.text(varName) + Doc.space + Doc.char(
          '='
        ) + Doc.space + generateExpression(expression) + Doc.char(
          ';'
        ) + Doc.line
      case SequenceStmt(stmts) => {
        var multipleStmts = Doc.empty
        for (stmt <- stmts) {
          multipleStmts = multipleStmts + generateStatement(stmt)
        }
        multipleStmts
      }
      case ReadIntStmt(varName) =>
        Doc.space + Doc.text("scanf(\"%d\", &") + Doc.text(varName) + Doc.text(
          ");"
        ) + Doc.line
      case WriteStmt(expression) => {
        Doc.space + Doc.text("printf(\"%d\", ") + generateExpression(
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
          Doc.text(name) + Doc.char('('),
          Doc.char(')')
        )
      }
      case IfElseStmt(condition, thenStmt, elseStmt) => {
        val ifCond = Doc.space + Doc.text("if (") + generateExpression(
          condition
        ) + Doc.text(
          ")"
        ) + Doc.line + generateStatement(thenStmt).tightBracketBy(
          Doc.text(" {"),
          Doc.text(" }")
        )
        val elseCond = elseStmt match {
          case Some(stmt) => {
            Doc.space + Doc.text("else ") + generateStatement(
              thenStmt
            ).tightBracketBy(
              Doc.text(" {"),
              Doc.text(" }")
            )
          }
          case None => Doc.empty
        }
        ifCond + elseCond
      }
      case WhileStmt(condition, stmt) =>
        Doc.space + Doc.text("while (") + generateExpression(
          condition
        ) + Doc.text(
          ")"
        ) + Doc.line + generateStatement(stmt).tightBracketBy(
          Doc.text(" {"),
          Doc.text(" }")
        )
      case _ => Doc.empty
    }

  }

  def generateExpression(expression: Expression): Doc = {
    expression match {
      case IntValue(v)         => Doc.text(v.toString())
      case BoolValue(v)        => Doc.text(if (v) "true" else "false")
      case Undef()             => Doc.text("undefined")
      case VarExpression(name) => Doc.text(name)
      case Brackets(exp) =>
        Doc.char('(') + Doc.space + generateExpression(exp) + Doc.space + Doc
          .char(')')
      case EQExpression(left, right) => generateBinExpression(left, right, "==")
      case AddExpression(left, right) => generateBinExpression(left, right, "+")
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
}

class PPrintBasedGenerator extends CCodeGenerator {
  override def generateCode(module: OberonModule): String = ???
}
