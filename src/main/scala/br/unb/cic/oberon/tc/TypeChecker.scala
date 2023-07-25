package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ir.ast.{NEQExpression, _}
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.visitor.OberonVisitorAdapter

class ExpressionTypeVisitor(val typeChecker: TypeChecker) extends OberonVisitorAdapter {
  type T = Option[Type]

  override def visit(t: Type): Option[Type] = t match {
    case UndefinedType => None
    case _ => typeChecker.env.baseType(t)
  }

  override def visit(exp: Expression): Option[Type] = visitExpression(exp).flatMap(t => typeChecker.env.baseType(t))

  def visitExpression(exp: Expression): Option[Type] = exp match {
    case Brackets(exp) => exp.accept(this)
    case IntValue(_) => Some(IntegerType)
    case RealValue(_) => Some(RealType)
    case CharValue(_) => Some(CharacterType)
    case BoolValue(_) => Some(BooleanType)
    case StringValue(_) => Some(StringType)
    case NullValue => Some(NullType)
    case Undef() => None
    case VarExpression(name) => typeChecker.env.lookup(name)
    case EQExpression(left, right) =>
      computeBinExpressionType(left, right, List(IntegerType, RealType, BooleanType), BooleanType)
    case NEQExpression(left, right) =>
      computeBinExpressionType(left, right, List(IntegerType, RealType, BooleanType), BooleanType)
    case GTExpression(left, right) =>
      computeBinExpressionType(left, right, List(IntegerType), BooleanType)
    case LTExpression(left, right) =>
      computeBinExpressionType(left, right, List(IntegerType), BooleanType)
    case GTEExpression(left, right) =>
      computeBinExpressionType(left, right, List(IntegerType), BooleanType)
    case LTEExpression(left, right) =>
      computeBinExpressionType(left, right, List(IntegerType), BooleanType)
    case AddExpression(left, right) =>
      computeBinExpressionType(left, right, List(IntegerType), IntegerType)
    case SubExpression(left, right) =>
      computeBinExpressionType(left, right, List(IntegerType), IntegerType)
    case MultExpression(left, right) =>
      computeBinExpressionType(left, right, List(IntegerType), IntegerType)
    case DivExpression(left, right) =>
      computeBinExpressionType(left, right, List(IntegerType), IntegerType)
    case AndExpression(left, right) =>
      computeBinExpressionType(left, right, List(BooleanType), BooleanType)
    case OrExpression(left, right) =>
      computeBinExpressionType(left, right, List(BooleanType), BooleanType)
    case FunctionCallExpression(name, args) => {
      try {
        val procedure = typeChecker.env.findProcedure(name)

        if (args.length != procedure.args.length) {
          return None
        }

        val givenArgumentTypes = args.map(_.accept(this))
        val neededArgumentTypes = procedure.args.map(_.argumentType)

        val areArgTypesWrong = givenArgumentTypes.zip(neededArgumentTypes).map({
          case (Some(givenType), neededType) if givenType == neededType =>
            Some(givenType)
          case _ => None
        }).contains(None)

        if (areArgTypesWrong) {
          None
        } else {
          Some(procedure.returnType.getOrElse(NullType))
        }
      } catch {
        case _: NoSuchElementException => None
      }
    }
    case ArrayValue(values, arrayType) =>
      if (values.isEmpty || values.forall(v => v.accept(this).get == arrayType.baseType)) {
        Some(arrayType)
      }
      else None

    case ArraySubscript(array, index) => arrayElementAccessCheck(array, index)

    case FieldAccessExpression(exp, attributeName) =>
      fieldAccessCheck(exp, attributeName)

    case PointerAccessExpression(name) => pointerAccessCheck(name)

    case LambdaExpression(args, exp) => lambdaExpressionCheck(args, exp)
  }

  def arrayElementAccessCheck(array: Expression, index: Expression): T = {
    (array.accept(this), index.accept(this)) match {
      case (Some(ArrayType(_, UndefinedType)), _) =>
        None
      case (Some(ArrayType(_, typeElements)), Some(IntegerType)) =>
        Some(typeElements)
      case _ => None
    }
  }

  def fieldAccessCheck(exp: Expression, attributeName: String): T = {
    exp.accept(this) match {
      case Some(ReferenceToUserDefinedType(userTypeName)) =>
        typeChecker.env.lookupUserDefinedType(userTypeName) match {
          case Some(UserDefinedType(_, RecordType(variables))) =>
            variables.find(v => v.name.equals(attributeName)).map(_.variableType).orElse(None)
          case _ => None
        }
      case Some(RecordType(variables)) =>
        variables.find(v => v.name.equals(attributeName)).map(_.variableType).orElse(None)
      case _ => None
    }
  }

  def pointerAccessCheck(name: String) = {
    typeChecker.env.lookup(name)
      .flatMap(_.accept(this))
      .flatMap({
        case PointerType(varType) => Some(varType)
        case _ => None
      })
  }

  def lambdaExpressionCheck(args: List[FormalArg], exp: Expression): T = {
    typeChecker.env = typeChecker.env.push()
    for(f<-args){typeChecker.env = typeChecker.env.setLocalVariable(f.name,f.argumentType)}
    val argTypes = args.map(a => a.argumentType)
    val expType = exp.accept(this)
    typeChecker.env = typeChecker.env.pop()

    expType match {
      case None => None
      case _ => Some(LambdaType(argTypes, expType.get))
    }
  }

  def computeBinExpressionType[A](left: Expression, right: Expression, expected: List[Type], result: Type) : Option[Type] = {
    val t1 = left.accept(this)
    val t2 = right.accept(this)
    if(t1 == t2 && expected.contains(t1.getOrElse(None))) Some(result) else None
  }
}

class TypeChecker extends OberonVisitorAdapter {
  type T = List[(Statement, String)]

  var env = new Environment[Type]()
  val expVisitor = new ExpressionTypeVisitor(this)

  override def visit(module: OberonModule): List[(Statement, String)] = {
    module.constants.foreach(c => env = env.setGlobalVariable(c.name, c.exp.accept(expVisitor).get))
    module.variables.foreach(v => env = env.setGlobalVariable(v.name, v.variableType))
    module.procedures.foreach(p => env = env.declareProcedure(p))
    module.tests.foreach(t => env = env.declareTest(t))
    module.userTypes.foreach(u => env = env.addUserDefinedType(u))

    var errorsTest = module.tests.flatMap(t => checkTest(t))
    var errors = module.procedures.flatMap(p => checkProcedure(p))
    errorsTest.foreach(t => errors = errors:+t)

    errors ++ module.stmt.fold(List.empty[(Statement, String)])(_.accept(this))
  }

  def checkProcedure(procedure: Procedure): List[(Statement, String)] = {
    val localEnv = procedure.args.foldLeft(env.push()) { (env, arg) =>
      env.setLocalVariable(arg.name, arg.argumentType)
    }

    val localEnvWithConstants = procedure.constants.foldLeft(localEnv) { (env, constant) =>
      env.setLocalVariable(constant.name, constant.exp.accept(expVisitor).get)
    }

    val localEnvWithVariables = procedure.variables.foldLeft(localEnvWithConstants) { (env, variable) =>
      env.setLocalVariable(variable.name, variable.variableType)
    }

    val errors = procedure.stmt.accept(this)
    val updatedEnv = localEnvWithVariables.pop()

    errors
  }

  def checkTest(test: Test): List[(Statement, String)] = {
    val localEnvWithConstants = test.constants.foldLeft(env.push()) { (env, constant) =>
      env.setLocalVariable(constant.name, constant.exp.accept(expVisitor).get)
    }

    val localEnvWithVariables = test.variables.foldLeft(localEnvWithConstants) { (env, variable) =>
      env.setLocalVariable(variable.name, variable.variableType)
    }

    env = localEnvWithVariables
    val errors = test.stmt.accept(this)
    env = localEnvWithVariables.pop()

    errors
  }

  def visitForEachStmt(forEachStmt: ForEachStmt): List[(Statement, String)] = {
    val expType = forEachStmt.exp.accept(expVisitor)
    val varType = env.lookup(forEachStmt.varName)

    val res = if (expType.isDefined && expType.get.isInstanceOf[ArrayType]) {
      val arrayBaseType = expType.get.asInstanceOf[ArrayType].baseType.accept(expVisitor)
      if (arrayBaseType != varType)
        List((forEachStmt, "invalid types in the foreach statement"))
      else
        List()
    }
    else {
      List((forEachStmt, "invalid types in the foreach statement"))
    }
    res ++ forEachStmt.stmt.accept(this)
  }
  override def visit(stmt: Statement): List[(Statement, String)] = stmt match {
    case AssignmentStmt(_, _) => visitAssignment(stmt)
    case IfElseStmt(_, _, _) => visitIfElseStmt(stmt)
    case WhileStmt(_, _) => visitWhileStmt(stmt)
    case ForEachStmt(v, e, s) => visitForEachStmt(ForEachStmt(v, e, s))
    case ExitStmt() => visitExitStmt()
    case ProcedureCallStmt(_, _) => procedureCallStmt(stmt)
    case SequenceStmt(stmts) => stmts.flatMap(s => visit(s))
    case ReturnStmt(exp) => exp.accept(expVisitor).fold(List((stmt, s"Expression $exp is ill-typed.")))(_ => List())
    case ReadLongRealStmt(v) => env.lookup(v).fold(List((stmt, s"Variable $v not declared.")))(_ => List())
    case ReadRealStmt(v) => env.lookup(v).fold(List((stmt, s"Variable $v not declared.")))(_ => List())
    case ReadLongIntStmt(v) => env.lookup(v).fold(List((stmt, s"Variable $v not declared.")))(_ => List())
    case ReadIntStmt(v) => env.lookup(v).fold(List((stmt, s"Variable $v not declared.")))(_ => List())
    case ReadShortIntStmt(v) => env.lookup(v).fold(List((stmt, s"Variable $v not declared.")))(_ => List())
    case ReadCharStmt(v) => env.lookup(v).fold(List((stmt, s"Variable $v not declared.")))(_ => List())
    case WriteStmt(exp) => exp.accept(expVisitor).fold(List((stmt, s"Expression $exp is ill-typed.")))(_ => List())
    case AssertTrueStmt(exp) => visitAssertStmt(stmt)
    case NewStmt(varName) => env.lookup(varName) match {
      case Some(PointerType(_)) => List()
      case _ => List((stmt, s"Expression $varName is ill-typed."))
    }
    case _ => throw new RuntimeException("Statement not part of Oberon-Core")
  }

  private def visitAssignment(stmt: Statement): List[(Statement, String)] = stmt match {
    case AssignmentStmt(VarAssignment(v), exp) =>
      env.lookup(v) match {
        case Some(varType) =>
          val varTypeResult = varType.accept(expVisitor)
          val expTypeResult = exp.accept(expVisitor)

          (varTypeResult, expTypeResult) match {
            case (Some(varTypeValue), Some(expTypeValue)) =>
              if (varTypeValue.isInstanceOf[PointerType] && expTypeValue == NullType) {
                List()
              } else if (varTypeValue == IntegerType && expTypeValue == BooleanType) {
                List()
              } else if (varTypeValue == BooleanType && expTypeValue == IntegerType) {
                List()
              } else if (varTypeValue.isInstanceOf[LambdaType] && expTypeValue.isInstanceOf[LambdaType]) {
                val expectedType = varTypeValue.asInstanceOf[LambdaType]
                val passedType = expTypeValue.asInstanceOf[LambdaType]

                val expectedArgs = expectedType.argsTypes
                val passedArgs = passedType.argsTypes

                if (expectedArgs.size != passedArgs.size) {
                  List((stmt, s"Wrong number of arguments. Expected ${expectedArgs.size}, got ${passedArgs.size}."))
                } else if (expectedType.returnType != passedType.returnType) {
                  List((stmt, s"Wrong return type. Expected ${expectedType.returnType}, got ${passedType.returnType}."))
                } else {
                  val allTypesMatch = expectedArgs.zip(passedArgs)
                    .map(pair => pair._1 == pair._2)
                    .forall(v => v)

                  if (!allTypesMatch) {
                    List((stmt, "Arguments types do not match type definition."))
                  } else {
                    List()
                  }
                }
              } else if (varTypeValue != expTypeValue) {
                List((stmt, s"Assignment between different types: $v, $exp"))
              } else {
                List()
              }
            case _ => List((stmt, s"Expression $exp is ill typed"))
          }
        case None => List((stmt, s"Variable $v not declared"))
      }
    case AssignmentStmt(designator, exp) =>
      val varType = visitAssignmentAlternative(designator)
      if (varType.isDefined && varType == exp.accept(expVisitor)) {
        List()
      } else {
        List((stmt, s"Expression $exp doesn't match variable type."))
      }
  }


  private def visitAssignmentAlternative(designator: Designator): Option[Type] = designator match {
    case PointerAssignment(pointerName) =>
      expVisitor.pointerAccessCheck(pointerName)
    case VarAssignment(varName) =>
      for {
        variable <- env.lookup(varName)
        variableType <- variable.accept(expVisitor)
      } yield variableType
    case ArrayAssignment(array, elem) =>
      expVisitor.arrayElementAccessCheck(array, elem)
    case RecordAssignment(record, atrib) =>
      expVisitor.fieldAccessCheck(record, atrib)
  }


  private def visitIfElseStmt(stmt: Statement): List[(Statement, String)] = stmt match {
    case IfElseStmt(condition, thenStmt, elseStmt) =>
      val thenErrors = thenStmt.accept(this)
      val conditionErrors = condition.accept(expVisitor) match {
        case Some(BooleanType) => List.empty[(Statement, String)]
        case _ => List((stmt, s"Expression $condition does not have a boolean type"))
      }
      val elseErrors = elseStmt.map(_.accept(this)).getOrElse(List.empty[(Statement, String)])
      thenErrors ++ conditionErrors ++ elseErrors
  }

  private def visitWhileStmt(stmt: Statement): List[(Statement, String)] = stmt match {
    case WhileStmt(condition, body) =>
      val errors = body.accept(this)

      condition.accept(expVisitor) match {
        case Some(BooleanType) => errors
        case _ => (stmt, s"Expression $condition does not have a boolean type") :: errors
      }
  }

  private def visitAssertStmt(stmt: Statement) = stmt match {
    case AssertTrueStmt(condition) =>
      val errorList = List[(br.unb.cic.oberon.ir.ast.Statement, String)]()
      if (condition.accept(expVisitor).contains(BooleanType)) {
        errorList
      } else {
        (stmt, s"Expression $condition does not have a boolean type") :: errorList
      }
  }


  private def visitExitStmt() = List[(br.unb.cic.oberon.ir.ast.Statement, String)]()

  /*
   * Type checker for a procedure call. This is the "toughest" implementation
   * here. We have to check:
   *
   * (a) the procedure exists
   * (b) the type of the actual arguments match the type of the formal arguments
   * (c) the procedure body (stmt) is well typed.
   *
   * @param stmt (a procedure call)
   *
   * @return Our error representation (statement + string with the error message)
   */
  private def procedureCallStmt(stmt: Statement): List[(Statement, String)] = stmt match {
    case ProcedureCallStmt(name, args) =>
      val procedure = env.findProcedure(name)
      if(procedure == null) return List((stmt, s"Procedure $name has not been declared."))
      else {
        // check if the type of the formal arguments and the actual arguments
        // match.
        val formalArgumentTypes = procedure.args.map(a => a.argumentType)
        val actualArgumentTypes = args.map(a => a.accept(expVisitor).get)
        // the two lists must have the same size.
        if(formalArgumentTypes.size != actualArgumentTypes.size) {
          return List((stmt, s"Wrong number of arguments to the $name procedure"))
        }
        val allTypesMatch = formalArgumentTypes.zip(actualArgumentTypes)
          .map(pair => pair._1 == pair._2)
          .forall(v => v)
        if(!allTypesMatch) {
          return List((stmt, s"The arguments do not match the $name formal arguments"))
        }
        // if everything above is ok, lets check the procedure body.
        procedure.stmt.accept(this)
      }
  }
}