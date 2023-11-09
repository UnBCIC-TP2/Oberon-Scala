package br.unb.cic.oberon.interpreter

import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.ir.ast._
import org.scalatest.funsuite.AnyFunSuite

class EvalLambdaExpressionTest extends AnyFunSuite {

  test("Test eval on simple values"){
  }

  test("Test eval on arithmetic expressions(add and mult)") {
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    var args: List[FormalArg] = List(ParameterByValue("x",IntegerType), ParameterByValue("y",IntegerType)) //(x:Integer)
    val operation = AddExpression(VarExpression("x"),VarExpression("y"))
    val exp = LambdaExpression(args,operation) //x+10
    val stmt = AssignmentStatement()
    val variavel = Constant("x", exp)
    env = env.setVariable("x", IntValue(2))
    val (env1,exp1) = interpreter.evalExpression(env,exp)
    assert(exp1 == IntValue(11))
  }
//a : (x: int, y: int) => x + y
  test("Test eval on arithmetic expressions (sub and div)") {
  }

  test("Test eval on boolean expressions ('and' and 'or')") {
  }


}
// val ex: (x: Integer, y: Integer) => x+y
//ex(2,3)