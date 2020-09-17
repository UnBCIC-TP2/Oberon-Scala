package br.unb.cic.oberon.tc

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ast.{AddExpression, AssignmentStmt, BoolValue, BooleanType, IfElseStmt, IntValue, IntegerType, ReadIntStmt, SequenceStmt, Undef, VarExpression, WhileStmt, WriteStmt}
import br.unb.cic.oberon.parser.OberonParser.ReadIntStmtContext
import br.unb.cic.oberon.parser.ScalaParser
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

  test("Test a sequence of statements type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(10)) // invalid stmt
    val stmt03 = AssignmentStmt("x", AddExpression(IntValue(5), BoolValue(false))) // invalid stmt
    val stmt04 = WriteStmt(VarExpression("x"))
    val stmt05 = WriteStmt(VarExpression("y"))

    visitor.env.setGlobalVariable("x", IntegerType)

    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor).size == 1)
    assert(stmt03.accept(visitor).size == 1)

    val seq1 = SequenceStmt(List(stmt01, stmt04))
    val seq2 = SequenceStmt(List(stmt01, stmt05))

    assert(seq1.accept(visitor).size == 0)
    assert(seq2.accept(visitor).size == 1)
  }

  test("Test if-else statement type checker (with invalid condition)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))

    visitor.env.setGlobalVariable("x", IntegerType)

    val stmt02 = IfElseStmt(IntValue(10), stmt01, None)
    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor).size == 1)
  }

  test("Test if-else statement type checker (with invalid then-stmt)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))

    val stmt02 = IfElseStmt(BoolValue(true), stmt01, None)
    assert(stmt01.accept(visitor).size == 1)
    assert(stmt02.accept(visitor).size == 1)
  }

  test("Test if-else statement type checker (with invalid then-stmt and else-stmt)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(10))
    val stmt03 = IfElseStmt(BoolValue(true), stmt01, Some(stmt02))

    assert(stmt01.accept(visitor).size == 1)
    assert(stmt02.accept(visitor).size == 1)
    assert(stmt03.accept(visitor).size == 2)
  }

  test("Test if-else statement type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(10))

    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("y", IntegerType)

    val stmt03 = IfElseStmt(BoolValue(true), stmt01, Some(stmt02))

    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 0)
    assert(stmt03.accept(visitor).size == 0)
  }

  test("Test while statement type checker (with invalid condition)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))

    visitor.env.setGlobalVariable("x", IntegerType)

    val stmt02 = WhileStmt(IntValue(10), stmt01)
    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor).size == 1)
  }

  test("Test while statement type checker (with invalid stmt)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))

    val stmt02 = WhileStmt(BoolValue(true), stmt01)
    assert(stmt01.accept(visitor).size == 1)
    assert(stmt02.accept(visitor).size == 1)
  }

  test("Test while statement type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))

    visitor.env.setGlobalVariable("x", IntegerType)

    val stmt02 = WhileStmt(BoolValue(true), stmt01)
    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 0)
  }

  /*
   * the following test cases read an oberon module with the
   * factorial procedure.
   */
  test("Test invalid procedure declaration") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/procedure04.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.procedures.size == 2)
    assert(module.stmt.isDefined)


  }
}
