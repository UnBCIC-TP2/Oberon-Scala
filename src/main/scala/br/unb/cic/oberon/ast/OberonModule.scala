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
case class IntValue(value: Int) extends Expression

/* Types */
trait Type
case object IntegerType extends Type
case object BooleanType extends Type
case object UndefinedType extends Type