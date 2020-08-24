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

  test("Testing the oberon simple03 code. This module has three constants and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple03.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 3)
    assert(module.constants(0) == Constant(Variable("x"), IntValue(5)))
    assert(module.constants(1) == Constant(Variable("y"), IntValue(10)))
    assert(module.constants(2) == Constant(Variable("z"), BoolValue(true)))


    assert(module.variables.size == 2)
    assert(module.variables(0) == VariableDeclaration(List(Variable("abc")), IntegerType))
    assert(module.variables(1) == VariableDeclaration(List(Variable("def")), BooleanType))
  }

  test("Testing the oberon simple04 code. This module has three constants and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple04.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 3)
    assert(module.constants(0) == Constant(Variable("x"), IntValue(5)))
    assert(module.constants(1) == Constant(Variable("y"), IntValue(10)))
    assert(module.constants(2) == Constant(Variable("z"), AddExpression(IntValue(5), IntValue(10))))


    assert(module.variables.size == 2)
    assert(module.variables(0) == VariableDeclaration(List(Variable("abc")), IntegerType))
    assert(module.variables(1) == VariableDeclaration(List(Variable("def")), BooleanType))
  }

  test("Testing the oberon simple05 code. This module has one constant and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple05.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 1)
    assert(module.constants(0) == Constant(Variable("z"), MultExpression(IntValue(5), IntValue(10))))


    assert(module.variables.size == 2)
    assert(module.variables(0) == VariableDeclaration(List(Variable("abc")), IntegerType))
    assert(module.variables(1) == VariableDeclaration(List(Variable("def")), BooleanType))
  }


  test("Testing the oberon simple06 code. This module has one compound constants and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple06.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 1)
    assert(module.constants(0) == Constant(Variable("z"), AddExpression(IntValue(5), MultExpression(IntValue(10), IntValue(3)))))


    assert(module.variables.size == 2)
    assert(module.variables(0) == VariableDeclaration(List(Variable("abc")), IntegerType))
    assert(module.variables(1) == VariableDeclaration(List(Variable("def")), BooleanType))
  }

    test("Testing the oberon simple07 code. This module has two compounds constants and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple07.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 2)
    assert(module.constants(0) == Constant(Variable("x"), AddExpression(IntValue(5), MultExpression(IntValue(10), IntValue(3)))))
      assert(module.constants(1) == Constant(Variable("y"),
        AddExpression(IntValue(5),
         DivExpression(
           MultExpression(IntValue(10), IntValue(3)),
           IntValue(5)))))


    assert(module.variables.size == 2)
    assert(module.variables(0) == VariableDeclaration(List(Variable("abc")), IntegerType))
    assert(module.variables(1) == VariableDeclaration(List(Variable("def")), BooleanType))
  }
}
