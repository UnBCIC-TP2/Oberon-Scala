package br.unb.cic.oberon.interpreter


import br.unb.cic.oberon.ast._
import org.scalatest.funsuite.AnyFunSuite

class EvalExpressionVisitorTest extends AnyFunSuite {

  test("Test eval on simple values") {
    val visitor = new EvalExpressionVisitor(new Interpreter())
    val val10 = IntValue(10)
    val bTrue = BoolValue(true)
    val bFalse = BoolValue(false)

    assert(val10.accept(visitor) == val10)

    assert(bTrue.accept(visitor) == bTrue)

    assert(bFalse.accept(visitor) == bFalse)
  }

  test("Test eval on arithmetic expressions (add and mult)") {
    val visitor = new EvalExpressionVisitor(new Interpreter())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val val30 = IntValue(30)

    val exp = AddExpression(val10, MultExpression(val20, val30))
    assert(exp.accept(visitor) == IntValue(610))
  }

  test("Test eval on arithmetic expressions (sub and div)") {
    val visitor = new EvalExpressionVisitor(new Interpreter())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val val30 = IntValue(30)

    val exp = SubExpression(val20, DivExpression(val30, val10))
    assert(exp.accept(visitor) == IntValue(17))
  }

  test("Test eval on boolean expressions ('and' and 'or')") {
    val visitor = new EvalExpressionVisitor(new Interpreter())

    val valTrue = BoolValue(true)
    val valFalse = BoolValue(false)


    val exp = AndExpression(valTrue, AndExpression(valTrue, OrExpression(valTrue, valFalse)))
    assert(exp.accept(visitor) == valTrue)
  }

  test("Test eval on global variables") {
    val interpreter = new Interpreter()
    interpreter.setGlobalVariable("x", IntValue(30))

    val visitor = new EvalExpressionVisitor(interpreter)

    val exp = AddExpression(IntValue(10), VarExpression("x"))

    assert(exp.accept(visitor) == IntValue(40))
  }

  test("Test eval on local (stack) and global variables") {
    val interpreter = new Interpreter()
    interpreter.setGlobalVariable("x", IntValue(30))
    interpreter.setLocalVariable("y", IntValue(10))

    val visitor = new EvalExpressionVisitor(interpreter)

    val exp = AddExpression(VarExpression("x"), VarExpression("y"))

    assert(exp.accept(visitor) == IntValue(40))
  }

  // TODO: Write test cases  dealing with different scopes and name collision.
}
