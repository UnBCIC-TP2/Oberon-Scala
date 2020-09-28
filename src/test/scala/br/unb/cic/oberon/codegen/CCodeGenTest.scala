package br.unb.cic.oberon.codegen

import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class CCodeGenTest extends AnyFunSuite {
  test("Testing the oberon simple01 code") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple01.oberon").getFile.replace("/C:/","C:/"))

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 1)
    assert(module.constants.head == Constant("x", IntValue(5)))
  }
}