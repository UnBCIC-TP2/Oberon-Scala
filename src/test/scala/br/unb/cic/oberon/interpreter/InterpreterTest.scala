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
  
}