package br.unb.cic.oberon.ast

import br.unb.cic.oberon.visitor.OberonVisitor
import br.unb.cic.oberon.environment.Environment


/** Abstract representation of an Oberon Module
 *
 * Here we use an object-oriented decomposition to represent
 * Oberon concepts as Scala classes and traits.
 *
 * The use of case classes here are handy, mostly because we
 * can use case case classes in pattern matching, and they also
 * provide useful methods (such as equality).
 *
 * Traits offers an interesting approach for software composition,
 * besides OO inheritance.
 */
case class OberonModule(name: String,
                        submodules: Set[String],
                        userTypes: List[UserDefinedType],
                        constants: List[Constant],
                        variables: List[VariableDeclaration],
                        procedures: List[Procedure],
                        stmt: Option[Statement]
                       ) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

// SequenceStatement(List[Stmt]) extends Stmt
// alternativa: SequenceStatement(stmt, stmt) extends Stmt

/* procedure declaration definition */
case class Procedure(name: String,
                     args: List[FormalArg],
                     returnType: Option[Type],
                     constants: List[Constant],
                     variables: List[VariableDeclaration],
                     stmt: Statement
                    ) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

/* formal argument definition */
case class FormalArg(name: String, argumentType: Type) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

/* Imports */
case class Import(name: String){
  def accept(v: OberonVisitor): v.T = v.visit(this)
}


/* Constant definition */
case class Constant(name: String, exp: Expression) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

/* Variable declaration definition */
case class VariableDeclaration(name: String, variableType: Type) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

/* Expressions */
trait Expression {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

sealed abstract class Value extends Expression with Ordered[Value]{
  type T
  def value: T

  override def compare(that: Value): Int = (this, that) match {
    case (v1: IntValue, v2: IntValue) => v1.value.compareTo(v2.value)
    case (v1: IntValue, v2: RealValue) => ValueConversion.intValue2RealValue(v1).compareTo(v2)
    case (v1: RealValue, v2: IntValue) => ValueConversion.intValue2RealValue(v2).compareTo(v1)
    case (v1: RealValue, v2: RealValue) => v1.value.compareTo(v2.value)
    case (v1: CharValue, v2: CharValue) => v1.value.compareTo(v2.value)
    case (v1: StringValue, v2: StringValue) => v1.value.compareTo(v2.value)
    case _ => throw new RuntimeException("Comparison is not defined for " + this.getClass + " and " + that.getClass)
  }
}

sealed trait Number extends Expression {
  def +(that: Number): Number
  def -(that: Number): Number
  def *(that: Number): Number
  def /(that: Number): Number

}
case class IntValue(value: Int) extends Value with Number{
  type T = Int
  def +(that: Number): Number = that match {
    case other: IntValue => IntValue(value + other.value)
    case other: RealValue => RealValue(value + other.value)
  }

  def -(that: Number): Number = that match {
    case other: IntValue => IntValue(value - other.value)
    case other: RealValue => RealValue(value - other.value)
  }

  def *(that: Number): Number = that match {
    case other: IntValue => IntValue(value * other.value)
    case other: RealValue => RealValue(value * other.value)
  }

  def /(that: Number): Number = that match {
    case other: IntValue => IntValue(value / other.value)
    case other: RealValue => RealValue(value / other.value)
  }

}

case class RealValue(value: Double) extends Value with Number {
  type T = Double

  def +(that: Number): Number = that match {
    case other: IntValue => RealValue(value + other.value)
    case other: RealValue => RealValue(value + other.value)
  }

  def -(that: Number): Number = that match {
    case other: IntValue => RealValue(value - other.value)
    case other: RealValue => RealValue(value - other.value)
  }

  def *(that: Number): Number = that match {
    case other: IntValue => RealValue(value * other.value)
    case other: RealValue => RealValue(value * other.value)
  }

  def /(that: Number): Number = that match {
    case other: IntValue => RealValue(value / other.value)
    case other: RealValue => RealValue(value / other.value)
  }
}

case class CharValue(value: Char) extends Value { type T = Char }
case class StringValue(value: String) extends Value { type T = String }
case class BoolValue(value: Boolean) extends Value { type T = Boolean }

case class Brackets(exp: Expression) extends Expression
case class ArrayValue(value: List[Expression]) extends Value { type T = List[Expression] }
case class ArraySubscript(arrayBase: Expression, index: Expression) extends Expression
case class Undef() extends Expression
case class FieldAccessExpression(exp: Expression, name: String) extends Expression
case class PointerAccessExpression(name: String) extends Expression
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
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

case class ScalaStmt(fn: Environment[Expression] => Unit) extends Statement
case class AssignmentStmt(varName: String, exp: Expression) extends Statement
case class EAssignmentStmt(designator: AssignmentAlternative, exp: Expression) extends Statement
case class SequenceStmt(stmts: List[Statement]) extends Statement
case class ReadLongRealStmt(varName: String) extends Statement
case class ReadRealStmt(varName: String) extends Statement
case class ReadLongIntStmt(varName: String) extends Statement
case class ReadIntStmt(varName: String) extends Statement
case class ReadShortIntStmt(varName: String) extends Statement
case class ReadCharStmt(varName: String) extends Statement
case class WriteStmt(expression: Expression) extends Statement
case class ProcedureCallStmt(name: String, args: List[Expression]) extends Statement
case class IfElseStmt(condition: Expression, thenStmt: Statement, elseStmt: Option[Statement]) extends Statement
case class IfElseIfStmt(condition: Expression, thenStmt: Statement, elseifStmt: List[ElseIfStmt], elseStmt: Option[Statement]) extends Statement
case class ElseIfStmt(condition: Expression, thenStmt: Statement) extends Statement
case class WhileStmt(condition: Expression, stmt: Statement) extends Statement
case class RepeatUntilStmt(condition: Expression, stmt: Statement) extends Statement
case class ForStmt(init: Statement, condition: Expression, stmt: Statement) extends Statement
case class LoopStmt(stmt: Statement) extends Statement
case class ReturnStmt(exp: Expression) extends Statement
case class CaseStmt(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement]) extends Statement
case class ExitStmt() extends Statement

trait CaseAlternative {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

case class SimpleCase(condition: Expression, stmt: Statement) extends CaseAlternative
case class RangeCase(min: Expression, max: Expression, stmt: Statement) extends CaseAlternative

trait AssignmentAlternative

case class VarAssignment(varName: String) extends AssignmentAlternative
case class ArrayAssignment(array: Expression, elem: Expression) extends AssignmentAlternative
case class RecordAssignment(record: Expression, atrib: String) extends AssignmentAlternative
case class PointerAssignment(pointerName: String) extends AssignmentAlternative


/**
 * User defined types.
 *
 * Users can declare either records or
 * array types.
 */
case class UserDefinedType(name: String, baseType: Type) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

/** The hierarchy for the Oberon supported types */
sealed trait Type {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

case object IntegerType extends Type
case object RealType extends Type
case object BooleanType extends Type
case object CharacterType extends Type
case object StringType extends Type
case object UndefinedType extends Type

case class RecordType(variables: List[VariableDeclaration]) extends Type
case class ArrayType(length: Int, variableType: Type) extends Type
case class PointerType(variableType: Type) extends Type

case class ReferenceToUserDefinedType(name: String) extends Type

/* useful for implementing the REPL feature */
trait REPL

case class REPLExpression(exp: Expression) extends REPL
case class REPLStatement(stmt: Statement) extends REPL
case class REPLVarDeclaration(declarations: List[VariableDeclaration]) extends REPL
case class REPLConstant(constants: Constant) extends REPL
case class REPLUserTypeDeclaration(userTypes: UserDefinedType) extends REPL

object ValueConversion {
  implicit def intValue2RealValue(intValue: IntValue): RealValue = RealValue(intValue.value)
  implicit def charValue2IntValue(charValue: CharValue): IntValue = IntValue(charValue.value)
}
