package br.unb.cic.oberon.interpreter

import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.ir.ast._
import org.scalatest.funsuite.AnyFunSuite


class EvalLambdaExpressionTest extends AnyFunSuite {

  test("Test eval lambda expression on arithmetic expressions(add and mult)") {
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    val args = List(ParameterByValue("x",IntegerType), ParameterByValue("y",IntegerType))
    val listexp = List(IntValue(5),IntValue(6))

    //Addition 
    val addition = AddExpression(VarExpression("x"),VarExpression("y"))
    var lambdaexp = LambdaExpression(args,addition)
    val (env1,exp1) = interpreter.evalExpression(env,LambdaApplication(lambdaexp,listexp))
    assert(exp1 == IntValue(11))

    //Multiplication
    val multiplication = MultExpression(VarExpression("x"), VarExpression("y"))
    lambdaexp = LambdaExpression(args,multiplication)
    val (env2,exp2) = interpreter.evalExpression(env,LambdaApplication(lambdaexp,listexp))
    assert(exp2 == IntValue(30))
  }


  test("Test eval lambda expression on arithmetic expressions (sub and div)") {
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    val args = List(ParameterByValue("x",IntegerType), ParameterByValue("y",IntegerType))
    val listexp = List(IntValue(10),IntValue(2))

    //Subtraction
    val subtraction = SubExpression(VarExpression("x"),VarExpression("y"))
    var lambdaexp = LambdaExpression(args,subtraction) 
    val (env1,exp1) = interpreter.evalExpression(env,LambdaApplication(lambdaexp,listexp))
    assert(exp1 == IntValue(8))

    //Division
    val division = DivExpression(VarExpression("x"),VarExpression("y"))
    lambdaexp = LambdaExpression(args, division)
    val (env2,exp2) = interpreter.evalExpression(env,LambdaApplication(lambdaexp,listexp))
    assert(exp2 == IntValue(5))
  }

  test("Test eval lambda expression on boolean expressions ('and' and 'or')") {
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    var args = List(ParameterByValue("x",BooleanType), ParameterByValue("y",BooleanType))
    val listexp = List(BoolValue(true),BoolValue(false))

    //OR
    val or = OrExpression(VarExpression("x"),VarExpression("y"))
    var lambdaexp = LambdaExpression(args,or) 
    val (env1,exp1) = interpreter.evalExpression(env,LambdaApplication(lambdaexp,listexp))
    assert(exp1 == BoolValue(true))

    //AND
    val and = AndExpression(VarExpression("x"),VarExpression("y"))
    lambdaexp = LambdaExpression(args,and)
    val (env2,exp2) = interpreter.evalExpression(env,LambdaApplication(lambdaexp,listexp))
    assert(exp2 == BoolValue(false))
  }

  test("Test eval lambda expression on 'foreach' and 'map'"){
    //Ideia: adicionar teste de foreach para lambda expression
    //Acho que vai precisar adicionar novas expressions na ast e no parser
    //ex: val a: List = List(1,2,3)
    //a.map(x => x+2)
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    var args = List(ParameterByValue("x",IntegerType))
    val listexp = ListBuffer(IntValue(1),IntValue(2),IntValue(3))

    //Map
  }
}
