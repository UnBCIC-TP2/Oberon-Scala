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
class Interpreter extends OberonVisitorAdapter {


  type T = Unit

  var exit = false
  val env = new Environment[Expression]()

  var printStream: PrintStream = new PrintStream(System.out)

  def setupStandardLibraries(): Unit = {
    val lib = new StandardLibrary[Expression](env)
    for(p <- lib.stdlib.procedures) {
      env.declareProcedure(p)
    }
  }

  override def visit(module: OberonModule): Unit = {
    // set up the global declarations
    module.userTypes.foreach(userType => userType.accept(this))
    module.constants.foreach(c => c.accept(this))
    module.variables.foreach(v => v.accept(this))
    module.procedures.foreach(p => p.accept(this))


    // execute the statement if it is defined.
    // remember, module.stmt is an Option[Statement].
    if (module.stmt.isDefined) {
      setupStandardLibraries()
      module.stmt.get.accept(this)
    }
  }

  override def visit(constant: Constant): Unit = {
    env.setGlobalVariable(constant.name, constant.exp)
  }

  override def visit(variable: VariableDeclaration): Unit = {
    env.baseType(variable.variableType) match {
      case Some(ArrayType(length, baseType)) => env.setGlobalVariable(variable.name, ArrayValue(ListBuffer.fill(length)(Undef()), ArrayType(length, baseType)))
      case _ => env.setGlobalVariable(variable.name, Undef())
    }
  }

  override def visit(userType: UserDefinedType): Unit = {
    env.addUserDefinedType(userType)
  }

  override def visit(procedure: Procedure): Unit = {
    env.declareProcedure(procedure)
  }

  def visitArrayAssignment(baseExp: Expression, indexExp: Expression, exp: Expression): Unit = {
    val array = evalExpression(baseExp)
    val index = evalExpression(indexExp)

    (array, index) match {
      case (ArrayValue(values, _), IntValue(v)) => values(v) = evalExpression(exp)
      case _ => throw new RuntimeException
    }
  }

  override def visit(stmt: Statement): Unit = {
    // we first check if we have encountered a return stmt.
    // if so, we should not execute any other statement
    // of a sequence of stmts. Whenever we encounter a
    // return stmt, we assign a new local variable
    // "return" as the return value.
    //
    // we also check if exit is true. if this is the case
    // we should also stop the execution of a block of
    // statements within a any kind of repeat statement.

    if (exit || env.lookup(Values.ReturnKeyWord).isDefined) {
      return
    }
    // otherwise, we pattern-match on the current stmt.
    stmt match {
      case AssignmentStmt(designator, exp) =>
        designator match {
          case ArrayAssignment(array, index) => visitArrayAssignment(array, index, exp)
          //TODO:
          case RecordAssignment(_, _) => ???
          case PointerAssignment(_) => ???
          case VarAssignment(name) => env.setVariable(name, evalExpression(exp))
        }

      case SequenceStmt(stmts) =>
        stmts.foreach(s => s.accept(this))

      case ReadRealStmt(name) =>
        env.setVariable(name, RealValue(StdIn.readLine().toFloat))

      case ReadIntStmt(name) =>
        env.setVariable(name, IntValue(StdIn.readLine().toInt))

      case ReadCharStmt(name) =>
        env.setVariable(name, CharValue(StdIn.readLine().charAt(0)))

      case WriteStmt(exp) =>
        printStream.println(evalExpression(exp))

      case IfElseStmt(condition, thenStmt, elseStmt) =>
        if (evalCondition(condition)) thenStmt.accept(this)
        else if (elseStmt.isDefined) elseStmt.get.accept(this)

      case WhileStmt(condition, whileStmt) =>
        while (evalCondition(condition) && exit == false )
          whileStmt.accept(this)
        exit = false

      case ForEachStmt(v, exp, stmt) =>
         val valArray = evalExpression(exp)
         valArray match {
           case ArrayValue(values, _) => {
             values.foreach(value => {
                 env.setVariable(v, evalExpression(value))
                 stmt.accept(this)
//               val assignment = AssignmentStmt(VarAssignment(v), value)
//               val stmts = SequenceStmt(List(assignment, stmt))
//               stmts.accept(this)
             })
           }

           case _ => throw new RuntimeException("erro.... melhorar")
         }
      case ExitStmt() =>
        exit = true

      case ReturnStmt(exp: Expression) =>
        setReturnExpression(evalExpression(exp))

      case MetaStmt(f) => f().accept(this)

      case ProcedureCallStmt(name, args) =>
        callProcedure(name, args)
        env.pop()
    }
  }

  private def checkIfElseIfStmt(condition: Expression, thenStmt: Statement, listOfElseIf: List[ElseIfStmt], elseStmt: Option[Statement]): Unit = {
    var matched = false
    var i = 0

    if (evalCondition(condition)) thenStmt.accept(this)
    else {
      while (i < listOfElseIf.size && !matched) {
        listOfElseIf(i) match {
          case ElseIfStmt(condition, stmt) => if (evalCondition(condition)) {
            stmt.accept(this)
            matched = true
          }
        }
        i += 1
      }
      if (!matched && elseStmt.isDefined) elseStmt.get.accept(this)
    }
  }

  /*
   * process the ReturnStmt(exp) statement.
   * In this case, we just create a new entry
   * in the local variables, assigning
   * "return" -> exp.
   */
  private def setReturnExpression(exp: Expression): Unit =
    env.setLocalVariable(Values.ReturnKeyWord, exp)

  def callProcedure(name: String, args: List[Expression]): Unit = {
    val procedure = env.findProcedure(name)
    updateEnvironmentWithProcedureCall(procedure, args)
    procedure.stmt.accept(this)
  }

  def updateEnvironmentWithProcedureCall(procedure: Procedure, args: List[Expression]): Unit = {
    val mappedArgs = procedure.args.zip(args).map(pair => pair match {
      case (ParameterByReference(_, _), VarExpression(name2)) => (pair._1, env.pointsTo(name2))
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(_, _), exp) => (pair._1, evalExpression(exp))
    })

    env.push() // after that, we can "push", to indicate a procedure call.

    mappedArgs.foreach(pair => pair match {
      case (ParameterByReference(name, _), Some(location: Location)) => env.setParameterReference(name, location)
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(name, _), exp: Expression) => env.setLocalVariable(name, exp)
	  case _ => throw new RuntimeException
    })
    procedure.constants.foreach(c => env.setLocalVariable(c.name, c.exp))
    procedure.variables.foreach(v => env.setLocalVariable(v.name, Undef()))
  }

  def returnProcedure() = {
    env.pop()
  }

  def evalCondition(expression: Expression): Boolean = {
    val evalVisitor = new EvalExpressionVisitor(this)
    expression.accept(evalVisitor).asInstanceOf[BoolValue].value
  }

  def evalExpression(expression: Expression): Expression = {
    val evalVisitor = new EvalExpressionVisitor(this)
    expression.accept(evalVisitor)
  }

  /*
   * This method is mostly useful for testing purposes.
   * That is, here we are considering testability a
   * design concern.
   */
  def setGlobalVariable(name: String, exp: Expression): Unit = {
    env.setGlobalVariable(name, exp)
  }

  /*
   * the same here.
   */
  def setLocalVariable(name: String, exp: Expression): Unit = {
    env.setLocalVariable(name, exp)
  }

  def setTestEnvironment() = {
    printStream = new PrintStream(new NullPrintStream())
  }
}

class EvalExpressionVisitor(val interpreter: Interpreter) extends OberonVisitorAdapter {
  type T = Expression


  override def visit(exp: Expression): Expression = exp match {
    case Brackets(expression) => expression.accept(this)
    case IntValue(v) => IntValue(v)
    case RealValue(v) => RealValue(v)
    case CharValue(v) => CharValue(v)
    case BoolValue(v) => BoolValue(v)
    case StringValue(v) => StringValue(v)
    case NullValue => NullValue
    case Undef() => Undef()
    case VarExpression(name) =>
      val variable = interpreter.env.lookup(name)
      if (variable.isEmpty) throw new NoSuchElementException(f"Variable $name is not defined")
      interpreter.env.lookup(name).get
    case ArraySubscript(a, i) => visitArraySubscriptExpression(ArraySubscript(a, i))
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
    case NotExpression(exp) => BoolValue(!exp.accept(this).asInstanceOf[Value].value.asInstanceOf[Boolean])
    case AndExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] && v2.value.asInstanceOf[Boolean]))
    case OrExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] || v2.value.asInstanceOf[Boolean]))
    case FunctionCallExpression(name, args) => {
      val exp = visitFunctionCall(name, args)
      exp
    }

    //TODO FieldAccessExpression
    //TODO PointerAccessExpression
  }

  def visitArraySubscriptExpression(arraySubscript: ArraySubscript): Expression = {
    val array =  arraySubscript.arrayBase.accept(this)
    val idx = arraySubscript.index.accept(this)

    (array, idx) match {
      case (ArrayValue(values: ListBuffer[Expression], _), IntValue(v)) => values(v)
      case _ => throw new RuntimeException
    }
  }
  def visitFunctionCall(name: String, args: List[Expression]): Expression = {
    interpreter.callProcedure(name, args)
    val returnValue = interpreter.env.lookup(Values.ReturnKeyWord)
    interpreter.returnProcedure()
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
  def arithmeticExpression(left: Expression, right: Expression, fn: (Number, Number) => Number): Expression = {
    val vl = left.accept(this).asInstanceOf[Number]
    val vr = right.accept(this).asInstanceOf[Number]

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
  def modularExpression(left: Expression, right: Expression, fn: (Modular, Modular) => Modular): Expression = {
    val vl = left.accept(this).asInstanceOf[Modular]
    val vr = right.accept(this).asInstanceOf[Modular]

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
  def binExpression(left: Expression, right: Expression, fn: (Value, Value) => Expression): Expression = {
    val v1 = left.accept(this).asInstanceOf[Value]
    val v2 = right.accept(this).asInstanceOf[Value]

    fn(v1, v2)
  }
}

class NullPrintStream extends PrintStream(new NullByteArrayOutputStream) {}

class NullByteArrayOutputStream extends ByteArrayOutputStream {
  override def writeTo(o: OutputStream): Unit = ()
}
