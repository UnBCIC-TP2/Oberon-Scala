package br.unb.cic.oberon.ir.common

import br.unb.cic.oberon.visitor.OberonVisitor
import scala.collection.mutable.ListBuffer

/* expressions */
trait Expression {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

/* procedure declaration definition */
case class Procedure[+A](
    name: String,
    args: List[FormalArg],
    returnType: Option[Type],
    constants: List[Constant],
    variables: List[VariableDeclaration],
    stmt: A
) {
  //def accept(v: OberonVisitor): v.T = v.visit(this)
}

/* formal argument definition */
sealed trait FormalArg {
  def accept(v: OberonVisitor): v.T = v.visit(this)
  def argumentType: Type
  def name: String
}
case class ParameterByValue(name: String, argumentType: Type) extends FormalArg
case class ParameterByReference(name: String, argumentType: Type)
    extends FormalArg

/* Imports */
case class Import(name: String) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

/* Constant definition */
case class Constant(name: String, exp: Expression) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

sealed abstract class Value extends Expression with Ordered[Value] {
  type T
  def value: T

  override def compare(that: Value): Int = (this, that) match {
    case (v1: IntValue, v2: IntValue) => v1.value.compareTo(v2.value)
    case (v1: IntValue, v2: RealValue) =>
      ValueConversion.intValue2RealValue(v1).compareTo(v2)
    case (v1: RealValue, v2: IntValue) =>
      ValueConversion.intValue2RealValue(v2).compareTo(v1)
    case (v1: RealValue, v2: RealValue)     => v1.value.compareTo(v2.value)
    case (v1: CharValue, v2: CharValue)     => v1.value.compareTo(v2.value)
    case (v1: StringValue, v2: StringValue) => v1.value.compareTo(v2.value)
    case _ =>
      throw new RuntimeException(
        "Comparison is not defined for " + this.getClass + " and " + that.getClass
      )
  }
}

sealed trait Number extends Expression {
  def +(that: Number): Number
  def -(that: Number): Number
  def *(that: Number): Number
  def /(that: Number): Number

}

sealed trait Modular extends Number {
  def mod(that: Modular): Modular
}

case class IntValue(value: Int) extends Value with Modular {
  type T = Int
  def +(that: Number): Number = that match {
    case other: IntValue  => IntValue(value + other.value)
    case other: RealValue => RealValue(value + other.value)
  }

  def -(that: Number): Number = that match {
    case other: IntValue  => IntValue(value - other.value)
    case other: RealValue => RealValue(value - other.value)
  }

  def *(that: Number): Number = that match {
    case other: IntValue  => IntValue(value * other.value)
    case other: RealValue => RealValue(value * other.value)
  }

  def /(that: Number): Number = that match {
    case other: IntValue  => IntValue(value / other.value)
    case other: RealValue => RealValue(value / other.value)
  }

  val positiveMod = (x: Int, y: Int) => {
    val res = x % y; if (x < 0) res + y else res
  }

  def mod(that: Modular): Modular = that match {
    case other: IntValue => IntValue(positiveMod(value, other.value))
  }
}

case class RealValue(value: Double) extends Value with Number {
  type T = Double

  def +(that: Number): Number = that match {
    case other: IntValue  => RealValue(value + other.value)
    case other: RealValue => RealValue(value + other.value)
  }

  def -(that: Number): Number = that match {
    case other: IntValue  => RealValue(value - other.value)
    case other: RealValue => RealValue(value - other.value)
  }

  def *(that: Number): Number = that match {
    case other: IntValue  => RealValue(value * other.value)
    case other: RealValue => RealValue(value * other.value)
  }

  def /(that: Number): Number = that match {
    case other: IntValue  => RealValue(value / other.value)
    case other: RealValue => RealValue(value / other.value)
  }
}

case class CharValue(value: Char) extends Value { type T = Char }
case class StringValue(value: String) extends Value { type T = String }
case class BoolValue(value: Boolean) extends Value { type T = Boolean }
case object NullValue extends Value {
  type T = Unit
  def value: T = ()
}

case class Location(loc: Int) extends Expression
case class Brackets(exp: Expression) extends Expression
case class ArrayValue(value: ListBuffer[Expression], arrayType: ArrayType)
    extends Value { type T = ListBuffer[Expression] }
case class ArraySubscript(arrayBase: Expression, index: Expression)
    extends Expression
case class Undef() extends Expression
case class FieldAccessExpression(exp: Expression, name: String)
    extends Expression
case class PointerAccessExpression(name: String) extends Expression
case class VarExpression(name: String) extends Expression
case class FunctionCallExpression(name: String, args: List[Expression])
    extends Expression
case class EQExpression(left: Expression, right: Expression) extends Expression
case class NEQExpression(left: Expression, right: Expression) extends Expression
case class GTExpression(left: Expression, right: Expression) extends Expression
case class LTExpression(left: Expression, right: Expression) extends Expression
case class GTEExpression(left: Expression, right: Expression) extends Expression
case class LTEExpression(left: Expression, right: Expression) extends Expression
case class AddExpression(left: Expression, right: Expression) extends Expression
case class SubExpression(left: Expression, right: Expression) extends Expression
case class MultExpression(left: Expression, right: Expression)
    extends Expression
case class DivExpression(left: Expression, right: Expression) extends Expression
case class OrExpression(left: Expression, right: Expression) extends Expression
case class AndExpression(left: Expression, right: Expression) extends Expression
case class ModExpression(left: Expression, right: Expression) extends Expression
case class NotExpression(exp: Expression) extends Expression
case class LambdaExpression(args: List[FormalArg], exp: Expression)
    extends Expression

    
sealed trait Designator

case class VarAssignment(varName: String) extends Designator
case class ArrayAssignment(array: Expression, index: Expression)
    extends Designator
case class RecordAssignment(record: Expression, field: String)
    extends Designator
case class PointerAssignment(pointerName: String) extends Designator

/** User defined types.
  *
  * Users can declare either records or array types.
  */
case class UserDefinedType(name: String, baseType: Type) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

/* useful for implementing the REPL feature */
trait REPL

case class REPLExpression(exp: Expression) extends REPL
case class REPLStatement(stmt: Statement) extends REPL
case class REPLVarDeclaration(declarations: List[VariableDeclaration])
    extends REPL
case class REPLConstant(constants: Constant) extends REPL
case class REPLUserTypeDeclaration(userTypes: UserDefinedType) extends REPL

object ValueConversion {
  def intValue2RealValue(intValue: IntValue): RealValue = RealValue(
    intValue.value.toDouble
  )
  def charValue2IntValue(charValue: CharValue): IntValue = IntValue(
    charValue.value.toInt
  )
}

trait CaseAlternative {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

/* Statements */
trait Statement {
  val label = Statement.getLabel()
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

object Statement {
  var label = 0

  def getLabel(): Int = {
    label += 1
    label
  }

  def reset(): Unit = {
    label = 0
  }
}