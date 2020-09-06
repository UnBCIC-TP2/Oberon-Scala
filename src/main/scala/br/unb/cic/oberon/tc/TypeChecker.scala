package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ast.{BoolValue, BooleanType, Constant, Expression, FormalArg, IntValue, IntegerType, OberonModule, Procedure, Statement, Type, Undef, VariableDeclaration}
import br.unb.cic.oberon.visitor.{OberonVisitor, OberonVisitorAdapter}

//class ComputeExpressionType extends OberonVisitorAdapter {
//  type T = Option[Type]
//
//  override def visit(exp: Expression): Unit = exp match {
//    case IntValue(v) => result = Some(IntegerType)
//    case BoolValue(v) => result = Some(BooleanType)
//    case Undef() => result = None
//    case
//  }
//}
//
//class TypeChecker extends OberonVisitorAdapter {
//  type T = List[String]
//
//  override def visit(exp: Expression): Unit = exp match {
//    case Undef() =>
//    case IntValue(v) =>
//    cas
//  }
//}
