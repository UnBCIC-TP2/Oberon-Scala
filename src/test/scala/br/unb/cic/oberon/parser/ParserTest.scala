package br.unb.cic.oberon.parser

import java.nio.file.{Files, Paths}

import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ast._

import scala.io.Source

class ParserTestSuite extends AnyFunSuite {

  test("Testing the oberon simple01 code") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 1)
    assert(module.constants(0) == Constant(Variable("x"), IntValue(5)))
  }


  test("Testing the oberon simple02 code. This module has constants and variable") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 1)
    assert(module.constants(0) == Constant(Variable("x"), IntValue(5)))
    assert(module.variables.size == 2)
    assert(module.variables(0) == VariableDeclaration(List(Variable("abc")), IntegerType))
    assert(module.variables(1) == VariableDeclaration(List(Variable("def")), BooleanType))
  }
}
