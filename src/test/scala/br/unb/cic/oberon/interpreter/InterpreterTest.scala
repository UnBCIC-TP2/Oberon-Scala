package br.unb.cic.oberon.interpreter

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class InterpreterTest extends AnyFunSuite{

test("Test interpreter on stmt07 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt07.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("z") == Some(IntValue(3)))

  }

}