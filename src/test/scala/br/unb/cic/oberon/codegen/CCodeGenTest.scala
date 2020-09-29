package br.unb.cic.oberon.codegen

import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class CCodeGenTest extends AnyFunSuite {

  // Tests for C code generator for stmt01.oberon - stmt16.oberon
  for(i <- 1 to 5) {
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
      assert(generatedCCode == cCode)
    }
  }

  for(i <- 7 to 16) {
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
      assert(generatedCCode == cCode)
    }
  }

  // Tests for C code generator for procedure01.oberon - procedure04.oberon
  for(i <- 1 to 4) {
    test(s"Testing C generator for procedure$i") {
      val procedureNumber = "%02d".format(i)
      val oberonPath = Paths.get(getClass.getClassLoader.getResource(s"procedures/procedure$procedureNumber.oberon").getFile.replace("/C:/","C:/"))
      assert(oberonPath != null)

      val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
      val module = ScalaParser.parse(oberonContent)
      val codeGen = PaigesBasedGenerator()
      val generatedCCode = codeGen.generateCode(module)

      val cPath = Paths.get(getClass.getClassLoader.getResource(s"cCode/procedures/procedure$procedureNumber.c").getFile.replace("/C:/","C:/"))
      assert(cPath != null)

      val cCode = String.join("\n", Files.readAllLines(cPath))
      assert(generatedCCode == cCode)
    }
  }

  // Tests for C code generator for interpreter_factorial01.oberon - interpreter_factorial03.oberon
  for (i <- 1 to 3) {
    test(s"Testing C generator for interpreter_factorial$i") {
      val procedureNumber = "%02d".format(i)
      val oberonPath = Paths.get(
        getClass.getClassLoader
          .getResource(
            s"procedures/interpreter_factorial$procedureNumber.oberon"
          )
          .getFile
          .replace("/C:/", "C:/")
      )
      assert(oberonPath != null)

      val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
      val module = ScalaParser.parse(oberonContent)
      val codeGen = PaigesBasedGenerator()
      val generatedCCode = codeGen.generateCode(module)

      val cPath = Paths.get(
        getClass.getClassLoader
          .getResource(
            s"cCode/procedures/interpreter_factorial$procedureNumber.c"
          )
          .getFile
          .replace("/C:/", "C:/")
      )
      assert(cPath != null)

      val cCode = String.join("\n", Files.readAllLines(cPath))
      assert(generatedCCode == cCode)
    }
  }

  // Test for C code generator for interpreter_fibonacci01.oberon
  test(s"Testing C generator for interpreter_fibonacci01") {
    val oberonPath = Paths.get(
      getClass.getClassLoader
        .getResource(s"procedures/interpreter_fibonacci01.oberon")
        .getFile
        .replace("/C:/", "C:/")
    )
    assert(oberonPath != null)

    val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
    val module = ScalaParser.parse(oberonContent)
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cPath = Paths.get(
      getClass.getClassLoader
        .getResource(
          s"cCode/procedures/interpreter_fibonacci01.c"
        )
        .getFile
        .replace("/C:/", "C:/")
    )
    assert(cPath != null)

    val cCode = String.join("\n", Files.readAllLines(cPath))
    assert(generatedCCode == cCode)
  }
}
