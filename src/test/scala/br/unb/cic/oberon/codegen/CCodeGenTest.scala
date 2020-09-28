package br.unb.cic.oberon.codegen

import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class CCodeGenTest extends AnyFunSuite {
  test("Testing C generator for add, readInt and writeExpression") {
    val oberonPath = Paths.get(getClass.getClassLoader.getResource("stmts/stmt01.oberon").getFile.replace("/C:/","C:/"))
    assert(oberonPath != null)

    val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
    val module = ScalaParser.parse(oberonContent)
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cPath = Paths.get(getClass.getClassLoader.getResource("cCode/stmts/stmt01.c").getFile.replace("/C:/","C:/"))
    assert(cPath != null)

    val cCode = String.join("\n", Files.readAllLines(cPath))
    assert(generatedCCode == cCode)
  }
}