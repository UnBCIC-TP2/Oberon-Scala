package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ir.ast.{AddExpression, BoolValue, BooleanType, DivExpression, EQExpression, IntValue, IntegerType, SubExpression}
import br.unb.cic.oberon.interpreter.Interpreter
import org.scalatest.funsuite.AnyFunSuite

class ExpressionTypeVisitorTestSuite extends AnyFunSuite {

  test("Test expression type on simple values") {
    val visitor = new ExpressionTypeChecker(new TypeChecker())
    val val10 = IntValue(10)
    val bTrue = BoolValue(true)
    val bFalse = BoolValue(false)

    assert(visitor.checkExp(val10) == Some(IntegerType))
    assert(visitor.checkExp(bTrue) == Some(BooleanType))
    assert(visitor.checkExp(bFalse) == Some(BooleanType))
  }

  test("Test expression type on add expressions") {
    val visitor = new ExpressionTypeChecker(new TypeChecker())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val add01 = AddExpression(val10, val20)
    val add02 = AddExpression(val10, add01)
    val add03 = AddExpression(add01, add02)

    assert(visitor.checkExp(add01) == Some(IntegerType))
    assert(visitor.checkExp(add02) == Some(IntegerType))
    assert(visitor.checkExp(add03) == Some(IntegerType))
  }

  test("Test expression type on sub expressions") {
    val visitor = new ExpressionTypeChecker(new TypeChecker())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val sub01 = SubExpression(val10, val20)
    val sub02 = SubExpression(val10, sub01)
    val sub03 = SubExpression(sub01, sub02)

    assert(visitor.checkExp(sub01) == Some(IntegerType))
    assert(visitor.checkExp(sub02) == Some(IntegerType))
    assert(visitor.checkExp(sub03) == Some(IntegerType))
  }

  test("Test expression type on div expressions") {
    val visitor = new ExpressionTypeChecker(new TypeChecker())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val sub01 = DivExpression(val10, val20)
    val sub02 = DivExpression(val10, sub01)
    val sub03 = DivExpression(sub01, sub02)

    assert(visitor.checkExp(sub01) == Some(IntegerType))
    assert(visitor.checkExp(sub02) == Some(IntegerType))
    assert(visitor.checkExp(sub03) == Some(IntegerType))
  }

  test("Test expression type on mult expressions") {
    val visitor = new ExpressionTypeChecker(new TypeChecker())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val mult01 = AddExpression(val10, val20)
    val mult02 = AddExpression(val10, mult01)
    val mult03 = AddExpression(mult01, mult02)

    assert(visitor.checkExp(mult01) == Some(IntegerType))
    assert(visitor.checkExp(mult02) == Some(IntegerType))
    assert(visitor.checkExp(mult03) == Some(IntegerType))
  }

  test("Test expression type on eq expressions") {
    val visitor = new ExpressionTypeChecker(new TypeChecker())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val eq01 = EQExpression(val10, val20)

    assert(visitor.checkExp(eq01) == Some(BooleanType))
  }

  test("Test expression add with boolean values") {
    val visitor = new ExpressionTypeChecker(new TypeChecker())
    val val10 = IntValue(10)
    val valTrue = BoolValue(true)
    val invalidAdd = AddExpression(val10, valTrue)

    assert(visitor.checkExp(invalidAdd) == None)
  }

}
