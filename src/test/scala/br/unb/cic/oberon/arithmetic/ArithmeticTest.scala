package br.unb.cic.oberon.arithmetic

import br.unb.cic.oberon.ast.{IntValue, RealValue, SetValue}
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

  test("Test set union") {
    val set1 = SetValue(Set(1,2,3))
    val set2 = SetValue(Set(4,5,6))
    val expected = SetValue(Set(1,2,3,4,5,6))

    assert(expected == (set1 + set2))
  }

  test("Test set difference") {
    val set1 = SetValue(Set(1,2,3,4,5,6))
    val set2 = SetValue(Set(4,5,6))
    val expected = SetValue(Set(1,2,3))

    assert(expected == (set1 - set2))
  }

  test("Test symmetric set difference") {
    val set1 = SetValue(Set(1,2,3,4,5))
    val set2 = SetValue(Set(4,5,6,7,8))
    val expected = SetValue(Set(1,2,3,6,7,8))

    assert(expected == (set1 / set2))
  }

  test("Test set intersection") {
    val set1 = SetValue(Set(1,2,3,4))
    val set2 = SetValue(Set(4,5,6,7))
    val expected = SetValue(Set(4))

    assert(expected == (set1 * set2))
  }
}
