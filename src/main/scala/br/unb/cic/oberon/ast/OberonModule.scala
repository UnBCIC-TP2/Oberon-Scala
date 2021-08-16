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
  def accept(v: OberonVisitor): Unit =
    v.visit(this)
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
  def accept(v: OberonVisitor) = v.visit(this)
}

/* formal argument definition */
case class FormalArg(name: String, argumentType: Type) {
  def accept(v: OberonVisitor) = v.visit(this)
}

/* Imports */
case class Import(name: String){
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

/*
  Abstract class to define a primitive type, and also defines numeric conversion methods
*/
abstract class Primitive[T](val value: T) extends Expression
{
  def toInt(): Int
  def toFloat(): Float
  def toLong(): Long
  def toShort(): Short
  def toDouble(): Double
}

case class IntValue(v: Int) extends Primitive[Int](v){
  def toInt(): Int = v.toInt
  def toFloat(): Float = v.toFloat
  def toLong(): Long = v.toLong
  def toShort(): Short = v.toShort
  def toDouble(): Double = v.toDouble
}

case class RealValue(v: Float) extends Primitive[Float](v) {
  def toInt(): Int = v.toInt
  def toFloat(): Float = v.toFloat
  def toLong(): Long = v.toLong
  def toShort(): Short = v.toShort
  def toDouble(): Double = v.toDouble
}

case class ShortValue(v: Short) extends Primitive[Short](v) {
  def toInt(): Int = v.toInt
  def toFloat(): Float = v.toFloat
  def toLong(): Long = v.toLong
  def toShort(): Short = v.toShort
  def toDouble(): Double = v.toDouble
}

case class LongValue(v: Long) extends Primitive[Long](v) {
  def toInt(): Int = v.toInt
  def toFloat(): Float = v.toFloat
  def toLong(): Long = v.toLong
  def toShort(): Short = v.toShort
  def toDouble(): Double = v.toDouble
}

case class LongRealValue(v: Double) extends Primitive[Double](v) {
  def toInt(): Int = v.toInt
  def toFloat(): Float = v.toFloat
  def toLong(): Long = v.toLong
  def toShort(): Short = v.toShort
  def toDouble(): Double = v.toDouble
}

case class CharValue(v: Char) extends Primitive[Char](v){
  def toInt(): Int = v.toInt
  def toFloat(): Float = v.toFloat
  def toLong(): Long = v.toLong
  def toShort(): Short = v.toShort
  def toDouble(): Double = v.toDouble
}

case class Brackets(exp: Expression) extends Expression
case class BoolValue(v: Boolean) extends Value[Boolean](v)
case class ArrayValue(v: List[Expression]) extends Value[List[Expression]](v)
case class ArraySubscript(arrayBase: Expression, index: Expression) extends Expression
case class Undef() extends Expression
case class FieldAccessExpression(exp: Expression, name: String) extends Expression
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
  def accept(v: OberonVisitor) = v.visit(this)
}

case class SimpleCase(condition: Expression, stmt: Statement) extends CaseAlternative
case class RangeCase(min: Expression, max: Expression, stmt: Statement) extends CaseAlternative

trait AssignmentAlternative

case class VarAssignment(varName: String) extends AssignmentAlternative
case class ArrayAssignment(array: Expression, elem: Expression) extends AssignmentAlternative
case class RecordAssignment(record: Expression, atrib: String) extends AssignmentAlternative

/* Types */
trait Type {
  def accept(v: OberonVisitor) = v.visit(this)
}

case object IntegerType extends Type
case object RealType extends Type
case object ShortType extends Type
case object LongType extends Type
case object LongRealType extends Type
case object BooleanType extends Type
case object CharacterType extends Type
case object UndefinedType extends Type

case class ReferenceToUserDefinedType(name: String) extends Type

trait UserDefinedType{
  def accept(v: OberonVisitor) = v.visit(this)
}

case class RecordType(name: String, variables: List[VariableDeclaration]) extends UserDefinedType
case class ArrayType(name: String, length: Int, variableType: Type) extends UserDefinedType

/* useful for implementing the REPL feature */
trait REPL

case class REPLExpression(exp: Expression) extends REPL
case class REPLStatement(stmt: Statement) extends REPL
case class REPLVarDeclaration(declarations: List[VariableDeclaration]) extends REPL
case class REPLConstant(constants: Constant) extends REPL
case class REPLUserTypeDeclaration(userTypes: UserDefinedType) extends REPL
