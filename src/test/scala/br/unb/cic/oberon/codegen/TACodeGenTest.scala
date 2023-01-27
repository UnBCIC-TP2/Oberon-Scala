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

//  ca}
// se class SLTOp
// case class SLTUOp}

  test("Generate add between add and constant") {
    
    Temporary.reset
    val expr = AddExpression(AddExpression(IntValue(1), IntValue(2)), IntValue(3))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      AddOp(t0, Constant("3", IntegerType), t1, "")
    )

    assert(list == ops)
  }

  test("Generate add between adds") {

    Temporary.reset
    val expr = AddExpression(AddExpression(IntValue(1), IntValue(2)), AddExpression(IntValue(3), IntValue(4)))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val t2 = new Temporary(IntegerType, 2, true)
    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      AddOp(Constant("3", IntegerType), Constant("4", IntegerType), t1, ""),
      AddOp(t0, t1, t2, "")
    )

    assert(list == ops)
  }

  test("Testing complex operation 2") {

    Temporary.reset
    val expr = SubExpression(DivExpression(MultExpression(IntValue(2), IntValue(2)), IntValue(3)), IntValue(6))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // ((2 * 2) / 3) - 6

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val t2 = new Temporary(IntegerType, 2, true)
    val ops = List(
      MulOp(Constant("2", IntegerType), Constant("2", IntegerType), t0, ""),
      DivOp(t0, Constant("3", IntegerType), t1, ""),
      SubOp(t1, Constant("6", IntegerType), t2, "")
    )
    // t0 = 2 * 2
    // t1 = t0 / 3
    // t2 = t1 - 6

    assert(list == ops)
  }

  test("Testing complex operation 3") {
    
    Temporary.reset
    val expr = OrExpression(AndExpression(BoolValue(true), BoolValue(false)), OrExpression(BoolValue(true), AndExpression(BoolValue(true), NotExpression(BoolValue(true)))))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (True and False) or (True or (True and False))

    Temporary.reset
    val t0 = new Temporary(BooleanType, 0, true)
    val t1 = new Temporary(BooleanType, 1, true)
    val t2 = new Temporary(BooleanType, 2, true)
    val t3 = new Temporary(BooleanType, 3, true)
    val t4 = new Temporary(BooleanType, 4, true)
    val ops = List(
      AndOp(Constant("true", BooleanType), Constant("false", BooleanType), t0, ""),
      NotOp(Constant("true", BooleanType), t1, ""),
      AndOp(Constant("true", BooleanType), t1, t2, ""),
      OrOp(Constant("true", BooleanType), t2, t3, ""),
      OrOp(t0, t3, t4, "")
    )
    // t0 = true and false
    // t1 = not(true)
    // t2 = true and t1
    // t3 = true or t2
    // t4 = t0 or t3

    assert(list == ops)
  }

  test ("TAC NotExpression") {

    Temporary.reset
    val expr = NotExpression(BoolValue(true))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // not(true)

    Temporary.reset
    val t0 = new Temporary(BooleanType, 0, true)
    val ops = List(
      NotOp(Constant("true", BooleanType), t0, "")
    )    
    // t0 = not true

    assert(list == ops)

  }

/*
    ==
    !=
    >
    <
    >=
    <=
*/

  test("Testing EQ") {
    
    Temporary.reset
    val expr = EQExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 == 2)

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      SubOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      SLTUOp(t0, Constant("1", IntegerType), t1, "")
    )    
    // t0 = 1 - 2
    // t1 = sltuop t0 1

    assert(list == ops)
  }

  test("Testing NEQ") {
    
    Temporary.reset
    val expr = NEQExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 != 2)

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      SubOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      SLTUOp(Constant("0", IntegerType), t0, t1, "")
    )    
    // t0 = 1 - 2
    // t1 = sltuop t0 1

    assert(list == ops)
  }

  test("Testing GT") {
    
    Temporary.reset
    val expr = GTExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 > 2)

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      SLTOp(Constant("2", IntegerType), Constant("1", IntegerType), t0, "")
    )    
    // t0 = 1 > 2

    assert(list == ops)
  }

  test("Testing LT") {
    
    Temporary.reset
    val expr = LTExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 < 2)

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      SLTOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, "")
    )    
    // t0 = slt 1 2

    assert(list == ops)
  }

  test("TAC Testing GT") {
    
    Temporary.reset
    val expr = GTExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 > 2)

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      SLTOp(Constant("2", IntegerType), Constant("1", IntegerType), t0, "")
    )    
    // t0 = slt 2 1

    assert(list == ops)
  }

  test("Testing GTE") {
    
    Temporary.reset
    val expr = GTEExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 >= 2)

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      SLTOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      NotOp(t0, t1, "")
    )    
    // t0 = slt 1 2
    // t1 = not t0

    assert(list == ops)
  }

  test("Testing LTE") {
    
    Temporary.reset
    val expr = LTEExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 <= 2)

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      SLTOp(Constant("2", IntegerType), Constant("1", IntegerType), t0, ""),
      NotOp(t0, t1, "")
    )    
    // t0 = slt 2 1
    // t1 = not t0

    assert(list == ops)
  }

}
