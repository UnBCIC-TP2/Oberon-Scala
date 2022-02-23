package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast.{BoolValue, IntValue, RealValue, StringValue}
import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

import scala.io.Source

class StandardLibraryTest extends AnyFunSuite {

  test("Test for the ABS function") {
    val module = ScalaParser.parseResource("stdlib/ABSTest.oberon")

    assert(module.name == "ABSTest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)
    assert(interpreter.env.lookup("x") == Some(IntValue(-10)))
    assert(interpreter.env.lookup("y") == Some(IntValue(10)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))
  }

  test("Test for the ODD function") {
    val module = ScalaParser.parseResource("stdlib/ODDTest.oberon")

    assert(module.name == "ODDTest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(10)))
    assert(interpreter.env.lookup("y") == Some(IntValue(11)))
    assert(interpreter.env.lookup("z") == Some(BoolValue(false)))
    assert(interpreter.env.lookup("w") == Some(BoolValue(true)))
  }

  test(testName = "Test for the FLR function") {
    val module = ScalaParser.parseResource("stdlib/FLRTest.oberon")

    assert(module.name == "FLRTest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(RealValue(10.0)))
    assert(interpreter.env.lookup("z") == Some(RealValue(50.0)))
  }

  test(testName = "Test for the RND function") {
    val module = ScalaParser.parseResource("stdlib/RNDTest.oberon")

    assert(module.name == "RNDTest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(RealValue(10.0)))
    assert(interpreter.env.lookup("z") == Some(RealValue(-1.0)))
  }

  test(testName = "Test for the POW function") {
    val module = ScalaParser.parseResource("stdlib/POWTest.oberon")

    assert(module.name == "POWTest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    assert(interpreter.env.lookup("z") == Some(RealValue(0.25298221281347033)))
    assert(interpreter.env.lookup("w") == Some(RealValue(-729.0)))
  }

  test(testName = "Test for the SQR function") {
    val module = ScalaParser.parseResource("stdlib/SQRTest.oberon")

    assert(module.name == "SQRTest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    assert(interpreter.env.lookup("z") == Some(RealValue(14.0)))
    assert(interpreter.env.lookup("y") == Some(RealValue(3.1622776601683795)))
  }

  test("Test for the CEIL function") {
    val module = ScalaParser.parseResource("stdlib/CEILTest.oberon")

    assert(module.name == "CEILTest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    assert(interpreter.env.lookup("z") == Some(RealValue(10.0)))
    assert(interpreter.env.lookup("w") == Some(RealValue(12.0)))

  }

  ignore("Test for the READFILE function") {
    val module = ScalaParser.parseResource("stdlib/READFILETest.oberon")

    assert(module.name == "READFILETest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(StringValue(_)))

  }

  test("Test for the WRITEFILE function") {
    val module = ScalaParser.parseResource("stdlib/WRITEFILETest.oberon")

    assert(module.name == "WRITEFILETest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    val fileName = "writtenByWRITEFILE.txt"
    val pathToWrite = if (System.getProperty("os.name").split(" ")(0).contains("Windows"))
                          "src\\test\\resources\\stdlib\\"
                      else
                          "src/test/resources/stdlib/"

    val toWriteFullPath = pathToWrite + fileName
    assert(interpreter.env.lookup("x") == Some(StringValue(toWriteFullPath)))
    assert(interpreter.env.lookup("y") == Some(StringValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")))

    val bufferedSource = Source.fromFile(toWriteFullPath)
    val fileContent = bufferedSource.getLines().mkString
    bufferedSource.close

    assert(interpreter.env.lookup("y") == Some(StringValue(fileContent)))

  }

  test("Test for the APPENDFILE function") {
    val module = ScalaParser.parseResource("stdlib/APPENDFILETest.oberon")

    assert(module.name == "APPENDFILETest")

    val interpreter = new Interpreter
    interpreter.setTestEnvironment()

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(StringValue("src/test/resources/stdlib/plainFile.txt")))
    assert(interpreter.env.lookup("w") == Some(StringValue("Lorem ipsum dolor sit amet, consectetur adipiscing elit.Testando append")))

    if (System.getProperty("os.name").split(" ")(0).contains("Windows"))
      assert(interpreter.env.lookup("m") == Some(StringValue("src\\test\\resources\\stdlib\\plainFile.txt")))
    else
      assert(interpreter.env.lookup("m") == Some(StringValue("src/test/resources/stdlib/plainFile.txt")))

  }

}