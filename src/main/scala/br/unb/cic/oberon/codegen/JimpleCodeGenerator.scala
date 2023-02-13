package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.ir.jimple._
import br.unb.cic.oberon.tc.{ExpressionTypeVisitor, TypeChecker}

import scala.collection.mutable.ListBuffer

object JimpleCodeGenerator extends CodeGenerator[ClassDecl] {
  override def generateCode(module: OberonModule): ClassDecl = {
    ClassDecl(
      modifiers = List(PublicModifer),
      classType = TObject(module.name),
      superClass = TObject("java.lang.Object"),
      interfaces = List.empty[JimpleType],
      fields = generateFields(module),
      methods = List(generateMainMethod(module))
    )
  }

  def generateMainMethod(module: OberonModule): JimpleMethod = JimpleMethod(
    modifiers = List(PublicModifer, StaticModifier, FinalModifier),
    returnType = TVoid,
    name = "main",
    formals = List(TArray(TString)),
    exceptions = List.empty[JimpleType],
    body = generateMainMethodBody(module),
  )

  def generateMainMethodBody(module: OberonModule): JimpleMethodBody = DefaultMethodBody(
    localVariableDecls = List.empty[JimpleLocalVariableDeclaration],
    stmts = generateStmt(module.stmt),
    catchClauses = List.empty[JimpleCatchClause],
  )

  def generateStmt(oberonStmt: Statement): List[JimpleStatement] = generateStmt(Some(oberonStmt))

  def generateStmt(oberonStmt: Option[Statement]): List[JimpleStatement] = oberonStmt match {
    case Some(someStmt) => someStmt match {
      case SequenceStmt(stmts) => stmts.flatMap(stmt => generateStmt(Some(stmt)))
      case AssignmentStmt(designator, exp) => designator match {
        case VarAssignment(varName) => List(JimpleAssignStmt(LocalVariable(varName), generateExpression(exp)))
      }
//      case IfElseStmt(condition, thenStmt, elseStmt) => generateIfStmt(generateExpression(condition), generateStmt(thenStmt), generateStmt(elseStmt))
//      case WhileStmt(condition, stmt) => generateWhileStmt(generateExpression(condition), generateStmt(stmt))
    }
    case None => List.empty[JimpleStatement]
  }

  def generateIfStmt(condition: JimpleExpression, thenStmts: List[JimpleStatement], elseStmts: List[JimpleStatement], labelIndex: Int): List[JimpleStatement] = {
    val buffer = ListBuffer[JimpleStatement]()
    val ifLabel = s"label${labelIndex}"
    val endIfLabel = s"label${labelIndex + 1}"

    buffer += JimpleIfStmt(condition, ifLabel)
    buffer ++= elseStmts
    buffer += JimpleGotoStmt(endIfLabel)
    buffer += JimpleLabelStmt(ifLabel)
    buffer ++= thenStmts
    buffer += JimpleLabelStmt(endIfLabel)

    buffer.result()
  }

  def generateWhileStmt(condition: JimpleExpression, stmts: List[JimpleStatement], labelIndex: Int): List[JimpleStatement] = {
    val buffer = ListBuffer[JimpleStatement]()
    val whileLabel = s"label${labelIndex}"
    val endWhileLabel = s"label${labelIndex + 1}"

    buffer += JimpleLabelStmt(whileLabel)
    buffer += JimpleIfStmt(condition, endWhileLabel)
    buffer ++= stmts
    buffer += JimpleGotoStmt(whileLabel)
    buffer += JimpleLabelStmt(endWhileLabel)

    buffer.result()
  }

  def generateFields(module: OberonModule) = generateConstants(module) ::: generateVariables(module)

  def generateConstants(module: OberonModule): List[JimpleField] = {
    val visitor = new ExpressionTypeVisitor(new TypeChecker())

    module.constants.map(constant => JimpleField(
      modifiers = List(PublicModifer, FinalModifier),
      fieldType = jimpleType(visitor.visitExpression(constant.exp), module),
      name = constant.name
    ))
  }

  def generateVariables(module: OberonModule): List[JimpleField] = module.variables.map(variable => JimpleField(
    modifiers = List(PublicModifer),
    fieldType = jimpleType(variable.variableType, module),
    name = variable.name
  ))

  def generateUserDefinedTypes(module: OberonModule): List[JimpleType] =
    module.userTypes.map(userType => jimpleUserDefinedType(userType.name, module))

  def generateMethodSignatures(module: OberonModule): List[JimpleMethodSignature] = module.procedures.map(procedure => JimpleMethodSignature(
    className = module.name,
    returnType = jimpleType(procedure.returnType, module),
    methodName = procedure.name,
    formals = procedure.args.map(arg => jimpleType(arg.argumentType, module))
  ))

  def generateExpression(oberonExpression: Expression): JimpleExpression = oberonExpression match {
    case value: Value => JimpleImmediateExpression(ImmediateValue(jimpleValue(value)))
    case Brackets(exp) => generateExpression(exp)
    case PointerAccessExpression(_) => throw new Exception("Pointers are not yet supported by Jimple code generation.")

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleValue(oberonValue: Value): JimpleValue = oberonValue match {
    case IntValue(int) => JimpleIntValue(int)
    case BoolValue(bool) => JimpleBooleanValue(bool)
    case RealValue(real) => JimpleDoubleValue(real)
    case CharValue(char) => JimpleStringValue(char.toString)
    case StringValue(string) => JimpleStringValue(string)
    case NullValue => JimpleNullValue

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleType(oberonType: Option[Type], module: OberonModule): JimpleType = oberonType match {
    case Some(someType) => jimpleType(someType, module)
    case None => TVoid
  }

  def jimpleType(oberonType: Type, module: OberonModule): JimpleType = oberonType match {
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

  def jimpleUserDefinedType(name: String, module: OberonModule): JimpleType =
    jimpleUserDefinedType(module.userTypes.find(userType => userType.name == name).get, module)

  def jimpleUserDefinedType(userType: UserDefinedType, module: OberonModule): JimpleType = userType.baseType match {
    case RecordType(_) => TObject(userType.name)
    case ArrayType(_, baseType) => TArray(jimpleType(baseType, module))

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }
}