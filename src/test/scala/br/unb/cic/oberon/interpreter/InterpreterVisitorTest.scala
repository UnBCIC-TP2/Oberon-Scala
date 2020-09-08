package br.unb.cic.oberon.interpreter

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ast.{IntValue, OberonModule}
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class InterpreterVisitorTest extends AnyFunSuite{

  test("Test interpreter on stmt05 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt05.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(625)))
    assert(interpreter.env.lookup("y") == Some(IntValue(100)))

  }

  test("Test eval on factorial module") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/procedure03.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "Factorial")

    module.accept(interpreter)

    assert(interpreter.env.lookup("res").isDefined)
    assert(interpreter.env.lookup("res") == Some(IntValue(120)))


  }
}
