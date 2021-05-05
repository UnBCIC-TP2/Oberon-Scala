package br.unb.cic.oberon

import br.unb.cic.oberon.codegen.PaigesBasedGenerator
import br.unb.cic.oberon.interpreter._
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.tc.TypeChecker
import org.rogach.scallop._

import java.nio.file.{Files, Paths}

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {

  version("Oberon 0.1.1-SNAPSHOT")
  banner("""Compiler and interpreter for Oberon""".stripMargin)
  //footer("\nFor all other tricks, consult the documentation!")

  val tyc = opt[String](name = "typeChecker", short = 't', descr = "Check if the oberon code is correctly typed", argName = "Oberon program path")
  val interpreter = opt[String](name = "interpreter", short = 'i', descr = "Interprets the Oberon program", argName = "Oberon program path Oberon program path" )
  val compile = opt[List[String]](name = "compile", short = 'c', descr = "Compile the Oberon program", argName = "Oberon program path")
  val repl = opt[Boolean](name = "repl", short = 'r', descr = "Run repl option")
  val repl2 = opt[Boolean](name = "repl2", short = 'R', descr = "Run repl option")

  verify()

}

object Main {
  def main(args: Array[String]) {
    val conf = new Conf(args)

    if(conf.tyc.isSupplied) {

      val path = Paths.get(conf.tyc())
      if(Files.exists(path)) {
        val content = String.join("\n", Files.readAllLines(path))

        val module = ScalaParser.parse(content)
        val visitor = new TypeChecker()

        val errors = visitor.visit(module)
        if (errors.isEmpty) println("The code is correctly typed")
        else println("Type error detected")
      }
      else {
        println("This file does not exist")
      }

    }

    if(conf.interpreter.isSupplied) {

      val path = Paths.get(conf.interpreter())

      if(Files.exists(path)) {
        val interpreter = new Interpreter()

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)

        val result = module.accept(interpreter)
      }
      else {
        println("This file does not exist")
      }

      //println(result)
      //println(interpreter.env.lookup("x"))
      //println(interpreter.env.lookup("factorial"))

    }

    if(conf.compile.isSupplied) {

      val oberonPath = Paths.get(conf.compile()(0))

      if(Files.exists(oberonPath)) {
        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)

        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        //val cFilePath = conf.compile()(1) + "/" + module.name + ".c"

        //println("cFilePath = " + cFilePath)
        var cPath = Paths.get("")
        if (conf.compile().length == 1) {
          cPath = Paths.get("compiled.c")  // If path not provided, default path applied
        } else cPath = Paths.get(conf.compile()(1))
  
        val writer = Files.newBufferedWriter(cPath)

        writer.write(generatedCCode)
        writer.flush()
        writer.close()
      }
      else {
        println("This file does not exist")
      }
      //Files.createFile(cPath)

    }

    if (conf.repl.isSupplied) {
      val interpreter = new EvalExpressionVisitor(new Interpreter)
      var input = scala.io.StdIn.readLine()
      while(input != "exit") {
        if (input == "") input = "0"
        // println(content)
        val exp = ScalaParser.parseExpression(input)

        val result = exp.accept(interpreter)
        println(result)
        input = scala.io.StdIn.readLine()
      }
    }

    if(conf.args.isEmpty) {
      val replObject = new Repl()
      replObject.runREPL()
    }

  }
}

class Repl {

  def runREPL(): Unit = {
    var keepRunning = true
    var input = ""
    val interpreter = new EvalExpressionVisitor(new Interpreter)

    while(keepRunning) {
      print("Oberon> ")
      input = scala.io.StdIn.readLine()
      if(input == "exit") keepRunning = false
      else if(input == "") print("")
      else {
        try {
          val exp = ScalaParser.parseExpression(input)
          val result = exp.accept(interpreter)
          println(result)
        }
        catch {
          //case a: NullPointerException => println("")
          case c: NoSuchElementException => println("Invalid input")
          case e: Throwable => println(e)
        }
      }
    }
  }

}
