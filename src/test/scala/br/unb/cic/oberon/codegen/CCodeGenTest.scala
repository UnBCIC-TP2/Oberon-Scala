package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.transformations.CoreVisitor
import br.unb.cic.oberon.util.Resources
import org.scalatest.funsuite.AnyFunSuite

import java.io.{BufferedWriter, File, FileWriter}

class CCodeGenTest extends AnyFunSuite {

  private def testGenerator(oberonFile: String) = {

    val module = ScalaParser.parseResource(oberonFile)
    val coreModule = if (module.stmt.isDefined) {
      val coreVisitor = new CoreVisitor()
      coreVisitor.transformModule(module)
    } else {
      module
    }

    val generatedCCode = PaigesBasedGenerator().generateCode(coreModule)
    val CFile: String = s"cCode/$oberonFile".replace(".oberon", ".c")

    saveStringToFile(generatedCCode, s"c:/$CFile")
    val cCode = Resources.getContent(CFile)

    assert(generatedCCode == cCode)
  }

  private def saveStringToFile(content: String, path: String): Unit = {

    val file = new File(path)
    val directory: File = file.getParentFile
    if (!directory.exists) {
      directory.mkdirs()
    }
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(content)
    bw.close()
  }

  for (i <- (1 to 16).toList) {
    val stmtNumber = "%02d".format(i)
    test(s" C generator for stmt$stmtNumber") {
      testGenerator(s"stmts/stmt$stmtNumber.oberon")
    }
  }

  for (i <- 1 to 4) {
    val procedureNumber = "%02d".format(i)
    test(s" C generator for procedure$procedureNumber") {
      testGenerator(s"procedures/procedure$procedureNumber.oberon")
    }
  }

  for (i <- 1 to 4) {
    val procedureNumber = "%02d".format(i)
    test(s" C generator for interpreter_factorial$procedureNumber") {
      testGenerator(s"procedures/interpreter_factorial$procedureNumber.oberon")
    }
  }

  test(" C generator for interpreter_fibonacci01") {
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

  test(" C generator for stmt30 (if-else-if)") {
    testGenerator("stmts/stmt30.oberon")
  }

  test(" C generator for ifelseif_stmt31") {
    testGenerator("stmts/ifelseif_stmt31.oberon")
  }

  test(" C generator for ifelseif_stmt32") {
    testGenerator("stmts/ifelseif_stmt32.oberon")
  }

  test(" C generator for ifelseif_stmt33") {
    testGenerator("stmts/ifelseif_stmt33.oberon")
  }

  test("C generator for stmt35 (Array Declaration)") {
    testGenerator("stmts/stmt35.oberon")
  }

  test("C generator for stmt36 (Array Declaration)") {
    testGenerator("stmts/stmt36.oberon")
  }

  test("C generator for stmt37 (Array Declaration)") {
    testGenerator("stmts/stmt37.oberon")
  }
  test("C generator for arrayAssignmentExpressions") {
    testGenerator("stmts/arrayAssignmentExpressions.oberon")
  }


  test("C generator for recordAssignmentStmt01") {
    testGenerator("stmts/recordAssignmentStmt01.oberon")
  }

  test("C generator for recordAssignmentStmt02") {
    testGenerator("stmts/recordAssignmentStmt02.oberon")
  }

  test("C generator for userTypeSimple02 (User Type Declaration)") {
    testGenerator("simple/userTypeSimple02.oberon")
  }

  test("C generator for userTypeSimple03 (Struct inside Struct)") {
    testGenerator("simple/userTypeSimple03.oberon")
  }

  test("C generator for userTypeSimple05 (Array Inside Struct)") {
    testGenerator("simple/userTypeSimple05.oberon")
  }

  test("C generator for userTypeSimple06 (Array of Struct)") {
    testGenerator("simple/userTypeSimple06.oberon")
  }

  for (i <- 2 to 3) {
    test(s"C generator for ExpressionNameParser$i.oberon") {
      testGenerator(s"stmts/ExpressionNameParser$i.oberon")
    }
  }

  test("C generator for Array Usage") {
    testGenerator("stmts/ArrayUsage.oberon")
  }

  test("C generator for Array and Records") {
    testGenerator("simple/arraysAndRecordsDeclaration.oberon")
  }

  test("C generator for Array") {
    testGenerator("simple/arrayScopes.oberon")
  }

  test("C generator for Procedure with local var"){
    testGenerator("procedures/procedure_with_local_var.oberon")
  }

  test("C generator for Procedure with local var array"){
    testGenerator("procedures/procedure_with_local_var_array.oberon")

  }


}