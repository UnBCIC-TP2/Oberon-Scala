package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ir.ast._
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
      try  {
        val procedure = typeChecker.env.findProcedure(name)
        
        if(args.length != procedure.args.length) {
          return None
        }

        val givenArgumentTypes = args.map(_.accept(this))
        val neededArgumentTypes = procedure.args.map(_.argumentType)

        val areArgTypesWrong = givenArgumentTypes.zip(neededArgumentTypes).map({
          case (Some(givenType), neededType) if givenType == neededType =>
            Some(givenType)
          case _ => None
        }).contains(None)

        if(areArgTypesWrong) {
          None
        } else {
          Some(procedure.returnType.getOrElse(NullType))
        }
      } catch { 
        case _ : NoSuchElementException => None
      }
    }
    case ArrayValue(values, arrayType) =>
      if(values.isEmpty || values.forall(v => v.accept(this).get == arrayType.baseType)) {
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
      case Some(ReferenceToUserDefinedType(userTypeName)) => {
        typeChecker.env.lookupUserDefinedType(userTypeName) match {
          case Some(UserDefinedType(_, RecordType(variables))) => 
            variables.find(v => v.name.equals(attributeName)).map(_.variableType)
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
    typeChecker.env.lookup(name)
      .flatMap(_.accept(this))
      .flatMap({
        case PointerType(varType) => Some(varType)
        case _ => None
      })
  }

  def lambdaExpressionCheck(args: List[FormalArg], exp: Expression): T = {
    typeChecker.env.push()
    args.foreach(a => typeChecker.env.setLocalVariable(a.name, a.argumentType))
    val argTypes = args.map(a => a.argumentType)
    val expType = exp.accept(this)
    typeChecker.env.pop()

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

  val env =  new Environment[Type]()
  val expVisitor = new ExpressionTypeVisitor(this)

  override def visit(module: OberonModule): List[(Statement, String)] = {
    module.constants.foreach(c => env.setGlobalVariable(c.name, c.exp.accept(expVisitor).get))
    module.variables.foreach(v => env.setGlobalVariable(v.name, v.variableType))
    module.procedures.foreach(env.declareProcedure)
    module.userTypes.foreach(env.addUserDefinedType) //added G04

    var errors = module.procedures.flatMap(p => checkProcedure(p))

    if(module.stmt.isDefined) errors ++ module.stmt.get.accept(this)
    else errors
  }

  def checkProcedure(procedure: Procedure): List[(Statement, String)] = {
    env.push()
    procedure.args.foreach(a => env.setLocalVariable(a.name, a.argumentType))
    procedure.constants.foreach(c => env.setLocalVariable(c.name, c.exp.accept(expVisitor).get))
    procedure.variables.foreach(v => env.setLocalVariable(v.name, v.variableType))
    val errors = procedure.stmt.accept(this)
    env.pop()
    errors
  }

  def visitForEachStmt(forEachStmt: ForEachStmt): List[(Statement, String)] = {
    val expType = forEachStmt.exp.accept(expVisitor)
    val varType = env.lookup(forEachStmt.varName)

    val res = if(expType.isDefined && expType.get.isInstanceOf[ArrayType]) {
      val arrayBaseType = expType.get.asInstanceOf[ArrayType].baseType.accept(expVisitor)
      if(arrayBaseType != varType)
        List((forEachStmt, "invalid types in the foreach statement"))
      else
        List()
    }
    else {
      List((forEachStmt, "invalid types in the foreach statement"))
    }
    res ++ forEachStmt.stmt.accept(this)
  }

  override def visit(stmt: Statement) = stmt match {
    case AssignmentStmt(_, _) => visitAssignment(stmt)
    case IfElseStmt(_, _, _) => visitIfElseStmt(stmt)
    case WhileStmt(_, _) => visitWhileStmt(stmt)
    case ForEachStmt(v, e, s) => visitForEachStmt(ForEachStmt(v, e, s))
    case ExitStmt() => visitExitStmt()
    case ProcedureCallStmt(_, _) => procedureCallStmt(stmt)
    case SequenceStmt(stmts) => stmts.flatMap(s => s.accept(this))
    case ReturnStmt(exp) => if(exp.accept(expVisitor).isDefined) List() else List((stmt, s"Expression $exp is ill typed."))
    case ReadLongRealStmt(v) => if(env.lookup(v).isDefined) List() else List((stmt, s"Variable $v not declared."))
    case ReadRealStmt(v) => if(env.lookup(v).isDefined) List() else List((stmt, s"Variable $v not declared."))
    case ReadLongIntStmt(v) => if(env.lookup(v).isDefined) List() else List((stmt, s"Variable $v not declared."))
    case ReadIntStmt(v) => if(env.lookup(v).isDefined) List() else List((stmt, s"Variable $v not declared."))
    case ReadShortIntStmt(v) => if(env.lookup(v).isDefined) List() else List((stmt, s"Variable $v not declared."))
    case ReadCharStmt(v) => if(env.lookup(v).isDefined) List() else List((stmt, s"Variable $v not declared."))
    case WriteStmt(exp) => if(exp.accept(expVisitor).isDefined) List() else List((stmt, s"Expression $exp is ill typed."))
    case NewStmt(varName) =>
      env.lookup(varName) match {
        case Some(PointerType(_)) => List()
        case _ => List((stmt, s"Expression $varName is ill typed"))
      }
    case _ => throw new RuntimeException("Statement not part of Oberon-Core")
  }

  private def visitAssignment(stmt: Statement) = stmt match {
    case AssignmentStmt(VarAssignment(v), exp) =>
      if (env.lookup(v).isDefined) {
        if (exp.accept(expVisitor).isDefined){
          if (env.lookup(v).get != exp.accept(expVisitor).get){
              val varType = env.lookup(v).get.accept(expVisitor).get
              val expType = exp.accept(expVisitor).get

              if ((varType.isInstanceOf[PointerType]) &&
                    (expType == NullType)){
                    List()
              }
              else if ((varType == IntegerType) &&
                    (expType == BooleanType)){
                    List()
              }
              else if ((varType == BooleanType) &&
                    (expType == IntegerType)){
                    List()
              }
              else if (varType.isInstanceOf[LambdaType]) {
                val expectedType = varType.asInstanceOf[LambdaType]
                val passedType = expType.asInstanceOf[LambdaType]

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

                  if(!allTypesMatch) {
                    List((stmt, "Arguments types do not match type definition."))
                  } 
                  else {
                    List()
                  }
                }
              }
              else{
                 List((stmt, s"Assignment between different types: $v, $exp"))
              }
          }
          else List()
        }
        else List((stmt, s"Expression $exp is ill typed"))
      }
      else List((stmt, s"Variable $v not declared"))
    case AssignmentStmt(designator, exp) => {
      val varType = visitAssignmentAlternative(designator)
      if (varType.isDefined && varType == exp.accept(expVisitor)){
        List()
      }
      else List((stmt, s"Expression $exp doesn't match variable type."))
    }
  }


  private def visitAssignmentAlternative(designator: Designator): Option[Type] = designator match {
    case PointerAssignment(pointerName) =>
      expVisitor.pointerAccessCheck(pointerName)
    case VarAssignment(varName) =>
      env.lookup(varName).flatMap(_.accept(expVisitor))
    case ArrayAssignment(array, elem) =>
      expVisitor.arrayElementAccessCheck(array, elem)
    case RecordAssignment(record, atrib) =>
      expVisitor.fieldAccessCheck(record, atrib)
  }

private def visitIfElseStmt(stmt: Statement) = stmt match {
  case IfElseStmt(condition, thenStmt, elseStmt) =>
    var errorList = thenStmt.accept(this)

    if(!condition.accept(expVisitor).contains(BooleanType)) {
      errorList = (stmt, s"Expression $condition does not have a boolean type") :: errorList
    }
    
    errorList ++ elseStmt.map(_.accept(this)).getOrElse(List())
}

  private def visitWhileStmt(stmt: Statement) = stmt match {
    case WhileStmt(condition, stmt) =>
      val errorList = stmt.accept(this)

      if(condition.accept(expVisitor).contains(BooleanType)) {
        errorList
      } else {
        (stmt, s"Expression $condition do not have a boolean type") :: errorList
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
