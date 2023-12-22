package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ir.ast.{NEQExpression, _}
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import cats.data.ContT

case class ExpT(exp: Expression, typeName: Option[Type])
trait Constraint
case class IsNum(type1: Option[Type], type2: Option[Type]) extends Constraint
case class HasEq(type1: Option[Type], type2: Option[Type]) extends Constraint
case class HasOrd(type1: Option[Type], type2: Option[Type]) extends Constraint

class ExpressionTypeChecker(val typeChecker: TypeChecker) {
  type T = Option[Type]

   def checkType(t: Type): Option[Type] = t match {
    case UndefinedType => None
    case _             => typeChecker.env.baseType(t)
  }
def unifyConstraints(acc: Option[Type], constraints: List[Constraint]): Option[Type] = {
    val Eqs = List(RealType, IntegerType, BooleanType)
    var newAcc: Option[Type] = None
    constraints match {
      case Nil =>
        acc 
      case IsNum(type1, type2) :: tail =>
        newAcc = (type1, type2) match {
          case (Some(IntegerType), Some(IntegerType)) => Some(IntegerType)
          case (Some(RealType), Some(RealType))       => Some(RealType)
          case (Some(IntegerType), Some(RealType))    => Some(RealType)
          case (Some(RealType), Some(IntegerType))    => Some(RealType)
          case (_, _)                     => None
        }
        newAcc match {
          case None => None
          case Some(IntegerType) => acc match {
            case None => Some(IntegerType)
            case Some(IntegerType) => Some(IntegerType)
            case Some(RealType) => Some(RealType)
            case _ => None
          }
          case Some(RealType) => acc match {
            case None => Some(RealType)
            case Some(IntegerType) => Some(RealType)
            case Some(RealType) => Some(RealType)
            case _ => None
          }
          case _ => None
        }
        unifyConstraints(newAcc, tail)

      case HasEq(type1, type2) :: tail =>
        newAcc = (type1, type2) match {
          case (Some(t1), Some(t2)) if(t1 == t2) =>
            if(Eqs.contains(t1)) Some(BooleanType)
            else None
          case (_, _)                           => None
        }

        newAcc match {
          case None => None
          case Some(BooleanType) => 
            acc match {
              case Some(BooleanType) => Some(BooleanType)
              case None => Some(BooleanType)
              case _ => None
            }
          case Some(IntegerType) => 
            acc match {
              case Some(IntegerType) => Some(IntegerType)
              case Some(RealType) => Some(RealType)
              case None => Some(IntegerType)
              case _ => None
            }
          case Some(RealType) => 
            acc match {
              case Some(RealType) => Some(RealType)
              case Some(IntegerType) => Some(RealType)
              case None => Some(RealType)
              case _ => None
            }
          case _ => None
        }
        unifyConstraints(newAcc, tail)

      case _ :: tail =>
        unifyConstraints(acc, tail)
    }
  }
  def unify(constraints: List[Constraint]): Option[Type] = {
    unifyConstraints(None, constraints)
}


  def checkExpression(exp: Expression): Option[Type] = {
    val (lista, expt) = computeGeneralExpressionType(exp)

    if(lista.nonEmpty) {
      return unify(lista)
    }
    expt.typeName
  }

  def computeGeneralExpressionType(exp: Expression): (List[Constraint], ExpT)= exp match {
    case Brackets(e)         => 
      val optype = checkExpression(e)
      (List(), ExpT(Brackets(e), optype))
    case IntValue(v)         => (List(), ExpT(IntValue(v), Some(IntegerType)))
    case RealValue(v)        => (List(), ExpT(RealValue(v), Some(RealType)))
    case CharValue(v)        => (List(), ExpT(CharValue(v), Some(CharacterType)))
    case BoolValue(v)        => (List(), ExpT(BoolValue(v) ,Some(BooleanType)))
    case StringValue(v)      => (List(), ExpT(StringValue(v), Some(StringType)))
    case NullValue           => (List(), ExpT(NullValue, Some(NullType)))
    case Undef()             => (List() ,ExpT(Undef(), None))
    case VarExpression(name) => 
      val optype = typeChecker.env.lookup(name)
      (List(), ExpT(VarExpression(name), optype))
    case EQExpression(left, right) =>
      val t1 = checkExpression(left)
      val t2 = checkExpression(right)
      (List(HasEq(t1, t2)), ExpT(EQExpression(left, right), None))
    case NEQExpression(left, right) =>
      val ty = computeBinExpressionType(
        left,
        right,
        List(IntegerType, RealType, BooleanType),
        BooleanType
      )
      (List(), ExpT(EQExpression(left, right), ty))
    case GTExpression(left, right) =>
      val ty = computeBinExpressionType(left, right, List(IntegerType), BooleanType)
      (List(), ExpT(GTEExpression(left, right), ty))
    case LTExpression(left, right) =>
      val ty = computeBinExpressionType(left, right, List(IntegerType), BooleanType)
      (List(), ExpT(LTExpression(left, right), ty))
    case GTEExpression(left, right) =>
      val ty = computeBinExpressionType(left, right, List(IntegerType), BooleanType)
      (List(), ExpT(GTEExpression(left, right), ty))
    case LTEExpression(left, right) =>
      val ty = computeBinExpressionType(left, right, List(IntegerType), BooleanType)
      (List(), ExpT(LTEExpression(left, right), ty))
    case AddExpression(left, right) =>
      val t1 = checkExpression(left)
      val t2 = checkExpression(right)
      (List(IsNum(t1, t2)), ExpT(AddExpression(left, right), None))
    case SubExpression(left, right) =>
      val t1 = checkExpression(left)
      val t2 = checkExpression(right)
      (List(IsNum(t1, t2)), ExpT(AddExpression(left, right), None))
    case MultExpression(left, right) =>
      val t1 = checkExpression(left)
      val t2 = checkExpression(right)
      (List(IsNum(t1, t2)), ExpT(AddExpression(left, right), None))
    case DivExpression(left, right) =>
      val t1 = checkExpression(left)
      val t2 = checkExpression(right)
      (List(IsNum(t1, t2)), ExpT(AddExpression(left, right), None))
    case AndExpression(left, right) =>
      val t1 = computeBinExpressionType(left, right, List(BooleanType), BooleanType)
      (List(), ExpT(AndExpression(left, right), None))
    case OrExpression(left, right) =>
      val t1 = computeBinExpressionType(left, right, List(BooleanType), BooleanType)
      (List(), ExpT(OrExpression(left, right), None))
    case FunctionCallExpression(name, args) => {
      try {
        val procedure = typeChecker.env.findProcedure(name)

        if (args.length != procedure.args.length) {
          return (List() , ExpT(FunctionCallExpression(name, args) ,None))
        }

        val givenArgumentTypes = args.map(arg => checkExpression(arg))
        val neededArgumentTypes = procedure.args.map(_.argumentType)

        val areArgTypesWrong = givenArgumentTypes
          .zip(neededArgumentTypes)
          .map({
            case (Some(givenType), neededType) if givenType == neededType =>
              Some(givenType)
            case _ => (List() , ExpT(FunctionCallExpression(name, args) ,None))
          })
          .contains(None)

        if (areArgTypesWrong) {
          (List() , ExpT(FunctionCallExpression(name, args) ,None))
        } else {
          (List() , ExpT(FunctionCallExpression(name, args) ,Some(procedure.returnType.getOrElse(NullType))))
        }
      } catch {
        case _: NoSuchElementException => (List() , ExpT(FunctionCallExpression(name, args) ,None))
      }
    }
    case ArrayValue(values, arrayType) =>
      if (
        values.isEmpty || values
          .forall(v => checkExpression(v).get == arrayType.baseType)
      ) {
        (List(), ExpT(ArrayValue(values, arrayType), Some(arrayType)))
      } else (List(), ExpT(ArrayValue(values, arrayType), None))

    case ArraySubscript(array, index) => 
      val ty = arrayElementAccessCheck(array, index)
      (List(), ExpT(ArraySubscript(array, index), ty))
    case FieldAccessExpression(exp, attributeName) =>
      val ty = fieldAccessCheck(exp, attributeName)
      (List(), ExpT(FieldAccessExpression(exp, attributeName), ty))
    case PointerAccessExpression(name) => 
      val ty = pointerAccessCheck(name)
      (List(), ExpT(PointerAccessExpression(name), ty))
    case LambdaExpression(args, exp) => 
      val ty = checkLambdaExpression(args, exp)
      (List(), ExpT(LambdaExpression(args, exp), ty ))
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
      case Some(ReferenceToUserDefinedType(userTypeName)) =>
        typeChecker.env.lookupUserDefinedType(userTypeName) match {
          case Some(UserDefinedType(_, RecordType(variables))) =>
            variables
              .find(v => v.name.equals(attributeName))
              .map(_.variableType)
          case _ => None
        }
        case Some(RecordType(variables)) => variables.find(v => v.name.equals(attributeName)).map(_.variableType).orElse(None)
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
    case AssertTrueStmt(exp) => visitAssertStmt(stmt)
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
      case Some((_, t, _)) => 
        if( t != IntegerType) List((AssignmentStmt(ArrayAssignment(arr, element), exp), s"The index expression must be an integer."))
        else List()
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

  private def visitAssertStmt(stmt: Statement) = stmt match {
    case AssertTrueStmt(condition) =>
      val errorList = List[(br.unb.cic.oberon.ir.ast.Statement, String)]()
      if (expVisitor.checkExpression(condition).contains(BooleanType)) {
        errorList
      } else {
        (stmt, s"Expression $condition does not have a boolean type") :: errorList
      }
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
