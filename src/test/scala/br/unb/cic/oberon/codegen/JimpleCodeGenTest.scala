package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.jimple._
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.{Files, Paths}

class JimpleCodeGenTest extends AnyFunSuite {
  test("Generate class declaration from simple01.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule1")

    val classDecl = JimpleCodeGenerator.generateCode(module)

    assert(classDecl.classType == TObject("SimpleModule1"))
  }

  test("Generate variables from simple03.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple03.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule3")

    val targetVariables = List(
      Field(
        modifiers = List(PublicModifer),
        fieldType = TInteger,
        name = "abc"
      ),
      Field(
        modifiers = List(PublicModifer),
        fieldType = TBoolean,
        name = "def"
      ),
    )
    val variables = JimpleCodeGenerator.generateVariables(module)

    assert(variables == targetVariables)
  }

  test("Generate constants from simple07.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple07.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule7")

    val targetConstants = List(
      Field(
        modifiers = List(PublicModifer, FinalModifier),
        fieldType = TInteger,
        name = "x"
      ),
      Field(
        modifiers = List(PublicModifer, FinalModifier),
        fieldType = TInteger,
        name = "y"
      ),
    )
    val constants = JimpleCodeGenerator.generateConstants(module)

    assert(constants == targetConstants)
  }

  test("Generate method signature from procedure01.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/procedure01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    val targetSignatures = List(MethodSignature(
      className = module.name,
      returnType = TInteger,
      methodName = "sum",
      formals = List(TInteger, TInteger)
    ))
    val signatures = JimpleCodeGenerator.generateMethodSignatures(module)

    assert(signatures == targetSignatures)
  }

  test("Generate user defined types from userTypeSimple02.oberon") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/userTypeSimple02.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "test")

    val targetUserTypes = List(
      TObject("HALLS"),
      TArray(TObject("HALLS"))
    )
    val userTypes = JimpleCodeGenerator.generateUserDefinedTypes(module)

    assert(userTypes == targetUserTypes)
  }
}
