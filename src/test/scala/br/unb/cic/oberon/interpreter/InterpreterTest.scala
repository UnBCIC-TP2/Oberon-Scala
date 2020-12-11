package br.unb.cic.oberon.interpreter

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class InterpreterTest extends AnyFunSuite{

test("Testing interpreter on interpreter_stmt01 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)
    
	
    assert(interpreter.env.lookup("x") == Some(IntValue(5))) // FOR TO x
    assert(interpreter.env.lookup("y") == Some(IntValue(6))) // y = x + 1 (after last FOR)
    assert(interpreter.env.lookup("z") == Some(IntValue(15))) // z = result

  }
  
test("Testing interpreter on interpreter_factorial01 program: factorial(5)") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_factorial01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)
	
	 assert(interpreter.env.lookup("x") == Some(IntValue(1)))
   assert(interpreter.env.lookup("y") == Some(IntValue(120)))

  }
  
test("Testing interpreter on interpreter_factorial02 program: factorial(1)") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_factorial02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)
	
	 assert(interpreter.env.lookup("x") == Some(IntValue(1)))
   assert(interpreter.env.lookup("y") == Some(IntValue(1)))

  }

test("Testing interpreter on interpreter_factorial03 program: factorial(0)") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_factorial03.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)
	
	assert(interpreter.env.lookup("x") == Some(IntValue(0)))
    assert(interpreter.env.lookup("y") == Some(IntValue(1)))

  }
  
test("Testing interpreter on interpreter_fibonacci program: Fibonacci index 7 = 13") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_fibonacci01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)
	
	assert(interpreter.env.lookup("ant") == Some(IntValue(13)))

  }

test("Testing interpreter on interpreter_stmt02 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)
	
	assert(interpreter.env.lookup("x") == Some(IntValue(15)))
  assert(interpreter.env.lookup("z") == Some(IntValue(18))) 

  }

test("Testing interpreter on interpreter_stmt03 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt03.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)
	
	assert(interpreter.env.lookup("x") == Some(IntValue(90)))
  assert(interpreter.env.lookup("z") == Some(IntValue(0)))

  }

test("Testing interpreter on interpreter_stmt04 program") {
  val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt04.oberon").getFile)

  assert(path != null)

  val content = String.join("\n", Files.readAllLines(path))
  val module = ScalaParser.parse(content)
  val interpreter = new Interpreter()
  assert(module.name == "SimpleModule")

  module.accept(interpreter)

  assert(interpreter.env.lookup("x") == Some(IntValue(101)))
  assert(interpreter.env.lookup("z") == Some(IntValue(0))) 

  }

  test("Testing interpreter on interpreter_stmt05 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt05.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(101)))
    assert(interpreter.env.lookup("z") == Some(IntValue(2)))

  }

test("Testing interpreter on interpreter_stmt06 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt06.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(52)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }

test("Testing interpreter on interpreter_stmt07 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt07.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(52)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }

test("Testing interpreter on interpreter_fibonacci02 program: Fibonacci index 10 = 55") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_fibonacci02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("ant") == Some(IntValue(55)))
  }

test("Testing interpreter on interpreter_factorial04 program: factorial(4)") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_factorial04.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)
	
	 assert(interpreter.env.lookup("x") == Some(IntValue(1)))
   assert(interpreter.env.lookup("y") == Some(IntValue(24)))

  }

test("Testing interpreter on interpreter_stmt08 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt08.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)
	
	 assert(interpreter.env.lookup("x") == Some(IntValue(-50)))
   assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }

test("Testing interpreter on interpreter_stmt09 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt09.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)
	
	 assert(interpreter.env.lookup("x") == Some(IntValue(25)))
   assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }
test("Testing IFELSEIF stmt on IfElseIfStmt01 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(1)));

  }

test("Testing IFELSEIF stmt on IfElseIfStmt02 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(2)));

  }

test("Testing IFELSEIF stmt on IfElseIfStmt03 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt03.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(3)));

  }

test("Testing IFELSEIF stmt on IfElseIfStmt04 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt04.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(4)));

  }

test("Testing IFELSEIF stmt on IfElseIfStmt05 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt05.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(5)));

  }

test("Testing IFELSEIF stmt on IfElseIfStmt06 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt06.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(2)));

  }

test("Testing IFELSEIF stmt on IfElseIfStmt07 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt07.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt08 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt08.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(3)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt01 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x").contains(IntValue(11)));
    assert(interpreter.env.lookup("sum").contains(IntValue(55)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt02 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("sum") == Some(IntValue(330)));
    assert(interpreter.env.lookup("x") == Some(IntValue(21)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt03 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt03.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("sum") == Some(IntValue(11)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt04 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt04.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(20)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt05 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt05.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(0)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt06 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt06.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "RepeatUntilModule")
    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(11)));
    assert(interpreter.env.lookup("y") == Some(IntValue(40)));

  }
  test("Testing RepeatUntil stmt on RepeatUntilStmt07 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt07.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(20)));
    assert(interpreter.env.lookup("y") == Some(IntValue(20)));
  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt08 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt08.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)
    val interpreter = new Interpreter()
    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(10)));
    assert(interpreter.env.lookup("y") == Some(IntValue(10)));
  }

}
