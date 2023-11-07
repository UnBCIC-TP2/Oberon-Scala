package br.unb.cic.oberon.interpreter

import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.ir.ast._
import org.scalatest.funsuite.AnyFunSuite

class EvalLambdaExpressionTest extends AnyFunSuite {

  test("Test eval on simple values") {
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    var args: List[FormalArg] = List(ParameterByValue("x",IntegerType))
    env = interpreter.setLocalVariable(env,"x", IntValue(0))
    val exp = LambdaExpression(args,AddExpression(IntValue(10),VarExpression("x")))
    val (env1,exp1) = interpreter.evalExpression(env,exp)
    assert(exp1 == IntValue(10))
  }
}