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

  test("Test eval lambda expression on Composite Operations") {
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    var args = List(ParameterByValue("x",RealType), ParameterByValue("y",RealType), ParameterByValue("z", RealType))
    val listexp = List(RealValue(10), RealValue(9), RealValue(8))

    //First Test
    val sumxy = AddExpression(VarExpression("x"), VarExpression("y"))
    val mult_xy_z = MultExpression(sumxy, VarExpression("z"))
    val lambdaexp = LambdaExpression(args, mult_xy_z)
    val (env1, exp1) = interpreter.evalExpression(env, LambdaApplication(lambdaexp, listexp))
    assert(exp1 == RealValue(152.0))

    //Second Test
    val subxy = SubExpression(VarExpression("x"), VarExpression("y"))
    val div_xyz = DivExpression(subxy, VarExpression("z"))
    val lambdaexp2 = LambdaExpression(args, div_xyz)
    val (env2, exp2) = interpreter.evalExpression(env, LambdaApplication(lambdaexp2, listexp))
    assert(exp2 == RealValue(0.125))

  }

  test("Test eval lambda expression on Composite Operations - 2") {
    val interpreter = new Interpreter()
    var env = new Environment[Expression]()
    var arr = ArrayValue(ListBuffer(RealValue(2.5), RealValue(3.5)),ArrayType(2,RealType))
    var args = List(ParameterByValue("x",IntegerType), ParameterByValue("y",IntegerType))
    val listexp = List(IntValue(4), IntValue(2))

    val mult_xy = MultExpression(arr.value(0), VarExpression("y"))
    val lambdaexp = LambdaExpression(args, mult_xy)
    val (env1, exp1) = interpreter.evalExpression(env, LambdaApplication(lambdaexp, listexp))
    assert(exp1 == RealValue(5.0))

    //Second Test
    val mult_xy2 = MultExpression(arr.value(1), VarExpression("x"))
    val lambdaexp2 = LambdaExpression(args, mult_xy2)
    val (env2, exp2) = interpreter.evalExpression(env, LambdaApplication(lambdaexp2, listexp))
    assert(exp2 == RealValue(14.0))

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
