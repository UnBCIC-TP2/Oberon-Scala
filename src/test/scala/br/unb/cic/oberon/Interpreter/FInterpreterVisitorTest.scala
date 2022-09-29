package br.unb.cic.oberon.interpreter

import br.unb.cic.oberon.ast.IntValue
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.interpreter.FInterpreter


class FInterpreterVisitorTest extends AnyFunSuite{

  test("Test FInterpreter on stmt05 program") {
    val module = ScalaParser.parseResource("stmts/stmt05.oberon")
    val interpreter = new FInterpreter
    assert(module.name == "SimpleModule")

    interpreter.setTestEnvironment()
    val (env, stmt) = interpreter.decomposer(module)
    val executed = interpreter.interpret(stmt.get, env)

    assert(executed.lookup("x") == Some(IntValue(625)))
    assert(executed.lookup("y") == Some(IntValue(100)))

  }

  test("Test eval on factorial module") {
    val module = ScalaParser.parseResource("procedures/procedure03.oberon")
    val interpreter = new FInterpreter
    assert(module.name == "Factorial")

    interpreter.setTestEnvironment()
    val (env, stmt) = interpreter.decomposer(module)
    val executed = interpreter.interpret(stmt.get, env)
    
    assert(executed.lookup("res").isDefined)
    assert(executed.lookup("res") == Some(IntValue(120)))
  }
}