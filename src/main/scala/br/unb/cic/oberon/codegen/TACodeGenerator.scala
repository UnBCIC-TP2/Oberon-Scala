package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{Constant => ASTConstant, _}
import br.unb.cic.oberon.ir.tac._
import br.unb.cic.oberon.tc.{ExpressionTypeVisitor, TypeChecker}

object TACodeGenerator extends CodeGenerator[List[TAC]] {
  
  val visitor = new ExpressionTypeVisitor(new TypeChecker())

  override def generateCode(module: OberonModule): List[TAC] = {
    List()
  }

  def generateProcedure() {}

  def generateBody() {}

  def generateExpression(expr: Expression, insts: List[TAC]): (Address, List[TAC]) = {
    expr match {//usar visitExpression do tc para ver as expressoes
      case AddExpression(left, right) =>
        val (l, insts1) = generateExpression(left, insts)
        val (r, insts2) = generateExpression(right, insts)
        val t = new Temporary(expr.accept(visitor).get)
        return (t, insts1 ++ insts2 :+ AddOp(l, r, t, ""))

      case IntValue(value) =>
        return (Constant(value.toString, IntegerType), List())
    }
  }

  def generateStatement() {}
}
