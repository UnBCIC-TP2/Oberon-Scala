package br.unb.cic.oberon.interpreter

import br.unb.cic.oberon.environment.Environment

import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.transformations.CoreTransformer
import org.scalatest.funsuite.AnyFunSuite

class InterpreterTest extends AnyFunSuite {

  val interpreter = new Interpreter()

  interpreter.setTestEnvironment()

  test("Testing interpreter on interpreter_stmt01 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt01.oberon")
    
    val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(5))) // FOR TO x
    assert(result.lookup("y") == Some(IntValue(6))) // y = x + 1 (after last FOR)
    assert(result.lookup("z") == Some(IntValue(15))) // z = result

  }

  test("Testing interpreter on interpreter_factorial01 program: factorial(5)") {
    val module = ScalaParser.parseResource("procedures/interpreter_factorial01.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(1)))
    assert(result.lookup("y") == Some(IntValue(120)))

  }

  test("Testing interpreter on interpreter_factorial02 program: factorial(1)") {
    val module = ScalaParser.parseResource("procedures/interpreter_factorial02.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(1)))
    assert(result.lookup("y") == Some(IntValue(1)))

  }

  test("Testing interpreter on interpreter_factorial03 program: factorial(0)") {
    val module = ScalaParser.parseResource("procedures/interpreter_factorial03.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(0)))
    assert(result.lookup("y") == Some(IntValue(1)))

  }

  test("Testing interpreter on interpreter_fibonacci program: Fibonacci index 7 = 13") {
    val module = ScalaParser.parseResource("procedures/interpreter_fibonacci01.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("ant") == Some(IntValue(13)))

  }

  test("Testing interpreter on interpreter_stmt02 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt02.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(15)))
    assert(result.lookup("z") == Some(IntValue(18)))

  }

  test("Testing interpreter on interpreter_stmt03 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt03.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(90)))
    assert(result.lookup("z") == Some(IntValue(0)))

  }

  test("Testing interpreter on interpreter_stmt04 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt04.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(101)))
    assert(result.lookup("z") == Some(IntValue(0)))

  }

  test("Testing interpreter on interpreter_stmt05 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt05.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(101)))
    assert(result.lookup("z") == Some(IntValue(2)))

  }

  test("Testing interpreter on interpreter_stmt06 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt06.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(52)))
    assert(result.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_stmt07 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt07.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(52)))
    assert(result.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_fibonacci02 program: Fibonacci index 10 = 55") {
    val module = ScalaParser.parseResource("procedures/interpreter_fibonacci02.oberon")
    
    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("ant") == Some(IntValue(55)))
  }

  test("Testing interpreter on interpreter_factorial04 program: factorial(4)") {
    val module = ScalaParser.parseResource("procedures/interpreter_factorial04.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(1)))
    assert(result.lookup("y") == Some(IntValue(24)))

  }

  test("Testing interpreter on interpreter_stmt08 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt08.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(-50)))
    assert(result.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_stmt09 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt09.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(25)))
    assert(result.lookup("z") == Some(IntValue(10)))

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt01 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt01.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(1)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt02 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt02.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt03 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt03.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(3)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt04 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt04.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(4)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt05 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt05.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(5)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt06 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt06.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt07 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt07.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt08 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt08.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "SimpleModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("y") == Some(IntValue(3)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt01 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt01.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x").contains(IntValue(11)));
    assert(result.lookup("sum").contains(IntValue(55)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt02 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt02.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("sum") == Some(IntValue(330)));
    assert(result.lookup("x") == Some(IntValue(21)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt03 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt03.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("sum") == Some(IntValue(11)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt04 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt04.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(20)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt05 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt05.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(0)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt06 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt06.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")
    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(11)));
    assert(result.lookup("y") == Some(IntValue(40)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt07 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt07.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(result.lookup("x") == Some(IntValue(3)));
    assert(result.lookup("y") == Some(IntValue(3)));
  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt08 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt08.oberon")

    
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
    val module = ScalaParser.parse(content)

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "LoopStmt")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x") == Some(IntValue(-1)))
  }

  test("Testing LoopStmt stmt on loop_stmt02 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt02.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    
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
    val module = ScalaParser.parse(content)

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "LoopStmt")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x") == Some(IntValue(10)))
    assert(result.lookup("y") == Some(IntValue(100)))
  }


  test("Testing array manipulation using the stmt35 oberon module") {
    val module = ScalaParser.parseResource("stmts/stmt35.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "UserTypeModule")

    val result = interpreter.runInterpreter(coreModule)

    assert(evalArraySubscript(result, "a", 0)._2 == IntValue(5))
    assert(evalArraySubscript(result, "b", 1)._2 == IntValue(10))
  }
  
  test("stmt36") {
    val module = ScalaParser.parseResource("stmts/stmt36.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "UserTypeModule")

    val result = interpreter.runInterpreter(coreModule)
    assert(evalArraySubscript(result, "a", 0)._2 == IntValue(5))
    assert(evalArraySubscript(result, "a", 1)._2 == IntValue(10))
    assert(evalArraySubscript(result, "b", 0)._2 == IntValue(10))
    assert(evalArraySubscript(result, "a", 2)._2 == IntValue(25))
  }

  test("stmt37") {
    val module = ScalaParser.parseResource("stmts/stmt37.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "UserTypeModule")

    val result = interpreter.runInterpreter(coreModule)
    assert(evalArraySubscript(result, "a", 0)._2 == IntValue(5))
    assert(evalArraySubscript(result, "a", 2)._2 == IntValue(25))
  }

  test("Module A has no imports"){
    val module = ScalaParser.parseResource("imports/A.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "A")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x").isDefined)
    assert(result.lookup("x") == Some(IntValue(1)))
  }

  ignore("Module B imports A"){
    val module = ScalaParser.parseResource("imports/B.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "B")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x").isDefined)
    assert(result.lookup("x") == Some(IntValue(1)))
  }


  ignore("Module F imports A using alias"){
    val module = ScalaParser.parseResource("imports/F.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "F")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x").isDefined)
    assert(result.lookup("x") == Some(IntValue(1)))
  }

  ignore("Module D imports A and C (A and C hava a variable 'x')"){
    val module = ScalaParser.parseResource("imports/D.oberon")

    
   val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(coreModule.name == "D")

    val result = interpreter.runInterpreter(coreModule)
    assert(result.lookup("x").isDefined)
    assert(result.lookup("x") == Some(IntValue(1)))
    //assert(interpreter.env.lookup("x") == Some(IntValue(2)))
  }

  test("Testing ArrayAssignmentStmt03"){
    val module = ScalaParser.parseResource("stmts/ArrayAssignmentStmt03.oberon")

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
    val module = ScalaParser.parseResource("stmts/ArrayAssignmentStmt06.oberon")

    assert(module.name == "ArrayAssignmentStmt06")
    val result = interpreter.runInterpreter(module)
    assert(result.lookup("i").isDefined)
    assert(result.lookup("arr").isDefined)

    assert(evalArraySubscript(result, "arr", 5)._2 == Undef())
    assert(evalArraySubscript(result, "arr", 0)._2 == IntValue(1))
    assert(evalArraySubscript(result, "arr", 9)._2 == IntValue(2))
  }
  
  test("Testing aritmetic37"){
    val module = ScalaParser.parseResource("aritmetic/aritmetic37.oberon")

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

  }

  test(testName = "Testing boolean32"){
    val module = ScalaParser.parseResource("boolean/boolean32.oberon")

    assert(module.name == "Boolean32")

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("x") == Some(BoolValue(true)))
    assert(result.lookup("a") == Some(BoolValue(false)))
    assert(result.lookup("b") == Some(BoolValue(true)))
  }

  test(testName = "Testing the module ForEachStmt"){
    val module = ScalaParser.parseResource("stmts/ForEachStmt.oberon")
    assert(module.name == "ForEachStmt")

    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("s") == Some(IntValue(6)))
  }

  test(testName = "Testing the module SUMMATION"){
    val module = ScalaParser.parseResource("recursion/Summation.oberon")
    assert(module.name == "SUMMATION")

    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("x") == Some(IntValue(6)))
  }

  test(testName = "Testing bee1013: Sample Test 1"){
    val module = ScalaParser.parseResource("stmts/bee1013_stmt01.oberon")
    assert(module.name == "bee1013")

    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("answer") == Some(IntValue(106)))
  }

  test(testName = "Testing bee1013: Sample Test 2"){
    val module = ScalaParser.parseResource("stmts/bee1013_stmt02.oberon")
    assert(module.name == "bee1013")

    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("answer") == Some(IntValue(217)))
  }

  test(testName = "Testing pointer assignment: pointerAssign0") {

    val module = ScalaParser.parseResource("pointers/pointerAssign0.oberon")

    assert(module.name == "pointerAssign")
    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("a") == Some(IntValue(5)))
    assert(result.lookup("b") == Some(IntValue(5)))
  }

  test(testName = "Testing pointer assignment: pointerAssign1") {

    val module = ScalaParser.parseResource("pointers/pointerAssign1.oberon")

    assert(module.name == "pointerAssign")
    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("a") == Some(IntValue(1)))
    assert(result.lookup("b") == Some(RealValue(9.5)))
    assert(result.lookup("c") == Some(CharValue('c')))
    assert(result.lookup("d") == Some(BoolValue(true)))
    assert(result.lookup("e") == Some(StringValue("Hello.")))

  }

  test(testName = "Testing pointer assignment: pointerAssign2") {

    val module = ScalaParser.parseResource("pointers/pointerAssign2.oberon")

    assert(module.name == "pointerAssign")
    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("a") == Some(RealValue(10.5)))
    assert(result.lookup("b") == Some(RealValue(10.5)))

  }

  test(testName = "Testing pointer assignment: pointerAssign3") {

    val module = ScalaParser.parseResource("pointers/pointerAssign3.oberon")

    assert(module.name == "pointerAssign")
    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("a") == Some(RealValue(10.5)))
    assert(result.lookup("b") == Some(RealValue(10.5)))

  }

  test(testName = "Testing pointer assignment: pointerAssign4") {

    val module = ScalaParser.parseResource("pointers/pointerAssign4.oberon")

    assert(module.name == "pointerAssign")
    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("a") == Some(RealValue(10.5)))
    assert(result.lookup("b") == Some(RealValue(10.5)))
    assert(result.lookup("c") == Some(RealValue(10.5)))
  }

  ignore(testName = "Testing pointer assignment: pointerAssign5") {

    val module = ScalaParser.parseResource("pointers/pointerAssign5.oberon")

    assert(module.name == "pointerAssign")
    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("a") == Some(RealValue(7.8)))
    assert(result.lookup("b") == Some(RealValue(7.8)))

  }

  test(testName = "Testing procedure and pointer: procedure_pointer1") {

    val module = ScalaParser.parseResource("pointers/procedure_pointer1.Oberon")

    assert(module.name == "pointerAssign")
    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("a") == Some(RealValue(1.5)))
    assert(result.lookup("b") == Some(RealValue(1.5)))
  }

  test(testName = "Testing records: recordAssign1") {

    val module = ScalaParser.parseResource("pointers/recordAssign1.oberon")

    assert(module.name == "pointerAssign")
    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("a.nome") == Some(StringValue("Manuela")));
    assert(result.lookup("a.idade") == Some(IntValue(23)));
  }

  test(testName = "Testing records: recordAssign2") {

    val module = ScalaParser.parseResource("pointers/recordAssign2.oberon")

    assert(module.name == "pointerAssign")
    assert(module.stmt.isDefined)

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("a.nome") == Some(StringValue("jurema")));
    assert(result.lookup("a.especie") == Some(StringValue("vaca")));
    assert(result.lookup("a.idade") == Some(IntValue(4)));
    assert(result.lookup("a.peso") == Some(RealValue(40.5)));

    assert(result.lookup("b.nome") == Some(StringValue("carijo")));
    assert(result.lookup("b.especie") == Some(StringValue("galo")));
    assert(result.lookup("b.idade") == Some(IntValue(1)));
    assert(result.lookup("b.peso") == Some(RealValue(9.5)));

    assert(result.lookup("c") == Some(IntValue(4 + 1)));

  }

  test("Testing multiple pointer indirections") {
    val module = new OberonModule("SimpleModule",
      Set(),
      List(),
      List(),
      List[VariableDeclaration](VariableDeclaration("a", PointerType(IntegerType)), VariableDeclaration("b", PointerType(PointerType(IntegerType))), VariableDeclaration("c", IntegerType)),
      List(), Some(SequenceStmt(List[Statement](AssignmentStmt(VarAssignment("c"), IntValue(5)), AssignmentStmt(PointerAssignment("a"), VarExpression("c")),
      AssignmentStmt(PointerAssignment("b"), VarExpression("a")), AssignmentStmt(PointerAssignment("b", 1), IntValue(3))))))

    val result = interpreter.runInterpreter(module)

    assert(result.lookup("c") == Some(IntValue(3)))
  }

  def evalArraySubscript(environment : Environment[Expression], name: String, index: Integer): (Environment[Expression], Expression) =
    interpreter.evalExpression(environment, ArraySubscript(VarExpression(name), IntValue(index)))
}
