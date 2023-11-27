package br.unb.cic.oberon.interpreter

import scala.collection.mutable.ListBuffer
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

  ignore("Test eval lambda expression on Array") {
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    var args = List(ParameterByValue("x",BooleanType), ParameterByValue("y",BooleanType))
    val listexp = List(IntValue(1), IntValue(4))

    //First Test
    var arr = ArrayValue(ListBuffer(IntValue(0),IntValue(0)), ArrayType(2, IntegerType)) 
    // val or = OrExpression(VarExpression("x"),VarExpression("y"))
    // var lambdaexp = LambdaExpression(args,or) 
    // val (env1,exp1) = interpreter.evalExpression(env,LambdaApplication(lambdaexp,listexp))
    // assert(exp1 == BoolValue(true))

    //Second Test
  }


  ignore("Test eval lambda expression on 'foreach' and 'map'"){
    //Ideia: adicionar teste de foreach para lambda expression
    //Acho que vai precisar adicionar novas expressions na ast e no parser
    //ex: val a: List = List(1,2,3)
    //a.map(x => x+2)
    //ANTLR: 'FOREACH'  varName = Id 'IN' arrayExp = expression stmt = statement 'END'     #ForEachStmt
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    val args = List(ParameterByValue("x",IntegerType))
    val expression = AddExpression(VarExpression("x"),IntValue(1))
    val lambdaExp = LambdaExpression(args,expression)
    //val foreach = ForEachStmt("X",lambdaExp,AssignmentStmt(VarAssignment(x),IntValue(1)))
    
    // val values = ListBuffer(IntValue(1),IntValue(2),IntValue(3))
    // val arrayType = ArrayType(3,IntegerType)
    // val arrayValue = ArrayValue(values,arrayType)

    //Map

  }
}
