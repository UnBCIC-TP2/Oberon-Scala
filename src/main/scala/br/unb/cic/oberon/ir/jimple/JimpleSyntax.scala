package br.unb.cic.oberon.ir.jimple

sealed trait JimpleValue

case class JimpleIntValue(iv: Int) extends JimpleValue

case class JimpleLongValue(lv: Long) extends JimpleValue

case class JimpleFloatValue(fv: Float) extends JimpleValue

case class JimpleDoubleValue(fv: Double) extends JimpleValue

case class JimpleStringValue(sv: String) extends JimpleValue

case class JimpleBooleanValue(bl: Boolean) extends JimpleValue

case class JimpleMethodValue(returnType: JimpleType, formals: List[JimpleType]) extends JimpleValue

case class JimpleClassValue(name: String) extends JimpleValue

case class JimpleMethodHandle(methodSig: JimpleMethodSignature) extends JimpleValue

case class JimpleFieldHandle(fieldSig: JimpleFieldSignature) extends JimpleValue

case object JimpleNullValue extends JimpleValue

sealed trait JimpleImmediate

case class Local(localName: String) extends JimpleImmediate

case class ImmediateValue(v: JimpleValue) extends JimpleImmediate

case object CaughtException extends JimpleImmediate

sealed trait JimpleClassOrInterface

case class ClassDecl(modifiers: List[JimpleModifier],
                     classType: JimpleType,
                     superClass: JimpleType,
                     interfaces: List[JimpleType],
                     fields: List[JimpleField],
                     methods: List[JimpleMethod]) extends JimpleClassOrInterface

case class InterfaceDecl(modifiers: List[JimpleModifier],
                         interfaceType: JimpleType,
                         interfaces: List[JimpleType],
                         fields: List[JimpleField],
                         methods: List[JimpleMethod]) extends JimpleClassOrInterface

case class JimpleField(modifiers: List[JimpleModifier], fieldType: JimpleType, name: String)

case class JimpleMethod(modifiers: List[JimpleModifier],
                        returnType: JimpleType, name: String,
                        formals: List[JimpleType],
                        exceptions: List[JimpleType],
                        body: JimpleMethodBody)

sealed trait JimpleMethodBody

case class DefaultMethodBody(localVariableDecls: List[JimpleLocalVariableDeclaration], stmts: List[JimpleStatement], catchClauses: List[JimpleCatchClause]) extends JimpleMethodBody

case object SignatureOnlyMethodBody extends JimpleMethodBody

case class JimpleLocalVariableDeclaration(varType: JimpleType, local: String)

case class JimpleCatchClause(exception: JimpleType, from: String, to: String, _with: String)

sealed trait JimpleVariable

case class LocalVariable(local: String) extends JimpleVariable

case class ArrayRef(reference: String, idx: JimpleImmediate) extends JimpleVariable

case class FieldRef(reference: String, field: JimpleFieldSignature) extends JimpleVariable

case class StaticField(field: JimpleFieldSignature) extends JimpleVariable

sealed trait JimpleStatement

case class JimpleLabelStmt(label: String) extends JimpleStatement

case object JimpleBreakpointStmt extends JimpleStatement

case class JimpleEnterMonitorStmt(immediate: JimpleImmediate) extends JimpleStatement

case class JimpleExitMonitorStmt(immediate: JimpleImmediate) extends JimpleStatement

case class JimpleTableSwitchStmt(immediate: JimpleImmediate, min: Int, max: Int, stmts: List[JimpleCaseStmt]) extends JimpleStatement

case class JimpleLookupSwitchStmt(immediate: JimpleImmediate, stmts: List[JimpleCaseStmt]) extends JimpleStatement

case class JimpleIdentityStmt(local: String, identifier: String, idType: JimpleType) extends JimpleStatement

case class JimpleIdentityNoTypeStmt(local: String, identifier: String) extends JimpleStatement

case class JimpleAssignStmt(variable: JimpleVariable, expression: JimpleExpression) extends JimpleStatement

case class JimpleIfStmt(exp: JimpleExpression, target: String) extends JimpleStatement

case object JimpleReturnEmptyStmt extends JimpleStatement

case class JimpleReturnStmt(immediate: JimpleImmediate) extends JimpleStatement

case class JimpleThrowStmt(immediate: JimpleImmediate) extends JimpleStatement

case class JimpleInvokeStmt(invokeExp: JimpleInvoke) extends JimpleStatement

case class JimpleGotoStmt(target: String) extends JimpleStatement

case object JimpleNopStmt extends JimpleStatement

sealed trait JimpleCaseStmt

case class CaseOption(option: Int, targetStmt: String) extends JimpleCaseStmt

case class DefaultOption(targetStmt: String) extends JimpleCaseStmt

sealed trait JimpleExpression

case class JimpleNewInstanceExpression(instanceType: JimpleType) extends JimpleExpression

case class JimpleNewArrayExpression(baseType: JimpleType, dims: List[JimpleArrayDescriptor]) extends JimpleExpression

case class JimpleCastExpression(toType: JimpleType, immediate: JimpleImmediate) extends JimpleExpression

case class JimpleInstanceOfExpression(baseType: JimpleType, immediate: JimpleImmediate) extends JimpleExpression

case class JimpleInvokeExpression(invokeExp: JimpleInvoke) extends JimpleExpression

case class JimpleArraySubscriptExpression(name: String, immediate: JimpleImmediate) extends JimpleExpression

case class JimpleStringSubscriptExpression(string: String, immediate: JimpleImmediate) extends JimpleExpression

case class JimpleLocalFieldRefExpression(local: String, className: String, fieldType: JimpleType, fieldName: String) extends JimpleExpression

case class JimpleFieldRefExpression(className: String, fieldType: JimpleType, fieldName: String) extends JimpleExpression

case class JimpleAndExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleOrExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleXorExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleReminderExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleIsNullExpression(immediate: JimpleImmediate) extends JimpleExpression

case class JimpleIsNotNullExpression(immediate: JimpleImmediate) extends JimpleExpression

case class JimpleCmpExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleCmpGExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleCmpLExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleCmpEqExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleCmpNeExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleCmpGtExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleCmpGeExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleCmpLtExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleCmpLeExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleShlExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleShrExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleUShrExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimplePlusExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleMinusExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleMultExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleDivExpression(lhs: JimpleImmediate, rhs: JimpleImmediate) extends JimpleExpression

case class JimpleLengthOfExpression(immediate: JimpleImmediate) extends JimpleExpression

case class JimpleNegExpression(immediate: JimpleImmediate) extends JimpleExpression

case class JimpleImmediateExpression(immediate: JimpleImmediate) extends JimpleExpression

sealed trait JimpleArrayDescriptor

case class FixedSize(size: Int) extends JimpleArrayDescriptor

case object VariableSize extends JimpleArrayDescriptor

sealed trait JimpleInvoke

case class SpecialInvoke(local: String, sig: JimpleMethodSignature, args: List[JimpleImmediate]) extends JimpleInvoke

case class VirtualInvoke(local: String, sig: JimpleMethodSignature, args: List[JimpleImmediate]) extends JimpleInvoke

case class InterfaceInvoke(local: String, sig: JimpleMethodSignature, args: List[JimpleImmediate]) extends JimpleInvoke

case class StaticMethodInvoke(sig: JimpleMethodSignature, args: List[JimpleImmediate]) extends JimpleInvoke

case class DynamicInvoke(bsmSig: JimpleMethodSignature, bsmArgs: List[JimpleImmediate], sig: JimpleMethodSignature, args: List[JimpleImmediate]) extends JimpleInvoke

case class JimpleFieldSignature(className: String, fieldType: JimpleType, fieldName: String)

case class JimpleMethodSignature(className: String, returnType: JimpleType, methodName: String, formals: List[JimpleType])

case class JimpleUnnamedMethodSignature(returnType: JimpleType, formals: List[JimpleType])

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

case class DefaultStmtContext(stmtId: Int, methodSignature: String, sourceCodeLine: Int) extends JimpleStmtContext
