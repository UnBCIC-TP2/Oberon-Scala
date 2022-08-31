package br.unb.cic.oberon.interpreter


import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.interpreter.FInterpreter
import org.scalatest.funsuite.AnyFunSuite

class FEvalTest extends AnyFunSuite {

  val interpreter = new FInterpreter

  test("Test eval on simple values") {
    val val10 = IntValue(10)
    val bTrue = BoolValue(true)
    val bFalse = BoolValue(false)

    assert(interpreter.fEval(val10) == val10)

    assert(interpreter.fEval(bTrue) == bTrue)

    assert(interpreter.fEval(bFalse) == bFalse)
  }

  test("Test eval on arithmetic expressions (add and mult)") {
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val val30 = IntValue(30)

    val exp = AddExpression(val10, MultExpression(val20, val30))
    assert(interpreter.fEval(exp) == IntValue(610))
  }

  test("Test eval on arithmetic expressions (sub and div)") {
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val val30 = IntValue(30)

    val exp = SubExpression(val20, DivExpression(val30, val10))
    assert(interpreter.fEval(exp) == IntValue(17))
  }

  test("Test eval on boolean expressions ('and' and 'or')") {

    val valTrue = BoolValue(true)
    val valFalse = BoolValue(false)


    val exp = AndExpression(valTrue, AndExpression(valTrue, OrExpression(valTrue, valFalse)))
    assert(interpreter.fEval(exp) == valTrue)
  }

  test("Test eval on global variables") {
    interpreter.setGlobalVariable("x", IntValue(30))

    val exp = AddExpression(IntValue(10), VarExpression("x"))

    assert(interpreter.fEval(exp) == IntValue(40))
  }

  test("Test eval on local (stack) and global variables") {
    interpreter.setGlobalVariable("x", IntValue(30))
    interpreter.setLocalVariable("y", IntValue(10))

    val exp = AddExpression(VarExpression("x"), VarExpression("y"))

    assert(interpreter.fEval(exp) == IntValue(40))
  }

  // TODO: Write test cases  dealing with different scopes and name collision.
}

