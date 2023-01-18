package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{Constant => ASTConstant, _}
import br.unb.cic.oberon.ir.tac._
import org.scalatest.funsuite.AnyFunSuite

class TACodeTest extends AnyFunSuite {

  test("Generate add between constants") {

    Temporary.reset
    val expr = AddExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // 1 + 2

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""))
    // t0 = 1 + 2

    assert(list == ops)
  }

  test("Generate add between add and constant") {
    
    Temporary.reset
    val expr = AddExpression(AddExpression(IntValue(1), IntValue(2)), IntValue(3))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 + 2) + 3

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      AddOp(t0, Constant("3", IntegerType), t1, "")
    )
    // t0 = 1 + 2
    // t1 = t0 + 3

    assert(list == ops)
  }

  test("Generate add between adds") {

    Temporary.reset
    val expr = AddExpression(AddExpression(IntValue(1), IntValue(2)), AddExpression(IntValue(3), IntValue(4)))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 + 2) + (3 + 4)

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val t2 = new Temporary(IntegerType, 2, true)
    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      AddOp(Constant("3", IntegerType), Constant("4", IntegerType), t1, ""),
      AddOp(t0, t1, t2, "")
    )
    // t0 = 1 + 2
    // t1 = 3 + 4
    // t2 = t0 + t1

    assert(list == ops)
  }

  test("Testing complex operation 1") {

    Temporary.reset
    val expr = MultExpression(IntValue(2), SubExpression(IntValue(1), AddExpression(IntValue(2), IntValue(3))))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // 2 * (1 - (2 + 3))

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val t2 = new Temporary(IntegerType, 2, true)
    val ops = List(
      AddOp(Constant("2", IntegerType), Constant("3", IntegerType), t0, ""),
      SubOp(Constant("1", IntegerType), t0, t1, ""),
      MulOp(Constant("2", IntegerType), t1, t2, "")
    )

    // t0 = 2 + 3
    // t1 = 1 - t0
    // t2 = 2 * t1

    assert(list == ops)
  }
}
