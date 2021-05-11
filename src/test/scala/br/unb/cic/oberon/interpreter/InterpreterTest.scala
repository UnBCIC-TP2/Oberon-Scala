package br.unb.cic.oberon.interpreter

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer

class InterpreterTest extends AnyFunSuite {

  val interpreter = new Interpreter()

  interpreter.setTestEnvironment()

  test("Testing interpreter on interpreter_stmt01 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(5))) // FOR TO x
    assert(interpreter.env.lookup("y") == Some(IntValue(6))) // y = x + 1 (after last FOR)
    assert(interpreter.env.lookup("z") == Some(IntValue(15))) // z = result

  }

  test("Testing interpreter on interpreter_factorial01 program: factorial(5)") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_factorial01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(1)))
    assert(interpreter.env.lookup("y") == Some(IntValue(120)))

  }

  test("Testing interpreter on interpreter_factorial02 program: factorial(1)") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_factorial02.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(1)))
    assert(interpreter.env.lookup("y") == Some(IntValue(1)))

  }

  test("Testing interpreter on interpreter_factorial03 program: factorial(0)") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_factorial03.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(0)))
    assert(interpreter.env.lookup("y") == Some(IntValue(1)))

  }

  test("Testing interpreter on interpreter_fibonacci program: Fibonacci index 7 = 13") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_fibonacci01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("ant") == Some(IntValue(13)))

  }

  test("Testing interpreter on interpreter_stmt02 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt02.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(15)))
    assert(interpreter.env.lookup("z") == Some(IntValue(18)))

  }

  test("Testing interpreter on interpreter_stmt03 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt03.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(90)))
    assert(interpreter.env.lookup("z") == Some(IntValue(0)))

  }

  test("Testing interpreter on interpreter_stmt04 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt04.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(101)))
    assert(interpreter.env.lookup("z") == Some(IntValue(0)))

  }

  test("Testing interpreter on interpreter_stmt05 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt05.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(101)))
    assert(interpreter.env.lookup("z") == Some(IntValue(2)))

  }

  test("Testing interpreter on interpreter_stmt06 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt06.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(52)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_stmt07 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt07.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(52)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_fibonacci02 program: Fibonacci index 10 = 55") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_fibonacci02.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("ant") == Some(IntValue(55)))
  }

  test("Testing interpreter on interpreter_factorial04 program: factorial(4)") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/interpreter_factorial04.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(1)))
    assert(interpreter.env.lookup("y") == Some(IntValue(24)))

  }

  test("Testing interpreter on interpreter_stmt08 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt08.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(-50)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }

  test("Testing interpreter on interpreter_stmt09 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/interpreter_stmt09.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(25)))
    assert(interpreter.env.lookup("z") == Some(IntValue(10)))

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt01 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(1)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt02 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt02.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt03 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt03.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(3)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt04 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt04.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(4)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt05 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt05.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(5)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt06 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt06.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt07 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt07.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(2)));

  }

  test("Testing IFELSEIF stmt on IfElseIfStmt08 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt08.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("y") == Some(IntValue(3)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt01 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x").contains(IntValue(11)));
    assert(interpreter.env.lookup("sum").contains(IntValue(55)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt02 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt02.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("sum") == Some(IntValue(330)));
    assert(interpreter.env.lookup("x") == Some(IntValue(21)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt03 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt03.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("sum") == Some(IntValue(11)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt04 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt04.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(20)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt05 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt05.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(0)));

  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt06 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt06.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "RepeatUntilModule")
    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(11)));
    assert(interpreter.env.lookup("y") == Some(IntValue(40)));

  }
  
  test("Testing RepeatUntil stmt on RepeatUntilStmt07 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt07.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(20)));
    assert(interpreter.env.lookup("y") == Some(IntValue(20)));
  }

  test("Testing RepeatUntil stmt on RepeatUntilStmt08 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/RepeatUntilStmt08.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "RepeatUntilModule")

    module.accept(interpreter)

    assert(interpreter.env.lookup("x") == Some(IntValue(10)));
    assert(interpreter.env.lookup("y") == Some(IntValue(10)));
  }

  test("Testing LoopStmt stmt on loop_stmt01 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt01.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "LoopStmt")

    module.accept(interpreter)
    assert(interpreter.env.lookup("x") == Some(IntValue(-1)))
  }

  test("Testing LoopStmt stmt on loop_stmt02 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt02.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "LoopStmt")

    module.accept(interpreter)
    assert(interpreter.env.lookup("x") == Some(IntValue(6)))
    assert(interpreter.env.lookup("factorial") == Some(IntValue(120)))
  }

  test("Testing LoopStmt stmt on loop_stmt03 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt03.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "LoopStmt")

    module.accept(interpreter)
    assert(interpreter.env.lookup("x") == Some(IntValue(10)))
    assert(interpreter.env.lookup("y") == Some(IntValue(100)))
  }

  test("stmt35") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt35.oberon").getFile.replaceFirst("\\/(.:\\/)", "$1"))

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "UserTypeModule")

    module.accept(interpreter)
    assert(interpreter.env.lookupArrayIndex("a", 0).contains(IntValue(5)))
    assert(interpreter.env.lookupArrayIndex("b", 1).contains(IntValue(10)))
  }
  test("stmt36") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt36.oberon").getFile.replaceFirst("\\/(.:\\/)", "$1"))

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "UserTypeModule")

    module.accept(interpreter)
    assert(interpreter.env.lookupArrayIndex("a", 0).contains(IntValue(5)))
    assert(interpreter.env.lookupArrayIndex("a", 1).contains(IntValue(10)))
    assert(interpreter.env.lookupArrayIndex("b", 0).contains(IntValue(10)))
    assert(interpreter.env.lookupArrayIndex("a", 2).contains(IntValue(25)))
  }

  test("stmt37") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt37.oberon").getFile.replaceFirst("\\/(.:\\/)", "$1"))

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "UserTypeModule")

    module.accept(interpreter)
    assert(interpreter.env.lookupArrayIndex("a", 0).contains(IntValue(5)))
    assert(interpreter.env.lookupArrayIndex("a", 2).contains(IntValue(25)))
  }


  // External Function tests
  test("Testing div on c_functions_01") {
    val path = Paths.get(getClass.getClassLoader.getResource("c_functions/c_functions01.oberon").getFile)
    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "Ffi")

    module.accept(interpreter)
    assert(interpreter.env.lookup("ans") == Some(IntValue(2)))
  }

  test("Testing abs on c_functions02 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("c_functions/c_functions02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "Ffi")


    module.accept(interpreter)
    assert(interpreter.env.lookup("ans") == Some(IntValue(5)))
  }

  test("Testing isalpha on c_functions03 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("c_functions/c_functions03.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    // 43 é o caractere ascii '+' (que não é do alfabeto), a função deve retornar 0
    module.accept(interpreter)
    assert(interpreter.env.lookup("ans") == Some(IntValue(0)))
  }

  test("Testing abs on c_functions04 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("c_functions/c_functions04.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    module.accept(interpreter)
    assert(interpreter.env.lookup("ans") == Some(IntValue(4)))
  }

  test("Testing abs and isdigit on c_functions05 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("c_functions/c_functions05.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    module.accept(interpreter)

    // pois 10 não está em [0, 9]
    assert(interpreter.env.lookup("ans") == Some(IntValue(0)))
  }

  test("Testing isupper on c_functions06 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("c_functions/c_functions06.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    module.accept(interpreter)
    
    // 65 é o código ascii para 'A'
    assert(interpreter.env.lookup("ans") != Some(IntValue(0)))
  }

  test("Testing div on c_functions07 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("c_functions/c_functions07.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    module.accept(interpreter)
    
    assert(interpreter.env.lookup("ans") == Some(IntValue(1)))
  }

  test("Testing abs and div on c_functions08 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("c_functions/c_functions08.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    module.accept(interpreter)
    
    assert(interpreter.env.lookup("ans") == Some(IntValue(123)))
  }

  test("Testing isalnum and abs on c_functions09 program") {
    val path = Paths.get(getClass.getClassLoader.getResource("c_functions/c_functions09.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    module.accept(interpreter)
    
    // 65 é o código ascii para 'A'
    assert(interpreter.env.lookup("ans") == Some(IntValue(1)))
  }

}
