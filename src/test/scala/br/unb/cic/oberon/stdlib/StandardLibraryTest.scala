package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast.{BoolValue, IntValue, StringValue}
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

  test("Test for the READFILE function") {
    val module = ScalaParser.parseResource("stdlib/READFILETest.oberon")

    assert(module.name == "READFILETest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(StringValue("C:\\Users\\pelut\\README.txt")))
    assert(interpreter.env.lookup("y") == Some(StringValue("teste")))
  }

}
