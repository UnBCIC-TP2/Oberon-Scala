package br.unb.cic.oberon.ir.jimple

sealed trait JimpleValue

case class IntValue(iv: Int) extends JimpleValue

case class LongValue(lv: Long) extends JimpleValue

case class FloatValue(fv: Float) extends JimpleValue

case class DoubleValue(fv: Double) extends JimpleValue

case class StringValue(sv: String) extends JimpleValue

case class BooleanValue(bl: Boolean) extends JimpleValue

case class MethodValue(returnType: JimpleType, formals: List[JimpleType])
    extends JimpleValue

case class ClassValue(name: String) extends JimpleValue

case class MethodHandle(methodSig: MethodSignature) extends JimpleValue

case class FieldHandle(fieldSig: FieldSignature) extends JimpleValue

case object NullValue extends JimpleValue

sealed trait JimpleImmediate

case class Local(localName: String) extends JimpleImmediate

case class ImmediateValue(v: JimpleValue) extends JimpleImmediate

case object CaughtException extends JimpleImmediate

sealed trait JimpleClassOrInterface

case class ClassDeclaration(
    modifiers: List[JimpleModifier],
    classType: JimpleType,
    superClass: JimpleType,
    interfaces: List[JimpleType],
    fields: List[Field],
    methods: List[Method]
) extends JimpleClassOrInterface

case class InterfaceDeclaration(
    modifiers: List[JimpleModifier],
    interfaceType: JimpleType,
    interfaces: List[JimpleType],
    fields: List[Field],
    methods: List[Method]
) extends JimpleClassOrInterface

case class Field(
    modifiers: List[JimpleModifier],
    fieldType: JimpleType,
    name: String
)

case class Method(
    modifiers: List[JimpleModifier],
    returnType: JimpleType,
    name: String,
    formals: List[JimpleType],
    exceptions: List[JimpleType],
    body: JimpleMethodBody
)

sealed trait JimpleMethodBody

case class DefaultMethodBody(
    localVariableDecls: List[LocalVariableDeclaration],
    stmts: List[JimpleStatement],
    catchClauses: List[CatchClause]
) extends JimpleMethodBody

case object SignatureOnlyMethodBody extends JimpleMethodBody

case class LocalVariableDeclaration(varType: JimpleType, local: String)

case class CatchClause(
    exception: JimpleType,
    from: String,
    to: String,
    _with: String
)

sealed trait JimpleVariable

case class LocalVariable(local: String) extends JimpleVariable

case class ArrayRef(reference: String, idx: JimpleImmediate)
    extends JimpleVariable

case class FieldRef(reference: String, field: FieldSignature)
    extends JimpleVariable

case class StaticField(field: FieldSignature) extends JimpleVariable

sealed trait JimpleStatement

case class LabelStmt(label: String) extends JimpleStatement

case object BreakpointStmt extends JimpleStatement

case class EnterMonitorStmt(immediate: JimpleImmediate) extends JimpleStatement

case class ExitMonitorStmt(immediate: JimpleImmediate) extends JimpleStatement

case class TableSwitchStmt(
    immediate: JimpleImmediate,
    min: Int,
    max: Int,
    stmts: List[JimpleCaseStmt]
) extends JimpleStatement

case class LookupSwitchStmt(
    immediate: JimpleImmediate,
    stmts: List[JimpleCaseStmt]
) extends JimpleStatement

case class IdentityStmt(local: String, identifier: String, idType: JimpleType)
    extends JimpleStatement

case class IdentityNoTypeStmt(local: String, identifier: String)
    extends JimpleStatement

case class AssignStmt(variable: JimpleVariable, expression: JimpleExpression)
    extends JimpleStatement

case class IfStmt(exp: JimpleExpression, target: String) extends JimpleStatement

case object ReturnEmptyStmt extends JimpleStatement

case class ReturnStmt(immediate: JimpleImmediate) extends JimpleStatement

case class ThrowStmt(immediate: JimpleImmediate) extends JimpleStatement

case class InvokeStmt(invokeExp: JimpleInvoke) extends JimpleStatement

case class GotoStmt(target: String) extends JimpleStatement

case object NopStmt extends JimpleStatement

sealed trait JimpleCaseStmt

case class CaseOption(option: Int, targetStmt: String) extends JimpleCaseStmt

case class DefaultOption(targetStmt: String) extends JimpleCaseStmt

sealed trait JimpleExpression

case class NewInstanceExpression(instanceType: JimpleType)
    extends JimpleExpression

case class NewArrayExpression(
    baseType: JimpleType,
    dims: List[JimpleArrayDescriptor]
) extends JimpleExpression

case class CastExpression(toType: JimpleType, immediate: JimpleImmediate)
    extends JimpleExpression

case class InstanceOfExpression(
    baseType: JimpleType,
    immediate: JimpleImmediate
) extends JimpleExpression

case class InvokeExpression(invokeExp: JimpleInvoke) extends JimpleExpression

case class ArraySubscriptExpression(name: String, immediate: JimpleImmediate)
    extends JimpleExpression

case class StringSubscriptExpression(string: String, immediate: JimpleImmediate)
    extends JimpleExpression

case class LocalFieldRefExpression(
    local: String,
    className: String,
    fieldType: JimpleType,
    fieldName: String
) extends JimpleExpression

case class FieldRefExpression(
    className: String,
    fieldType: JimpleType,
    fieldName: String
) extends JimpleExpression

case class AndExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class OrExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class XorExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class RemainderExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class IsNullExpression(immediate: JimpleImmediate) extends JimpleExpression

case class IsNotNullExpression(immediate: JimpleImmediate)
    extends JimpleExpression

case class CmpExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class CmpGExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class CmpLExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class CmpEqExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class CmpNeExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class CmpGtExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class CmpGeExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class CmpLtExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class CmpLeExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class ShlExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class ShrExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class UShrExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class PlusExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class MinusExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class MultExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class DivExpression(lhs: JimpleImmediate, rhs: JimpleImmediate)
    extends JimpleExpression

case class LengthOfExpression(immediate: JimpleImmediate)
    extends JimpleExpression

case class NegExpression(immediate: JimpleImmediate) extends JimpleExpression

case class ImmediateExpression(immediate: JimpleImmediate)
    extends JimpleExpression

sealed trait JimpleArrayDescriptor

case class FixedSize(size: Int) extends JimpleArrayDescriptor

case object VariableSize extends JimpleArrayDescriptor

sealed trait JimpleInvoke

case class SpecialInvoke(
    local: String,
    sig: MethodSignature,
    args: List[JimpleImmediate]
) extends JimpleInvoke

case class VirtualInvoke(
    local: String,
    sig: MethodSignature,
    args: List[JimpleImmediate]
) extends JimpleInvoke

case class InterfaceInvoke(
    local: String,
    sig: MethodSignature,
    args: List[JimpleImmediate]
) extends JimpleInvoke

case class StaticMethodInvoke(sig: MethodSignature, args: List[JimpleImmediate])
    extends JimpleInvoke

case class DynamicInvoke(
    bsmSig: MethodSignature,
    bsmArgs: List[JimpleImmediate],
    sig: MethodSignature,
    args: List[JimpleImmediate]
) extends JimpleInvoke

case class FieldSignature(
    className: String,
    fieldType: JimpleType,
    fieldName: String
)

case class MethodSignature(
    className: String,
    returnType: JimpleType,
    methodName: String,
    formals: List[JimpleType]
)

case class UnnamedMethodSignature(
    returnType: JimpleType,
    formals: List[JimpleType]
)

sealed trait JimpleModifier

case object PublicModifer extends JimpleModifier

case object ProtectedModifier extends JimpleModifier

case object PrivateModifier extends JimpleModifier

case object AbstractModifier extends JimpleModifier

case object StaticModifier extends JimpleModifier

case object FinalModifier extends JimpleModifier

case object SynchronizedModifier extends JimpleModifier

case object NativeModifier extends JimpleModifier

case object StrictfpModifier extends JimpleModifier

case object TransientModifier extends JimpleModifier

case object VolatileModifier extends JimpleModifier

case object EnumModifier extends JimpleModifier

case object AnnotationModifier extends JimpleModifier

case object SyntheticModifier extends JimpleModifier

sealed trait JimpleType

case object TByte extends JimpleType

case object TBoolean extends JimpleType

case object TShort extends JimpleType

case object TCharacter extends JimpleType

case object TInteger extends JimpleType

case object TFloat extends JimpleType

case object TDouble extends JimpleType

case object TLong extends JimpleType

case class TObject(name: String) extends JimpleType

case class TArray(baseType: JimpleType) extends JimpleType

case object TVoid extends JimpleType

case object TString extends JimpleType

case object TMethodValue extends JimpleType

case object TClassValue extends JimpleType

case object TMethodHandle extends JimpleType

case object TFieldHandle extends JimpleType

case object TNull extends JimpleType

case object TUnknown extends JimpleType

sealed trait JimpleStmtContext

case object NoContext extends JimpleStmtContext

case class DefaultStmtContext(
    stmtId: Int,
    methodSignature: String,
    sourceCodeLine: Int
) extends JimpleStmtContext
