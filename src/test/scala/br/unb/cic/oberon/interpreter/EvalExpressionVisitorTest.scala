package br.unb.cic.oberon.interpreter

import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.ir.ast._
import org.scalatest.funsuite.AnyFunSuite

class EvalExpressionVisitorTest extends AnyFunSuite {

  test("Test eval on simple values") {
    val interpreter = new Interpreter()
    val env = new Environment[Expression]()
    val val10 = IntValue(10)
    val bTrue = BoolValue(true)
    val bFalse = BoolValue(false)

    val (_, exp1) = interpreter.evalExpression(env, val10)
    assert(exp1 == val10)

    val (_, exp2) = interpreter.evalExpression(env, bTrue)
    assert(exp2 == bTrue)

    val (_, exp3) = interpreter.evalExpression(env, bFalse)
    assert(exp3 == bFalse)
  }

  test("Test eval on arithmetic expressions (add and mult)") {
    val interpreter = new Interpreter()
    val env = new Environment[Expression]()
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val val30 = IntValue(30)

    val exp = AddExpression(val10, MultExpression(val20, val30))

    val (_, exp1) = interpreter.evalExpression(env, exp)
    assert(exp1 == IntValue(610))
  }

  test("Test eval on arithmetic expressions (sub and div)") {
    val interpreter = new Interpreter()
    val env = new Environment[Expression]()
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val val30 = IntValue(30)

    val exp = SubExpression(val20, DivExpression(val30, val10))

    val (_, exp1) = interpreter.evalExpression(env, exp)
    assert(exp1 == IntValue(17))
  }

  test("Test eval on boolean expressions ('and' and 'or')") {
    val interpreter = new Interpreter()
    val env = new Environment[Expression]()
    val valTrue = BoolValue(true)
    val valFalse = BoolValue(false)
    val exp = AndExpression(valTrue, AndExpression(valTrue, OrExpression(valTrue, valFalse)))

    val (_, exp1) = interpreter.evalExpression(env, exp)
    assert(exp1 == valTrue)
  }

  test("Test eval on global variables") {
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    env = interpreter.setGlobalVariable(env, "x", IntValue(30))

    val exp = AddExpression(IntValue(10), VarExpression("x"))

    val (_, exp1) = interpreter.evalExpression(env, exp)
    assert(exp1 == IntValue(40))
  }

  test("Test eval on local (stack) and global variables") {
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    env = interpreter.setGlobalVariable(env, "x", IntValue(30))
    env = interpreter.setLocalVariable(env, "y", IntValue(10))

    val exp = AddExpression(VarExpression("x"), VarExpression("y"))
    val (_, exp1) = interpreter.evalExpression(env, exp)
    assert(exp1 == IntValue(40))
  }

  // TODO: Write test cases  dealing with different scopes and name collision.
}
 