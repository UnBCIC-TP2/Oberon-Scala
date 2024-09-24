package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{
  IntValue => OberonIntValue,
  BoolValue => OberonBoolValue,
  RealValue => OberonRealValue,
  CharValue => OberonCharValue,
  StringValue => OberonStringValue,
  NullValue => OberonNullValue,
  AndExpression => OberonAndExpression,
  OrExpression => OberonOrExpression,
  NotExpression => OberonNotExpression,
  AddExpression => OberonAddExpression,
  SubExpression => OberonSubExpression,
  MultExpression => OberonMultExpression,
  DivExpression => OberonDivExpression,
  ModExpression => OberonModExpression,
  _
}
import br.unb.cic.oberon.ir.jimple._
import br.unb.cic.oberon.tc.{TypeChecker}
import br.unb.cic.oberon.environment.Environment

import scala.collection.mutable.ListBuffer

object JimpleCodeGenerator extends CodeGenerator[ClassDeclaration] {
  override def generateCode(module: OberonModule): ClassDeclaration = {
    val fields = generateFields(module)
    val methodSignatures = generateMethodSignatures(module)
    val methods = generateMethods(module, fields, methodSignatures)

    ClassDeclaration(
      modifiers = List(PublicModifer),
      classType = TObject(module.name),
      superClass = TObject("java.lang.Object"),
      interfaces = List.empty[JimpleType],
      fields = fields,
      methods = methods
    )
  }

  def generateConstants(module: OberonModule): List[Field] = {
    val env = new Environment[Type]()

    module.constants.map(constant =>
      Field(
        modifiers = List(PublicModifer, StaticModifier, FinalModifier),
        fieldType = jimpleType(TypeChecker.checkExpression(constant.exp).runA(env).toOption, module),
        name = constant.name
      )
    )
  }

  def generateVariables(module: OberonModule): List[Field] =
    module.variables.map(variable =>
      Field(
        modifiers = List(PublicModifer, StaticModifier),
        fieldType = jimpleType(variable.variableType, module),
        name = variable.name
      )
    )

  def generateFields(module: OberonModule) =
    generateConstants(module) ::: generateVariables(module)

  def generateUserDefinedTypes(module: OberonModule): List[JimpleType] =
    module.userTypes.map(userType =>
      jimpleUserDefinedType(userType.name, module)
    )

  def generateMethodSignatures(module: OberonModule): List[MethodSignature] =
    module.procedures.map(procedure =>
      MethodSignature(
        className = module.name,
        returnType = jimpleType(procedure.returnType, module),
        methodName = procedure.name,
        formals =
          procedure.args.map(arg => jimpleType(arg.argumentType, module))
      )
    )

  def generateConstantAssignments(
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature]
  ): List[JimpleStatement] =
    module.constants.map(constant =>
      AssignStmt(
        StaticField(
          FieldSignature(
            module.name,
            fields.find(field => field.name == constant.name).get.fieldType,
            constant.name
          )
        ),
        jimpleExpression(constant.exp, module, fields, methodSignatures)
      )
    )

  def generateMethods(
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature]
  ): List[Method] =
    module.procedures.map(procedure =>
      jimpleMethod(procedure, module, fields, methodSignatures)
    ) :+ Method(
      modifiers = List(PublicModifer, StaticModifier),
      returnType = TVoid,
      name = "main",
      formals = List(TArray(TString)),
      exceptions = List.empty[JimpleType],
      body = DefaultMethodBody(
        localVariableDecls =
          List(LocalVariableDeclaration(TArray(TString), "args")),
        stmts = generateConstantAssignments(
          module,
          fields,
          methodSignatures
        ) ::: jimpleStatement(module.stmt, module, fields, methodSignatures, 0),
        catchClauses = List.empty[CatchClause]
      )
    )

  def jimpleMethod(
      procedure: Procedure,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature]
  ): Method = Method(
    modifiers = List(PublicModifer, StaticModifier),
    returnType = jimpleType(procedure.returnType, module),
    name = procedure.name,
    formals = procedure.args.map(arg => jimpleType(arg.argumentType, module)),
    exceptions = List.empty[JimpleType],
    body = jimpleMethodBody(procedure, module, fields, methodSignatures)
  )

  // FIXME: statements don't work with local variables
  def jimpleMethodBody(
      procedure: Procedure,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature]
  ): JimpleMethodBody = {
    val localVariableDecls = procedure.args.map(arg =>
      LocalVariableDeclaration(jimpleType(arg.argumentType, module), arg.name)
    )
    DefaultMethodBody(
      localVariableDecls = localVariableDecls,
      stmts = jimpleStatement(
        procedure.stmt,
        module,
        fields,
        methodSignatures,
        indexOffset = 0
      ),
      catchClauses = List.empty[CatchClause]
    )
  }

  def jimpleStatement(
      oberonStmt: Option[Statement],
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature],
      indexOffset: Int
  ): List[JimpleStatement] = oberonStmt match {
    case Some(someStmt) =>
      jimpleStatement(someStmt, module, fields, methodSignatures, indexOffset)
    case None => List.empty[JimpleStatement]
  }

  def jimpleStatement(
      oberonStmt: Statement,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature],
      indexOffset: Int
  ): List[JimpleStatement] = oberonStmt match {
    case AssignmentStmt(designator, exp) =>
      jimpleAssignment(designator, exp, module, fields, methodSignatures)

    case SequenceStmt(stmts) => {
      var index = indexOffset
      var jimpleStmts = List.empty[JimpleStatement]

      for (stmt <- stmts) {
        jimpleStmts = jimpleStmts ::: jimpleStatement(
          stmt,
          module,
          fields,
          methodSignatures,
          index
        )
        index += calculateIndexOffset(stmt)
      }
      jimpleStmts
    }
    case IfElseStmt(condition, thenStmt, elseStmt) =>
      jimpleIfStatement(
        condition,
        thenStmt,
        elseStmt,
        module,
        fields,
        methodSignatures,
        indexOffset
      )
    case WhileStmt(condition, stmt) =>
      jimpleWhileStatement(
        condition,
        stmt,
        module,
        fields,
        methodSignatures,
        indexOffset
      )

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleAssignment(
      designator: Designator,
      exp: Expression,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature],
      methodBody: Option[DefaultMethodBody] = None
  ): List[JimpleStatement] = designator match {
    case VarAssignment(varName) =>
      List(
        AssignStmt(
          StaticField(
            FieldSignature(
              module.name,
              fields.find(field => field.name == varName).get.fieldType,
              varName
            )
          ),
          jimpleExpression(exp, module, fields, methodSignatures)
        )
      )
    case ArrayAssignment(array, index) =>
      array match {
        case VarExpression(arrayName) =>
          List(
            AssignStmt(
              ArrayRef(arrayName, jimpleImmediate(index)),
              jimpleExpression(exp, module, fields, methodSignatures)
            )
          )
        case _ => throw new Exception("Array must be a field reference.")
      }
    case RecordAssignment(record, field) =>
      record match {
        case VarExpression(recordName) => {
          val recordType =
            fields.find(field => field.name == recordName).get.fieldType
          val recordClassName = recordType.asInstanceOf[TObject].name

          List(
            AssignStmt(
              FieldRef(
                recordName,
                FieldSignature(recordClassName, recordType, field)
              ),
              jimpleExpression(exp, module, fields, methodSignatures)
            )
          )
        }
        case _ => throw new Exception("Record must be a field reference.")
      }

    case PointerAssignment(_) =>
      throw new Exception(
        "Pointers are not yet supported by Jimple code generation."
      )
    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleIfStatement(
      condition: Expression,
      thenStmt: Statement,
      elseStmt: Option[Statement],
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature],
      indexOffset: Int
  ): List[JimpleStatement] = {
    var index = indexOffset
    val ifLabel = s"label${index}"
    val endIfLabel = s"label${index + 1}"
    val buffer = ListBuffer[JimpleStatement]()

    index += 2

    buffer += IfStmt(
      jimpleExpression(condition, module, fields, methodSignatures),
      ifLabel
    )
    buffer ++= jimpleStatement(
      elseStmt,
      module,
      fields,
      methodSignatures,
      index
    )
    index += calculateIndexOffset(elseStmt)
    buffer += GotoStmt(endIfLabel)
    buffer += LabelStmt(ifLabel)
    buffer ++= jimpleStatement(
      thenStmt,
      module,
      fields,
      methodSignatures,
      index
    )
    index += calculateIndexOffset(thenStmt)
    buffer += LabelStmt(endIfLabel)

    buffer.result()
  }

  def jimpleWhileStatement(
      condition: Expression,
      stmt: Statement,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature],
      indexOffset: Int
  ): List[JimpleStatement] = {
    var index = indexOffset
    val whileLabel = s"label${index}"
    val endWhileLabel = s"label${index + 1}"
    val buffer = ListBuffer[JimpleStatement]()

    index += 2

    buffer += LabelStmt(whileLabel)
    buffer += IfStmt(
      jimpleExpression(condition, module, fields, methodSignatures),
      endWhileLabel
    )
    buffer ++= jimpleStatement(stmt, module, fields, methodSignatures, index)
    index += calculateIndexOffset(stmt)
    buffer += GotoStmt(whileLabel)
    buffer += LabelStmt(endWhileLabel)

    buffer.result()
  }

  def jimpleExpression(
      oberonExpression: Expression,
      module: OberonModule,
      fields: List[Field],
      methodSignatures: List[MethodSignature]
  ): JimpleExpression = oberonExpression match {
    case value: Value => ImmediateExpression(jimpleImmediate(value))
    case Brackets(exp) =>
      jimpleExpression(exp, module, fields, methodSignatures)

    case ArraySubscript(arrayBase, index) =>
      arrayBase match {
        case VarExpression(name) =>
          ArraySubscriptExpression(name, jimpleImmediate(index))
        case _ => throw new Exception("Non-exhaustive match in case statement.")
      }
    case VarExpression(name) => {
      val fieldType = fields.find(field => field.name == name).get.fieldType
      FieldRefExpression(module.name, fieldType, name)
    }

    case EQExpression(left, right) =>
      CmpEqExpression(jimpleImmediate(left), jimpleImmediate(right))
    case NEQExpression(left, right) =>
      CmpNeExpression(jimpleImmediate(left), jimpleImmediate(right))
    case GTExpression(left, right) =>
      CmpGtExpression(jimpleImmediate(left), jimpleImmediate(right))
    case LTExpression(left, right) =>
      CmpLtExpression(jimpleImmediate(left), jimpleImmediate(right))
    case GTEExpression(left, right) =>
      CmpGeExpression(jimpleImmediate(left), jimpleImmediate(right))
    case LTEExpression(left, right) =>
      CmpLeExpression(jimpleImmediate(left), jimpleImmediate(right))

    case OberonAddExpression(left, right) =>
      PlusExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonSubExpression(left, right) =>
      MinusExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonMultExpression(left, right) =>
      MultExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonDivExpression(left, right) =>
      DivExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonModExpression(left, right) =>
      RemainderExpression(jimpleImmediate(left), jimpleImmediate(right))

    case OberonAndExpression(left, right) =>
      AndExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonOrExpression(left, right) =>
      OrExpression(jimpleImmediate(left), jimpleImmediate(right))
    case OberonNotExpression(exp) => NegExpression(jimpleImmediate(exp))

    case PointerAccessExpression(_) =>
      throw new Exception(
        "Pointers are not yet supported by Jimple code generation."
      )

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleImmediate(oberonExpression: Expression): JimpleImmediate =
    oberonExpression match {
      case value: Value        => ImmediateValue(jimpleValue(value))
      case VarExpression(name) => Local(name)

      case _ => throw new Exception("Non-exhaustive match in case statement.")
    }

  def jimpleValue(oberonValue: Value): JimpleValue = oberonValue match {
    case OberonIntValue(int)       => IntValue(int)
    case OberonBoolValue(bool)     => BooleanValue(bool)
    case OberonRealValue(real)     => FloatValue(real.toFloat)
    case OberonCharValue(char)     => StringValue(char.toString)
    case OberonStringValue(string) => StringValue(string)
    case OberonNullValue           => NullValue

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def jimpleType(oberonType: Option[Type], module: OberonModule): JimpleType =
    oberonType match {
      case Some(someType) => jimpleType(someType, module)
      case None           => TVoid
    }

  def jimpleType(oberonType: Type, module: OberonModule): JimpleType =
    oberonType match {
      case IntegerType   => TInteger
      case RealType      => TFloat
      case BooleanType   => TBoolean
      case CharacterType => TCharacter
      case StringType    => TString
      case UndefinedType => TUnknown
      case NullType      => TNull

      case ArrayType(_, baseType) => TArray(jimpleType(baseType, module))

      case ReferenceToUserDefinedType(name) =>
        jimpleUserDefinedType(name, module)
      case PointerType(_) =>
        throw new Exception(
          "Pointers are not yet supported by Jimple code generation."
        )

      case _ => throw new Exception("Non-exhaustive match in case statement.")
    }

  def jimpleUserDefinedType(name: String, module: OberonModule): JimpleType =
    jimpleUserDefinedType(
      module.userTypes.find(userType => userType.name == name).get,
      module
    )

  def jimpleUserDefinedType(
      userType: UserDefinedType,
      module: OberonModule
  ): JimpleType = userType.baseType match {
    case RecordType(_)          => TObject(userType.name)
    case ArrayType(_, baseType) => TArray(jimpleType(baseType, module))

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }

  def calculateIndexOffset(oberonStmt: Option[Statement]): Int =
    oberonStmt match {
      case Some(someStmt) => calculateIndexOffset(someStmt)
      case None           => 0
    }

  def calculateIndexOffset(oberonStmt: Statement): Int = oberonStmt match {
    case SequenceStmt(stmts)  => stmts.map(calculateIndexOffset).sum
    case AssignmentStmt(_, _) => 0
    case IfElseStmt(_, thenStmt, elseStmt) =>
      calculateIndexOffset(thenStmt) + calculateIndexOffset(elseStmt) + 2
    case WhileStmt(_, stmt) => calculateIndexOffset(stmt) + 2

    case _ => throw new Exception("Non-exhaustive match in case statement.")
  }
}
