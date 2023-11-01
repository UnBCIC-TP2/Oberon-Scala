package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.visitor.OberonVisitorAdapter

class ExpressionTypeChecker(val typeChecker: TypeChecker) {
  type T = Option[Type]

   def checkType(t: Type): Option[Type] = t match {
    case UndefinedType => None
    case _             => typeChecker.env.baseType(t)
  }

   def checkExpression(exp: Expression): Option[Type] =
     computeGeneralExpressionType(exp).flatMap(t => typeChecker.env.baseType(t))

  def computeGeneralExpressionType(exp: Expression): Option[Type] = exp match {
    case Brackets(exp)       => checkExpression(exp)
    case IntValue(_)         => Some(IntegerType)
    case RealValue(_)        => Some(RealType)
    case CharValue(_)        => Some(CharacterType)
    case BoolValue(_)        => Some(BooleanType)
    case StringValue(_)      => Some(StringType)
    case NullValue           => Some(NullType)
    case Undef()             => None
    case VarExpression(name) => typeChecker.env.lookup(name)
    case EQExpression(left, right) =>
      computeBinExpressionType(
        left,
        right,
        List(IntegerType, RealType, BooleanType),
        BooleanType
      )
    case NEQExpression(left, right) =>
      computeBinExpressionType(
        left,
        right,
        List(IntegerType, RealType, BooleanType),
        BooleanType
      )
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

        val givenArgumentTypes = args.map(arg => checkExpression(arg))
        val neededArgumentTypes = procedure.args.map(_.argumentType)

        val areArgTypesWrong = givenArgumentTypes
          .zip(neededArgumentTypes)
          .map({
            case (Some(givenType), neededType) if givenType == neededType =>
              Some(givenType)
            case _ => None
          })
          .contains(None)

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
      if (
        values.isEmpty || values
          .forall(v => checkExpression(v).get == arrayType.baseType)
      ) {
        Some(arrayType)
      } else None

    case ArraySubscript(array, index) => arrayElementAccessCheck(array, index)

    case FieldAccessExpression(exp, attributeName) =>
      fieldAccessCheck(exp, attributeName)

    case PointerAccessExpression(name) => pointerAccessCheck(name)

    case LambdaExpression(args, exp) => checkLambdaExpression(args, exp)
  }

  def arrayElementAccessCheck(array: Expression, index: Expression): T = {
    (checkExpression(array), checkExpression(index)) match {
      case (Some(ArrayType(_, UndefinedType)), _) =>
        None
      case (Some(ArrayType(_, typeElements)), Some(IntegerType)) =>
        Some(typeElements)
      case _ => None
    }
  }

  def fieldAccessCheck(exp: Expression, attributeName: String): T = {
    checkExpression(exp) match {
      case Some(ReferenceToUserDefinedType(userTypeName)) => {
        typeChecker.env.lookupUserDefinedType(userTypeName) match {
          case Some(UserDefinedType(_, RecordType(variables))) =>
            variables
              .find(v => v.name.equals(attributeName))
              .map(_.variableType)
          case _ => None
        }
      }
      case Some(RecordType(variables)) => {
        val attribute = variables.find(v => v.name.equals(attributeName))
        if (attribute.isDefined) Some(attribute.get.variableType) else None
      }
      case _ => None
    }
  }

  def pointerAccessCheck(name: String) = {
    typeChecker.env
      .lookup(name)
      .flatMap(t => checkType(t))
      .flatMap({
        case PointerType(varType) => Some(varType)
        case _                    => None
      })
  }

  def checkLambdaExpression(args: List[FormalArg], exp: Expression): T = {
    typeChecker.env = typeChecker.env.push()
    args.foreach(a => typeChecker.env = typeChecker.env.setLocalVariable(a.name, a.argumentType))
    val argTypes = args.map(a => a.argumentType)
    val expType = checkExpression(exp)
    typeChecker.env = typeChecker.env.pop()
    expType match {
      case None => None
      case _    => Some(LambdaType(argTypes, expType.get))
    }
  }

  def computeBinExpressionType[A](
      left: Expression,
      right: Expression,
      expected: List[Type],
      result: Type
  ): Option[Type] = {
    val t1 = checkExpression(left)
    val t2 = checkExpression(right)
    if (t1 == t2 && expected.contains(t1.getOrElse(None))) Some(result)
    else None
  }
}

class TypeChecker {
  type T = List[(Statement, String)]

  var env = new Environment[Type]()
  val expVisitor = new ExpressionTypeChecker(this)

   def checkModule(module: OberonModule): List[(Statement, String)] = {
    env = module.constants.foldLeft(env)((acc, c) => acc.setGlobalVariable(c.name, expVisitor.checkExpression(c.exp).get))
    env = module.variables.foldLeft(env)((acc, v) => acc.setGlobalVariable(v.name, v.variableType))
    env = module.procedures.foldLeft(env)((acc, p) => acc.declareProcedure(p))
    env = module.userTypes.foldLeft(env)((acc, t) => acc.addUserDefinedType(t))

    val errors = module.procedures.flatMap(p => checkProcedure(p))

    if (module.stmt.isDefined) errors ++ checkStmt(module.stmt.get)
    else errors
  }

  def checkProcedure(procedure: Procedure): List[(Statement, String)] = {
    env = env.push()
    env = procedure.args.foldLeft(env)((acc, a) => acc.setLocalVariable(a.name, a.argumentType))
    env = procedure.constants.foldLeft(env)((acc, c) => acc.setLocalVariable(c.name, expVisitor.checkExpression(c.exp).get))
    env = procedure.variables.foldLeft(env)((acc, v) => acc.setLocalVariable(v.name, v.variableType))
    val errors = checkStmt(procedure.stmt)
    env = env.pop()
    errors
  }

   def checkStmt(stmt: Statement): List[(Statement, String)] = stmt match {
    case AssignmentStmt(_, _)    => checkAssignment(stmt)
    case IfElseStmt(_, _, _)     => visitIfElseStmt(stmt)
    case WhileStmt(_, _)         => visitWhileStmt(stmt)
    case ForEachStmt(v, e, s)    => visitForEachStmt(ForEachStmt(v, e, s))
    case ExitStmt()              => visitExitStmt()
    case ProcedureCallStmt(_, _) => procedureCallStmt(stmt)
    case SequenceStmt(stmts)     => stmts.flatMap(s => checkStmt(s))
    case ReturnStmt(exp) =>
      if (expVisitor.checkExpression(exp).isDefined) List()
      else List((stmt, s"Expression $exp is ill typed."))
    case ReadLongRealStmt(v) =>
      if (env.lookup(v).isDefined) List()
      else List((stmt, s"Variable $v not declared."))
    case ReadRealStmt(v) =>
      if (env.lookup(v).isDefined) List()
      else List((stmt, s"Variable $v not declared."))
    case ReadLongIntStmt(v) =>
      if (env.lookup(v).isDefined) List()
      else List((stmt, s"Variable $v not declared."))
    case ReadIntStmt(v) =>
      if (env.lookup(v).isDefined) List()
      else List((stmt, s"Variable $v not declared."))
    case ReadShortIntStmt(v) =>
      if (env.lookup(v).isDefined) List()
      else List((stmt, s"Variable $v not declared."))
    case ReadCharStmt(v) =>
      if (env.lookup(v).isDefined) List()
      else List((stmt, s"Variable $v not declared."))
    case WriteStmt(exp) =>
      if (expVisitor.checkExpression(exp).isDefined) List()
      else List((stmt, s"Expression $exp is ill typed."))
    case NewStmt(varName) =>
      env.lookup(varName) match {
        case Some(PointerType(_)) => List()
        case _ => List((stmt, s"Expression $varName is ill typed"))
      }
    case _ => throw new RuntimeException("Statement not part of Oberon-Core")
  }


  private def checkAssignment(stmt: Statement): T = stmt match {
    case AssignmentStmt(VarAssignment(v), exp) => checkVarAssigment(v, exp)
    case AssignmentStmt(PointerAssignment(p), exp) => checkPointerAssigment(p, exp)
    case AssignmentStmt(ArrayAssignment(arr, element), exp) => checkArrayAssigment(arr, element, exp)
    case AssignmentStmt(RecordAssignment(record, field), exp) => checkRecordAssigment(record, field, exp)
  }

  private def checkVarAssigment(v: String, exp: Expression): T = {
    val result = for {
      varType <- env.lookup(v)
      varBaseType <- env.baseType(varType)
      expType <- expVisitor.checkExpression(exp)
    } yield (varBaseType, expType)
    result match {
      case Some((PointerType(_), NullType)) => List()
      case Some((IntegerType, BooleanType)) => List()
      case Some((BooleanType, IntegerType)) => List()
      case Some((t1, t2)) if t1 == t2 => List()
      case Some((t1, t2)) if t1 != t2 => List((AssignmentStmt(VarAssignment(v), exp), s"Assignment between different types: $v, $exp"))
      case None => if(! env.lookup(v).isDefined) List((AssignmentStmt(VarAssignment(v), exp), s"Variable $v not declared")) else List((AssignmentStmt(VarAssignment(v), exp), s"Expression $exp is ill typed"))
    }
  }

  private def checkPointerAssigment(v: String, exp: Expression): T = {
    val res = for {
      pointerType <- expVisitor.pointerAccessCheck(v)
      expType <- expVisitor.checkExpression(exp)
    } yield (pointerType, expType)
    res match {
      case Some((t1, t2)) if t1 == t2 => List()
      case Some((t1, t2)) if t1 != t2 => List((AssignmentStmt(PointerAssignment(v), exp), s"Expression $exp doesn't match variable type."))
      case None => List((AssignmentStmt(PointerAssignment(v), exp), s"Could not compute the types correctly."))
    }
  }

  private def checkArrayAssigment(arr: Expression, element: Expression, exp: Expression): T = {
    val res = for {
      arrType <- expVisitor.checkExpression(arr)
      elementType <- expVisitor.checkExpression(element)
      expType <- expVisitor.checkExpression(exp)
    } yield (arrType, elementType, expType)
    res match {
      case Some((ArrayType(length, t1), IntegerType, t2)) if t1 == t2 => List()
      case Some((ArrayType(length, t1), IntegerType, t2)) if t1 != t2 => List((AssignmentStmt(ArrayAssignment(arr, element), exp), s"Expression $exp doesn't match the array type."))
      case Some((_, t, _)) if t != IntegerType => List((AssignmentStmt(ArrayAssignment(arr, element), exp), s"The index expression must be an integer."))
      case None => List((AssignmentStmt(ArrayAssignment(arr, element), exp), s"Could not compute the types correctly."))
    }
  }

  private def checkRecordAssigment(record: Expression, field: String, exp: Expression): T = {
    val res = for {
      fieldAccessType <- expVisitor.fieldAccessCheck(record, field)
      expType <- expVisitor.checkExpression(exp)
    } yield (fieldAccessType, expType)
    res match {
      case Some((t1, t2)) if t1 == t2 => List()
      case Some((t1, t2)) if t1 != t2 => List((AssignmentStmt(RecordAssignment(record, field), exp), s"Expression $exp doesn't match variable type."))
      case None => List((AssignmentStmt(RecordAssignment(record, field), exp), s"Could not compute the types correctly."))
    }
  }



  private def visitIfElseStmt(stmt: Statement) = stmt match {
    case IfElseStmt(condition, thenStmt, elseStmt) =>
      var errorList = checkStmt(thenStmt)
      if (!expVisitor.checkExpression(condition).contains(BooleanType)) {
        errorList = (
          stmt,
          s"Expression $condition does not have a boolean type"
        ) :: errorList
      }
      errorList ++ elseStmt.map(s => checkStmt(s)).getOrElse(List())
  }

  private def visitWhileStmt(stmt: Statement) = stmt match {
    case WhileStmt(condition, stmt) =>
      val errorList = checkStmt(stmt)

      if (expVisitor.checkExpression(condition).contains(BooleanType)) {
        errorList
      } else {
        (stmt, s"Expression $condition do not have a boolean type") :: errorList
      }
  }

  def visitForEachStmt(forEachStmt: ForEachStmt): List[(Statement, String)] = {
    val expType = expVisitor.checkExpression(forEachStmt.exp)
    val varType = env.lookup(forEachStmt.varName)

    val res = if (expType.isDefined && expType.get.isInstanceOf[ArrayType]) {
      val arrayBaseType =
        expVisitor.checkType(expType.get.asInstanceOf[ArrayType].baseType)
      if (arrayBaseType != varType)
        List((forEachStmt, "invalid types in the foreach statement"))
      else
        List()
    } else {
      List((forEachStmt, "invalid types in the foreach statement"))
    }
    res ++ checkStmt(forEachStmt.stmt)
  }
  private def visitExitStmt(): T = List()

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
  private def procedureCallStmt(stmt: Statement): T =
    stmt match {
      case ProcedureCallStmt(name, args) =>
        val procedure = env.findProcedure(name)
        if (procedure == null)
          List((stmt, s"Procedure $name has not been declared."))
        else {
          // check if the type of the formal arguments and the actual arguments
          // match.
          val formalArgumentTypes = procedure.args.map(a => a.argumentType)
          val actualArgumentTypes = args.map(a => expVisitor.checkExpression(a).get)
          // the two lists must have the same size.
          if (formalArgumentTypes.size != actualArgumentTypes.size) {
            return List(
              (stmt, s"Wrong number of arguments to the $name procedure")
            )
          }
          val allTypesMatch = formalArgumentTypes
            .zip(actualArgumentTypes)
            .map(pair => pair._1 == pair._2)
            .forall(v => v)
          if (!allTypesMatch) {
            return List(
              (stmt, s"The arguments do not match the $name formal arguments")
            )
          }
          // if everything above is ok, lets check the procedure body.
          checkStmt(stmt)
        }
    }
}
