package br.unb.cic.oberon

import br.unb.cic.oberon.ast.{REPLConstant, REPLExpression, REPLStatement, REPLUserTypeDeclaration, REPLVarDeclaration}
import br.unb.cic.oberon.codegen.PaigesBasedGenerator
import br.unb.cic.oberon.interpreter._
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.tc.TypeChecker
import org.rogach.scallop._
import org.rogach.scallop.exceptions

import java.beans.Expression
import java.nio.file.{Files, Paths}
import java.sql.Statement


class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {

  version("Oberon 0.1.1-SNAPSHOT")
  banner("""Compiler and interpreter for Oberon""".stripMargin)
  //footer("\n")

  val tyc = opt[String](name = "typeChecker", short = 't', descr = "Check if a oberon program is correctly typed", argName = "Oberon program path")
  val interpreter = opt[String](name = "interpreter", short = 'i', descr = "Interprets an Oberon program", argName = "Oberon program path" )
  val compile = opt[List[String]](name = "compile", short = 'c', descr = "Compiles an Oberon program to C", argName = "Oberon program path> <C program destiny path")
  val repl = opt[Boolean](name = "REPL", short = 'R', descr = "Run Oberon REPL")

  override def onError(e: Throwable): Unit = e match {
    case exceptions.Help("") => printHelp()
    case exceptions.Version => println("Oberon 0.1.1-SNAPSHOT")
    case other => println(other.getMessage)
  }

  verify()

}

object Main {

  def main(args: Array[String]) {
    val conf = new Conf(args)
    try {
      if (conf.tyc.isSupplied) {

        val path = Paths.get(conf.tyc())
        if (Files.exists(path)) {
          val content = String.join("\n", Files.readAllLines(path))

          val module = ScalaParser.parse(content)
          val visitor = new TypeChecker()

          val errors = visitor.visit(module)
          errors.foreach(v => if (v._2 != "None") println(v))
          if (errors.isEmpty) println("The code is correctly typed")
          else println("Type error detected")
        }
        else {
          println("The file '" + conf.tyc() + "' does not exist")
        }

      }

      if (conf.interpreter.isSupplied) {

        val path = Paths.get(conf.interpreter())

        if (Files.exists(path)) {
          val interpreter = new Interpreter()

          val content = String.join("\n", Files.readAllLines(path))
          val module = ScalaParser.parse(content)

          val result = module.accept(interpreter)
        }
        else {
          println("The file '" + conf.interpreter() + "' does not exist")
        }
      }

      if (conf.compile.isSupplied) {

        val oberonPath = Paths.get(conf.compile()(0))

        if (Files.exists(oberonPath)) {
          val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
          val module = ScalaParser.parse(oberonContent)

          val codeGen = PaigesBasedGenerator()
          val generatedCCode = codeGen.generateCode(module)

          var cPath = Paths.get("")
          if (conf.compile().length == 1) {
            cPath = Paths.get("compiled.c") // If path not provided, default path applied
          } else cPath = Paths.get(conf.compile()(1))

          val writer = Files.newBufferedWriter(cPath)

          writer.write(generatedCCode)
          writer.flush()
          writer.close()
        }
        else {
          println("The file '" + conf.compile() + "' does not exist")
        }
      }
    }
    catch {
      case t : Throwable => println(t.getMessage)
    }

    if(conf.repl.isSupplied) {
      val replObject = new Repl()
      replObject.runREPL()
    }

    if(conf.args.isEmpty) conf.printHelp()

  }
}

class Repl {

  def runREPL(): Unit = {
    var keepRunning = true
    var input = ""
    val interpreter = new Interpreter
    val expressionEval = new EvalExpressionVisitor(interpreter)

    while(keepRunning) {
      print("Oberon> ")
      input = scala.io.StdIn.readLine()
      if(input == "exit") keepRunning = false
      else if(input == "") print("")
      else {
        try {
          val command = ScalaParser.parserREPL(input)
          command match {
            case v: REPLVarDeclaration =>
              v.declarations.foreach(variable => variable.accept(interpreter))
            case c: REPLConstant =>
              c.constants.accept(interpreter)
            case u: REPLUserTypeDeclaration =>
              println(u.userTypes)
              u.userTypes.accept(interpreter)
            case s: REPLStatement =>
              s.stmt.accept(interpreter)
            case e: REPLExpression =>
              val result = e.exp.accept(expressionEval)
              println(result)
          }
        }
        catch {
          case v: ClassCastException => println("This is an invalid operation: " + v.getMessage)
          case e: NoSuchElementException => println("A variable is not defined " + e.getMessage)
          case n: NullPointerException => println("This is an invalid operation")
          case d: Throwable => println(d)
        }
      }
    }
  }

}
