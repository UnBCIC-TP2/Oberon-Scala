package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast.{BoolValue, IntValue, RealValue, StringValue}
import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class StandardLibraryTest extends AnyFunSuite {

  val interpreter = new Interpreter()

  interpreter.setTestEnvironment()

  test(testName = "Test for the INC function"){
    val module = ScalaParser.parseResource("stmts/INCTest.oberon")

    assert(module.name == "INCTest")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(RealValue(10.0)))
    assert(interpreter.env.lookup("y") == Some(IntValue(-7)))

  }

  test(testName = "Test for the DEC function"){
    val module = ScalaParser.parseResource("stmts/DECTest.oberon")

    assert(module.name == "DECTest")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(8)))
    assert(interpreter.env.lookup("y") == Some(RealValue(-9.0)))
  }


  test("Test for the ABS function") {
    val module = ScalaParser.parseResource("stdlib/ABSTest.oberon")

    assert(module.name == "ABSTest")


    module.accept(interpreter)
    assert(interpreter.env.lookup("x") == Some(IntValue(-10)))
    assert(interpreter.env.lookup("y") == Some(IntValue(10)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))
  }

  test("Test for the ODD function") {
    val module = ScalaParser.parseResource("stdlib/ODDTest.oberon")

    assert(module.name == "ODDTest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(10)))
    assert(interpreter.env.lookup("y") == Some(IntValue(11)))
    assert(interpreter.env.lookup("z") == Some(BoolValue(false)))
    assert(interpreter.env.lookup("w") == Some(BoolValue(true)))
  }

  test(testName = "Test for the FLOOR function") {
    val module = ScalaParser.parseResource("stdlib/FLRTest.oberon")

    assert(module.name == "FLOORTest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(10)))
    assert(interpreter.env.lookup("z") == Some(IntValue(50)))
  }

  test(testName = "Test for the RND function") {
    val module = ScalaParser.parseResource("stdlib/RNDTest.oberon")

    assert(module.name == "RNDTest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(10)))
    assert(interpreter.env.lookup("z") == Some(IntValue(-1)))
  }

  test(testName = "Test for the FLT function"){
    val module = ScalaParser.parseResource("stdlib/FLTTest.oberon")

    assert(module.name == "FLTTest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(RealValue(-8.0)))
    assert(interpreter.env.lookup("z") == Some(RealValue(2.0)))
  }

  test(testName = "Test for the POW function") {
    val module = ScalaParser.parseResource("stdlib/POWTest.oberon")

    assert(module.name == "POWTest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("z") == Some(RealValue(0.25298221281347033)))
    assert(interpreter.env.lookup("w") == Some(RealValue(-729.0)))
  }

  test(testName = "Test for the SQR function") {
    val module = ScalaParser.parseResource("stdlib/SQRTest.oberon")

    assert(module.name == "SQRTest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("z") == Some(RealValue(14.0)))
    assert(interpreter.env.lookup("y") == Some(RealValue(3.1622776601683795)))
  }

  test("Test for the CEIL function") {
    val module = ScalaParser.parseResource("stdlib/CEILTest.oberon")

    assert(module.name == "CEILTest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("z") == Some(IntValue(10)))
    assert(interpreter.env.lookup("w") == Some(IntValue(12)))

  }

  test("Test for the READFILE function") {
    val module = ScalaParser.parseResource("stdlib/READFILETest.oberon")

    assert(module.name == "READFILETest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(StringValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.Testando append")))

  }

  ignore("Test for the WRITEFILE function") {
    val module = ScalaParser.parseResource("stdlib/WRITEFILETest.oberon")

    assert(module.name == "WRITEFILETest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(StringValue("src/test/resources/stdlib/plainFile.txt")))
    assert(interpreter.env.lookup("y") == Some(StringValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")))

    if (System.getProperty("os.name").split(" ")(0).contains("Windows"))
      assert(interpreter.env.lookup("z") == Some(StringValue("src\\test\\resources\\stdlib\\plainFile.txt")))
    else
      assert(interpreter.env.lookup("z") == Some(StringValue("src/test/resources/stdlib/plainFile.txt")))

  }

  test("Test for the APPENDFILE function") {
    val module = ScalaParser.parseResource("stdlib/APPENDFILETest.oberon")

    assert(module.name == "APPENDFILETest")


    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(StringValue("src/test/resources/stdlib/plainFile.txt")))
    assert(interpreter.env.lookup("w") == Some(StringValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.Testando append")))

    if (System.getProperty("os.name").split(" ")(0).contains("Windows"))
      assert(interpreter.env.lookup("m") == Some(StringValue("src\\test\\resources\\stdlib\\plainFile.txt")))
    else
      assert(interpreter.env.lookup("m") == Some(StringValue("src/test/resources/stdlib/plainFile.txt")))

  }

}