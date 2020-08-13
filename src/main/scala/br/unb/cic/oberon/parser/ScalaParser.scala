package br.unb.cic.oberon.parser

import org.antlr.v4.runtime._

import br.unb.cic.oberon.ast._
import scala.collection.JavaConverters._
/**
  * A parser for the Oberon language using 
  * the ANTLR infrastructure. The main idea of 
  * this parser is to parse a Oberon source code 
  * and convert it into our Oberon AST representation
  * in Scala. Here we use the ANTLR idiom for parsing 
  * source code. 
  * 
  * @author rbonifacio   
  */
object ScalaParser {
  def parse(input: String) : OberonModule = {
    val charStream = new ANTLRInputStream(input)
    val lexer = new OberonLexer(charStream)
    val tokens = new CommonTokenStream(lexer)
    val parser = new OberonParser(tokens)

    val visitor = new ParserVisitor
    visitor.visitCompilationUnit(parser.compilationUnit())
    visitor.module
  }
}

class ParserVisitor {

  var module : OberonModule = _

  def visitCompilationUnit(ctx: OberonParser.CompilationUnitContext): Unit = {
    val name = ctx.name
    val constants = ctx.constant().asScala.toList.map(c => visitConstant(c))
    module = OberonModule(name.getText, constants)
  }

  /**
   * Visit a constant
   * @param ctx
   * @return
   */
  def visitConstant(ctx: OberonParser.ConstantContext): Constant = {
    val variable = Variable(ctx.varName.getText)
    val value = visitExpression(ctx.exp)
    Constant(variable, value)
  }

  /**
   * Visit an expression node
   * @param ctx the context expression
   * @return the abstract representation of an expression.
   */
  def visitExpression(ctx: OberonParser.ExpressionContext) : Expression = {
    IntValue(ctx.Number().getText.toInt)
  }


}
