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
import cats.data.State
import State._

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
  type IResult[A] = State[Environment[Expression], A]

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

  def declareUserDefinedType(environment : Environment[Expression], userType: UserDefinedType): Environment[Expression] = {
    environment.addUserDefinedType(userType)
  }

  def declareProcedure(environment : Environment[Expression], procedure: Procedure): Environment[Expression] = {
    environment.declareProcedure(procedure)
  }



  def executeStatement(stmt: Statement): IResult[Unit] = {
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


  def updateEnvironmentWithProcedureCall(procedure: Procedure, args: List[Expression]): IResult[Unit] = {
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

  def evalExpression(exp: Expression): IResult[Expression] = for { stateValue <- exp match {
    case IntValue(v) => pure(IntValue(v))
    case RealValue(v) => pure(RealValue(v)) 
    case CharValue(v) => pure(CharValue(v))
    case BoolValue(v) => pure(BoolValue(v))
    case StringValue(v) => pure(StringValue(v))
    case NullValue => pure(NullValue)
    case Undef() => pure(Undef())
    case VarExpression(name) => evalVarExpression(name)
    //TODO eval array
    //case ArrayValue(v, t) =>
    case ArraySubscript(a, i) => evalArraySubscript(ArraySubscript(a, i))
    case AddExpression(left, right) => arithmeticExpression(left, right, (v1: Number, v2: Number) => v1+v2)
    case SubExpression(left, right) => arithmeticExpression(left, right, (v1: Number, v2: Number) => v1-v2)
    case MultExpression(left, right) => arithmeticExpression(left, right, (v1: Number, v2: Number) => v1*v2)
    case DivExpression(left, right) => arithmeticExpression(left, right, (v1: Number, v2: Number) => v1/v2)
    case ModExpression(left, right) => modularExpression(left, right, (v1: Modular, v2: Modular) => v1.mod(v2))
    case EQExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 == v2))
    case NEQExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 != v2))
    case GTExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 > v2))
    case LTExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 < v2))
    case GTEExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 >= v2))
    case LTEExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 <= v2))
    case NotExpression(exp) => for { expression <- evalExpression(exp) } yield BoolValue(!expression.asInstanceOf[Value].value.asInstanceOf[Boolean])
    case AndExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] && v2.value.asInstanceOf[Boolean]))
    case OrExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] || v2.value.asInstanceOf[Boolean]))
    case FunctionCallExpression(name, args) => evalFunctionCall(name, args)
    // TODO FieldAccessExpression
    // TODO PointerAccessExpression
    }
  } yield stateValue

  def evalVarExpression(name: String): IResult[Expression] = for {
    env <- get[Environment[Expression]]
    variable = env.lookup(name)
  } yield 
    if (variable.isEmpty) throw new NoSuchElementException(f"Variable $name is not defined")
    else variable.get

  def evalArraySubscript(arraySubscript: ArraySubscript): IResult[Expression] = for {
    array <- evalExpression(arraySubscript.arrayBase)
    idx <- evalExpression(arraySubscript.index)
    // nao sei se funciona direito colocar o match na frente do yield
  } yield (array, idx) match {
      case (ArrayValue(values: ListBuffer[Expression], _), IntValue(v)) => values(v)
      case _ => throw new RuntimeException
    }

  def evalFunctionCall(name: String, args: List[Expression]): IResult[Expression] =  for {
    env <- get[Environment[Expression]]
    procedure = env.findProcedure(name)
    _ <- updateEnvironmentWithProcedureCall(procedure, args)
    _ <- executeStatement(procedure.stmt)
    env <- get[Environment[Expression]]
    returnValue = env.lookup(Values.ReturnKeyWord)
    _ <- modify[Environment[Expression]](_.pop())
  } yield returnValue

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
  def arithmeticExpression(left: Expression, right: Expression, fn: (Number, Number) => Number): IResult[Expression] = for {
      vl <- evalExpression(left)
      vr <- evalExpression(right)
  } yield (fn(vl.asInstanceOf[Number], vr.asInstanceOf[Number]))


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
  def modularExpression(left: Expression, right: Expression, fn: (Modular, Modular) => Modular): IResult[Expression] = for {
    vl <- evalExpression(left)
    vr <- evalExpression(right)
  } yield fn(vl.asInstanceOf[Modular], vr.asInstanceOf[Modular])


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
  def binExpression(left: Expression, right: Expression, fn: (Value, Value) => Expression): IResult[Expression] = for {
    v1 <- evalExpression(left)
    v2 <- evalExpression(right)
  } yield fn(v1.asInstanceOf[Value], v2.asInstanceOf[Value])
}

class NullPrintStream extends PrintStream(new NullByteArrayOutputStream) {}

class NullByteArrayOutputStream extends ByteArrayOutputStream {
  override def writeTo(o: OutputStream): Unit = ()
}
