package br.unb.cic.oberon.interpreter

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ir.ast.{IntValue, OberonModule}
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class InterpreterVisitorTest extends AnyFunSuite {

  test("Test interpreter on stmt05 program") {
    val module = ScalaParser.parseResource("stmts/stmt05.oberon")
    val interpreter = new Interpreter()
    assert(module.name == "SimpleModule")

    interpreter.setTestEnvironment()
    val result = interpreter.run(module)

    assert(result.lookup("x") == Some(IntValue(625)))
    assert(result.lookup("y") == Some(IntValue(100)))

  }

  ignore("Test eval on factorial module") {
    val module = ScalaParser.parseResource("procedures/procedure03.oberon")
    val interpreter = new Interpreter()
    assert(module.name == "Factorial")

    interpreter.setTestEnvironment()
    val result = interpreter.run(module)

    assert(result.lookup("res").isDefined)
    assert(result.lookup("res") == Some(IntValue(120)))
  }

  test("Testing bee1161SomadeFatoriais") {
    val module = ScalaParser.parseResource("Grupo_06/bee1161_SomadeFatoriais.oberon")
    val interpreter = new Interpreter()
    assert(module.name == "bee1161SomadeFatoriais")

    interpreter.setTestEnvironment()
    val result = interpreter.run(module)

    assert(result.lookup("res").isDefined)
    assert(result.lookup("res") == Some(IntValue(48)))
    assert(result.lookup("res2").isDefined)
    assert(result.lookup("res2") == Some(IntValue(2)))
    assert(result.lookup("res3").isDefined)
    assert(result.lookup("res3") == Some(IntValue(3)))


  }

  test("Testing interpreter on bee1161_SomadeFatoriais2 program") {
    val module = ScalaParser.parseResource("Grupo_06/bee1161_SomadeFatoriais2.oberon")

    
    val interpreter = new Interpreter()

    assert(module.name == "bee1161SomadeFatoriais2")

    val result = interpreter.run(module)

    assert(result.lookup("res") == Some(IntValue(48)))
    assert(result.lookup("res2") == Some(IntValue(2)))
    assert(result.lookup("res3") == Some(IntValue(3)))

  }

  test(testName = "Beecrownd test of Weighted Averages 1161"){
    val module = ScalaParser.parseResource("Grupo_06/bee1161_SomadeFatoriais.oberon")

    val coreVisitor = new CoreVisitor()
    val coreModule = coreVisitor.transformModule(module)

    assert(module.name == "bee1079Average")

    coreModule.accept(interpreter)

    assert(evalArraySubscript("r", 0) == IntValue(48))
    assert(evalArraySubscript("r", 1) == IntValue(2))
    assert(evalArraySubscript("r", 2) == IntValue(3))

  }
  
      test("Testing bee1088BolhasBaldes") {
    val module = ScalaParser.parseResource("Grupo_06/bee1088_BolhasBaldes.oberon")
    val interpreter = new Interpreter()
    assert(module.name == "bee1088BolhasBaldes")

    interpreter.setTestEnvironment()
    val result = interpreter.run(module)

    assert(result.lookup("res").isDefined)
    assert(result.lookup("res") == Some("Marcelo"))
  }
    test("Testing bee1279") {
    val module = ScalaParser.parseResource("Grupo_06/bee1279.oberon")
    val interpreter = new Interpreter()
    assert(module.name == "bee1279")

    interpreter.setTestEnvironment()
    val result = interpreter.run(module)

    assert(result.lookup("saida1").isDefined)
    assert(result.lookup("saida1") == Some(StringValue("Este e um ano bissexto.")))

    assert(result.lookup("saida2").isDefined)
    assert(result.lookup("saida2") == Some(StringValue("Este e um ano bissexto e de festival huluculu.")))

    assert(result.lookup("saida3").isDefined)
    assert(result.lookup("saida3") == Some(StringValue("Este e um ano de festival huluculu.")))

    assert(result.lookup("saida4").isDefined)
    assert(result.lookup("saida4") == Some(StringValue("Este e um ano comum.")))
  }
}
