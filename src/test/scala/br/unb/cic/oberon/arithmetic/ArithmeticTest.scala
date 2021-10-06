package br.unb.cic.oberon.arithmetic

import br.unb.cic.oberon.ast.{IntValue, RealValue}
import org.scalatest.funsuite.AnyFunSuite

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
}
