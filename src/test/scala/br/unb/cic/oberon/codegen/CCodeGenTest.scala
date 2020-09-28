package br.unb.cic.oberon.codegen

import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class CCodeGenTest extends AnyFunSuite {

  for(i <- 1 to 2) {
    val stmtNumber = "%02d".format(i)
    test(s"Testing C generator for stmt$stmtNumber") {
      val oberonPath = Paths.get(getClass.getClassLoader.getResource(s"stmts/stmt$stmtNumber.oberon").getFile.replace("/C:/","C:/"))
      assert(oberonPath != null)

      val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
      val module = ScalaParser.parse(oberonContent)
      val codeGen = PaigesBasedGenerator()
      val generatedCCode = codeGen.generateCode(module)

      val cPath = Paths.get(getClass.getClassLoader.getResource(s"cCode/stmts/stmt$stmtNumber.c").getFile.replace("/C:/","C:/"))
      assert(cPath != null)

      val cCode = String.join("\n", Files.readAllLines(cPath))
      println(generatedCCode)
      assert(generatedCCode == cCode)
    }
  }
}