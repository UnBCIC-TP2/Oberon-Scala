package br.unb.cic.oberon.ast

/* Abstract representation of an Oberon Module */
case class OberonModule(name: String,
                        constants: List[Constant],
                        variables: List[VariableDeclaration],
                        procedires: List[Procedure],
                        stmt: Option[Statement]
                       )

/* procedure declaration definition */
case class Procedure(name: String,
                     args: List[FormalArg],
                     returnType: Option[Type],
                     constants: List[Constant],
                     variables: List[VariableDeclaration],
                     stmt: Statement
                    )

/* formal argument definition */
case class FormalArg(name: String, argumentType: Type)

/* Constant definition */
case class Constant(name: Variable, exp: Expression)

/* Variable declaration definition */
case class VariableDeclaration(nane: String, variableType: Type)

/* Variable definition */
case class Variable(name: String)

/* Expressions */
trait Expression
case class Brackets(exp: Expression) extends Expression
case class IntValue(value: Int) extends Expression
case class BoolValue(value: Boolean) extends Expression
case class VarExpression(name: String) extends Expression
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
trait Statement
case class AssignmentStmt(varName: String, exp: Expression) extends Statement
case class SequenceStmt(stmts: List[Statement]) extends Statement
case class ReadStmt(varName: String) extends Statement
case class WriteStmt(expression: Expression) extends Statement
case class IfElseStmt(condition: Expression, thenStmt: Statement, elseStmt: Option[Statement]) extends Statement
case class WhileStmt(condition: Expression, stmt: Statement) extends Statement

/* Types */
trait Type
case object IntegerType extends Type
case object BooleanType extends Type
case object UndefinedType extends Type
