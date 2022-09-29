package br.unb.cic.oberon.interpreter


import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.interpreter.FInterpreter
import br.unb.cic.oberon.environment.FEnvironment
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.auxiliary.DAssert

class EvalExpressionTest extends AnyFunSuite {

  val env = new FEnvironment()
  val interpreter = new FInterpreter
  val delta = 0.0000000000001

  test("Test eval on simple values") {
    val val10 = IntValue(10)
    val bTrue = BoolValue(true)
    val bFalse = BoolValue(false)

    assert(interpreter.evalExpression(val10, env) == val10)

    assert(interpreter.evalExpression(bTrue, env) == bTrue)

    assert(interpreter.evalExpression(bFalse, env) == bFalse)
  }

  test("Test eval on arithmetic expressions (add and mult)") {
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val val30 = IntValue(30)

    val exp = AddExpression(val10, MultExpression(val20, val30))
    assert(interpreter.evalExpression(exp, env) == IntValue(610))
  }

  test("Test eval on arithmetic expressions (sub and div)") {
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val val30 = IntValue(30)

    val exp = SubExpression(val20, DivExpression(val30, val10))
    assert(interpreter.evalExpression(exp, env) == IntValue(17))
  }

  test("Test eval on boolean expressions ('and' and 'or')") {

    val valTrue = BoolValue(true)
    val valFalse = BoolValue(false)


    val exp = AndExpression(valTrue, AndExpression(valTrue, OrExpression(valTrue, valFalse)))
    assert(interpreter.evalExpression(exp, env) == valTrue)
  }

  test("Test eval on global variables") {
    val environment =  new FEnvironment()
    environment.setGlobalVariable("x", IntValue(30))

    val exp = AddExpression(IntValue(10), VarExpression("x"))

    assert(interpreter.evalExpression(exp, environment) == IntValue(40))
  }

  test("Test eval on local (stack) and global variables") {
    val environment = new FEnvironment()
    environment.setGlobalVariable("x", IntValue(30))
    environment.setLocalVariable("y", IntValue(10))

    val exp = AddExpression(VarExpression("x"), VarExpression("y"))

    assert(interpreter.evalExpression(exp, environment) == IntValue(40))
  }

  test("Test eval on 0 sum") {
    val val0 = IntValue(0)

    val exp = AddExpression(val0, val0)

    assert(interpreter.evalExpression(exp, env) == IntValue(0))
  }

  test("Test eval on Sum over 2Bytes Unsigned") {
    val x = IntValue(32753)
    val y = IntValue(32783)

    val exp = AddExpression(x, y)

    assert(interpreter.evalExpression(exp, env) == IntValue(65536))
  }


  test("Test eval on Sum of Negative Values") {
    val x = IntValue(-10)

    val exp = AddExpression(x, x)

    assert(interpreter.evalExpression(exp, env) == IntValue(-20))
  }

  test("Test eval on Sum of Negative and Positive Values") {
    val x = IntValue(-10)
    val y = IntValue(10)

    val exp = AddExpression(x, y)

    assert(interpreter.evalExpression(exp, env) == IntValue(0))
  }
  test("Test eval on Sum of Real Values") {
    val x = RealValue(5.1)
    val y = RealValue(10.9)

    val exp = AddExpression(x, y)

    assert((interpreter.evalExpression(exp, env).asInstanceOf[RealValue].value - (16)).abs < delta)
  }

  test("Multiplication with 0 and a Integer") {
    val x = IntValue(0)
    val y = IntValue(10)

    val exp = MultExpression(x, y)

    assert(interpreter.evalExpression(exp, env) == IntValue(0))
  }

  test("Multiplication with Negative Number") {
    val x = IntValue(10)
    val y = IntValue(-5)

    val exp = MultExpression(x, y)

    assert(interpreter.evalExpression(exp, env) == IntValue(-50))
  }

  test("Multiplication with just 0s") {
    val x = IntValue(0)

    val exp = MultExpression(x, x)

    assert(interpreter.evalExpression(exp, env) == IntValue(0))
  }
  test("Multiplication with Sum") {
    val x = IntValue(10)
    val y = IntValue(4)

    val exp = AddExpression(y, MultExpression(x, x))

    assert(interpreter.evalExpression(exp, env) == IntValue(104))
  }
  test("Sum of Real Values") {
    val x = RealValue(15.2)
    val y = RealValue(4.9)

    val exp = AddExpression(x, y)

    DAssert.deltaAssert(interpreter.evalExpression(exp, env), 20.1, delta)
  }

  test("Sum of Real Value with Negative Real Value - 1") {
    val x = RealValue(18.7)
    val y = RealValue(-2.91)

    val exp = AddExpression(x, y)

    DAssert.deltaAssert(interpreter.evalExpression(exp, env), 15.79, delta)
  }

 test("Sum of Real Value with Negative Real Value - 2") {
   val x = RealValue(-4.9)
   val y = RealValue(4)

   val exp = AddExpression(x, y)

    DAssert.deltaAssert(interpreter.evalExpression(exp, env), -0.9, delta)
 }

 test("Sum of Negative Real Values") {
   val x = RealValue(-6.2)
   val y = RealValue(-203.7)

   val exp = AddExpression(x, y)

    DAssert.deltaAssert(interpreter.evalExpression(exp, env), -209.9, delta)
 }

  test("Sum of Real and Integer Values") {
    val x = RealValue(0.0)
    val y = IntValue(4)

    val exp = AddExpression(x, y)

    DAssert.deltaAssert(interpreter.evalExpression(exp, env), 4, delta)
  }

  test("Multiplication of Real Positive Values") {
    val x = RealValue(2.3)
    val y = RealValue(9.13)

    val exp = MultExpression(x, y)

    DAssert.deltaAssert(interpreter.evalExpression(exp, env), 20.999, delta)
  }

 test("Multiplication of Real Positive and Real Negative Values") {
   val x = RealValue(4.6)
   val y = RealValue(-13.5)

   val exp = MultExpression(x, y)

   DAssert.deltaAssert(interpreter.evalExpression(exp, env), -62.1, delta)
 }

 test("Multiplication of Real and Integer Values") {
   val x = RealValue(3.7)
   val y = IntValue(9)

   val exp = MultExpression(x, y)

   DAssert.deltaAssert(interpreter.evalExpression(exp, env), 33.3, delta)
 }

  test("Multiplication of Real Negative Values") {
    val x = RealValue(-14.5)
    val y = RealValue(-0.9835)

    val exp = MultExpression(x, y)
    
    DAssert.deltaAssert(interpreter.evalExpression(exp, env), 14.26075, delta)
  }
  // TODO: Write test cases  dealing with different scopes and name collision.
}
