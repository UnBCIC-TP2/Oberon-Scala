package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.transformations.CoreVisitor
import br.unb.cic.oberon.util.Resources
import org.scalatest.funsuite.AnyFunSuite

import java.io.{BufferedWriter, File, FileWriter}

class CCodeGenTest extends AnyFunSuite {

  private def testGenerator(oberonFile :String, lineSpaces :Int = 4) = {


    val module = ScalaParser.parseResource(oberonFile)
    val coreModule = new CoreVisitor().transformModule(module)
    val generatedCCode = PaigesBasedGenerator(lineSpaces).generateCode(coreModule)
    val CFile :String = s"cCode/$oberonFile".replace(".oberon", ".c")
    val cCode = Resources.getContent(CFile)


    //saveStringToFile(generatedCCode, s"c:/$CFile")
    assert(generatedCCode == cCode)
  }

  private def saveStringToFile(content: String, path: String): Unit ={

    val file = new File(path)
    val directory:File = file.getParentFile
    if (!directory.exists) {
      directory.mkdirs()
    }
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(content)
    bw.close()
  }

  for (i <- (1 to 16).toList) {
    val stmtNumber = "%02d".format(i)
    test(s"Testing C generator for stmt$stmtNumber") {
      testGenerator(s"stmts/stmt$stmtNumber.oberon")
    }
  }

  for (i <- 1 to 4) {
    val procedureNumber = "%02d".format(i)
    test(s"Testing C generator for procedure$procedureNumber") {
      testGenerator(s"procedures/procedure$procedureNumber.oberon")
    }
  }

  for (i <- 1 to 3) {
    val procedureNumber = "%02d".format(i)
    test(s"Testing C generator for interpreter_factorial$procedureNumber") {
      testGenerator(s"procedures/interpreter_factorial$procedureNumber.oberon")
    }
  }

  test("Testing C generator for interpreter_fibonacci01") {
    testGenerator("procedures/interpreter_fibonacci01.oberon")
  }


  test("First RepeatUntil Test") {
    testGenerator("stmts/repeatuntil.oberon")
  }

  test("RepeatUntil Test with just one loop") {
    testGenerator("stmts/repeatuntil01.oberon")
  }

  test("RepeatUntil Nested") {
    testGenerator("stmts/repeatuntil02.oberon")
  }

  test("RepeatUntil Compound Exit Condition") {
    testGenerator("stmts/repeatuntil03.oberon")
  }

  test("RepeatUntil In Procedure") {
    testGenerator("stmts/repeatuntil04.oberon")
  }

  test("Testing C generator for stmt30 (if-else-if)") {
    testGenerator("stmts/stmt30.oberon")
  }

  test("Testing C generator for ifelseif_stmt31") {
    testGenerator("stmts/ifelseif_stmt31.oberon")
  }

  test("Testing C generator for ifelseif_stmt32") {
    testGenerator("stmts/ifelseif_stmt32.oberon")
  }

  test("Testing C generator for ifelseif_stmt33") {
    testGenerator("stmts/ifelseif_stmt33.oberon" )
  }
}