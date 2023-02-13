package br.unb.cic.oberon.ast

import br.unb.cic.oberon.visitor.OberonVisitor
import br.unb.cic.oberon.environment.Environment
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer


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
trait FormalArg{
  def accept(v: OberonVisitor): v.T = v.visit(this)
  def argumentType: Type
  def name: String
}
case class ParameterByValue(name: String, argumentType: Type) extends FormalArg
case class ParameterByReference(name: String, argumentType: Type) extends FormalArg


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

sealed trait Modular extends Number {
  def mod(that: Modular): Modular
}

case class IntValue(value: Int) extends Value with Modular {
  type T = Int
  def +(that: Number): Number = that match {
    case other: IntValue => IntValue(value + other.value)
    case other: CharValue => IntValue(value + other.value)
    case other: RealValue => RealValue(value + other.value)
  }

  def -(that: Number): Number = that match {
    case other: IntValue => IntValue(value - other.value)
    case other: CharValue => IntValue(value - other.value)
    case other: RealValue => RealValue(value - other.value)
  }

  def *(that: Number): Number = that match {
    case other: IntValue => IntValue(value * other.value)
    case other: CharValue => IntValue(value * other.value)
    case other: RealValue => RealValue(value * other.value)
  }

  def /(that: Number): Number = that match {
    case other: IntValue => IntValue(value / other.value)
    case other: CharValue => IntValue(value / other.value)
    case other: RealValue => RealValue(value / other.value)
  }

  val positiveMod = (x: Int, y:Int) => {val res = x % y; if (x < 0) res + y else res}

  def mod(that: Modular): Modular = that match{
    case other: IntValue => IntValue(positiveMod(value, other.value))
  }
}

case class RealValue(value: Double) extends Value with Number {
  type T = Double

  def +(that: Number): Number = that match {
    case other: IntValue => RealValue(value + other.value)
    case other: CharValue => RealValue(value + other.value)
    case other: RealValue => RealValue(value + other.value)
  }

  def -(that: Number): Number = that match {
    case other: IntValue => RealValue(value - other.value)
    case other: CharValue => RealValue(value - other.value)
    case other: RealValue => RealValue(value - other.value)
  }

  def *(that: Number): Number = that match {
    case other: IntValue => RealValue(value * other.value)
    case other: CharValue => RealValue(value * other.value)
    case other: RealValue => RealValue(value * other.value)
  }

  def /(that: Number): Number = that match {
    case other: IntValue => RealValue(value / other.value)
    case other: CharValue => RealValue(value / other.value)
    case other: RealValue => RealValue(value / other.value)
  }
}

case class CharValue(value: Char) extends Value with Modular {
  type T = Char
  def +(that: Number): Number = that match {
    case other: IntValue => IntValue(value + other.value)
    case other: IntValue => IntValue(value + other.value)
    case other: RealValue => RealValue(value + other.value)
  }

  def -(that: Number): Number = that match {
    case other: IntValue => IntValue(value - other.value)
    case other: IntValue => IntValue(value - other.value)
    case other: RealValue => RealValue(value - other.value)
  }

  def *(that: Number): Number = that match {
    case other: IntValue => IntValue(value * other.value)
    case other: IntValue => IntValue(value * other.value)
    case other: RealValue => RealValue(value * other.value)
  }

  def /(that: Number): Number = that match {
    case other: IntValue => IntValue(value / other.value)
    case other: IntValue => IntValue(value / other.value)
    case other: RealValue => RealValue(value / other.value)
  }

  val positiveMod = (x:Char, y:Int) => {val res = x % y; if (x < 0) res + y else res}

  def mod(that: Modular): Modular = that match{
    case other: IntValue => IntValue(positiveMod(value, other.value))
  }
}

case class StringValue(value: String) extends Value { type T = String }
case class BoolValue(value: Boolean) extends Value { type T = Boolean }
case object NullValue extends Expression
case class Location(loc: Int) extends Expression
case class Brackets(exp: Expression) extends Expression
case class ArrayValue(value: ListBuffer[Expression], arrayType: ArrayType) extends Value { type T = ListBuffer[Expression] }
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
case class ModExpression(left: Expression, right: Expression) extends Expression
case class NotExpression(exp: Expression) extends Expression

/* Statements */
trait Statement {
  val label = Statement.getLabel()
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

object Statement{
  var label = 0

  def getLabel() : Int = {
    label += 1
    label
  }

  def reset() : Unit = {
    label = 0
  }
}

case class AssignmentStmt(designator: Designator, exp: Expression) extends Statement
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
case class ForEachStmt(varName: String, exp: Expression, stmt: Statement) extends Statement
case class LoopStmt(stmt: Statement) extends Statement
case class ReturnStmt(exp: Expression) extends Statement
case class CaseStmt(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement]) extends Statement
case class ExitStmt() extends Statement
case class NewStmt(varName: String) extends Statement
case class MetaStmt(f: () => Statement) extends Statement
trait CaseAlternative {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

case class SimpleCase(condition: Expression, stmt: Statement) extends CaseAlternative
case class RangeCase(min: Expression, max: Expression, stmt: Statement) extends CaseAlternative

sealed trait Designator

case class VarAssignment(varName: String) extends Designator
case class ArrayAssignment(array: Expression, index: Expression) extends Designator
case class RecordAssignment(record: Expression, field: String) extends Designator
case class PointerAssignment(pointerName: String) extends Designator


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
abstract class Type(st : Option[Type]) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

/**
object Type {
  def is subType() {}
}


object Type {

  def isSubType(t1: Type, t2: Type): Boolean = (t1, t2) match {
    case (IntegerType, RealType) => true
    case (CharacterType, RealType) => true
    /**case (CharacterType, IntegerType) => true**/
    case _ => false 
  }

  def isSuperType(t1: Type, t2: Type): Boolean = {
    if !isSubType(t1, t2):
      if t1.st == t2.st:
        return true
      return false
  }
}
**/

object CastType {
  def subType(t1: Type, t2: Type): Boolean = (t1, t2) match {
    case (IntegerType, RealType) => true
    case (CharacterType, IntegerType) => true
    case (CharacterType, RealType) => true
    case _ => false 
  }

  def promote(t1: Type, t2: Type): Type = (t1, t2) match {
    // base cases (not allowing promotion of CurrentType to CurrentType)
    case (IntegerType, IntegerType) => NullType
    case (RealType, RealType) => NullType
    case (BooleanType, BooleanType) => NullType
    case (CharacterType, CharacterType) => NullType
    case (StringType, StringType) => NullType
    case (UndefinedType, UndefinedType) => NullType
    case (NullType, NullType) => NullType
    case (LocationType, LocationType) => NullType
    
    case (IntegerType, RealType) => RealType
    case (RealType, IntegerType) => RealType
    case (CharacterType, IntegerType) => IntegerType
    case (IntegerType, CharacterType) => IntegerType
    case (CharacterType, RealType) => RealType
    case (RealType, CharacterType) => RealType
    // todo
    case _ => UndefinedType
  }
}

case object Any extends Type(None)
case object UndefinedType extends Type(Some(Any))

case object NumberType extends Type(Some(Any))

case object IntegerType extends Type(Some(NumberType))
case object RealType extends Type(Some(NumberType))
/*case object BooleanType extends Type(Some(NumberType))    TODO: se permitir operacoes, precisa de um case class com Modular na linha ~199 e passar Bollean no TypeChecker*/
case object BooleanType extends Type(Some(Any))
case object CharacterType extends Type(Some(NumberType))

case object RefType extends Type(Some(Any))

case object StringType extends Type(Some(RefType))
case object NullType extends Type(Some(RefType))
case object LocationType extends Type(Some(RefType))
case class RecordType(variables: List[VariableDeclaration]) extends Type(Some(RefType))
case class ArrayType(length: Int, baseType: Type) extends Type(Some(RefType))
case class PointerType(variableType: Type) extends Type(Some(RefType))

case class ReferenceToUserDefinedType(name: String) extends Type(Some(RefType))

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
