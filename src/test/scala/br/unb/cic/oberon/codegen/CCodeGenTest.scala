package br.unb.cic.oberon.codegen

import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.util.Resources
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.transformations.{CoreChecker, CoreVisitor}
import org.scalatest.funsuite.AnyFunSuite

class CCodeGenTest extends AnyFunSuite {

  // Tests for C code generator for stmt01.oberon - stmt16.oberon
  val successfulTests = (1 to 16).toList
  for (i <- successfulTests) {
    val stmtNumber = "%02d".format(i)
    test(s"Testing C generator for stmt$stmtNumber") {
      val stmtNumber = "%02d".format(i)
      testGenerator(s"stmts/stmt$stmtNumber.oberon", s"cCode/stmts/stmt$stmtNumber.c")
    }
  }

  // Tests for C code generator for procedure01.oberon - procedure04.oberon

  private def testGenerator(oberonFile :String, CFile :String, lineSpaces :Int = 2) = {
    val module = ScalaParser.parseResource(oberonFile)
    val codeGen = PaigesBasedGenerator(lineSpaces)

    if (CoreChecker.isModuleCore(module)) { //sucess
      val generatedCCode = codeGen.generateCode(module)
      val cCode = Resources.getContent(CFile)
      assert(generatedCCode == cCode)
    }
    else { //fail
      intercept[Exception] {
        val generatedCCode = codeGen.generateCode(module)
      }
    }
  }

  for (i <- 1 to 4) {
    val procedureNumber = "%02d".format(i)
    test(s"Testing C generator for procedure$procedureNumber") {
      testGenerator(s"procedures/procedure$procedureNumber.oberon", s"cCode/procedures/procedure$procedureNumber.c")
    }
  }

  // Tests for C code generator for interpreter_factorial01.oberon - interpreter_factorial03.oberon
  for (i <- 1 to 3) {
    val procedureNumber = "%02d".format(i)
    test(s"Testing C generator for interpreter_factorial$procedureNumber") {
      testGenerator(s"procedures/interpreter_factorial$procedureNumber.oberon", s"cCode/procedures/interpreter_factorial$procedureNumber.c")
    }
  }

  // Test for C code generator for interpreter_fibonacci01.oberon
  test(s"Testing C generator for interpreter_fibonacci01") {
    testGenerator(s"procedures/interpreter_fibonacci01.oberon", s"cCode/procedures/interpreter_fibonacci01.c")
  }

  test("Testing C generator for stmt01 with 4 spaces indent ") {
    testGenerator(s"stmts/stmt01.oberon", s"cCode/stmts/stmt01_4spaces.c", 4)
  }


  test("First RepeatUntil Test") {
    testGenerator(s"stmts/repeatuntil.oberon", s"cCode/stmts/repeatuntil.c")
  }

  test("RepeatUntil Test with just one loop") {
    testGenerator(s"stmts/repeatuntil01.oberon", s"cCode/stmts/repeatuntil01.c")
  }

  test("RepeatUntil Nested") {
    testGenerator(s"stmts/repeatuntil02.oberon", s"cCode/stmts/repeatuntil02.c")
  }

  test("RepeatUntil Compound Exit Condition") {
    testGenerator(s"stmts/repeatuntil03.oberon", s"cCode/stmts/repeatuntil03.c")
  }

  test("RepeatUntil In Procedure") {
    testGenerator(s"stmts/repeatuntil04.oberon", s"cCode/stmts/repeatuntil04.c")
  }

  //Tests for IfElseIf
  test("Testing C generator for stmt30") {
    testGenerator(s"stmts/stmt30.oberon", s"cCode/stmts/stmt17.c")
  }

  test("Testing C generator for ifelseif_stmt31") {
    testGenerator(s"stmts/ifelseif_stmt31.oberon", s"cCode/stmts/stmt18.c")
  }

  test("Testing C generator for stmt32") {
    testGenerator(s"stmts/ifelseif_stmt32.oberon", s"cCode/stmts/stmt19.c")
  }

  test("Testing C generator for stmt33") {
    testGenerator(s"stmts/ifelseif_stmt33.oberon", s"cCode/stmts/stmt20.c")
  }

  /*test("Testing C generator for stmt34") {
    testGenerator(s"stmts/stmt34.oberon", s"cCode/stmts/stmt21.c")
  }*/
}