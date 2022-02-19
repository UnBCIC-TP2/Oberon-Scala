package br.unb.cic.oberon.codegen

import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.util.Resources
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class CCodeGenTest extends AnyFunSuite {

  // Tests for C code generator for stmt01.oberon - stmt16.oberon
  val successfulTests = (1 to 10).toList ++ List(13, 14)
  val unsuccessfulTests = List(11, 12, 15, 16)
  for (i <- successfulTests) {
    val stmtNumber = "%02d".format(i)
    test(s"Testing C generator for stmt$stmtNumber") {
      val module = ScalaParser.parseResource(s"stmts/stmt$stmtNumber.oberon")
      val codeGen = PaigesBasedGenerator()
      val generatedCCode = codeGen.generateCode(module)

      val cCode = Resources.getContent(s"cCode/stmts/stmt$stmtNumber.c")
      assert(generatedCCode == cCode)
    }
  }

  for (i <- unsuccessfulTests) {
    val stmtNumber = "%02d".format(i)
    ignore(s"Testing C generator for stmt$stmtNumber") {
      val module = ScalaParser.parseResource(s"stmts/stmt$stmtNumber.oberon")
      val codeGen = PaigesBasedGenerator()
      val generatedCCode = codeGen.generateCode(module)

      val cCode = Resources.getContent(s"cCode/stmts/stmt$stmtNumber.c")
      assert(generatedCCode == cCode)
    }
  }

  // Tests for C code generator for procedure01.oberon - procedure04.oberon
  for (i <- 1 to 4) {
    val procedureNumber = "%02d".format(i)
    test(s"Testing C generator for procedure$procedureNumber") {
      val module = ScalaParser.parseResource(s"procedures/procedure$procedureNumber.oberon")
      val codeGen = PaigesBasedGenerator()
      val generatedCCode = codeGen.generateCode(module)

      val cCode = Resources.getContent(s"cCode/procedures/procedure$procedureNumber.c")
      assert(generatedCCode == cCode)
    }
  }

  // Tests for C code generator for interpreter_factorial01.oberon - interpreter_factorial03.oberon
  for (i <- 1 to 3) {
    val procedureNumber = "%02d".format(i)
    test(s"Testing C generator for interpreter_factorial$procedureNumber") {
      val module = ScalaParser.parseResource(s"procedures/interpreter_factorial$procedureNumber.oberon")
      val codeGen = PaigesBasedGenerator()
      val generatedCCode = codeGen.generateCode(module)

      val cCode = Resources.getContent(s"cCode/procedures/interpreter_factorial$procedureNumber.c")
      assert(generatedCCode == cCode)
    }
  }

  // Test for C code generator for interpreter_fibonacci01.oberon
  test(s"Testing C generator for interpreter_fibonacci01") {
    val module = ScalaParser.parseResource(s"procedures/interpreter_fibonacci01.oberon")
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cCode = Resources.getContent(s"cCode/procedures/interpreter_fibonacci01.c")
    assert(generatedCCode == cCode)
  }

  test("Testing C generator for stmt01 with 4 spaces indent ") {
      val module = ScalaParser.parseResource(s"stmts/stmt01.oberon")
      val codeGen = PaigesBasedGenerator(4)
      val generatedCCode = codeGen.generateCode(module)

      val cCode = Resources.getContent(s"cCode/stmts/stmt01_4spaces.c")
      assert(generatedCCode == cCode)
    }


  test("First RepeatUntil Test") {
    val module = ScalaParser.parseResource(s"stmts/repeatuntil.oberon")
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cCode = Resources.getContent(s"cCode/stmts/repeatuntil.c")

    assert(generatedCCode == cCode)
  }

  test("RepeatUntil Test with just one loop") {
    val module = ScalaParser.parseResource(s"stmts/repeatuntil01.oberon")
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cCode = Resources.getContent(s"cCode/stmts/repeatuntil01.c")

    assert(generatedCCode == cCode)
  }

  test("RepeatUntil Nested") {
    val module = ScalaParser.parseResource(s"stmts/repeatuntil02.oberon")
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cCode = Resources.getContent(s"cCode/stmts/repeatuntil02.c")

    assert(generatedCCode == cCode)
  }

  test("RepeatUntil Compound Exit Condition") {
    val module = ScalaParser.parseResource(s"stmts/repeatuntil03.oberon")
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cCode = Resources.getContent(s"cCode/stmts/repeatuntil03.c")

    assert(generatedCCode == cCode)
  }

  test("RepeatUntil In Procedure") {
    val module = ScalaParser.parseResource(s"stmts/repeatuntil04.oberon")
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cCode = Resources.getContent(s"cCode/stmts/repeatuntil04.c")

    assert(generatedCCode == cCode)
  }

//Tests for IfElseIf
  test("Testing C generator for stmt30") {
    val module = ScalaParser.parseResource(s"stmts/stmt30.oberon")
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cCode = Resources.getContent(s"cCode/stmts/stmt17.c")
    assert(generatedCCode == cCode)
  }

  test("Testing C generator for ifelseif_stmt31") {
    val module = ScalaParser.parseResource(s"stmts/ifelseif_stmt31.oberon")
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cCode = Resources.getContent(s"cCode/stmts/stmt18.c")
    assert(generatedCCode == cCode)
  }

  test("Testing C generator for stmt32") {
    val module = ScalaParser.parseResource(s"stmts/ifelseif_stmt32.oberon")
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cCode = Resources.getContent(s"cCode/stmts/stmt19.c")
    assert(generatedCCode == cCode)
  }

  test("Testing C generator for stmt33") {
    val module = ScalaParser.parseResource(s"stmts/ifelseif_stmt33.oberon")
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cCode = Resources.getContent(s"cCode/stmts/stmt20.c")
    assert(generatedCCode == cCode)
  }

  ignore("Testing C generator for stmt34") {
    val module = ScalaParser.parseResource(s"stmts/stmt34.oberon")
    val codeGen = PaigesBasedGenerator()
    val generatedCCode = codeGen.generateCode(module)

    val cCode = Resources.getContent(s"cCode/stmts/stmt21.c")
    assert(generatedCCode == cCode)
  }
}
