package br.unb.cic.oberon.interpreter

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ast.{IntValue, OberonModule}
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class InterpreterVisitorTest extends AnyFunSuite{

  test("Test interpreter on stmt05 program") {
    val module = ScalaParser.parseResource("stmts/stmt05.oberon")
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    interpreter.setTestEnvironment()
    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(625)))
    assert(interpreter.env.lookup("y") == Some(IntValue(100)))

  }

  test("Test eval on factorial module") {
    val module = ScalaParser.parseResource("procedures/procedure03.oberon")
    val interpreter = new Interpreter()
    assert(module.name == "Factorial")

    interpreter.setTestEnvironment()
    module.accept(interpreter)

    assert(interpreter.env.lookup("res").isDefined)
    assert(interpreter.env.lookup("res") == Some(IntValue(120)))
  }
}
