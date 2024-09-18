package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.ir.ast.{AddExpression, BoolValue, BooleanType, DivExpression, EQExpression, IntValue, IntegerType, SubExpression, Type}
import br.unb.cic.oberon.interpreter.Interpreter
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.environment.Environment

class ExpressionTypeVisitorTestSuite extends AnyFunSuite {

  test("Test expression type on simple values") {
    val env = new Environment[Type]()
    val visitor = new ExpressionTypeChecker(new TypeChecker(env), env)
    val val10 = IntValue(10)
    val bTrue = BoolValue(true)
    val bFalse = BoolValue(false)

    assert(visitor.checkExpression(val10, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
    assert(visitor.checkExpression(bTrue, visitor.env).runA(visitor.env).value.value == Some(BooleanType))
    assert(visitor.checkExpression(bFalse, visitor.env).runA(visitor.env).value.value == Some(BooleanType))
  }

  test("test len expression") {
    val env = new Environment[Type]()
    val visitor = new ExpressionTypeChecker(new TypeChecker(env), env)
    val list = ListValue(List(IntValue(2), IntValue(3), IntValue(4)))

    val lenExp = LenExpression(list)

    assert(visitor.checkExpression(lenExp, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
  }

  test("test cons expression") {
  val env = new Environment[Type]()
  val visitor = new ExpressionTypeChecker(new TypeChecker(env), env)
  val lista = ConsExpression(IntValue(1))

  assert(visitor.checkExpression(lista, visitor.env).runA(visitor.env).value.value == Some(ListType(IntegerType)))
  }

  test("test concat expression") {
  val env = new Environment[Type]()
  val visitor = new ExpressionTypeChecker(new TypeChecker(env), env)
  
  val list1 = ListValue(List(IntValue(1), IntValue(2)))
  val list2 = ListValue(List(IntValue(3), IntValue(4)))
  
  val concatExp = ConcatExpression(list1, list2)
  
  assert(visitor.checkExpression(concatExp, visitor.env).runA(visitor.env).value.value == Some(ListType(IntegerType)))
  }

  test("test remove expression") {
  val env = new Environment[Type]()
  val visitor = new ExpressionTypeChecker(new TypeChecker(env), env)
  
  val list = ListValue(List(IntValue(1), IntValue(2), IntValue(3), IntValue(4)))
  val itemToRemove = IntValue(3)
  
  val removeExp = RemoveExpression(itemToRemove, list)
  
  assert(visitor.checkExpression(removeExp, visitor.env).runA(visitor.env).value.value == Some(ListType(IntegerType)))
  }


  test("Test expression type on add expressions") {
    val env = new Environment[Type]()
    val visitor = new ExpressionTypeChecker(new TypeChecker(env), env)
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val add01 = AddExpression(val10, val20)
    val add02 = AddExpression(val10, add01)
    val add03 = AddExpression(add01, add02)

    assert(visitor.checkExpression(add01, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
    assert(visitor.checkExpression(add02, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
    assert(visitor.checkExpression(add03, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
  }

  test("Test expression type on sub expressions") {
    val env = new Environment[Type]()
    val visitor = new ExpressionTypeChecker(new TypeChecker(env), env)
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val sub01 = SubExpression(val10, val20)
    val sub02 = SubExpression(val10, sub01)
    val sub03 = SubExpression(sub01, sub02)

    assert(visitor.checkExpression(sub01, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
    assert(visitor.checkExpression(sub02, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
    assert(visitor.checkExpression(sub03, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
  }

  test("Test expression type on div expressions") {
    val env = new Environment[Type]()
    val visitor = new ExpressionTypeChecker(new TypeChecker(env), env)
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val sub01 = DivExpression(val10, val20)
    val sub02 = DivExpression(val10, sub01)
    val sub03 = DivExpression(sub01, sub02)

    assert(visitor.checkExpression(sub01, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
    assert(visitor.checkExpression(sub02, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
    assert(visitor.checkExpression(sub03, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
  }

  test("Test expression type on mult expressions") {
    val env = new Environment[Type]()
    val visitor = new ExpressionTypeChecker(new TypeChecker(env), env)
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val mult01 = AddExpression(val10, val20)
    val mult02 = AddExpression(val10, mult01)
    val mult03 = AddExpression(mult01, mult02)

    assert(visitor.checkExpression(mult01, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
    assert(visitor.checkExpression(mult02, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
    assert(visitor.checkExpression(mult03, visitor.env).runA(visitor.env).value.value == Some(IntegerType))
  }

  test("Test expression type on eq expressions") {
    val env = new Environment[Type]()
    val visitor = new ExpressionTypeChecker(new TypeChecker(env), env)
    val val10 = IntValue(10)
    val val20 = IntValue(20)
    val eq01 = EQExpression(val10, val20)

    assert(visitor.checkExpression(eq01, visitor.env).runA(visitor.env).value.value == Some(BooleanType))
  }

  test("Test expression add with boolean values") {
    val env = new Environment[Type]()
    val visitor = new ExpressionTypeChecker(new TypeChecker(env), env)
    val val10 = IntValue(10)
    val valTrue = BoolValue(true)
    val invalidAdd = AddExpression(val10, valTrue)

    assert(visitor.checkExpression(invalidAdd, visitor.env).runA(visitor.env).value.value == None)
  }

}
