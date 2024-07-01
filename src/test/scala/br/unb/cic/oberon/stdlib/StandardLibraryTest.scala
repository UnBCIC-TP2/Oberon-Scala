package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ir.ast.{BoolValue, IntValue, RealValue, StringValue}
import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class StandardLibraryTest extends AnyFunSuite {

  val interpreter = new Interpreter()

  interpreter.setTestEnvironment()

  test(testName = "Test for the INC function") {
    val module = ScalaParser.parseResource("stmts/INCTest.oberon")

    assert(module.name == "INCTest")

    val result = interpreter.run(module)

    assert(result.lookup("x") == Some(RealValue(10.0)))
    assert(result.lookup("y") == Some(IntValue(-7)))

  }

  test(testName = "Test for the DEC function") {
    val module = ScalaParser.parseResource("stmts/DECTest.oberon")

    assert(module.name == "DECTest")

    val result = interpreter.run(module)

    assert(result.lookup("x") == Some(IntValue(8)))
    assert(result.lookup("y") == Some(RealValue(-9.0)))
  }

  test("Test for the ABS function") {
    val module = ScalaParser.parseResource("stdlib/ABSTest.oberon")

    assert(module.name == "ABSTest")

    val result = interpreter.run(module)
    assert(result.lookup("x") == Some(IntValue(-10)))
    assert(result.lookup("y") == Some(IntValue(10)))
    assert(result.lookup("z") == Some(IntValue(10)))

  }

  test("Test for the ODD function") {
    val module = ScalaParser.parseResource("stdlib/ODDTest.oberon")

    assert(module.name == "ODDTest")


    val result = interpreter.run(module)

    assert(result.lookup("x") == Some(IntValue(10)))
    assert(result.lookup("y") == Some(IntValue(11)))
    assert(result.lookup("z") == Some(BoolValue(false)))
    assert(result.lookup("w") == Some(BoolValue(true)))
  }

  test(testName = "Test for the FLOOR function") {
    val module = ScalaParser.parseResource("stdlib/FLRTest.oberon")

    assert(module.name == "FLOORTest")


    val result = interpreter.run(module)

    assert(result.lookup("y") == Some(IntValue(10)))
    assert(result.lookup("z") == Some(IntValue(50)))
  }

  test(testName = "Test for the RND function") {
    val module = ScalaParser.parseResource("stdlib/RNDTest.oberon")

    assert(module.name == "RNDTest")


    val result = interpreter.run(module)

    assert(result.lookup("y") == Some(IntValue(10)))
    assert(result.lookup("z") == Some(IntValue(-1)))
  }

  test(testName = "Test for the FLT function") {
    val module = ScalaParser.parseResource("stdlib/FLTTest.oberon")

    assert(module.name == "FLTTest")

    val result = interpreter.run(module)

    assert(result.lookup("y") == Some(RealValue(-8.0)))
    assert(result.lookup("z") == Some(RealValue(2.0)))
  }

  test(testName = "Test for the POW function") {
    val module = ScalaParser.parseResource("stdlib/POWTest.oberon")

    assert(module.name == "POWTest")


    val result = interpreter.run(module)

    assert(result.lookup("z") == Some(RealValue(0.25298221281347033)))
    assert(result.lookup("w") == Some(RealValue(-729.0)))
  }

  test(testName = "Test for the SQR function") {
    val module = ScalaParser.parseResource("stdlib/SQRTest.oberon")

    assert(module.name == "SQRTest")

    val result = interpreter.run(module)

    assert(result.lookup("z") == Some(RealValue(14.0)))
    assert(result.lookup("y") == Some(RealValue(3.1622776601683795)))
  }

  test("Test for the CEIL function") {
    val module = ScalaParser.parseResource("stdlib/CEILTest.oberon")

    assert(module.name == "CEILTest")

    val result = interpreter.run(module)

    assert(result.lookup("z") == Some(IntValue(10)))
    assert(result.lookup("w") == Some(IntValue(12)))

  }

  test("Test for the READFILE function") {
    val module = ScalaParser.parseResource("stdlib/READFILETest.oberon")

    assert(module.name == "READFILETest")

    val result = interpreter.run(module)

    assert(result.lookup("y") == Some(StringValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.Testando append")))
  }

  ignore("Test for the WRITEFILE function") {
    val module = ScalaParser.parseResource("stdlib/WRITEFILETest.oberon")

    assert(module.name == "WRITEFILETest")


    val result = interpreter.run(module)

    assert(result.lookup("x") == Some(StringValue("src/test/resources/stdlib/plainFile.txt")))
    assert(result.lookup("y") == Some(StringValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")))

    if (System.getProperty("os.name").split(" ")(0).contains("Windows"))
      assert(result.lookup("z") == Some(StringValue("src\\test\\resources\\stdlib\\plainFile.txt")))
    else
      assert(result.lookup("z") == Some(StringValue("src/test/resources/stdlib/plainFile.txt")))

  }

  test("Test for the APPENDFILE function") {
    val module = ScalaParser.parseResource("stdlib/APPENDFILETest.oberon")

    assert(module.name == "APPENDFILETest")


    val result = interpreter.run(module)

    assert(result.lookup("x") == Some(StringValue("src/test/resources/stdlib/plainFile.txt")))
    assert(result.lookup("w") == Some(StringValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.Testando append")))

    if (System.getProperty("os.name").split(" ")(0).contains("Windows"))
      assert(result.lookup("m") == Some(StringValue("src\\test\\resources\\stdlib\\plainFile.txt")))
    else
      assert(result.lookup("m") == Some(StringValue("src/test/resources/stdlib/plainFile.txt")))

  }


  test(testName = "Test for the STRINGTOINT function") {
    val module = ScalaParser.parseResource("stdlib/STRINGTOINTTest.oberon")

    assert(module.name == "STRINGTOINTTest")


    val result = interpreter.run(module)

    assert(result.lookup("y") == Some(IntValue(-8)))
    assert(result.lookup("z") == Some(IntValue(2)))
  }

  test(testName = "Test for the STRINGTOREAL function") {
    val module = ScalaParser.parseResource("stdlib/STRINGTOREALTest.oberon")

    assert(module.name == "STRINGTOREALTest")

    val result = interpreter.run(module)

    assert(result.lookup("y") == Some(RealValue(-8.0)))
    assert(result.lookup("z") == Some(RealValue(2.5)))
  }
}
