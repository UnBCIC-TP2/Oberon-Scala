package br.unb.cic.oberon.interpreter

import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.ir.ast._
import org.scalatest.funsuite.AnyFunSuite

class EvalLambdaExpressionTest extends AnyFunSuite {

  test("Test eval on simple values") {
    val interpreter = new Interpreter()
    val env = new Environment[Expression]()
    val args: List[FormalArg] = (("x",IntegerType))
    val (env1,exp1) = evalExpression(env,LambdaExpression(args,AddExpression(IntValue(10),args)))
  }
}