package br.unb.cic.oberon.interpreter

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ir.ast.{IntValue, OberonModule}
import br.unb.cic.oberon.parser.Oberon2ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class InterpreterVisitorTest extends AnyFunSuite with Oberon2ScalaParser {

  test("Test interpreter on stmt05 program") {
    val module = parseResource("stmts/stmt05.oberon")
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    interpreter.setTestEnvironment()
    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(625)))
    assert(interpreter.env.lookup("y") == Some(IntValue(100)))

  }

  test("Test eval on factorial module") {
    val module = parseResource("procedures/procedure03.oberon")

    val interpreter = new Interpreter()
    assert(module.name == "Factorial")

    interpreter.setTestEnvironment()
    module.accept(interpreter)

    assert(interpreter.env.lookup("res").isDefined)
    assert(interpreter.env.lookup("res") == Some(IntValue(120)))
  }
}
