package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.visitor.{OberonVisitorAdapter}

class ExpressionTypeVisitor(val typeChecker: TypeChecker) extends OberonVisitorAdapter {
  type T = Option[Type]

  override def visit(t: Type): Option[Type] = t match {
    case UndefinedType => None
    case _ => Some(t)
  }
  override def visit(exp: Expression): Option[Type] = exp match {
    case Brackets(exp) => exp.accept(this)
    case IntValue(_) => Some(IntegerType)
    case RealValue(_) => Some(RealType)
    case CharValue(_) => Some(CharacterType)
    case BoolValue(_) => Some(BooleanType)
    case StringValue(_) => Some(StringType)
    case NullValue => Some(NullType)
    case Undef() => None
    case VarExpression(name) =>
      typeChecker.env.lookup(name).flatMap(_.accept(this))
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
    case ArrayValue(value) => {
      val firstExpressionType = value.headOption.flatMap(_.accept(this))

      if (!firstExpressionType.isDefined) {
        Some(ArrayType(value.length, UndefinedType))
      } else {
        firstExpressionType.map(ArrayType(value.length, _))
      }
    }
    case ArraySubscript(array, index) => {
      (array.accept(this), index.accept(this)) match {
        case (Some(ArrayType(_, UndefinedType)), _) =>
          None
        case (Some(ArrayType(_, typeElements)), Some(IntegerType)) =>
          Some(typeElements)
        case _ => None
      }
    }

    case FieldAccessExpression(exp, attributeName) => {
      val expType = visit(exp)
      if (expType.isEmpty) None
      expType.get match {
        case ReferenceToUserDefinedType(userTypeName) => {
          val userType = typeChecker.env.lookupUserDefinedType(userTypeName)
          println(userType)
          if (userType.isEmpty) None
          val UserDefinedType(name, baseType) = userType.get
          if (baseType.isInstanceOf[RecordType]) {
            val recordType = baseType.asInstanceOf[RecordType]
            val attribute = recordType.variables.find(v => v.name.equals(attributeName))
            if(attribute.isDefined) Some(attribute.get.variableType) else None
          } else None
        }
        case RecordType(variables) => {
          val attribute = variables.find(v => v.name.equals(attributeName))
          if(attribute.isDefined) Some(attribute.get.variableType) else None
        }
        case _ => None
      }
    }

    case PointerAccessExpression(name) => {
      if(typeChecker.env.lookup(name).isDefined) {
        val pointerType = typeChecker.env.lookup(name).get.accept(this).get
        val PointerType(varType) = pointerType
        Some(varType)
      }
      else None
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
    module.constants.map(c => env.setGlobalVariable(c.name, c.exp.accept(expVisitor).get))
    module.variables.map(v => env.setGlobalVariable(v.name, v.variableType))
    module.procedures.map(p => env.declareProcedure(p))
    module.userTypes.map(u => env.addUserDefinedType(u)) //added G04

    // TODO: check if the procedures are well typed.

    if(module.stmt.isDefined) module.stmt.get.accept(this)
    else List()
  }

  override def visit(stmt: Statement) = stmt match {
    case AssignmentStmt(_, _) => visitAssignment(stmt)
    case EAssignmentStmt(_, _) => visitEAssignment(stmt)
    case IfElseStmt(_, _, _) => visitIfElseStmt(stmt)
    case WhileStmt(_, _) => visitWhileStmt(stmt)
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
    case _ => throw new RuntimeException("Statement not part of Oberon-Core")
  }

  private def visitAssignment(stmt: Statement) = stmt match {
    case AssignmentStmt(v, exp) =>
      if (env.lookup(v).isDefined) {
        if (exp.accept(expVisitor).isDefined){
          if (env.lookup(v).get.accept(expVisitor).get != exp.accept(expVisitor).get){
              if ((env.lookup(v).get.accept(expVisitor).get.isInstanceOf[PointerType]) &&
                    (exp.accept(expVisitor).get == NullType)){
                    List()
              }
              else if ((env.lookup(v).get.accept(expVisitor).get == IntegerType) &&
                    (exp.accept(expVisitor).get == BooleanType)){
                    List()
              }
              else if ((env.lookup(v).get.accept(expVisitor).get == BooleanType) &&
                    (exp.accept(expVisitor).get == IntegerType)){
                    List()
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
  }

  private def visitEAssignment(stmt: Statement) = stmt match {
    case EAssignmentStmt(designator, exp) => 
      val varType = visitAssignmentAlternative(designator)
      if (varType == exp.accept(expVisitor).get){
        List()
      }
      else List((stmt, s"Expression $exp doesn't match variable type."))
  }

  private def visitAssignmentAlternative(designator: AssignmentAlternative) = designator match {
    case PointerAssignment(pointerName) => 
      val pointer = env.lookup(pointerName).get.accept(expVisitor).get
      val PointerType(varType) = pointer
      varType
    case VarAssignment(varName) => env.lookup(varName).get.accept(expVisitor).get
    //TODO
    case ArrayAssignment(array, elem) =>
      val ArrayType(index, varType) = array.accept(expVisitor).get
      varType
      //"array" and "elem" are expressions.
    case RecordAssignment(record, atrib) => FieldAccessExpression(record, atrib)
      //"record" is an expression and "atrib" is a string.
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

  private def visitExitStmt() = List[(br.unb.cic.oberon.ast.Statement, String)]()

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
