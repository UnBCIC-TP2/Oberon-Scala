package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ast.{Constant, Expression, FormalArg, OberonModule, Procedure, Statement, Type, VariableDeclaration}
import br.unb.cic.oberon.visitor.OberonVisitor

class TypeChecker extends OberonVisitor {
  type T = Unit

  override def visit(module: OberonModule): Unit = ???

  override def visit(constant: Constant): Unit =
    constant.exp.accept(this)

  override def visit(variable: VariableDeclaration): Unit = ???

  override def visit(procedure: Procedure): Unit = ???

  override def visit(arg: FormalArg): Unit = ???

  override def visit(exp: Expression): Unit = ???

  override def visit(stmt: Statement): Unit = ???

  override def visit(aType: Type): Unit = ???
}
