package br.unb.cic.oberon

import br.unb.cic.oberon.ast.{REPLConstant, REPLExpression, REPLStatement, REPLUserTypeDeclaration, REPLVarDeclaration}
import br.unb.cic.oberon.codegen.PaigesBasedGenerator
import br.unb.cic.oberon.interpreter._
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.tc.TypeChecker
import org.rogach.scallop._
import org.rogach.scallop.exceptions

import java.nio.file.{Files, Paths}


/**
 * This is the configuration of the CLI (command line interface),
 * using the Scallop library. For more details visit the library
 * website: https://github.com/scallop/scallop
 *
 * @param arguments The string list with the command line arguments.
 */
class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  version("Oberon 0.1.1-SNAPSHOT")
  banner("""Compiler and interpreter for Oberon""".stripMargin)

  val tc = opt[String](name = "typeChecker", short = 't', descr = "Check if a oberon program is well typed", argName = "Oberon program path")
  val interpreter = opt[String](name = "interpreter", short = 'i', descr = "Interprets an Oberon program", argName = "Oberon program path" )
  val compile = opt[List[String]](name = "compile", short = 'c', descr = "Compiles an Oberon program to C", argName = "Oberon program path> <C program output path")
  val repl = opt[Boolean](name = "REPL", short = 'R', descr = "Run Oberon REPL")

  requireOne(tc, interpreter, compile, repl)

  override def onError(e: Throwable): Unit = e match {
    case exceptions.Help("") => printHelp()
    case exceptions.Version => println("Oberon 0.1.1-SNAPSHOT")
    case other => println(other.getMessage)
  }

  verify()

}

/**
 * The Main program of our implementation, using a Scala
 * object. In this case, we opted for inheriting from the
 * App trait.
 *
 * see more information here:
 *    * https://www.scala-lang.org/documentation/your-first-lines-of-scala.html
 */
object Main extends App {

  /* main block of code */
  val conf = new Conf(args)

  try {
    if (conf.tc.isSupplied) {
      typeCheck(conf)
    }
    else if (conf.interpreter.isSupplied) {
      interpret(conf)
    }
    else if (conf.compile.isSupplied) {
      compile(conf)
    }
    else if (conf.repl.isSupplied) {
      runREPL()
    }
  }
  catch {
    case t: Throwable => println(t.getMessage)
  }
  if (conf.args.isEmpty) conf.printHelp()

  /* end of the main block */

  /**
   * Executes the REPL (Read, Eval, Print, Loop) program
   */
  private def runREPL() = {
    Repl.runREPL()
  }

  /**
   * Executes the Oberon compiler.
   *
   * @param conf the CLI configuration
   */
  private def compile(conf: Conf) = {
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

  /**
   * Executes the Oberon interpreter.
   *
   * @param conf the CLI configuration
   */
  private def interpret(conf: Conf) = {
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

  /**
   * Executes the type checker
   *
   * @param conf the CLI configuration
   */
  private def typeCheck(conf: Conf) = {
    val path = Paths.get(conf.tc())
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
      println("The file '" + conf.tc() + "' does not exist")
    }
  }
}

/**
 * Our REPL module singleton class (i.e., an
 * Scala object).
 */
object Repl {

  /**
   * Executes the REPL Oberon interpreter
   */
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
