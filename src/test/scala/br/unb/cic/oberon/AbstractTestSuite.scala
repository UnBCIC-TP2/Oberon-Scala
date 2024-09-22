package br.unb.cic.oberon

//import br.unb.cic.oberon.ir.ast.{AssignmentStmt, Expression, VarAssignment}
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ir.common._
import br.unb.cic.oberon.ir.ast._


abstract class AbstractTestSuite extends AnyFunSuite {
  def AssignmentStmt(varName: String, expression: Expression) =
    new AssignmentStmt(VarAssignment(varName), expression)
}
