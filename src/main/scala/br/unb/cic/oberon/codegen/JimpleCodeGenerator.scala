package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{
  Type => OberonType,
  Statement => OberonStmt,
  Expression => OberonExpression,
  IntValue => OberonIntValue,
  BoolValue => OberonBoolValue,
  RealValue => OberonRealValue,
  CharValue => OberonCharValue,
  StringValue => OberonStringValue,
  NullValue => OberonNullValue,
  _
}
import br.unb.cic.oberon.ir.jimple._
import br.unb.cic.oberon.tc.{ExpressionTypeVisitor, TypeChecker}

object JimpleCodeGenerator extends CodeGenerator[ClassDecl] {
  override def generateCode(module: OberonModule): ClassDecl = {
    ClassDecl(
      modifiers = List(PublicModifer),
      classType = TObject(module.name),
      superClass = TObject("java.lang.Object"),
      interfaces = List.empty[Type],
      fields = generateFields(module),
      methods = List(generateMainMethod(module))
    )
  }

  def generateMainMethod(module: OberonModule): Method = Method(
    modifiers = List(PublicModifer, StaticModifier, FinalModifier),
    returnType = TVoid,
    name = "main",
    formals = List(TArray(TString)),
    exceptions = List.empty[Type],
    body = generateMainMethodBody(module),
  )

  def generateMainMethodBody(module: OberonModule): MethodBody = DefaultMethodBody(
    localVariableDecls = List.empty[LocalVariableDeclaration],
    stmts = generateStmts(module.stmt),
    catchClauses = List.empty[CatchClause],
  )

  def generateStmts(oberonStmt: Option[OberonStmt]): List[Statement] = oberonStmt match {
    case Some(someStmt) => someStmt match {
      case SequenceStmt(stmts) => stmts.flatMap(stmt => generateStmts(Some(stmt)))
      case AssignmentStmt(designator, exp) => designator match {
        case VarAssignment(varName) => List(AssignStmt(LocalVariable(varName), jimpleExpression(exp)))
      }

    }
    case None => List.empty[Statement]
  }

  def generateFields(module: OberonModule) = generateConstants(module) ::: generateVariables(module)

  def generateConstants(module: OberonModule): List[Field] = {
    val visitor = new ExpressionTypeVisitor(new TypeChecker())

    module.constants.map(constant => Field(
      modifiers = List(PublicModifer, FinalModifier),
      fieldType = jimpleType(visitor.visitExpression(constant.exp), module),
      name = constant.name
    ))
  }

  def generateVariables(module: OberonModule): List[Field] = module.variables.map(variable => Field(
    modifiers = List(PublicModifer),
    fieldType = jimpleType(variable.variableType, module),
    name = variable.name
  ))

  def generateUserDefinedTypes(module: OberonModule): List[Type] =
    module.userTypes.map(userType => jimpleUserDefinedType(userType.name, module))

  def generateMethodSignatures(module: OberonModule): List[MethodSignature] = module.procedures.map(procedure => MethodSignature(
    className = module.name,
    returnType = jimpleType(procedure.returnType, module),
    methodName = procedure.name,
    formals = procedure.args.map(arg => jimpleType(arg.argumentType, module))
  ))

  def jimpleExpression(oberonExpression: OberonExpression): Expression = oberonExpression match {
    case OberonIntValue(value) => ImmediateExpression(ImmediateValue(IntValue(value)))
    case OberonBoolValue(value) => ImmediateExpression(ImmediateValue(BooleanValue(value)))
    case OberonRealValue(value) => ImmediateExpression(ImmediateValue(DoubleValue(value)))
    case OberonCharValue(value) => ImmediateExpression(ImmediateValue(StringValue(value.toString)))
    case OberonStringValue(value) => ImmediateExpression(ImmediateValue(StringValue(value)))
    case OberonNullValue => ImmediateExpression(ImmediateValue(NullValue))

    case Brackets(exp) => jimpleExpression(exp)
    case PointerAccessExpression(_) => throw new Exception("Pointers are not yet supported by Jimple code generation.")

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleType(oberonType: Option[OberonType], module: OberonModule): Type = oberonType match {
    case Some(someType) => jimpleType(someType, module)
    case None => TVoid
  }

  def jimpleType(oberonType: OberonType, module: OberonModule): Type = oberonType match {
    case IntegerType => TInteger
    case RealType => TFloat
    case BooleanType => TBoolean
    case CharacterType => TCharacter
    case StringType => TString
    case UndefinedType => TUnknown
    case NullType => TNull

    case ReferenceToUserDefinedType(name) => jimpleUserDefinedType(name, module)
    case PointerType(_) => throw new Exception("Pointers are not yet supported by Jimple code generation.")

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleUserDefinedType(name: String, module: OberonModule): Type =
    jimpleUserDefinedType(module.userTypes.find(userType => userType.name == name).get, module)

  def jimpleUserDefinedType(userType: UserDefinedType, module: OberonModule): Type = userType.baseType match {
    case RecordType(_) => TObject(userType.name)
    case ArrayType(_, baseType) => TArray(jimpleType(baseType, module))

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }
}