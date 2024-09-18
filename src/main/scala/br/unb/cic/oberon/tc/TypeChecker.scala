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
    case _             => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), env.baseType(t)))}
  }

  // Pergunta: A expressão [typeChecker.env.baseType(t)], pode ser substituída por checkType(t)?
  // State[Environment, Writer[List[String], Type]]
  def checkExpression(exp: Expression, env: Environment[Type]): T  =
    computeGeneralExpressionType(exp, env).flatMap(t => State[Environment[Type], Writer[List[String], Option[Type]]] {env => {
      if (t.value == None) {
        (env, Writer(t.written, None))
      }
      else {
        (env, Writer(List(), env.baseType(t.value.get)))  
      }
    }})

  // Função Monadificável
  def computeGeneralExpressionType(exp: Expression, env: Environment[Type]): T = exp match {
  //   case Brackets(exp)       => checkExpression(exp)
  //   // Retorna os tipos nativos
    case IntValue(_)         => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(IntegerType)))}
    case RealValue(_)        => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(RealType)))}
    case CharValue(_)        => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(CharacterType)))}
    case BoolValue(_)        => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(BooleanType)))}
    case StringValue(_)      => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(StringType)))}
    case NullValue           => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
    case Undef()             => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), None))}
    case VarExpression(name) => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), typeChecker.env.lookup(name)))}
    case ListValue(elements) => val elementTypes = elements.map(e => checkExpression(e, env).runA(env).value.value.getOrElse(UndefinedType))

  if (elementTypes.forall(_ == elementTypes.headOption.getOrElse(UndefinedType))) {
    // Se todos os elementos têm o mesmo tipo, retorna o tipo da lista com esse tipo
    State[Environment[Type], Writer[List[String], Option[Type]]] { env =>
      (env, Writer(List(), Some(ListType(elementTypes.headOption.getOrElse(UndefinedType)))))
    }
  } else {
    // Caso contrário, retorna um erro
    State[Environment[Type], Writer[List[String], Option[Type]]] { env =>
      (env, Writer(List("Error: Elements have different types in the list."), None))
    }
  }
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
    case LenExpression(exp) => 
        computeGeneralExpressionType(exp, env).flatMap { t =>
        t.value match {
        case Some(ListType(_)) =>
          State[Environment[Type], Writer[List[String], Option[Type]]] { env => (env, Writer(List(), Some(IntegerType)))}
          case _ =>
          State[Environment[Type], Writer[List[String], Option[Type]]] { env =>(env, Writer(List("Error: len expects a list."), None))
            }
    }
  }

    case RemoveExpression(item, list) =>
      for {
        itemType <- checkExpression(item, env)
        listType <- checkExpression(list, env)
      } yield {
        (itemType.value, listType.value) match {
          case (Some(itType), Some(ListType(lType))) if itType == lType =>
            Writer(List(), Some(ListType(lType)))  // O tipo do item é o mesmo da lista, operação válida
          case (Some(itType), Some(ListType(_))) =>
            Writer(List("Error: remove expects an item of the same type as the list elements."), None)
          case (Some(_), _) =>
            Writer(List("Error: remove expects a list as the second argument."), None)
          case _ =>
            Writer(List("Error: remove expression has invalid types."), None)
    }
  }

    case ConsExpression(head) =>
      for {
        headType <- checkExpression(head, env)
      } yield {
        headType.value match {
          case Some(ht) =>
            Writer(List(), Some(ListType(ht)))  // O tipo do elemento é o tipo da lista com um único elemento
          case None =>
            Writer(List("Error: cons expects a valid head element."), None)
        }
      }

    case ConcatExpression(left, right) =>
      for {
        leftType <- checkExpression(left, env)
        rightType <- checkExpression(right, env)
      } yield {
        (leftType.value, rightType.value) match {
          case (Some(ListType(lType)), Some(ListType(rType))) if lType == rType =>
            Writer(List(), Some(ListType(lType)))  // Ambos são listas do mesmo tipo, concatenação válida
          case (Some(ListType(_)), Some(ListType(_))) =>
            Writer(List("Error: concat expects lists of the same type."), None)
          case _ =>
            Writer(List("Error: concat expects two lists."), None)
        }
      }
  // Verifica se os argumentos da função são do tipo esperado pela definição dela.
    //TODO: Definir mensagens de erro.
    case FunctionCallExpression(name, args) => {
        val procedure = env.findProcedure(name)

        if (args.length != procedure.args.length) {
          return State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List("Error. "), None))}
        }

        val givenArgumentTypes = args.map(arg => checkExpression(arg, env).runA(env).value.value.getOrElse(UndefinedType)) //Type: List[Types]
        val neededArgumentTypes = procedure.args.map(_.argumentType)  //Type: List[Types]

        if(givenArgumentTypes == neededArgumentTypes) {
          State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(procedure.returnType.getOrElse(UndefinedType))))}
        } else {
          State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List("Error. "), None))}
        }
  } 
    case ListValue(elements) => 
      val elementTypes = elements.map(e => checkExpression(e, env).runA(env).value.value.getOrElse(UndefinedType))
      if (elementTypes.forall(_ == elementTypes.headOption.getOrElse(UndefinedType))) {
        State[Environment[Type], Writer[List[String], Option[Type]]] { env => (env, Writer(List(), Some(ListType(elementTypes.headOption.getOrElse(UndefinedType)))))}
        } else {
          State[Environment[Type], Writer[List[String], Option[Type]]] { env => (env, Writer(List("Error: Elements have different types in list."), None))
        }
  }

    // Verifica se todos os elementos da array são do tipo esperado.
    case ArrayValue(values, arrayType) =>
      if (values.isEmpty || values.forall(v => checkExpression(v, env).runA(env).value.value.getOrElse(None) == arrayType.baseType)) {
        State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(arrayType)))}
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
    expressao2 <- checkExpression(index, env)
  }yield(expressao1.mapBoth{(lista, tipo) => expressao2.mapBoth{ (lista2, tipo2) =>
    val lista3 = lista ++ lista2
    if(tipo.getOrElse(None).isInstanceOf[ArrayType] && tipo2.getOrElse(None) == IntegerType){
      // O tipo retornado precisa ser definido melhor
      (List(), Some(IntegerType))
    }else{
     (lista3 ++ List("Error. "), None)
    }
  }.run
  })


  def fieldAccessCheck(exp: Expression, attributeName: String, env: Environment[Type]): T = for
  {
    expressao1 <- checkExpression(exp, env)
  }yield(expressao1.mapBoth{(lista, tipo) => tipo.getOrElse(None) match{
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
    ponteiro <- checkType(env.lookup(name).getOrElse(UndefinedType))
  }yield(ponteiro.value match{
    case Some(PointerType(varType)) => Writer(List(), Some(varType))
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
  }yield(tipo.value.getOrElse(None) match{
    case None => Writer(List("Error. "), None)
    case _ => Writer(List(), Some(LambdaType(argTypes, tipo.value.get)))
  })
}

  // Faz a verificação de tipos para comparações lógicas/binárias
  def computeBinExpressionType(
    left: Expression,
    right: Expression,
    expected: List[Type],
    result: Type
  ): T = for {
    t1 <- checkExpression(left, env)
    t2 <- checkExpression(right, env)
    /* Verifica se os tipos são iguais
        Em seguida, é verificado se o tipo do primeiro elemento está na lista de tipos esperados*/
  } yield(if (t1.value.getOrElse(None) == t2.value.getOrElse(None) && expected.contains(t1.value.getOrElse(UndefinedType))) {
          t1.mapBoth{(lista, tipo) => t2.mapBoth{(lista2, tipo2) => 
            val lista3 = lista ++ lista2
            (lista3, Some(result))
          }.run }
          }
          else {
          t1.mapBoth{(lista, tipo) => t2.mapBoth{(lista2, tipo2) => 
            val lista3 = lista ++ lista2
            (lista3 ++ List("Incorrect types."), None)
          }.run }
          })

  def updateEnvironment(nEnv: Environment[Type]){
      env = nEnv
  }

}

class TypeChecker (envPassado: Environment[Type]){
  var env = envPassado
  type T = State[Environment[Type], Writer[List[String], Option[Type]]]

  // O Environment está sendo passado como argumento da classe, logo ainda é global
  // porém está explicito na classe. Além disso, todas as mudanças no environment passam
  // a ser internas.
  val expVisitor = new ExpressionTypeChecker(this, env)

  // O checkModule deverá ser parte do construtor da classe
  def checkModule(module: OberonModule): /*List[(Statement, String)]*/ T = {
    expVisitor.updateEnvironment(module.constants.foldLeft(expVisitor.env)((acc, c) => acc.setGlobalVariable(c.name, expVisitor.checkExpression(c.exp, expVisitor.env).runA(expVisitor.env).value.value.getOrElse(UndefinedType) )))
    env = expVisitor.env
    expVisitor.updateEnvironment(module.variables.foldLeft(expVisitor.env)((acc, v) => acc.setGlobalVariable(v.name, v.variableType)))
    env = expVisitor.env
    expVisitor.updateEnvironment(module.procedures.foldLeft(expVisitor.env)((acc, p) => acc.declareProcedure(p)))
    env = expVisitor.env
    expVisitor.updateEnvironment(module.userTypes.foldLeft(expVisitor.env)((acc, t) => acc.addUserDefinedType(t)))
    env = expVisitor.env
    var errors = module.procedures.flatMap(p => checkProcedure(p).runA(expVisitor.env).value.written)

    if (module.stmt.isDefined) {
      for {
        estado <- checkStmt(module.stmt.get)
      }yield( estado.mapBoth{(lista, tipo) =>
        errors = errors ++ lista
        (errors, tipo)
      })
    }else{
      State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(errors, Some(UndefinedType)))}
    }
  }

  def checkProcedure(procedure: Procedure): /*List[(Statement, String)]*/ T = {
    expVisitor.updateEnvironment(expVisitor.env.push())
    env = expVisitor.env
    expVisitor.updateEnvironment(procedure.args.foldLeft(expVisitor.env)((acc, a) => acc.setLocalVariable(a.name, a.argumentType)))
    env = expVisitor.env
    expVisitor.updateEnvironment(procedure.constants.foldLeft(expVisitor.env)((acc, c) => acc.setLocalVariable(c.name, expVisitor.checkExpression(c.exp, env).runA(env).value.value.get)))
    env = expVisitor.env
    expVisitor.updateEnvironment(procedure.variables.foldLeft(expVisitor.env)((acc, v) => acc.setLocalVariable(v.name, v.variableType)))
    env = expVisitor.env

    val errors = checkStmt(procedure.stmt)

    // Colocar o Update nesta parte
    expVisitor.updateEnvironment(env.pop())
    errors
  }

  // Responsável por retornar as mensagens de erro
  def checkStmt(stmt: Statement): /*List[(Statement, String)]*/ T = stmt match {
    case AssignmentStmt(_, _)    => checkAssignment(stmt)
    case IfElseStmt(_, _, _)     => visitIfElseStmt(stmt)
    case WhileStmt(_, _)         => visitWhileStmt(stmt)
    case ForEachStmt(v, e, s)    => visitForEachStmt(ForEachStmt(v, e, s))
    case ExitStmt()              => visitExitStmt()
    case ProcedureCallStmt(_, _) => procedureCallStmt(stmt)
    case SequenceStmt(stmts)     => State[Environment[Type], Writer[List[String], Option[Type]]] {
        env => (env, Writer(stmts.flatMap(s => checkStmt(s).runA(env).value.written), Some(NullType)))}
        
    case ReturnStmt(exp) =>
      if (expVisitor.checkExpression(exp, env).runA(env).value.value.isDefined){
        State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
      } 
      else{
        State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Expression $exp is ill typed."), None))}
      }
    case ReadLongRealStmt(v) =>
      if (env.lookup(v).isDefined){
        State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
      } 
      else {
        State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Variable $v not declared."), None))}
      }
    case ReadRealStmt(v) =>
      if (env.lookup(v).isDefined) State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
      else State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Variable $v not declared."), None))}
    case ReadLongIntStmt(v) =>
      if (env.lookup(v).isDefined) {
        State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
      } 
      else {
        State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Variable $v not declared."), None))}
      }
    case ReadIntStmt(v) =>
      if (env.lookup(v).isDefined) State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
      else {
        State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Variable $v not declared."), None))}
      }
    case ReadShortIntStmt(v) =>
      if (env.lookup(v).isDefined) State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
      else{
        State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Variable $v not declared."), None))}
      }
    case ReadCharStmt(v) =>
      if (env.lookup(v).isDefined) State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
      else {
        State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Variable $v not declared."), None))}
      }
    case WriteStmt(exp) =>
      var state = expVisitor.checkExpression(exp, env)  
      if (state.runA(env).value.value.isDefined && state.runA(env).value.written.isEmpty) State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
      else{
        State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Expression $exp is ill typed."), None))}
      }
    
    case NewStmt(varName) =>
      env.lookup(varName) match {
        case Some(PointerType(_)) => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
        case _ => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Expression $varName is ill typed."), None))}
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
      var varType = env.lookup(v).getOrElse(UndefinedType)
      var varBaseType = env.baseType(varType)
      var expType = expVisitor.checkExpression(exp, env).runA(env).value.value
    (varBaseType.getOrElse(None), expType.getOrElse(None)) match {
      case (PointerType(_), NullType) => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
      case (IntegerType, BooleanType) => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
      case (BooleanType, IntegerType) => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}

      case (_, None) => if(! env.lookup(v).isDefined) State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Variable $v not declared"), None))} else State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Expression $exp is ill typed"), None))}

      case (t1, t2) if t1 == t2 => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}
      case (t1, t2) if t1 != t2 => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Assignment between different types: $v, $exp"), None))}

      case _ => State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List("It is ill typed"), None))}
    }
  }

  private def checkPointerAssigment(v: String, exp: Expression): T = {
    for {
      pointerType <- expVisitor.pointerAccessCheck(v, env)
      expType <- expVisitor.checkExpression(exp, env)
    } yield (Some((pointerType.value, expType.value)) match {
      case Some((t1, t2)) => if (t1 == t2){Writer(List(), Some(NullType))}
        else{Writer(List(s"Expression $exp doesn't match variable type."), None)}
      case _ => Writer(List(s"Could not compute the types correctly."), None)
    })
  }

  private def checkArrayAssigment(arr: Expression, element: Expression, exp: Expression): T = {
    for {
      arrType <- expVisitor.checkExpression(arr, env)
      elementType <- expVisitor.checkExpression(element, env)
      expType <- expVisitor.checkExpression(exp, env)
    } yield ( (arrType.value, elementType.value, expType.value) match {
      case (Some(ArrayType(length, t1)), Some(IntegerType), Some(t2)) =>
        if (t1 == t2) {Writer(List(), Some(NullType))}
        else {Writer(List(s"Expression $exp doesn't match the array type."), None)}
      case (_, Some(t), _) => if (t != IntegerType) {Writer(List( s"The index expression must be an integer."), None)} else{Writer(List(s"Could not compute the types correctly."), None)}
      case (None, None, None) => Writer(List(s"Could not compute the types correctly."), None)
      case _ => Writer(List(s"Could not compute the types correctly."), None)
    })
  }

  private def checkRecordAssigment(record: Expression, field: String, exp: Expression): T = {
    for {
      fieldAccessType <- expVisitor.fieldAccessCheck(record, field, env)
      expType <- expVisitor.checkExpression(exp, env)
    } yield ( (fieldAccessType.value, expType.value) match {
      case (Some(t1), Some(t2)) => if (t1 == t2) {Writer(List(), Some(NullType))}
        else Writer(List(s"Expression $exp doesn't match variable type."), None)
      case _ => Writer(List(s"Could not compute the types correctly."), None)
    })
  }

  private def visitIfElseStmt(stmt: Statement): T = stmt match {
    case IfElseStmt(condition, thenStmt, elseStmt) =>
      var errorList = checkStmt(thenStmt).runA(env).value.written
      if (!expVisitor.checkExpression(condition, env).runA(env).value.value.contains(BooleanType)) {
        errorList = (s"Expression $condition does not have a boolean type") :: errorList
      }
      
      val errors = errorList ++ elseStmt.map(s => checkStmt(s).runA(env).value.written).getOrElse(List())
      if (errors.isEmpty){
      State[Environment[Type], Writer[List[String], Option[Type]]] {
        env => (env, Writer(errors, Some(NullType)))
      }}
      else {State[Environment[Type], Writer[List[String], Option[Type]]] {
        env => (env, Writer(errors, None))
      }}
  }

  private def visitWhileStmt(stmt: Statement): T = stmt match {
    case WhileStmt(condition, stmt) =>
      val errorList = checkStmt(stmt).runA(env).value.written

      if (expVisitor.checkExpression(condition, env).runA(env).value.value.contains(BooleanType)) {
        State[Environment[Type], Writer[List[String], Option[Type]]] {
        env => (env, Writer(errorList, Some(NullType)))
      }
      } else {
        State[Environment[Type], Writer[List[String], Option[Type]]] {
        env => (env, Writer(s"Expression $condition do not have a boolean type" :: errorList, Some(NullType)))
        }
      }
  }

  def visitForEachStmt(forEachStmt: ForEachStmt): /* List[(Statement, String)] */ T = {
    val expType = expVisitor.checkExpression(forEachStmt.exp, env)
    val varType = env.lookup(forEachStmt.varName)

    val res = if (expType.runA(env).value.value.isDefined && expType.runA(env).value.value.getOrElse(None).isInstanceOf[ArrayType]) {
      val arrayBaseType =
        expVisitor.checkType(expType.runA(env).value.value.get.asInstanceOf[ArrayType].baseType)
      if (arrayBaseType.runA(env).value.value.getOrElse(None) != varType.getOrElse(None))
        List("invalid types in the foreach statement")
      else
        List()
    } else {
      List("invalid types in the foreach statement")
    }
    val errors = res ++ checkStmt(forEachStmt.stmt).runA(env).value.written

    if (errors.isEmpty){
      State[Environment[Type], Writer[List[String], Option[Type]]] {
        env => (env, Writer(errors, Some(NullType)))
      }}
      else {State[Environment[Type], Writer[List[String], Option[Type]]] {
        env => (env, Writer(errors, None))
      }}
 
  }

  private def visitExitStmt(): T = State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(), Some(NullType)))}

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
          State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Procedure $name has not been declared."), None))}
        else {
          // check if the type of the formal arguments and the actual arguments
          // match.
          val formalArgumentTypes = procedure.args.map(a => a.argumentType)
          val actualArgumentTypes = args.map(a => expVisitor.checkExpression(a, env).runA(env).value.value.get)
          // the two lists must have the same size.
          if (formalArgumentTypes.size != actualArgumentTypes.size) {
            State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"Wrong number of arguments to the $name procedure"), None))
          }}
          val allTypesMatch = formalArgumentTypes
            .zip(actualArgumentTypes)
            .map(pair => pair._1 == pair._2)
            .forall(v => v)
          if (!allTypesMatch) {
            State[Environment[Type], Writer[List[String], Option[Type]]] {env => (env, Writer(List(s"The arguments do not match the $name formal arguments"), None))}
          }
          // if everything above is ok, lets check the procedure body.
          checkStmt(stmt)
        }
    }
}