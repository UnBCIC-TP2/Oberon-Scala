package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ast.{AddExpression, AndExpression, AssignmentStmt, BoolValue, BooleanType, Brackets, Constant, DivExpression, EQExpression, Expression, FormalArg, GTEExpression, GTExpression, IfElseStmt, IntValue, IntegerType, LTEExpression, LTExpression, MultExpression, NEQExpression, OberonModule, OrExpression, Procedure, ReadIntStmt, SequenceStmt, Statement, SubExpression, Type, Undef, UndefinedType, VarExpression, VariableDeclaration, WhileStmt, WriteStmt}
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.visitor.{OberonVisitor, OberonVisitorAdapter}

class ExpressionTypeVisitor(val typeChecker: TypeChecker) extends OberonVisitorAdapter {
  type T = Option[Type]

  override def visit(t: Type): Option[Type] = t match {
    case UndefinedType => None
    case _ => Some(t)
  }
  override def visit(exp: Expression): Option[Type] = exp match {
    case Brackets(exp) => exp.accept(this)
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
    case AndExpression(left, right) => computeBinExpressionType(left, right, BooleanType, BooleanType)
    case OrExpression(left, right) => computeBinExpressionType(left, right, BooleanType, BooleanType)
    // TODO: function call ...
  }

  def computeBinExpressionType(left: Expression, right: Expression, expected: Type, result: Type) : Option[Type] = {
    val t1 = left.accept(this)
    val t2 = right.accept(this)
    if(t1 == t2 && t1.contains(expected)) Some(result) else None
  }
}

class TypeChecker extends OberonVisitorAdapter {
  type T = List[(Statement, String)]

  val env =  new Environment[Type]()
  val expVisitor = new ExpressionTypeVisitor(this)

  override def visit(stmt: Statement) = stmt match {
    case AssignmentStmt(_, _) => visitAssignment(stmt)
    case IfElseStmt(_, _, _) => visitIfElseStmt(stmt)
    case WhileStmt(_, _) => visitWhileStmt(stmt)
    case SequenceStmt(stmts) => stmts.flatMap(s => s.accept(this))
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

  private def visitIfElseStmt(stmt: Statement) = stmt match {
    case IfElseStmt(condition, thenStmt, elseStmt) =>
      if(condition.accept(expVisitor).contains(BooleanType)) {
        val list1 = thenStmt.accept(this)
        val list2 = if(elseStmt.isDefined) elseStmt.get.accept(this) else List()
        list1 ++ list2
      }
      else List((stmt, s"Expression $condition do not have a boolean type"))
  }

  private def visitWhileStmt(stmt: Statement) = stmt match {
    case WhileStmt(condition, stmt) =>
      if(condition.accept(expVisitor).contains(BooleanType)) {
        stmt.accept(this)
      }
      else List((stmt, s"Expression $condition do not have a boolean type"))
  }
}
