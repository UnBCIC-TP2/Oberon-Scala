package br.unb.cic.oberon.codegen

sealed trait Value

case class IntValue(iv: Int) extends Value
case class LongValue(lv: Long) extends Value
case class FloatValue(fv: Float) extends Value
case class DoubleValue(fv: Double) extends Value
case class StringValue(sv: String) extends Value
case class BooleanValue(bl: Boolean) extends Value
case class MethodValue(returnType: Type, formals: List[Type]) extends Value
case class ClassValue(name: String) extends Value
case class MethodHandle(methodSig: MethodSignature) extends Value
case class FieldHandle(fieldSig: FieldSignature) extends Value
case object NullValue extends Value

sealed trait Immediate

case class Local(localName: String) extends Immediate
case class ImmediateValue(v: Value) extends Immediate
case object CaughtException extends Immediate

sealed trait ClassOrInterfaceDeclaration

case class ClassDecl(modifiers: List[Modifier],
                     classType: Type,
                     superClass: Type,
                     interfaces: List[Type],
                     fields: List[Field],
                     methods: List[Method]) extends ClassOrInterfaceDeclaration

case class InterfaceDecl(modifiers: List[Modifier],
                         interfaceType: Type,
                         interfaces: List[Type],
                         fields: List[Field],
                         methods: List[Method]) extends ClassOrInterfaceDeclaration

case class Field(modifiers: List[Modifier], fieldType: Type, name: String)

case class Method(modifiers: List[Modifier],
                  returnType: Type, name: String,
                  formals: List[Type],
                  exceptions: List[Type],
                  body: MethodBody)

sealed trait MethodBody

case class StandardMethodBody(localVariableDecls: List[LocalVariableDeclaration],
                              stmts: List[Statement],
                              catchClauses: List[CatchClause]) extends MethodBody

case object SignatureOnly extends MethodBody

case class LocalVariableDeclaration(varType: Type, local: String)

case class CatchClause(exception: Type, from: String, to: String, _with: String)

sealed trait Variable

case class LocalVariable(local: String) extends Variable
case class ArrayRef(reference: String, idx: Immediate) extends Variable
case class FieldRef(reference: String, field: FieldSignature) extends Variable
case class StaticField(field: FieldSignature) extends Variable

sealed trait Statement

case class LabelStmt(label: String) extends Statement
case object BreakpointStmt extends Statement
case class EnterMonitorStmt(immediate: Immediate) extends Statement
case class ExitMonitorStmt(immediate: Immediate) extends Statement
case class TableSwitchStmt(immediate: Immediate, min: Int, max: Int, stmts: List[CaseStmt]) extends Statement
case class LookupSwitchStmt(immediate: Immediate, stmts: List[CaseStmt]) extends Statement
case class IdentityStmt(local: String, identifier: String, idType: Type) extends Statement
case class IdentityNoTypeStmt(local: String, identifier: String) extends Statement
case class AssignStmt(variable: Variable, expression: Expression) extends Statement
case class IfStmt(exp: Expression, target: String) extends Statement
case object RetEmptyStmt extends Statement
case class RetStmt(immediate: Immediate) extends Statement
case object ReturnEmptyStmt extends Statement
case class ReturnStmt(immediate: Immediate) extends Statement
case class ThrowStmt(immediate: Immediate) extends Statement
case class InvokeStmt(invokeExp: Invoke) extends Statement
case class GotoStmt(target: String) extends Statement
case object NopStmt extends Statement

sealed trait CaseStmt

case class CaseOption(option: Int, targetStmt: String) extends CaseStmt
case class DefaultOption(targetStmt: String) extends CaseStmt

sealed trait Expression

case class NewInstanceExpression(instanceType: Type) extends Expression
case class NewArrayExpression(baseType: Type, dims: List[ArrayDescriptor]) extends Expression
case class CastExpression(toType: Type, immediate: Immediate) extends Expression
case class InstanceOfExpression(baseType: Type, immediate: Immediate) extends Expression
case class InvokeExpression(invokeExp: Invoke) extends Expression
case class ArraySubscriptExpression(name: String, immediate: Immediate) extends Expression
case class StringSubscriptExpression(string: String, immediate: Immediate) extends Expression
case class LocalFieldRefExpression(local: String, className: String, fieldType: Type, fieldName: String) extends Expression
case class FieldRefExpression(className: String, fieldType: Type, fieldName: String) extends Expression
case class AndExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class OrExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class XorExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class ReminderExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class IsNullExpression(immediate: Immediate) extends Expression
case class IsNotNullExpression(immediate: Immediate) extends Expression
case class CmpExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class CmpGExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class CmpLExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class CmpEqExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class CmpNeExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class CmpGtExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class CmpGeExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class CmpLtExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class CmpLeExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class ShlExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class ShrExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class UShrExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class PlusExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class MinusExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class MultExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class DivExpression(lhs: Immediate, rhs: Immediate) extends Expression
case class LengthOfExpression(immediate: Immediate) extends Expression
case class NegExpression(immediate: Immediate) extends Expression
case class ImmediateExpression(immediate: Immediate) extends Expression

sealed trait ArrayDescriptor

case class FixedSize(size: Int) extends ArrayDescriptor
case object VariableSize extends ArrayDescriptor

sealed trait Invoke

case class SpecialInvoke(local: String, sig: MethodSignature, args: List[Immediate]) extends Invoke
case class VirtualInvoke(local: String, sig: MethodSignature, args: List[Immediate]) extends Invoke
case class InterfaceInvoke(local: String, sig: MethodSignature, args: List[Immediate]) extends Invoke
case class StaticMethodInvoke(sig: MethodSignature, args: List[Immediate]) extends Invoke
case class DynamicInvoke(bsmSig: MethodSignature, bsmArgs: List[Immediate], sig: MethodSignature, args: List[Immediate]) extends Invoke

case class FieldSignature(className: String, fieldType: Type, fieldName: String)

case class MethodSignature(className: String, returnType: Type, methodName: String, formals: List[Type])

case class UnnamedMethodSignature(returnType: Type, formals: List[Type])

sealed trait Modifier

case object PublicModifer extends Modifier
case object ProtectedModifier extends Modifier
case object PrivateModifier extends Modifier
case object AbstractModifier extends Modifier
case object StaticModifier extends Modifier
case object FinalModifier extends Modifier
case object SynchronizedModifier extends Modifier
case object NativeModifier extends Modifier
case object StrictfpModifier extends Modifier
case object TransientModifier extends Modifier
case object VolatileModifier extends Modifier
case object EnumModifier extends Modifier
case object AnnotationModifier extends Modifier
case object SyntheticModifier extends Modifier

sealed trait Type

case object TByte extends Type
case object TBoolean extends Type
case object TShort extends Type
case object TCharacter extends Type
case object TInteger extends Type
case object TFloat extends Type
case object TDouble extends Type
case object TLong extends Type
case class TObject(name: String) extends Type
case class TArray(baseType: Type) extends Type
case object TVoid extends Type
case object TString extends Type
case object TMethodValue extends Type
case object TClassValue extends Type
case object TMethodHandle extends Type
case object TFieldHandle extends Type
case object TNull extends Type
case object TUnknown extends Type

sealed trait StmtContext

case object NoContext extends StmtContext
case class DefaultStmtContext(stmtId: Int, methodSignature: String, sourceCodeLine: Int) extends StmtContext
