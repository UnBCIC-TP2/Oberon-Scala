package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ast.{AddExpression, AssignmentStmt, BoolValue, BooleanType, Constant, DivExpression, EQExpression, Expression, FormalArg, GTEExpression, GTExpression, IntValue, IntegerType, LTEExpression, LTExpression, MultExpression, NEQExpression, OberonModule, Procedure, ReadIntStmt, SequenceStmt, Statement, SubExpression, Type, Undef, UndefinedType, VarExpression, VariableDeclaration, WriteStmt}
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.visitor.{OberonVisitor, OberonVisitorAdapter}

class ExpressionTypeVisitor(val typeChecker: TypeChecker) extends OberonVisitorAdapter {
  type T = Option[Type]

  override def visit(t: Type): Option[Type] = t match {
    case UndefinedType => None
    case _ => Some(t)
  }
  override def visit(exp: Expression): Option[Type] = exp match {
    case IntValue(_) => Some(IntegerType)
    case BoolValue(_) => Some(BooleanType)
    case Undef() => None
    case VarExpression(name) => if(typeChecker.env.lookup(name).isDefined) typeChecker.env.lookup(name).get.accept(this) else None
    case EQExpression(left, right) => computeBinExpressionType(left, right, IntegerType, BooleanType)
    case NEQExpression(left, right) => computeBinExpressionType(left, right, IntegerType, BooleanType)
    case GTExpression(left, right) => computeBinExpressionType(left, right, IntegerType, BooleanType)
    case LTExpression(left, right) => computeBinExpressionType(left, right, IntegerType, BooleanType)
    case GTEExpression(left, right) => computeBinExpressionType(left, right, IntegerType, BooleanType)
    case LTEExpression(left, right) => computeBinExpressionType(left, right, IntegerType, BooleanType)
    case AddExpression(left, right) => computeBinExpressionType(left, right, IntegerType, IntegerType)
    case SubExpression(left, right) => computeBinExpressionType(left, right, IntegerType, IntegerType)
    case MultExpression(left, right) => computeBinExpressionType(left, right, IntegerType, IntegerType)
    case DivExpression(left, right) => computeBinExpressionType(left, right, IntegerType, IntegerType)
    // TODO: function call ...
  }

  def computeBinExpressionType(left: Expression, right: Expression, expected: Type, result: Type) : Option[Type] = {
    val t1 = left.accept(this)
    val t2 = right.accept(this)
    if(t1 == t2 && t1 == Some(expected)) Some(result) else None
  }
}

class TypeChecker extends OberonVisitorAdapter {
  type T = List[(Statement, String)]

  val env =  new Environment[Type]()
  val expVisitor = new ExpressionTypeVisitor(this)

  override def visit(stmt: Statement) = stmt match {
    case AssignmentStmt(_, _) => visitAssignment(stmt)
    case SequenceStmt(stmts) => stmts.map(s => s.accept(this)).flatten
    case ReadIntStmt(v) => if(env.lookup(v).isDefined) List() else List((stmt, s"Variable $v not declared."))
    case WriteStmt(exp) => if(exp.accept(expVisitor).isDefined) List() else List((stmt, s"Expression $exp is ill typed."))
  }

  private def visitAssignment(stmt: Statement) = stmt match {
    case AssignmentStmt(v, exp) =>
      if (env.lookup(v).isDefined) {
        if (exp.accept(expVisitor).isDefined)
          List()
        else List((stmt, s"Expression $exp is ill typed"))
      }
      else List((stmt, s"Variable $v not declared"))
  }
}
