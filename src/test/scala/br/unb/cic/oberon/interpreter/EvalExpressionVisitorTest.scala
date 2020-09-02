package br.unb.cic.oberon.interpreter

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ast.{AddExpression, AndExpression, BoolValue, Constant, DivExpression, Expression, IntValue, MultExpression, OrExpression, SubExpression, VarExpression}
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class EvalExpressionVisitorTest extends AnyFunSuite {

  test("Test eval on simple values") {
    val visitor = new EvalExpressionVisitor(new Interpreter())
    val val10 = IntValue(10)
    val bTrue = BoolValue(true)
    val bFalse = BoolValue(false)

    val10.accept(visitor)
    assert(visitor.result == val10)

    bTrue.accept(visitor)
    assert(visitor.result == bTrue)

    bFalse.accept(visitor)
    assert(visitor.result == bFalse)
  }

  test("Test eval on arithmetic expressions (add and mult)") {
    val visitor = new EvalExpressionVisitor(new Interpreter())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val val30 = IntValue(30)

    val exp = AddExpression(val10, MultExpression(val20, val30))
    exp.accept(visitor)

    assert(visitor.result == IntValue(610))
  }

  test("Test eval on arithmetic expressions (sub and div)") {
    val visitor = new EvalExpressionVisitor(new Interpreter())
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val val30 = IntValue(30)

    val exp = SubExpression(val20, DivExpression(val30, val10))
    exp.accept(visitor)

    assert(visitor.result == IntValue(17))
  }

  test("Test eval on boolean expressions ('and' and 'or')") {
    val visitor = new EvalExpressionVisitor(new Interpreter())

    val valTrue = BoolValue(true)
    val valFalse = BoolValue(false)


    val exp = AndExpression(valTrue, AndExpression(valTrue, OrExpression(valTrue, valFalse)))
    exp.accept(visitor)

    assert(visitor.result == valTrue)
  }

  test("Test eval on global variables") {
    val interpreter = new Interpreter()
    interpreter.setGlobalVariable("x", IntValue(30))

    val visitor = new EvalExpressionVisitor(interpreter)

    val exp = AddExpression(IntValue(10), VarExpression("x"))

    exp.accept(visitor)

    assert(visitor.result == IntValue(40))
  }

  test("Test eval on local (stack) and global variables") {
    val interpreter = new Interpreter()
    interpreter.setGlobalVariable("x", IntValue(30))
    interpreter.setLocalVariable("y", IntValue(10))

    val visitor = new EvalExpressionVisitor(interpreter)

    val exp = AddExpression(VarExpression("x"), VarExpression("y"))

    exp.accept(visitor)

    assert(visitor.result == IntValue(40))
  }

  // TODO: Write test cases  dealing with different scopes and name collision.
}
