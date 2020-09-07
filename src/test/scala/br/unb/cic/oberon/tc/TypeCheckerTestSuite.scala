package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ast.{AddExpression, AssignmentStmt, BoolValue, BooleanType, IntValue, IntegerType, ReadIntStmt, Undef, WriteStmt}
import br.unb.cic.oberon.parser.OberonParser.ReadIntStmtContext
import org.scalatest.funsuite.AnyFunSuite

class TypeCheckerTestSuite  extends AnyFunSuite {

  test("Test read statement type checker") {
    val visitor = new TypeChecker()
    val read01 = ReadIntStmt("x")
    val read02 = ReadIntStmt("y")

    visitor.env.setGlobalVariable("x", IntegerType)

    assert(read01.accept(visitor) == List())
    assert(read02.accept(visitor).size == 1)
  }

  test("Test write statement type checker") {
    val visitor = new TypeChecker()
    val write01 = WriteStmt(IntValue(5))
    val write02 = WriteStmt(AddExpression(IntValue(5), BoolValue(false)))

    assert(write01.accept(visitor) == List())
    assert(write02.accept(visitor).size == 1)
  }

  test("Test assignment statement type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(10)) // invalid stmt
    val stmt03 = AssignmentStmt("x", AddExpression(IntValue(5), BoolValue(false))) // invalid stmt

    visitor.env.setGlobalVariable("x", IntegerType)

    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor).size == 1)
    assert(stmt03.accept(visitor).size == 1)
  }
}
