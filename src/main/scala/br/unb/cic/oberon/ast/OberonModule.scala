package br.unb.cic.oberon.ast

import br.unb.cic.oberon.visitor.OberonVisitor

/* Abstract representation of an Oberon Module */
case class OberonModule(name: String,
                        constants: List[Constant],
                        variables: List[VariableDeclaration],
                        procedures: List[Procedure],
                        stmt: Option[Statement]
                       ) {
  def accept(v: OberonVisitor): Unit = v.visit(this)
}

/* procedure declaration definition */
case class Procedure(name: String,
                     args: List[FormalArg],
                     returnType: Option[Type],
                     constants: List[Constant],
                     variables: List[VariableDeclaration],
                     stmt: Statement
                    ) {
  def accept(v: OberonVisitor) = v.visit(this)
}

/* formal argument definition */
case class FormalArg(name: String, argumentType: Type) {
  def accept(v: OberonVisitor) = v.visit(this)
}

/* Constant definition */
case class Constant(name: String, exp: Expression) {
  def accept(v: OberonVisitor) = v.visit(this)
}

/* Variable declaration definition */
case class VariableDeclaration(name: String, variableType: Type) {
  def accept(v: OberonVisitor) = v.visit(this)
}

/* Expressions */
trait Expression {
  def accept(v: OberonVisitor) = v.visit(this)
}

abstract class Value[T](val value: T) extends Expression

case class Brackets(exp: Expression) extends Expression
case class IntValue(v: Int) extends Value[Int](v)
case class BoolValue(v: Boolean) extends Value[Boolean](v)
case class Undef() extends Expression
case class VarExpression(name: String) extends Expression
case class FunctionCallExpression(name: String, args: List[Expression]) extends Expression
case class EQExpression(left:  Expression, right: Expression) extends Expression
case class NEQExpression(left:  Expression, right: Expression) extends Expression
case class GTExpression(left:  Expression, right: Expression) extends Expression
case class LTExpression(left:  Expression, right: Expression) extends Expression
case class GTEExpression(left:  Expression, right: Expression) extends Expression
case class LTEExpression(left:  Expression, right: Expression) extends Expression
case class AddExpression(left: Expression, right: Expression) extends Expression
case class SubExpression(left: Expression, right: Expression) extends Expression
case class MultExpression(left: Expression, right: Expression) extends Expression
case class DivExpression(left: Expression, right: Expression) extends Expression
case class OrExpression(left: Expression, right: Expression) extends Expression
case class AndExpression(left: Expression, right: Expression) extends Expression

/* Statements */
trait Statement {
  def accept(v: OberonVisitor) = v.visit(this)
}

case class AssignmentStmt(varName: String, exp: Expression) extends Statement
case class SequenceStmt(stmts: List[Statement]) extends Statement
case class ReadIntStmt(varName: String) extends Statement
case class WriteStmt(expression: Expression) extends Statement
case class ProcedureCallStmt(name: String, args: List[Expression]) extends Statement
case class IfElseStmt(condition: Expression, thenStmt: Statement, elseStmt: Option[Statement]) extends Statement
case class WhileStmt(condition: Expression, stmt: Statement) extends Statement
case class ForStmt(init: Statement, condition: Expression, stmt: Statement) extends Statement
case class ReturnStmt(exp: Expression) extends Statement
case class CaseStmt(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement]) extends Statement

trait CaseAlternative {
  def accept(v: OberonVisitor) = v.visit(this)
}

case class SimpleCase(condition: Expression, stmt: Statement) extends CaseAlternative
case class RangeCase(min: Expression, max: Expression, stmt: Statement) extends CaseAlternative

/* Types */
trait Type {
  def accept(v: OberonVisitor) = v.visit(this)
}

case object IntegerType extends Type
case object BooleanType extends Type
case object UndefinedType extends Type
