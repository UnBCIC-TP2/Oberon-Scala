package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ir.ast.{AddExpression, BoolValue, BooleanType, DivExpression, EQExpression, IntValue, IntegerType, SubExpression}
import br.unb.cic.oberon.interpreter.Interpreter
import org.scalatest.funsuite.AnyFunSuite

class ExpressionTypeVisitorTestSuite extends AnyFunSuite {

    test("Test expression type on simple values") {
      val visitor = new ExpressionTypeVisitor(new TypeChecker())
      val val10 = IntValue(10)
      val bTrue = BoolValue(true)
      val bFalse = BoolValue(false)

      assert(val10.accept(visitor) == Some(IntegerType))

      assert(bTrue.accept(visitor) == Some(BooleanType))

      assert(bFalse.accept(visitor) == Some(BooleanType))
    }

  test("Test expression type on add expressions") {
    val visitor = new ExpressionTypeVisitor(new TypeChecker())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val add01 = AddExpression(val10, val20)
    val add02 = AddExpression(val10, add01)
    val add03 = AddExpression(add01, add02)

    assert(add01.accept(visitor) == Some(IntegerType))
    assert(add02.accept(visitor) == Some(IntegerType))
    assert(add03.accept(visitor) == Some(IntegerType))
  }

  test("Test expression type on sub expressions") {
    val visitor = new ExpressionTypeVisitor(new TypeChecker())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val sub01 = SubExpression(val10, val20)
    val sub02 = SubExpression(val10, sub01)
    val sub03 = SubExpression(sub01, sub02)

    assert(sub01.accept(visitor) == Some(IntegerType))
    assert(sub02.accept(visitor) == Some(IntegerType))
    assert(sub03.accept(visitor) == Some(IntegerType))
  }

  test("Test expression type on div expressions") {
    val visitor = new ExpressionTypeVisitor(new TypeChecker())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val sub01 = DivExpression(val10, val20)
    val sub02 = DivExpression(val10, sub01)
    val sub03 = DivExpression(sub01, sub02)

    assert(sub01.accept(visitor) == Some(IntegerType))
    assert(sub02.accept(visitor) == Some(IntegerType))
    assert(sub03.accept(visitor) == Some(IntegerType))
  }

  test("Test expression type on mult expressions") {
    val visitor = new ExpressionTypeVisitor(new TypeChecker())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val mult01 = AddExpression(val10, val20)
    val mult02 = AddExpression(val10, mult01)
    val mult03 = AddExpression(mult01, mult02)

    assert(mult01.accept(visitor) == Some(IntegerType))
    assert(mult02.accept(visitor) == Some(IntegerType))
    assert(mult03.accept(visitor) == Some(IntegerType))
  }

  test("Test expression type on eq expressions") {
    val visitor = new ExpressionTypeVisitor(new TypeChecker())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val eq01 = EQExpression(val10, val20)

    assert(eq01.accept(visitor) == Some(BooleanType))
  }

  test("Test expression add with boolean values") {
    val visitor = new ExpressionTypeVisitor(new TypeChecker())
    val val10 = IntValue(10)
    val valTrue = BoolValue(true)
    val invalidAdd = AddExpression(val10, valTrue)

    assert(invalidAdd.accept(visitor) == None)
  }


}
