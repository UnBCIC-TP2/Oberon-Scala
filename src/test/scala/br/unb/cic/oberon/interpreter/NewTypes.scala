package br.unb.cic.oberon.interpreter

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ir.common._


class NewTypesTest extends AnyFunSuite {

  val interpreter = new Interpreter()

  interpreter.setTestEnvironment()

  test("Testing a lot of assignments") {
    val path = Paths.get(
      getClass.getClassLoader.getResource("aritmetic/aritmetic0.oberon").toURI
    )

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    val result = interpreter.run(module)

    assert(result.lookup("b") == Some(IntValue(2.toInt))) // FOR TO x
    assert(result.lookup("h") == Some(IntValue(8.toInt))) // FOR TO x
    assert(result.lookup("n") == Some(IntValue(14.toInt))) // FOR TO x
    assert(result.lookup("y") == Some(IntValue(24.toInt))) // FOR TO x

    assert(result.lookup("a") == Some(RealValue(1.5.toFloat))) // FOR TO x
    assert(result.lookup("g") == Some(RealValue(7.5.toFloat))) // FOR TO x
    assert(result.lookup("l") == Some(RealValue(12.5.toFloat))) // FOR TO x
    assert(result.lookup("w") == Some(RealValue(21.5.toFloat))) // FOR TO x

    assert(result.lookup("e") == Some(IntValue(5))) // FOR TO x
    assert(result.lookup("f") == Some(IntValue(6))) // FOR TO x
    assert(result.lookup("m") == Some(IntValue(13))) // FOR TO x
    assert(result.lookup("z") == Some(IntValue(24))) // FOR TO x
    assert(result.lookup("c") == Some(IntValue(3))) // FOR TO x
    assert(result.lookup("j") == Some(IntValue(10))) // FOR TO x
    assert(result.lookup("k") == Some(IntValue(11))) // FOR TO x
    assert(result.lookup("x") == Some(IntValue(24))) // FOR TO x
    assert(result.lookup("d") == Some(RealValue(4.5.toFloat))) // FOR TO x
    assert(result.lookup("i") == Some(RealValue(9.5.toFloat))) // FOR TO x
    assert(result.lookup("o") == Some(RealValue(15.5.toFloat))) // FOR TO x
    assert(result.lookup("t") == Some(RealValue(29.5.toFloat))) // FOR TO x
  }

  test("Testing LONG and SHORT operations") {
    val path = Paths.get(
      getClass.getClassLoader.getResource("aritmetic/aritmetic1.oberon").toURI
    )

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    val result = interpreter.run(module)
    assert(result.lookup("x") == Some(IntValue(5000))) // FOR TO x
  }

  test("Testing LONGREAL and REAL +") {
    val path = Paths.get(
      getClass.getClassLoader.getResource("aritmetic/aritmetic2.oberon").toURI
    )

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    val result = interpreter.run(module)
    assert(result.lookup("x") == Some(RealValue(26.500000005.toFloat))) // FOR TO x
  }
}
