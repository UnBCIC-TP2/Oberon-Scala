package br.unb.cic.oberon.interpreter

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.transformations.CoreVisitor
import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer

class InterpreterTest extends AnyFunSuite {

  val interpreter = new Interpreter()

  interpreter.setTestEnvironment()

  test("Testing interpreter on interpreter_stmt01 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt01.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)
    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(5))) // FOR TO x
    assert(interpreter.env.lookup("y") == Some(IntValue(6))) // y = x + 1 (after last FOR)
    assert(interpreter.env.lookup("z") == Some(IntValue(15))) // z = result

  }

  test("Testing interpreter on interpreter_factorial01 program: factorial(5)") {
    val module = ScalaParser.parseResource("procedures/interpreter_factorial01.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(1)))
    assert(interpreter.env.lookup("y") == Some(IntValue(120)))

  }

  test("Testing interpreter on interpreter_factorial02 program: factorial(1)") {
    val module = ScalaParser.parseResource("procedures/interpreter_factorial02.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(1)))
    assert(interpreter.env.lookup("y") == Some(IntValue(1)))

  }

  test("Testing interpreter on interpreter_factorial03 program: factorial(0)") {
    val module = ScalaParser.parseResource("procedures/interpreter_factorial03.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(0)))
    assert(interpreter.env.lookup("y") == Some(IntValue(1)))

  }

  test("Testing interpreter on interpreter_fibonacci program: Fibonacci index 7 = 13") {
    val module = ScalaParser.parseResource("procedures/interpreter_fibonacci01.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("ant") == Some(IntValue(13)))

  }

  test("Testing interpreter on interpreter_stmt02 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt02.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(15)))
    assert(interpreter.env.lookup("z") == Some(IntValue(18)))

  }

  test("Testing interpreter on interpreter_stmt03 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt03.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(90)))
    assert(interpreter.env.lookup("z") == Some(IntValue(0)))

  }

  test("Testing interpreter on interpreter_stmt04 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt04.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(101)))
    assert(interpreter.env.lookup("z") == Some(IntValue(0)))

  }

  test("Testing interpreter on interpreter_stmt05 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt05.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(101)))
    assert(interpreter.env.lookup("z") == Some(IntValue(2)))

  }

  test("Testing interpreter on interpreter_stmt06 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt06.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(52)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_stmt07 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt07.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(52)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_fibonacci02 program: Fibonacci index 10 = 55") {
    val module = ScalaParser.parseResource("procedures/interpreter_fibonacci02.oberon")
    
    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("ant") == Some(IntValue(55)))
  }

  test("Testing interpreter on interpreter_factorial04 program: factorial(4)") {
    val module = ScalaParser.parseResource("procedures/interpreter_factorial04.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(1)))
    assert(interpreter.env.lookup("y") == Some(IntValue(24)))

  }

  test("Testing interpreter on interpreter_stmt08 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt08.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(-50)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_stmt09 program") {
    val module = ScalaParser.parseResource("stmts/interpreter_stmt09.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(25)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt01 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt01.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(1)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt02 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt02.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt03 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt03.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(3)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt04 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt04.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(4)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt05 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt05.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(5)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt06 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt06.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt07 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt07.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt08 program") {
    val module = ScalaParser.parseResource("stmts/IfElseIfStmt08.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "SimpleModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(3)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt01 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt01.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x").contains(IntValue(11)));
    assert(interpreter.env.lookup("sum").contains(IntValue(55)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt02 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt02.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("sum") == Some(IntValue(330)));
    assert(interpreter.env.lookup("x") == Some(IntValue(21)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt03 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt03.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("sum") == Some(IntValue(11)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt04 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt04.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(20)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt05 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt05.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(0)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt06 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt06.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "RepeatUntilModule")
    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(11)));
    assert(interpreter.env.lookup("y") == Some(IntValue(40)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt07 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt07.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(3)));
    assert(interpreter.env.lookup("y") == Some(IntValue(3)));
  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt08 program") {
    val module = ScalaParser.parseResource("stmts/RepeatUntilStmt08.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "RepeatUntilModule")

    coreModule.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(10)));
    assert(interpreter.env.lookup("y") == Some(IntValue(10)));
  }

  test("Testing LoopStmt stmt on loop_stmt01 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "LoopStmt")

    coreModule.accept(interpreter)
    assert(interpreter.env.lookup("x") == Some(IntValue(-1)))
  }

  test("Testing LoopStmt stmt on loop_stmt02 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt02.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "LoopStmt")

    coreModule.accept(interpreter)
    assert(interpreter.env.lookup("x") == Some(IntValue(6)))
    assert(interpreter.env.lookup("factorial") == Some(IntValue(120)))
  }

  test("Testing LoopStmt stmt on loop_stmt03 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt03.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "LoopStmt")

    coreModule.accept(interpreter)
    assert(interpreter.env.lookup("x") == Some(IntValue(10)))
    assert(interpreter.env.lookup("y") == Some(IntValue(100)))
  }


  ignore("Testing array manipulation using the stmt35 oberon module") {
    val module = ScalaParser.parseResource("stmts/stmt35.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "UserTypeModule")

    coreModule.accept(interpreter)
    assert(interpreter.env.lookupArrayIndex("a", 0).contains(IntValue(5)))
    assert(interpreter.env.lookupArrayIndex("b", 1).contains(IntValue(10)))
  }
  
  ignore("stmt36") {
    val module = ScalaParser.parseResource("stmts/stmt36.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "UserTypeModule")

    coreModule.accept(interpreter)
    assert(interpreter.env.lookupArrayIndex("a", 0).contains(IntValue(5)))
    assert(interpreter.env.lookupArrayIndex("a", 1).contains(IntValue(10)))
    assert(interpreter.env.lookupArrayIndex("b", 0).contains(IntValue(10)))
    assert(interpreter.env.lookupArrayIndex("a", 2).contains(IntValue(25)))
  }

  ignore("stmt37") {
    val module = ScalaParser.parseResource("stmts/stmt37.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "UserTypeModule")

    coreModule.accept(interpreter)
    assert(interpreter.env.lookupArrayIndex("a", 0).contains(IntValue(5)))
    assert(interpreter.env.lookupArrayIndex("a", 2).contains(IntValue(25)))
  }

  test("Module A has no imports"){
    val module = ScalaParser.parseResource("imports/A.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "A")

    coreModule.accept(interpreter)
    assert(interpreter.env.lookup("x").isDefined)
    assert(interpreter.env.lookup("x") == Some(IntValue(1)))
  }

  ignore("Module B imports A"){
    val module = ScalaParser.parseResource("imports/B.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "B")

    coreModule.accept(interpreter)
    assert(interpreter.env.lookup("x").isDefined)
    assert(interpreter.env.lookup("x") == Some(IntValue(1)))
  }


  ignore("Module F imports A using alias"){
    val module = ScalaParser.parseResource("imports/F.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "F")

    coreModule.accept(interpreter)
    assert(interpreter.env.lookup("x").isDefined)
    assert(interpreter.env.lookup("x") == Some(IntValue(1)))
  }

  ignore("Module D imports A and C (A and C hava a variable 'x')"){
    val module = ScalaParser.parseResource("imports/D.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(coreModule.name == "D")

    coreModule.accept(interpreter)
    assert(interpreter.env.lookup("x").isDefined)
    assert(interpreter.env.lookup("x") == Some(IntValue(1)))
    //assert(interpreter.env.lookup("x") == Some(IntValue(2)))
  }

  test("Testing ArrayAssignmentStmt03"){
    val module = ScalaParser.parseResource("stmts/ArrayAssignmentStmt03.oberon")

    assert(module.name == "ArrayAssignmentStmt03")
    module.accept(interpreter)
    assert(interpreter.env.lookup("array").isDefined)
    assert(interpreter.env.lookup("outroarray").isDefined)


    assert(interpreter.env.lookupArrayIndex("outroarray", 0) == Some(IntValue(1)))
    assert(interpreter.env.lookupArrayIndex("outroarray", 1) == Some(IntValue(5)))

    for (i <- 0 to 2){
      assert(interpreter.env.lookupArrayIndex("array", i) == Some(IntValue(10*(i+1))))
    }
  }

  test("Testing ArrayAssignmentStmt06"){
    val module = ScalaParser.parseResource("stmts/ArrayAssignmentStmt06.oberon")

    assert(module.name == "ArrayAssignmentStmt06")
    module.accept(interpreter)
    assert(interpreter.env.lookup("i").isDefined)
    assert(interpreter.env.lookup("arr").isDefined)

    assert(interpreter.env.lookupArrayIndex("arr", 5) == Some(Undef()))
    assert(interpreter.env.lookupArrayIndex("arr", 0) == Some(IntValue(1)))
    assert(interpreter.env.lookupArrayIndex("arr", 9) == Some(IntValue(2)))
    assert(interpreter.env.lookupArrayIndex("arr", -1) == Some(IntValue(2)))
    assert(interpreter.env.lookupArrayIndex("arr", -10) == Some(IntValue(1)))
  }
  
  test("Testing aritmetic37"){
    val module = ScalaParser.parseResource("aritmetic/aritmetic37.oberon")

    assert(module.name == "Aritmetic37")
    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(5)))
    assert(interpreter.env.lookup("y") == Some(IntValue(3)))
    assert(interpreter.env.lookup("z") == Some(IntValue(2)))
    assert(interpreter.env.lookup("w") == Some(IntValue(1)))

  }

  test("Testing procedure06"){
    val module = ScalaParser.parseResource("procedures/procedure06.oberon")

    assert(module.name == "Procedure06")
    module.accept(interpreter)

    assert(interpreter.env.lookup("a") == Some(IntValue(2)))
    assert(interpreter.env.lookup("b") == Some(IntValue(4)))
    assert(interpreter.env.lookup("c") == Some(IntValue(1)))

  }

  test(testName = "Testing boolean32"){
    val module = ScalaParser.parseResource("boolean/boolean32.oberon")

    assert(module.name == "Boolean32")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(BoolValue(true)))
    assert(interpreter.env.lookup("a") == Some(BoolValue(false)))
    assert(interpreter.env.lookup("b") == Some(BoolValue(true)))
  }
}
