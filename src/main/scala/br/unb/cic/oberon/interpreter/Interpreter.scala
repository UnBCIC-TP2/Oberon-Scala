package br.unb.cic.oberon.interpreter

import java.io.{ByteArrayOutputStream, OutputStream, PrintStream}
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.environment.{Environment, MetaStmt}
import br.unb.cic.oberon.stdlib.StandardLibrary
import br.unb.cic.oberon.util.Values
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import scala.collection.mutable.ListBuffer
import scala.io.StdIn
import scala.language.{existentials, postfixOps}

/**
 * The interpreter visitor first updates the
 * environment with the global constants, variables,
 * and procedures; after that, it visits the
 * main program statement.
 *
 * It uses an additional visitor (EvalExpressionVisitor) to
 * compute the value of the different expressions.
 *
 * We assume the program is well-typed, otherwise,
 * a runtime exception might be thrown.
 */
class Interpreter {
  type T = Unit

  var exit = false

  var printStream: PrintStream = new PrintStream(System.out)


  def setupStandardLibraries(environment: Environment[Expression]): Environment[Expression] = {
    var temp = environment
    val lib = new StandardLibrary[Expression]()
    for(p <- lib.stdlib.procedures) {
      temp = temp.declareProcedure(p)
    }
    temp
  }

def runInterpreter(module: OberonModule): Environment[Expression] = {
    // set up the global declarations
    val env1 = module.userTypes.foldLeft(new Environment[Expression]())((a, b) => declareUserDefinedType(a, b))
    val env2 = module.constants.foldLeft(env1)((a, b) => declareConstant(a, b))
    val env3 = module.variables.foldLeft(env2)((a, b) => declareVariable(a, b))
    val env4 = module.procedures.foldLeft(env3)((a, b) => declareProcedure(a, b))

    // execute the statement if it is defined.
    // remember, module.stmt is an Option[Statement].
    if (module.stmt.isDefined) {
      val env5 = setupStandardLibraries(env4)
      executeStatement(env5, module.stmt.get)
    }
    else {
      env4
    }
  }

  def declareConstant(environment : Environment[Expression], constant: Constant): Environment[Expression] = {
    environment.setGlobalVariable(constant.name, constant.exp)
  }

  def declareVariable(environment : Environment[Expression], variable: VariableDeclaration): Environment[Expression] = {
    environment.baseType(variable.variableType) match {
      case Some(ArrayType(length, baseType)) => environment.setGlobalVariable(variable.name, ArrayValue(ListBuffer.fill(length)(Undef()), ArrayType(length, baseType)))
      case _ => environment.setGlobalVariable(variable.name, Undef())
    }
  }
  
  def declareParameter(environment : Environment[Expression], variable: VariableDeclaration): Environment[Expression] = {
    environment.baseType(variable.variableType) match {
      case Some(ArrayType(length, baseType)) => environment.setLocalVariable(variable.name, ArrayValue(ListBuffer.fill(length)(Undef()), ArrayType(length, baseType)))
      case _ => environment.setLocalVariable(variable.name, Undef())
    }
  } 

  def declareUserDefinedType(environment : Environment[Expression], userType: UserDefinedType): Environment[Expression] = {
    environment.addUserDefinedType(userType)
  }

  def declareProcedure(environment : Environment[Expression], procedure: Procedure): Environment[Expression] = {
    environment.declareProcedure(procedure)
  }



  def executeStatement(environment : Environment[Expression], stmt: Statement): Environment[Expression] = {
    // we first check if we have encountered a return stmt.
    // if so, we should not execute any other statement
    // of a sequence of stmts. Whenever we encounter a
    // return stmt, we assign a new local variable
    // "return" as the return value.
    //
    // we also check if exit is true. if this is the case
    // we should also stop the execution of a block of
    // statements within a any kind of repeat statement.

    var envt = environment

    if (exit || environment.lookup(Values.ReturnKeyWord).isDefined) {
      return environment
    }
    // otherwise, we pattern-match on the current stmt.
    stmt match {
      case AssignmentStmt(designator, exp) =>
        designator match {
          case VarAssignment(name) => envt = envt.setVariable(name, evalExpression(envt, exp)._2)
          case ArrayAssignment(array, index) => envt = arrayAssignment(envt, array, index, exp)
          case RecordAssignment(_, _) => ???
          case PointerAssignment(_) => ???
        }
        envt

      case SequenceStmt(stmts) =>
        stmts.foreach(s => envt = executeStatement(envt, s))
        envt

      case ReadRealStmt(name) =>
        envt = envt.setVariable(name, RealValue(StdIn.readLine().toFloat))
        envt

      case ReadIntStmt(name) =>
        envt = envt.setVariable(name, IntValue(StdIn.readLine().toInt))
        envt

      case ReadCharStmt(name) =>
        envt = envt.setVariable(name, CharValue(StdIn.readLine().charAt(0)))
        envt

      case WriteStmt(exp) =>
        printStream.println(evalExpression(envt, exp))
        envt

      case IfElseStmt(condition, thenStmt, elseStmt) =>
        if (evalCondition(envt, condition)) executeStatement(envt, thenStmt)
        else if (elseStmt.isDefined) executeStatement(envt, elseStmt.get)
        else envt

      case WhileStmt(condition, whileStmt) =>
        while (evalCondition(envt, condition) && ! exit)
          envt = executeStatement(envt, whileStmt)
        exit = false
        envt

      case ForEachStmt(v, exp, stmt) =>
        val (env, valArray) = evalExpression(envt, exp)
        envt = env
        valArray match {
          case ArrayValue(values, _) => {
            envt = envt.push()
            values.foreach(value => {
              envt = envt.setLocalVariable(v, evalExpression(envt, value)._2)
              envt = executeStatement(envt, stmt)
              //               val assignment = AssignmentStmt(VarAssignment(v), value)
              //               val stmts = SequenceStmt(List(assignment, stmt))
              //               stmts.accept(this)
            })
            envt = envt.pop()
          }
          case _ => throw new RuntimeException("erro.... melhorar")
        }
        envt
      case ExitStmt() =>
        exit = true
        envt

      case ReturnStmt(exp: Expression) =>
        setReturnExpression(envt, evalExpression(envt, exp)._2)

      case MetaStmt(f) => {
        val s = f(envt)
        executeStatement(envt, s)
      }

      case ProcedureCallStmt(name, args) =>
        callProcedure(name, args, envt)
    }
  }


  /*
   * process the ReturnStmt(exp) statement.
   * In this case, we just create a new entry
   * in the local variables, assigning
   * "return" -> exp.
   */
  private def setReturnExpression(environment : Environment[Expression], exp: Expression): Environment[Expression] =
    environment.setLocalVariable(Values.ReturnKeyWord, exp)

  def callProcedure(name: String, args: List[Expression], environment : Environment[Expression]): Environment[Expression] = {
    val procedure = environment.findProcedure(name)
    var env1 = updateEnvironmentWithProcedureCall(procedure, args, environment)
    env1 = executeStatement(env1, procedure.stmt)
    env1 = env1.pop()
    env1
  }

  def updateEnvironmentWithProcedureCall(procedure: Procedure, args: List[Expression], environment : Environment[Expression]): Environment[Expression] = {
    val mappedArgs = procedure.args.zip(args).map(pair => pair match {
      case (ParameterByReference(_, _), VarExpression(name2)) => (pair._1, environment.pointsTo(name2).get)
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(_, _), exp) => (pair._1, evalExpression(environment, exp)._2)
    })
    var envt = environment.push() // after that, we can "push", to indicate a procedure call.
    mappedArgs.foreach(pair => pair match {
      case (ParameterByReference(name, _), exp: Location) => envt = envt.setParameterReference(name, exp)
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(name, _), exp: Expression) => envt = envt.setLocalVariable(name, exp)
	  case _ => throw new RuntimeException
    })
    procedure.constants.foreach(c => envt = envt.setLocalVariable(c.name, c.exp))
    procedure.variables.foreach(v => envt = envt.setLocalVariable(v.name, Undef()))

    envt
  }


  def evalCondition(environment : Environment[Expression], expression: Expression): Boolean = {
    evalExpression(environment, expression)._2.asInstanceOf[BoolValue].value
  }

  def arrayAssignment(environment: Environment[Expression], baseExp: Expression, indexExp: Expression, exp: Expression): Environment[Expression] = {
    val array = evalExpression(environment, baseExp)._2
    val index = evalExpression(environment, indexExp)._2

    (array, index) match {
      case (ArrayValue(values, _), IntValue(v)) => values(v) = evalExpression(environment, exp)._2
      case _ => throw new RuntimeException
    }
    environment
  }

  /*
   * This method is mostly useful for testing purposes.
   * That is, here we are considering testability a
   * design concern.
   */
  def setGlobalVariable(env: Environment[Expression], name: String, exp: Expression): Environment[Expression] = {
    env.setGlobalVariable(name, exp)
  }

  /*
   * the same here.
   */
  def setLocalVariable(env: Environment[Expression], name: String, exp: Expression): Environment[Expression] = {
    env.setLocalVariable(name, exp)
  }

  def setTestEnvironment() = {
    printStream = new PrintStream(new NullPrintStream())
  }

  def evalExpression(environment: Environment[Expression], exp: Expression): (Environment[Expression], Expression) = exp match {
    case IntValue(v) => (environment, IntValue(v))
    case RealValue(v) => (environment, RealValue(v))
    case CharValue(v) => (environment, CharValue(v))
    case BoolValue(v) => (environment, BoolValue(v))
    case StringValue(v) => (environment, StringValue(v))
    case NullValue => (environment, NullValue)
    case Undef() => (environment, Undef())
    case VarExpression(name) => evalVarExpression(environment, name)
    //TODO eval array
    //case ArrayValue(v, t) =>
    case ArraySubscript(a, i) => (environment, evalArraySubscript(environment, ArraySubscript(a, i)))
    case AddExpression(left, right) => arithmeticExpression(environment, left, right, (v1: Number, v2: Number) => v1+v2)
    case SubExpression(left, right) => arithmeticExpression(environment, left, right, (v1: Number, v2: Number) => v1-v2)
    case MultExpression(left, right) => arithmeticExpression(environment, left, right, (v1: Number, v2: Number) => v1*v2)
    case DivExpression(left, right) => arithmeticExpression(environment, left, right, (v1: Number, v2: Number) => v1/v2)
    case ModExpression(left, right) => modularExpression(environment, left, right, (v1: Modular, v2: Modular) => v1.mod(v2))
    case EQExpression(left, right) => binExpression(environment, left, right, (v1: Value, v2: Value) => BoolValue(v1 == v2))
    case NEQExpression(left, right) => binExpression(environment, left, right, (v1: Value, v2: Value) => BoolValue(v1 != v2))
    case GTExpression(left, right) => binExpression(environment, left, right, (v1: Value, v2: Value) => BoolValue(v1 > v2))
    case LTExpression(left, right) => binExpression(environment, left, right, (v1: Value, v2: Value) => BoolValue(v1 < v2))
    case GTEExpression(left, right) => binExpression(environment, left, right, (v1: Value, v2: Value) => BoolValue(v1 >= v2))
    case LTEExpression(left, right) => binExpression(environment, left, right, (v1: Value, v2: Value) => BoolValue(v1 <= v2))
    case NotExpression(exp) => (environment, BoolValue(!(evalExpression(environment, exp)._2).asInstanceOf[Value].value.asInstanceOf[Boolean]))
    case AndExpression(left, right) => binExpression(environment, left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] && v2.value.asInstanceOf[Boolean]))
    case OrExpression(left, right) => binExpression(environment, left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] || v2.value.asInstanceOf[Boolean]))
    case FunctionCallExpression(name, args) => evalFunctionCall(environment, name, args)
    case LambdaExpression(args,exp) => evalLambdaExpression(environment,args,exp)
    case LambdaApplication(exp, listExp) => evalLambdaApplication(environment,exp, listExp)
    // TODO FieldAccessExpression
    // TODO PointerAccessExpression
  }

   def evalLambdaExpression(environment: Environment[Expression], args: List[FormalArg], exp: Expression): (Environment[Expression],Expression) = {
    var envt = environment
    args.foreach(formal => envt = declareParameter(envt, VariableDeclaration(formal.name,formal.argumentType)))
    (envt,LambdaExpression(args,exp))
  }
  
  def evalLambdaApplication(environment: Environment[Expression], expression: Expression, listExpression: List[Expression]) : (Environment[Expression], Expression) = {
    var (env,lambdaExp) = evalExpression(environment.push,expression)

    (lambdaExp) match{
        case (LambdaExpression(args,exp)) => {
          var (envt,_) = evalExpression(env,lambdaExp)
          val variables = envt.allLocalVariables.toList.zip(listExpression)
          variables.foreach{case (variable,value) => envt = envt.setVariable(variable,value)}
          var (envt1,exp1) = evalExpression(envt,exp)
          (envt1.pop,exp1)
        }

        // case (_, LambdaExpression(args,exp)) => {
        //   var (envt,exp) = evalExpression(environment.push, expression)
        //   //associar args com listExp
        //   val variables = envt.allLocalVariables.toList.zip(listExpression)
        //   variables.foreach{case (variable,value) => envt = envt.setVariable(variable,value)}
        //   var (envt1,exp1) = evalExpression(envt,exp)
        //   (envt1.pop,exp1)        
        // }

        case _ => {
          env = env.pop
          throw new RuntimeException(s"It is not a Lambda Expression")
        }
    }
  } 

  def evalVarExpression(environment: Environment[Expression], name: String) = {
    val variable = environment.lookup(name)
    if (variable.isEmpty) throw new NoSuchElementException(f"Variable $name is not defined")
    (environment, environment.lookup(name).get)
  }

  def evalArraySubscript(environment : Environment[Expression], arraySubscript: ArraySubscript): Expression = {
    val array = evalExpression(environment, arraySubscript.arrayBase)._2
    val idx = evalExpression(environment, arraySubscript.index)._2

    (array, idx) match {
      case (ArrayValue(values: ListBuffer[Expression], _), IntValue(v)) => values(v)
      case _ => throw new RuntimeException
    }
  }

  def evalFunctionCall(environment : Environment[Expression], name: String, args: List[Expression]): (Environment[Expression], Expression) = {
    val procedure = environment.findProcedure(name)
    var env1 = updateEnvironmentWithProcedureCall(procedure, args, environment)
    env1 = executeStatement(env1, procedure.stmt)
    val returnValue = env1.lookup(Values.ReturnKeyWord)
    env1 = env1.pop()
    (env1, returnValue.get)
  }

  /**
   * Eval an arithmetic expression on Numbers
   *
   * @param left  the left expression
   * @param right the right expression
   * @param op    a function representing the operator
   * @return the application of the operator after
   *         evaluating left and right to reduce them to
   *         numbers.
   */
  def arithmeticExpression(environment: Environment[Expression], left: Expression, right: Expression, fn: (Number, Number) => Number): (Environment[Expression], Expression) = {
    val (_, vl) = evalExpression(environment, left)
    val (_, vr) = evalExpression(environment, right)
    (environment, fn(vl.asInstanceOf[Number], vr.asInstanceOf[Number]))
  }


  /**
   * Eval an modular expression on Numbers
   *
   * @param left  the left expression
   * @param right the right expression
   * @param op    a function representing the operator
   * @return the application of the operator after
   *         evaluating left and right to reduce them to
   *         numbers.
   */
  def modularExpression(environment: Environment[Expression], left: Expression, right: Expression, fn: (Modular, Modular) => Modular): (Environment[Expression], Expression) = {
    val vl = evalExpression(environment, left)._2.asInstanceOf[Modular]
    val vr = evalExpression(environment, right)._2.asInstanceOf[Modular]

    (environment, fn(vl, vr))
  }


  /**
   * Eval a binary expression on values.
   *
   * @param left  the left expression
   * @param right the right expression
   * @param fn    a function that constructs an expression. Here we
   *              are using a high-order function. We assign to
   *              the "result" visitor attribute the value we compute
   *              after applying this function.
   */
  def binExpression(environment : Environment[Expression], left: Expression, right: Expression, fn: (Value, Value) => Expression): (Environment[Expression], Expression) = {
    val v1 = evalExpression(environment, left)._2.asInstanceOf[Value]
    val v2 = evalExpression(environment, right)._2.asInstanceOf[Value]
    (environment, fn(v1, v2))
  }
}

class NullPrintStream extends PrintStream(new NullByteArrayOutputStream) {}

class NullByteArrayOutputStream extends ByteArrayOutputStream {
  override def writeTo(o: OutputStream): Unit = ()
}
