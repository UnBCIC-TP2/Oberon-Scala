package br.unb.cic.oberon.arithmetic

//import br.unb.cic.oberon.ir.ast.{IntValue, MultExpression, RealValue}
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon._
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.ir.common._


class ArithmeticTestSuite extends AnyFunSuite {
  test("Test sum between two integers") {
    val i05 = IntValue(5)
    val i10 = IntValue(10)
    val expected = IntValue(15)

    assert(expected == (i05 + i10))
  }

  test("Test a sum between one integer and one real") {
    val i05 = IntValue(5)
    val i10 = RealValue(10)
    val expected = RealValue(15)

    assert(expected == (i05 + i10))
  }

  test("Test a division between two integers") {
    val i02 = IntValue(2)
    val i06 = IntValue(6)
    val i09 = IntValue(9)

    var expected = IntValue(3)

    assert(expected == (i06 / i02))

    expected = IntValue(4)

    assert(expected == (i09 / i02))
  }

  test("Test a multiplication between two real") {

    val r10 = RealValue(10.0)
    val r2 = RealValue(2.0)
    val m = MultExpression(r10, r2)

    val expected = RealValue(20.0)

    val interpreter = new Interpreter()
    val env = new Environment[Expression]()
    val res = interpreter.evalExpression(m).runA(env).value
    assert(expected == res)

  }
}
