package br.unb.cic.oberon.visitor

import br.unb.cic.oberon.ast.{Constant, Expression, FormalArg, OberonModule, Procedure, Statement, Type, VariableDeclaration, CaseAlternative}

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

  def visit(module: OberonModule) : T
  def visit(constant: Constant) : T
  def visit(variable: VariableDeclaration) : T
  def visit(procedure: Procedure) : T
  def visit(arg: FormalArg) : T
  def visit(exp: Expression) : T
  def visit(stmt: Statement) : T
  def visit(aType: Type): T
  def visit(caseAlt: CaseAlternative): T
}

abstract class OberonVisitorAdapter extends OberonVisitor {
  override def visit(module: OberonModule): T = ???   // ??? means: undef
  override def visit(constant: Constant): T = ???
  override def visit(variable: VariableDeclaration): T = ???
  override def visit(procedure: Procedure): T = ???
  override def visit(arg: FormalArg): T = ???
  override def visit(exp: Expression): T = ???
  override def visit(stmt: Statement): T = ???
  override def visit(aType: Type): T = ???
  override def visit(caseAlt: CaseAlternative): T = ???
}