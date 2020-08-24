package br.unb.cic.oberon.ast

case class OberonModule(
                         name: String,
                         constants: List[Constant],
                         variables: List[VariableDeclaration]
                       )
/* Constant definition */
case class Constant(variable: Variable, exp: Expression)

/* Variable declaration definition */
case class VariableDeclaration(variables: List[Variable], variableType: Type)

/* Variable definition */
case class Variable(name: String)

/* Expressions */
trait Expression
case class Brackets(exp: Expression) extends Expression
case class IntValue(value: Int) extends Expression
case class BoolValue(value: Boolean) extends Expression
case class VarExpression(name: String) extends Expression
case class AddExpression(left: Expression, right: Expression) extends Expression
case class SubExpression(left: Expression, right: Expression) extends Expression
case class MultExpression(left: Expression, right: Expression) extends Expression
case class DivExpression(left: Expression, right: Expression) extends Expression
case class OrExpression(left: Expression, right: Expression) extends Expression
case class AndExpression(left: Expression, right: Expression) extends Expression


/* Types */
trait Type
case object IntegerType extends Type
case object BooleanType extends Type
case object UndefinedType extends Type
