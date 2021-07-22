package br.unb.cic.oberon.arithmetic

import br.unb.cic.oberon.ast
import br.unb.cic.oberon.ast.IntValue
import org.scalatest.funsuite.AnyFunSuite

class ArithmeticTestSuite extends AnyFunSuite {
  test("Test sum between integers and reals") {
    val i05 = IntValue(5)
    val i10 = IntValue(10)
    val expected = IntValue(15)

    assert(expected == (i05 + i10))
  }

}
