package br.unb.cic.oberon.interpreter

import java.io.{ByteArrayOutputStream, OutputStream, PrintStream}
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.environment.Environment
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
  var env = new Environment[Expression]()

  var printStream: PrintStream = new PrintStream(System.out)

  def setupStandardLibraries(environment: Environment[Expression]): Environment[Expression] = {
    var envt = environment
    val lib = new StandardLibrary[Expression](environment)
    for(p <- lib.stdlib.procedures) {
      envt = envt.declareProcedure(p)
    }
    envt
  }

def runInterpreter(module: OberonModule): Environment[Expression] = {
    var envt = new Environment[Expression]()
    env = envt

    // set up the global declarations
    val env1 = module.userTypes.foldLeft(envt)((a, b) => execUserDefinedType(a, b))
    val env2 = module.constants.foldLeft(env1)((a, b) => execConstant(a, b))
    val env3 = module.variables.foldLeft(env2)((a, b) => execVariable(a, b))
    val env4 = module.procedures.foldLeft(env3)((a, b) => execProcedure(a, b))

    envt = env4
    // execute the statement if it is defined.
    // remember, module.stmt is an Option[Statement].
    if (module.stmt.isDefined) {
      envt = setupStandardLibraries(envt)
      envt = execStatement(envt, module.stmt.get)
    }
    env = envt
    envt
  }

  def execConstant(environment : Environment[Expression], constant: Constant): Environment[Expression] = {
    environment.setGlobalVariable(constant.name, constant.exp)
  }

  def execVariable(environment : Environment[Expression], variable: VariableDeclaration): Environment[Expression] = {
    environment.baseType(variable.variableType) match {
      case Some(ArrayType(length, baseType)) => environment.setGlobalVariable(variable.name, ArrayValue(ListBuffer.fill(length)(Undef()), ArrayType(length, baseType)))
      case _ => environment.setGlobalVariable(variable.name, Undef())
    }
  }

  def execUserDefinedType(environment : Environment[Expression], userType: UserDefinedType): Environment[Expression] = {
    environment.addUserDefinedType(userType)
  }

  def execProcedure(environment : Environment[Expression], procedure: Procedure): Environment[Expression] = {
    environment.declareProcedure(procedure)
  }

  def arrayAssignment(environment : Environment[Expression], baseExp: Expression, indexExp: Expression, exp: Expression): Environment[Expression] = {
    val array = evalExpression(environment, baseExp)
    val index = evalExpression(environment, indexExp)

    (array, index) match {
      case (ArrayValue(values, _), IntValue(v)) => values(v) = evalExpression(environment, exp)
      case _ => throw new RuntimeException
    }
    environment
  }

  def execStatement(environment : Environment[Expression], stmt: Statement): Environment[Expression] = {
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
          case VarAssignment(name) => environment.setVariable(name, evalExpression(environment, exp))
          case ArrayAssignment(array, index) => arrayAssignment(environment, array, index, exp)
          //TODO:
          case RecordAssignment(_, _) => ???
          case PointerAssignment(_) => ???
        }

      case SequenceStmt(stmts) =>
        stmts.foreach(s => envt = execStatement(envt, s))
        envt

      case ReadRealStmt(name) =>
        environment.setVariable(name, RealValue(StdIn.readLine().toFloat))

      case ReadIntStmt(name) =>
        environment.setVariable(name, IntValue(StdIn.readLine().toInt))

      case ReadCharStmt(name) =>
        environment.setVariable(name, CharValue(StdIn.readLine().charAt(0)))

      case WriteStmt(exp) =>
        printStream.println(evalExpression(environment, exp))
        envt

      case IfElseStmt(condition, thenStmt, elseStmt) =>
        if (evalCondition(environment, condition)) execStatement(environment, thenStmt)
        else if (elseStmt.isDefined) execStatement(environment, elseStmt.get)
        else envt

      case WhileStmt(condition, whileStmt) =>
        var envteste = envt
        while (evalCondition(envteste, condition) && exit == false)
          envteste = execStatement(envteste, whileStmt)
        exit = false
        envteste

      case ForEachStmt(v, exp, stmt) =>
        val valArray = evalExpression(envt, exp)
        valArray match {
          case ArrayValue(values, _) => {
            envt = envt.push()
            values.foreach(value => {
              envt = envt.setLocalVariable(v, evalExpression(envt, value))
              envt = execStatement(envt, stmt)
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
        setReturnExpression(envt, evalExpression(envt, exp))

      //TODO
      //case MetaStmt(f) => f().accept(this)

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
    val env1 = updateEnvironmentWithProcedureCall(procedure, args, environment)
    execStatement(env1, procedure.stmt)
  }

  def updateEnvironmentWithProcedureCall(procedure: Procedure, args: List[Expression], environment : Environment[Expression]): Environment[Expression] = {
    val mappedArgs = procedure.args.zip(args).map(pair => pair match {
      case (ParameterByReference(_, _), VarExpression(name2)) => (pair._1, evalExpression(environment, VarExpression(name2)))
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(_, _), exp) => (pair._1, evalExpression(environment, exp))
    })

    var envt = environment.push() // after that, we can "push", to indicate a procedure call.

    mappedArgs.foreach(pair => pair match {
      case (ParameterByReference(name, _), exp) => envt = envt.setLocalVariable(name, exp)
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(name, _), exp: Expression) => envt = envt.setLocalVariable(name, exp)
	  case _ => throw new RuntimeException
    })
    procedure.constants.foreach(c => envt = envt.setLocalVariable(c.name, c.exp))
    procedure.variables.foreach(v => envt = envt.setLocalVariable(v.name, Undef()))

    envt
  }


  def evalCondition(environment : Environment[Expression], expression: Expression): Boolean = {
    evalExpression(environment, expression).asInstanceOf[BoolValue].value
  }


  /*
   * This method is mostly useful for testing purposes.
   * That is, here we are considering testability a
   * design concern.
   */
  def setGlobalVariable(name: String, exp: Expression): Unit = {
    env = env.setGlobalVariable(name, exp)
  }

  /*
   * the same here.
   */
  def setLocalVariable(name: String, exp: Expression): Unit = {
    env=env.setLocalVariable(name, exp)
  }

  def setTestEnvironment() = {
    printStream = new PrintStream(new NullPrintStream())
  }


  def evalExpression(environment: Environment[Expression], exp: Expression): Expression = exp match {


    case IntValue(v) => IntValue(v)
    case RealValue(v) => RealValue(v)
    case CharValue(v) => CharValue(v)
    case BoolValue(v) => BoolValue(v)
    case StringValue(v) => StringValue(v)
    case NullValue => NullValue
    case Undef() => Undef()

    case VarExpression(name) =>
      val variable = environment.lookup(name)
      if (variable.isEmpty) throw new NoSuchElementException(f"Variable $name is not defined")
      environment.lookup(name).get

    //TODO eval array
    //case ArrayValue(v, t) =>
    case ArraySubscript(a, i) => ArraySubscriptExpression(environment, ArraySubscript(a, i))

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
    case NotExpression(exp) => BoolValue(!evalExpression(environment, exp).asInstanceOf[Value].value.asInstanceOf[Boolean])
    case AndExpression(left, right) => binExpression(environment, left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] && v2.value.asInstanceOf[Boolean]))
    case OrExpression(left, right) => binExpression(environment, left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] || v2.value.asInstanceOf[Boolean]))
    case FunctionCallExpression(name, args) => FunctionCall(environment, name, args)

    //TODO FieldAccessExpression
    //TODO PointerAccessExpression
  }

  //TODO array assignment

  def ArraySubscriptExpression(environment : Environment[Expression], arraySubscript: ArraySubscript): Expression = {
    val array = evalExpression(environment, arraySubscript.arrayBase)
    val idx = evalExpression(environment, arraySubscript.index)

    (array, idx) match {
      case (ArrayValue(values: ListBuffer[Expression], _), IntValue(v)) => values(v)
      case _ => throw new RuntimeException
    }
  }


  def FunctionCall(environment : Environment[Expression], name: String, args: List[Expression]): Expression = {
    var envt = callProcedure(name, args, environment)
    val returnValue = envt.lookup(Values.ReturnKeyWord)
    envt = envt.pop()
    assert(returnValue.isDefined) // a function call must set a local variable with the "return" expression
    returnValue.get
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
  def arithmeticExpression(environment: Environment[Expression], left: Expression, right: Expression, fn: (Number, Number) => Number): Expression = {
    val vl = evalExpression(environment, left).asInstanceOf[Number]
    val vr = evalExpression(environment, right).asInstanceOf[Number]

    fn(vl, vr)
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
  def modularExpression(environment: Environment[Expression], left: Expression, right: Expression, fn: (Modular, Modular) => Modular): Expression = {
    val vl = evalExpression(environment, left).asInstanceOf[Modular]
    val vr = evalExpression(environment, right).asInstanceOf[Modular]

    fn(vl, vr)
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
  def binExpression(environment : Environment[Expression], left: Expression, right: Expression, fn: (Value, Value) => Expression): Expression = {
    val v1 = evalExpression(environment, left).asInstanceOf[Value]
    val v2 = evalExpression(environment, right).asInstanceOf[Value]

    fn(v1, v2)
  }
}

class NullPrintStream extends PrintStream(new NullByteArrayOutputStream) {}

class NullByteArrayOutputStream extends ByteArrayOutputStream {
  override def writeTo(o: OutputStream): Unit = ()
}
