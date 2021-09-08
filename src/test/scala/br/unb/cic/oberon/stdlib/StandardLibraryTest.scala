package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast.IntValue
import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class StandardLibraryTest extends AnyFunSuite {

  test("Test for the ABS function") {
    val module = ScalaParser.parseResource("stdlib/STDLibTest.oberon")

    assert(module.name == "STDLibTest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(-10)))
    assert(interpreter.env.lookup("y") == Some(IntValue(10)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))
  }

}
