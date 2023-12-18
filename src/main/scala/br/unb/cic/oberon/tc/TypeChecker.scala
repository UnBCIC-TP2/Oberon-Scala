package br.unb.cic.oberon.tc

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.visitor.OberonVisitorAdapter

import cats.data.State
import cats.data.Writer

class ExpressionTypeChecker(val typeChecker: TypeChecker, var env: Environment[Type]) {
  type T = State[Environment[Type], Writer[List[String], Option[Type]]]


  /* Tem a função de pegar o tipo.
  Caso não esteja definido retorna None, caso contrário retorna o tipo encontrado. */

  // TODO: case_ verificar env.baseType(t)
  def checkType(t: Type): T = t match {
    case UndefinedType => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List("Error. "), None))}
    case _             => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(""), env.baseType(t)))}
  }

  // Pergunta: A expressão [typeChecker.env.baseType(t)], pode ser substituída por checkType(t)?
  // State[Environment, Writer[List[String], Type]]
  def checkExpression(exp: Expression, env: Environment[Type]): T  =
    computeGeneralExpressionType(exp, env).flatMap(t => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(""), env.baseType(t.value.get)))})

  // Função Monadificável
  def computeGeneralExpressionType(exp: Expression, env: Environment[Type]): T = exp match {
  //   case Brackets(exp)       => checkExpression(exp)
  //   // Retorna os tipos nativos
    case IntValue(_)         => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(""), Some(IntegerType)))}
    case RealValue(_)        => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(""), Some(RealType)))}
    case CharValue(_)        => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(""), Some(CharacterType)))}
    case BoolValue(_)        => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(""), Some(BooleanType)))}
    case StringValue(_)      => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(""), Some(StringType)))}
    case NullValue           => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(""), Some(NullType)))}
    case Undef()             => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(""), None))}

  //   /* Verifica se a variável avaliada já foi definida anteriormente, 
  //   se não for definida retorna None. A mensagem de erro é retornada pelo checkStmt*/
  //   case VarExpression(name) => typeChecker.env.lookup(name)
    case EQExpression(left, right) => computeBinExpressionType(
        left,
        right,
        List(IntegerType, RealType, BooleanType),
        BooleanType
    )
    case NEQExpression(left, right) => computeBinExpressionType(
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
  // Verifica se os argumentos da função são do tipo esperado pela definição dela.
    //TODO: Definir mensagens de erro.
    case FunctionCallExpression(name, args) => {
        val procedure = env.findProcedure(name)

        if (args.length != procedure.args.length) {
          return State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List("Error. "), None))}
        }

        val givenArgumentTypes = args.map(arg => checkExpression(arg, env).get) //Type: List[Types]
        val neededArgumentTypes = procedure.args.map(_.argumentType)  //Type: List[Types]

        if(givenArgumentTypes == neededArgumentTypes) {
          State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(""), Some(procedure.returnType.getOrElse(NullType))))}
        } else {
          State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List("Error. "), None))}
        }
  } 


    // Verifica se todos os elementos da array são do tipo esperado.
    case ArrayValue(values, arrayType) =>
      if (values.isEmpty || values.forall(v => checkExpression(v, env).get == arrayType.baseType)) 
          {State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(""), Some(arrayType)))}
      } else State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List("Error. "), None))}
    
     // Verifica um elemento especificado pelo index da array
     case ArraySubscript(array, index) => arrayElementAccessCheck(array, index, env)
     // Verifica se o objeto/expressão tem o atributo indicado
     case FieldAccessExpression(exp, attributeName) =>
        fieldAccessCheck(exp, attributeName, env)
     // Verifica se o nome passado possui um endereço/ponteiro definido.
     // Ser definido significa que o nome foi utilizado para atribuir algo anteriormente.
     // OBS: O endereço deve possuir algo com tipo válido. 
     case PointerAccessExpression(name) => pointerAccessCheck(name, env)
     
     case LambdaExpression(args, exp) => checkLambdaExpression(args, exp, env)
  }


  def arrayElementAccessCheck(array: Expression, index: Expression, env: Environment[Type]): T = for{
    expressao1 <- checkExpression(array, env)
    expressao2 <-  checkExpression(index, env)
  }yield(expressao1.mapBoth{(lista, tipo) => expressao2.mapBoth{ (lista2, tipo2) =>
    val lista3 = lista ++ lista2
    if(tipo.get == ArrayType && tipo2.get == IntegerType){
      // O tipo retornado precisa ser definido
      (lista3, Some(UndefinedType))
    }else{
     (lista3 ++ List("Error. "), None)
    }
  }.run
  })


  def fieldAccessCheck(exp: Expression, attributeName: String, env: Environment[Type]): T = for
  {
    expressao1 <- checkExpression(exp, env)
  }yield(expressao1.mapBoth{(lista, tipo) => tipo.get match{
    case ReferenceToUserDefinedType(userTypeName) => env.lookupUserDefinedType(userTypeName) match{
      case Some(UserDefinedType(name, RecordType(variables))) => {
          (lista, variables
          .find(v => v.name.equals(attributeName))   //TODO: consertar retorno desse caso?
          .map(_.variableType))}
      case _ => (lista ++ List("Error. "), None)
    }
    case RecordType(variables) => {
        val attribute = variables.find(v => v.name.equals(attributeName))
        if (attribute.isDefined) {
          (lista, Some(attribute.get.variableType))
        } else {
          (lista ++ List("Error. "), None)
        }
      }
    case _ => (lista ++ List("Error. "), None)
    }
  })

  def pointerAccessCheck(name: String, env: Environment[Type]): T = for{ 
    ponteiro <- checkType(env.lookup(name).get)
  }yield(ponteiro.value match{
    case Some(PointerType(varType)) => Writer(List(""), Some(varType))
    case _ => Writer(List("Error. "), None)
  })

  // Verificar o impacto dos monads nesta parte
  def checkLambdaExpression(args: List[FormalArg], exp: Expression, env: Environment[Type]): T = {
  var env1 = env.push()
  args.foreach(a => env1 = env1.setLocalVariable(a.name, a.argumentType))
  val argTypes = args.map(a => a.argumentType)
  val expType = checkExpression(exp, env1)
  // O pop retorna a stack do Environment sem o elemento do topo
  env1 = env1.pop()
  for{
    tipo <- expType
  }yield(tipo.value match{
    case None => Writer(List("Error. "), None)
    case _ => Writer(List(""), Some(LambdaType(argTypes, tipo.value.get)))
  })
}

  // Faz a verificação de tipos para comparações lógicas/binárias
  def computeBinExpressionType(
    left: Expression,
    right: Expression,
    expected: List[Type],
    result: Type
  ) = for {
    t1 <- checkExpression(left, env)
    t2 <- checkExpression(right, env)
    /* Verifica se os tipos são iguais
        Em seguida, é verificado se o tipo do primeiro elemento está na lista de tipos esperados*/
  } yield(if (t1.value == t2.value && expected.contains(t1.value)) {
          t1.mapBoth{(lista, tipo) => t2.mapBoth{(lista2, tipo2) => 
            val lista3 = lista ++ lista2
            (lista3, tipo)
          }.run }
          }
          else {
          t1.mapBoth{(lista, tipo) => t2.mapBoth{(lista2, tipo2) => 
            val lista3 = lista ++ lista2
            (lista3, None)
          }.run }
          })

  def updateEnvironment(nEnv: Environment[Type]){
      env = nEnv
  }

}

class TypeChecker (envPassado: Environment[Type]){
  var env = envPassado
  type T = State[Environment[Type], Writer[List[String], Type]]

  // O Environment está sendo passado como argumento da classe, logo ainda é global
  // porém está explicito na classe. Além disso, todas as mudanças no environment passam
  // a ser internas.
  val expVisitor = new ExpressionTypeChecker(this, env)

  // O checkModule deverá ser parte do construtor da classe
  def checkModule(module: OberonModule): /*List[(Statement, String)]*/ T = {
    expVisitor.updateEnvironment(module.constants.foldLeft(expVisitor.env)((acc, c) => acc.setGlobalVariable(c.name, expVisitor.checkExpression(c.exp, env).get)))
    expVisitor.updateEnvironment(module.variables.foldLeft(expVisitor.env)((acc, v) => acc.setGlobalVariable(v.name, v.variableType)))
    expVisitor.updateEnvironment(module.procedures.foldLeft(expVisitor.env)((acc, p) => acc.declareProcedure(p)))
    expVisitor.updateEnvironment(module.userTypes.foldLeft(expVisitor.env)((acc, t) => acc.addUserDefinedType(t)))

    val errors = module.procedures.flatMap(p => checkProcedure(p))

    if (module.stmt.isDefined) errors ++ checkStmt(module.stmt.get)
    else errors
  }

  def checkProcedure(procedure: Procedure): /*List[(Statement, String)]*/ T = {
    expVisitor.updateEnvironment(expVisitor.env.push())
    expVisitor.updateEnvironment(procedure.args.foldLeft(expVisitor.env)((acc, a) => acc.setLocalVariable(a.name, a.argumentType)))
    expVisitor.updateEnvironment(procedure.constants.foldLeft(expVisitor.env)((acc, c) => acc.setLocalVariable(c.name, expVisitor.checkExpression(c.exp, env).get)))
    expVisitor.updateEnvironment(procedure.variables.foldLeft(expVisitor.env)((acc, v) => acc.setLocalVariable(v.name, v.variableType)))

    val errors = checkStmt(procedure.stmt)

    // Colocar o Update nesta parte
    expVisitor.updateEnvironment(env.pop())
    errors
  }

  // Responsável por retornar as mensagens de erro
  def checkStmt(stmt: Statement): /*List[(Statement, String)]*/ T = stmt match {
    case AssignmentStmt(_, _)    => checkAssignment(stmt)
    // case IfElseStmt(_, _, _)     => visitIfElseStmt(stmt)
    // case WhileStmt(_, _)         => visitWhileStmt(stmt)
    // case ForEachStmt(v, e, s)    => visitForEachStmt(ForEachStmt(v, e, s))
    // case ExitStmt()              => visitExitStmt()
    // case ProcedureCallStmt(_, _) => procedureCallStmt(stmt)
    // case SequenceStmt(stmts)     => stmts.flatMap(s => checkStmt(s))
    // case ReturnStmt(exp) =>
    //   if (expVisitor.checkExpression(exp).isDefined) List()
    //   else List((stmt, s"Expression $exp is ill typed."))
    // case ReadLongRealStmt(v) =>
    //   if (env.lookup(v).isDefined) List()
    //   else List((stmt, s"Variable $v not declared."))
    // case ReadRealStmt(v) =>
    //   if (env.lookup(v).isDefined) List()
    //   else List((stmt, s"Variable $v not declared."))
    // case ReadLongIntStmt(v) =>
    //   if (env.lookup(v).isDefined) List()
    //   else List((stmt, s"Variable $v not declared."))
    // case ReadIntStmt(v) =>
    //   if (env.lookup(v).isDefined) List()
    //   else List((stmt, s"Variable $v not declared."))
    // case ReadShortIntStmt(v) =>
    //   if (env.lookup(v).isDefined) List()
    //   else List((stmt, s"Variable $v not declared."))
    // case ReadCharStmt(v) =>
    //   if (env.lookup(v).isDefined) List()
    //   else List((stmt, s"Variable $v not declared."))
    // case WriteStmt(exp) =>
    //   if (expVisitor.checkExpression(exp).isDefined) List()
    //   else List((stmt, s"Expression $exp is ill typed."))
    // case NewStmt(varName) =>
    //   env.lookup(varName) match {
    //     case Some(PointerType(_)) => List()
    //     case _ => List((stmt, s"Expression $varName is ill typed"))
    //   }
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