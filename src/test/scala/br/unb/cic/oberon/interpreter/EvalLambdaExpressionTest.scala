package br.unb.cic.oberon.interpreter

import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.ir.ast._
import org.scalatest.funsuite.AnyFunSuite

class EvalLambdaExpressionTest extends AnyFunSuite {

  test("Test eval on simple values") {
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    var args: List[FormalArg] = List(ParameterByValue("x",IntegerType), ParameterByValue("y",IntegerType)) //(x:Integer)
    val exp = LambdaExpression(args,AddExpression(VarExpression("y"),VarExpression("x"))) //x+10
    val (env1,exp1) = interpreter.evalExpression(env,exp)
    assert(exp1 == AddExpression(VarExpression("y"),VarExpression("x")))
  }
}