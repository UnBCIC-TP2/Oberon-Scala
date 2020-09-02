package br.unb.cic.oberon.visitor

import br.unb.cic.oberon.ast.{Constant, Expression, FormalArg, OberonModule, Procedure, Statement, Type, VariableDeclaration}

/**
 * The abstract definition of an Oberon Visitor.
 * Note: here we are using a hybrid approach for
 * implementing visitors. Although it resembles
 * the OO flavor, we relies on pattern matching
 * to deal with the specificities of all concrete
 * elements of the Oberon language.
 *
 * @author rbonifacio
 */
trait OberonVisitor {
  type T
  var result : T = _
  def visit(module: OberonModule) : Unit
  def visit(constant: Constant) : Unit
  def visit(variable: VariableDeclaration) : Unit
  def visit(procedure: Procedure) : Unit
  def visit(arg: FormalArg) : Unit
  def visit(exp: Expression) : Unit
  def visit(stmt: Statement) : Unit
  def visit(aType: Type): Unit
}

abstract class OberonVisitorAdapter extends OberonVisitor {
  override def visit(module: OberonModule): Unit = {}
  override def visit(constant: Constant): Unit = {}
  override def visit(variable: VariableDeclaration): Unit = {}
  override def visit(procedure: Procedure): Unit = {}
  override def visit(arg: FormalArg): Unit = {}
  override def visit(exp: Expression): Unit = {}
  override def visit(stmt: Statement): Unit = {}
  override def visit(aType: Type): Unit = {}
}