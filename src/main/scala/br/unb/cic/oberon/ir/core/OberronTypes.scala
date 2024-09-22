package br.unb.cic.oberon.ir.common

import br.unb.cic.oberon.visitor.OberonVisitor
import scala.collection.mutable.ListBuffer

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
case object NullType extends Type
case object LocationType extends Type

case class RecordType(variables: List[VariableDeclaration]) extends Type
case class ArrayType(length: Int, baseType: Type) extends Type
case class PointerType(variableType: Type) extends Type
case class LambdaType(argsTypes: List[Type], returnType: Type) extends Type

/* Variable declaration definition */
case class VariableDeclaration(name: String, variableType: Type) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

case class ReferenceToUserDefinedType(name: String) extends Type