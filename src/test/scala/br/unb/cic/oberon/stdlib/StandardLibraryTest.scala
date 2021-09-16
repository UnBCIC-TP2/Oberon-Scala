package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast.{BoolValue, IntValue}
import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class StandardLibraryTest extends AnyFunSuite {

  test("Test for the ABS function") {
    val module = ScalaParser.parseResource("stdlib/ABSTest.oberon")

    assert(module.name == "ABSTest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)
    assert(interpreter.env.lookup("x") == Some(IntValue(-10)))
    assert(interpreter.env.lookup("y") == Some(IntValue(10)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))
  }

  test("Test for the ODD function") {
    val module = ScalaParser.parseResource("stdlib/ODDTest.oberon")

    assert(module.name == "ODDTest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(10)))
    assert(interpreter.env.lookup("y") == Some(IntValue(11)))
    assert(interpreter.env.lookup("z") == Some(BoolValue(false)))
    assert(interpreter.env.lookup("w") == Some(BoolValue(true)))
  }

}
