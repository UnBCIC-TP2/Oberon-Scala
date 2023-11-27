package br.unb.cic.oberon.interpreter

import br.unb.cic.oberon.environment.Environment

import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.parser.{Oberon2ScalaParser, ScalaParser}
import br.unb.cic.oberon.transformations.CoreTransformer
import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer

class InterpreterTest extends AnyFunSuite with Oberon2ScalaParser {

  val interpreter = new Interpreter()

  interpreter.setTestEnvironment()

  test("Testing interpreter on interpreter_stmt01 program") {

    val module = parseResource("stmts/interpreter_stmt01.oberon")
    
    val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(5))) // FOR TO x
    assert(result.lookup("y") == Some(IntValue(6))) // y = x + 1 (after last FOR)
    assert(result.lookup("z") == Some(IntValue(15))) // z = result

  }

  test("Testing interpreter on interpreter_factorial01 program: factorial(5)") {
    val module = parseResource("procedures/interpreter_factorial01.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(1)))
    assert(result.lookup("y") == Some(IntValue(120)))

  }

  test("Testing interpreter on interpreter_factorial02 program: factorial(1)") {
    val module = parseResource("procedures/interpreter_factorial02.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(1)))
    assert(result.lookup("y") == Some(IntValue(1)))

  }

  test("Testing interpreter on interpreter_factorial03 program: factorial(0)") {
    val module = parseResource("procedures/interpreter_factorial03.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(0)))
    assert(result.lookup("y") == Some(IntValue(1)))

  }

  test("Testing interpreter on interpreter_fibonacci program: Fibonacci index 7 = 13") {
    val module = parseResource("procedures/interpreter_fibonacci01.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("ant") == Some(IntValue(13)))

  }

  test("Testing interpreter on interpreter_stmt02 program") {
    val module = parseResource("stmts/interpreter_stmt02.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(15)))
    assert(result.lookup("z") == Some(IntValue(18)))

  }

  test("Testing interpreter on interpreter_stmt03 program") {
    val module = parseResource("stmts/interpreter_stmt03.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(90)))
    assert(result.lookup("z") == Some(IntValue(0)))

  }

  test("Testing interpreter on interpreter_stmt04 program") {
    val module = parseResource("stmts/interpreter_stmt04.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(101)))
    assert(result.lookup("z") == Some(IntValue(0)))

  }

  test("Testing interpreter on interpreter_stmt05 program") {
    val module = parseResource("stmts/interpreter_stmt05.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(101)))
    assert(result.lookup("z") == Some(IntValue(2)))

  }

  test("Testing interpreter on interpreter_stmt06 program") {
    val module = parseResource("stmts/interpreter_stmt06.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(52)))
    assert(result.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_stmt07 program") {
    val module = parseResource("stmts/interpreter_stmt07.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(52)))
    assert(result.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_fibonacci02 program: Fibonacci index 10 = 55") {
    val module = parseResource("procedures/interpreter_fibonacci02.oberon")
    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("ant") == Some(IntValue(55)))
  }

  test("Testing interpreter on interpreter_factorial04 program: factorial(4)") {
    val module = parseResource("procedures/interpreter_factorial04.oberon")

    
    val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(1)))
    assert(result.lookup("y") == Some(IntValue(24)))

  }

  test("Testing interpreter on lambdaExpressionsIT01 program") {
    val module = ScalaParser.parseResource("lambda/lambdaExpressionsIT01.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT01")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(IntValue(5)))
  }

  test("Testing interpreter on lambdaExpressionsIT02 program") {
    val module = ScalaParser.parseResource("lambda/lambdaExpressionsIT02.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT02")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(IntValue(2)))
  }

  test("Testing interpreter on lambdaExpressionsIT03 program") {
    val module = ScalaParser.parseResource("lambda/lambdaExpressionsIT03.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT03")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(BoolValue(true)))
  }

  test("Testing interpreter on lambdaExpressionsIT04 program") {
    val module = ScalaParser.parseResource("lambda/lambdaExpressionsIT04.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT04")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(BoolValue(true)))
  }

  test("Testing interpreter on lambdaExpressionsIT05 program") {
    val module = ScalaParser.parseResource("lambda/lambdaExpressionsIT05.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT05")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(BoolValue(false)))
  }

  test("Testing interpreter on lambdaExpressionsIT06 program") {
    val module = ScalaParser.parseResource("lambda/lambdaExpressionsIT06.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT06")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(BoolValue(true)))
  }

  test("Testing interpreter on interpreter_stmt08 program") {
    val module = parseResource("stmts/interpreter_stmt08.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(-50)))
    assert(result.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_stmt09 program") {
    val module = parseResource("stmts/interpreter_stmt09.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(25)))
    assert(result.lookup("z") == Some(IntValue(10)))

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt01 program") {
    val module = parseResource("stmts/IfElseIfStmt01.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(1)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt02 program") {
    val module = parseResource("stmts/IfElseIfStmt02.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt03 program") {
    val module = parseResource("stmts/IfElseIfStmt03.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(3)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt04 program") {
    val module = parseResource("stmts/IfElseIfStmt04.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(4)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt05 program") {
    val module = parseResource("stmts/IfElseIfStmt05.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(5)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt06 program") {
    val module = parseResource("stmts/IfElseIfStmt06.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt07 program") {
    val module = parseResource("stmts/IfElseIfStmt07.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt08 program") {
    val module = parseResource("stmts/IfElseIfStmt08.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(3)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt01 program") {
    val module = parseResource("stmts/RepeatUntilStmt01.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x").contains(IntValue(11)));
    assert(result.lookup("sum").contains(IntValue(55)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt02 program") {
    val module = parseResource("stmts/RepeatUntilStmt02.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("sum") == Some(IntValue(330)));
    assert(result.lookup("x") == Some(IntValue(21)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt03 program") {
    val module = parseResource("stmts/RepeatUntilStmt03.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("sum") == Some(IntValue(11)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt04 program") {
    val module = parseResource("stmts/RepeatUntilStmt04.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(20)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt05 program") {
    val module = parseResource("stmts/RepeatUntilStmt05.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(0)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt06 program") {
    val module = parseResource("stmts/RepeatUntilStmt06.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")
    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(11)));
    assert(result.lookup("y") == Some(IntValue(40)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt07 program") {
    val module = parseResource("stmts/RepeatUntilStmt07.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(3)));
    assert(result.lookup("y") == Some(IntValue(3)));
  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt08 program") {
    val module = parseResource("stmts/RepeatUntilStmt08.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(10)));
    assert(result.lookup("y") == Some(IntValue(10)));
  }

  test("Testing LoopStmt stmt on loop_stmt01 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = parseAbs(parse(oberonParser,content))

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "LoopStmt")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x") == Some(IntValue(-1)))
  }

  test("Testing LoopStmt stmt on loop_stmt02 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt02.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = parseAbs(parse(oberonParser,content))

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "LoopStmt")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x") == Some(IntValue(6)))
    assert(result.lookup("factorial") == Some(IntValue(120)))
  }

  test("Testing LoopStmt stmt on loop_stmt03 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt03.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = parseAbs(parse(oberonParser,content))

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "LoopStmt")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x") == Some(IntValue(10)))
    assert(result.lookup("y") == Some(IntValue(100)))
  }


  test("Testing array manipulation using the stmt35 oberon module") {
    val module = parseResource("stmts/stmt35.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "UserTypeModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(evalArraySubscript(result, "a", 0)._2 == IntValue(5))
    assert(evalArraySubscript(result, "b", 1)._2 == IntValue(10))
  }

  test("stmt36") {
    val module = parseResource("stmts/stmt36.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "UserTypeModule")

    val result = interpreter.runInterpreter(coreModule)
    assert(evalArraySubscript(result, "a", 0)._2 == IntValue(5))
    assert(evalArraySubscript(result, "a", 1)._2 == IntValue(10))
    assert(evalArraySubscript(result, "b", 0)._2 == IntValue(10))
    assert(evalArraySubscript(result, "a", 2)._2 == IntValue(25))
  }

  test("stmt37") {
    val module = parseResource("stmts/stmt37.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "UserTypeModule")

    val result = interpreter.runInterpreter(coreModule)
    assert(evalArraySubscript(result, "a", 0)._2 == IntValue(5))
    assert(evalArraySubscript(result, "a", 2)._2 == IntValue(25))
  }

  // test("lambdaExpressions01"){
  //   val module = scalaPaser.parseResource("lambda/lambdaExpressions01.oberon")
  //   val coreModule = CoreTransformer.reduceOberonModule(module)
  //   assert(coreModule.name == "LambdaExpressions01")
  //   val result = interpreter.runInterpreter(coreModule)
    
  // }

  test("Module A has no imports"){
    val module = parseResource("imports/A.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "A")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x").isDefined)
    assert(result.lookup("x") == Some(IntValue(1)))
  }

  ignore("Module B imports A"){
    val module = parseResource("imports/B.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "B")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x").isDefined)
    assert(result.lookup("x") == Some(IntValue(1)))
  }


  ignore("Module F imports A using alias"){
    val module = parseResource("imports/F.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "F")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x").isDefined)
    assert(result.lookup("x") == Some(IntValue(1)))
  }

  ignore("Module D imports A and C (A and C hava a variable 'x')"){
    val module = parseResource("imports/D.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "D")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x").isDefined)
    assert(result.lookup("x") == Some(IntValue(1)))
    //assert(interpreter.env.lookup("x") == Some(IntValue(2)))
  }

  test("Testing ArrayAssignmentStmt03"){
    val module = parseResource("stmts/ArrayAssignmentStmt03.oberon")

    assert(module.name == "ArrayAssignmentStmt03")
    val result = interpreter.runInterpreter(module)
    assert(result.lookup("array").isDefined)
    assert(result.lookup("outroarray").isDefined)


    assert(evalArraySubscript(result, "outroarray", 0)._2 == IntValue(1))
    assert(evalArraySubscript(result, "outroarray", 1)._2 == IntValue(5))

    for (i <- 0 to 2){
      assert(evalArraySubscript(result, "array", i)._2 == IntValue(10*(i+1)))
    }
  }

  test("Testing ArrayAssignmentStmt06"){
    val module = parseResource("stmts/ArrayAssignmentStmt06.oberon")

    assert(module.name == "ArrayAssignmentStmt06")
    val result = interpreter.runInterpreter(module)
    assert(result.lookup("i").isDefined)
    assert(result.lookup("arr").isDefined)

    assert(evalArraySubscript(result, "arr", 5)._2 == Undef())
    assert(evalArraySubscript(result, "arr", 0)._2 == IntValue(1))
    assert(evalArraySubscript(result, "arr", 9)._2 == IntValue(2))
  }

  test("Testing aritmetic37"){
    val module = parseResource("aritmetic/aritmetic37.oberon")

    assert(module.name == "Aritmetic37")
    val result = interpreter.runInterpreter(module)

    assert(result.lookup("x") == Some(IntValue(5)))
    assert(result.lookup("y") == Some(IntValue(3)))
    assert(result.lookup("z") == Some(IntValue(2)))
    assert(result.lookup("w") == Some(IntValue(1)))

  }

  ignore("Testing procedure06"){
    val module = ScalaParser.parseResource("procedures/procedure06.oberon")

    assert(module.name == "Procedure06")
    val result = interpreter.runInterpreter(module)

    assert(result.lookup("a") == Some(IntValue(2)))
    assert(result.lookup("b") == Some(IntValue(4)))
    assert(result.lookup("c") == Some(IntValue(1)))

    assert(result.lookup("a") == Some(IntValue(2)))
    assert(result.lookup("b") == Some(IntValue(4)))
    assert(result.lookup("c") == Some(IntValue(1)))

  }

  test("Testing Assert true (true)") {
    val module = parseResource("stmts/AssertTrueStmt01.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    interpreter.runInterpreter(coreModule)
  }

  test("Testing Assert true (false)") {
    val module = parseResource("stmts/AssertTrueStmt02.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    val thrown = intercept[Exception]{interpreter.runInterpreter(coreModule)}
    assert(thrown.getMessage() == "Exception thrown from assert")
  }

  test("Testing Assert error (no arguments)"){
    val module = parseResource("stmts/AssertErrorStmt01.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    val thrown = intercept[Exception] {
      interpreter.runInterpreter(coreModule)
    }
    assert(thrown.getMessage() == "Exception thrown from assert")
  }


  test("Testing Assert equal (right)") {
    val module = parseResource("stmts/AssertEqualStmt01.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    interpreter.runInterpreter(coreModule)
  }

  test("Testing Assert equal (wrong)") {
    val module = parseResource("stmts/AssertEqualStmt02.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    val thrown = intercept[Exception] {
      interpreter.runInterpreter(coreModule)
    }
    assert(thrown.getMessage() == "Exception thrown from assert")
  }

  test("Testing Assert not equal (right)") {
    val module = parseResource("stmts/AssertNEqualStmt01.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)
    

    interpreter.runInterpreter(coreModule) == ()
  }

  test("Testing Assert not equal (wrong)") {
    val module = parseResource("stmts/AssertNEqualStmt02.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    val thrown = intercept[Exception] {
      interpreter.runInterpreter(coreModule)
    }
    assert(thrown.getMessage() == ("Exception thrown from assert"))
  }

  ignore("Testing Test interpreter1") {
    val module = parseResource("procedures/procedureTest01.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(interpreter.runInterpreter(coreModule, "TEST") == ())
  }


  ignore("Testing Test interpreter2") {
    val module = parseResource("procedures/procedureTest02.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    // interpreter.runInterpreter(coreModule, "TEST")
  }

  test("Testing base interpreter in procedure Test 3") {
    val module = parseResource("procedures/procedureTest03.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    interpreter.runInterpreter(coreModule) == ()
  }

  ignore("Testing both interpreters in procedure Test 3") {
    val module = parseResource("procedures/procedureTest03.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    val thrown = intercept[Exception] {
      interpreter.runInterpreter(coreModule, "TEST")
    }
    assert(thrown.getMessage() == "Exception thrown from assert")

  }

  test("Testing Ignore") {
    val module = parseResource("procedures/procedureIgnore01.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    interpreter.runInterpreter(coreModule)
  }

  test("Testing interpreter on lambdaExpressionsIT01 program - New Parser") {
    val module = parseResource("lambda/lambdaExpressionsIT01.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT01")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(IntValue(5)))
  }

   test("Testing interpreter on lambdaExpressionsIT02 program - New Parser") {
    val module = parseResource("lambda/lambdaExpressionsIT02.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT02")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(IntValue(2)))
  }

  test("Testing interpreter on lambdaExpressionsIT03 program - New Parser") {
    val module = parseResource("lambda/lambdaExpressionsIT03.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT03")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(BoolValue(true)))
  }

  test("Testing interpreter on lambdaExpressionsIT04 program - New Parser") {
    val module = parseResource("lambda/lambdaExpressionsIT04.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT04")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(BoolValue(true)))
  }

  test("Testing interpreter on lambdaExpressionsIT05 program - New Parser") {
    val module = parseResource("lambda/lambdaExpressionsIT05.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT05")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(BoolValue(false)))
  }

  test("Testing interpreter on lambdaExpressionsIT06 program - New Parser") {
    val module = parseResource("lambda/lambdaExpressionsIT06.oberon")
    val coreModule = CoreTransformer.reduceOberonModule(module)
    assert(coreModule.name == "LambdaExpressionsIT06")
    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("r") == Some(BoolValue(true)))
  }

  test("Testing Arithmetic Lambda - New Parser"){
    val module = parseResource("lambda/lambdaExpressionsIT07.oberon")
    
    val coreModule = CoreTransformer.reduceOberonModule(module)
    val result = interpreter.runInterpreter(coreModule, "TEST")
    assert(result.lookup("r").isDefined)
    assert(result.lookup("r") == Some(IntValue(2)))
  }

  test("Testing Arithmetic LambdaApplication Test"){
    val module = parseResource("lambda/lambdaTest01.oberon")
    
    val coreModule = CoreTransformer.reduceOberonModule(module)
    val result = interpreter.runInterpreter(coreModule, "lambdaTest")
    assert(result.findTest("lambdaTestM").description == StringValue("Testing LambdaApplication - Addition"))
    assert(result.findTest("lambdaTestm").description == StringValue("Testing LambdaApplication - Subtraction"))
    assert(result.findTest("lambdaTestx").description == StringValue("Testing LambdaApplication - Multiplication"))
    assert(result.findTest("lambdaTestd").description == StringValue("Testing LambdaApplication - Division"))
    assert(result.lookup("r") == Some(IntValue(2)))
    assert(result.lookup("y") == Some(IntValue(5)))
    assert(result.lookup("z") == Some(IntValue(16)))
    assert(result.lookup("w") == Some(IntValue(8)))
  }

  test("Testing Boolean LambdaApplication Test"){
    val module = parseResource("lambda/lambdaTest02.oberon")
    
    val coreModule = CoreTransformer.reduceOberonModule(module)
    val result = interpreter.runInterpreter(coreModule, "lambdaTest")
    assert(result.findTest("lambdaTestT").description == StringValue("Testing LambdaApplication - Boolean Operation True"))
    assert(result.findTest("lambdaTestF").description == StringValue("Testing LambdaApplication - Boolean Operation False"))
    assert(result.findTest("lambdaTestAnd").description == StringValue("Testing LambdaApplication - AND Operation"))
    assert(result.findTest("lambdaTestOr").description == StringValue("Testing LambdaApplication - OR Operation"))
    assert(result.lookup("r") == Some(BoolValue(true)))
    assert(result.lookup("y") == Some(BoolValue(false)))
    assert(result.lookup("z") == Some(BoolValue(false)))
    assert(result.lookup("w") == Some(BoolValue(true)))
  }

  test("Testing LambdaApplication on Array"){
    val module = parseResource("lambda/lambdaTest03.oberon")
    
    val coreModule = CoreTransformer.reduceOberonModule(module)
    val result = interpreter.runInterpreter(coreModule, "lambdaTest")
    assert(result.findTest("lambdaTestArray_A").description == StringValue("Testing LambdaApplication on Array - First Test"))
    assert(result.findTest("lambdaTestArray_B").description == StringValue("Testing LambdaApplication on Array - Second Test"))
    assert(result.findTest("lambdaTestArray_C").description == StringValue("Testing LambdaApplication on Array - Third Test"))
    assert(result.findTest("lambdaTestArray_D").description == StringValue("Testing LambdaApplication on Array - Fourth Test"))
    assert(result.lookup("vector") == Some(ArrayValue(ListBuffer(IntValue(36), IntValue(25), IntValue(6), IntValue(51)),ArrayType(4,IntegerType))))
  }


  test(testName = "Testing boolean32"){
    val module = parseResource("boolean/boolean32.oberon")

    assert(module.name == "Boolean32")

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("x") == Some(BoolValue(true)))
    assert(result.lookup("a") == Some(BoolValue(false)))
    assert(result.lookup("b") == Some(BoolValue(true)))
  }

  test(testName = "Testing the module ForEachStmt"){
    val module = parseResource("stmts/ForEachStmt.oberon")
    assert(module.name == "ForEachStmt")

    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("s") == Some(IntValue(6)))
  }

  test(testName = "Testing the module SUMMATION"){
    val module = parseResource("recursion/Summation.oberon")
    assert(module.name == "SUMMATION")

    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("x") == Some(IntValue(6)))
  }

  test(testName = "Testing bee1013: Sample Test 1"){
    val module = parseResource("stmts/bee1013_stmt01.oberon")
    assert(module.name == "bee1013")

    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("answer") == Some(IntValue(106)))
  }

  test(testName = "Testing bee1013: Sample Test 2"){
    val module = parseResource("stmts/bee1013_stmt02.oberon")
    assert(module.name == "bee1013")

    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("answer") == Some(IntValue(217)))
  }


  def evalArraySubscript(environment : Environment[Expression], name: String, index: Integer): (Environment[Expression], Expression) =
    interpreter.evalExpression(environment, ArraySubscript(VarExpression(name), IntValue(index)))
}
