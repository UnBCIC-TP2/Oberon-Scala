package br.unb.cic.oberon.repl

import br.unb.cic.oberon.ast.{REPLConstant, REPLExpression, REPLStatement, REPLUserTypeDeclaration, REPLVarDeclaration}
import br.unb.cic.oberon.interpreter._
import br.unb.cic.oberon.parser.ScalaParser

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

    while(keepRunning) {
      print("Oberon> ")
      input = scala.io.StdIn.readLine()
      if(input == "exit") keepRunning = false
      else if(input == "") print("")
      else {
        try {
          // Acho que seria interessante connector retornar uma string
          Connector.connect(input)
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

object Connector {
  val interpreter = new Interpreter
  val expressionEval = new EvalExpressionVisitor(interpreter)

  def connect(input:String): Unit = {
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
}
