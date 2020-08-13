package br.unb.cic.oberon.ast

case class OberonModule(
                         name: String,
                         constants: List[Constant]
                       )
/* Constant definition */
case class Constant(variable: Variable, exp: Expression)

/* Variable definition */
case class Variable(name: String)

/* Expressions */
trait Expression
case class IntValue(value: Int) extends Expression