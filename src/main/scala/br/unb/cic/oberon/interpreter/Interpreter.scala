package br.unb.cic.oberon.interpreter

import java.io.{ByteArrayOutputStream, OutputStream, PrintStream}
import br.unb.cic.oberon.ast._
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
    module.constants.foreach(c => c.accept(this))
    module.variables.foreach(v => v.accept(this))
    module.procedures.foreach(p => p.accept(this))
    module.userTypes.foreach(userType => userType.accept(this))

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
    variable.variableType match {
      case ArrayType(length, _) => env.setGlobalVariable(variable.name, ArrayValue(ListBuffer.fill(length)(Undef())))
      case PointerType(variableType) => env.setPointerVariable(variable.name)
      case _ => env.setGlobalVariable(variable.name, Undef())
    }
  }

  override def visit(userType: UserDefinedType): Unit = {
    env.addUserDefinedType(userType)
  }

  override def visit(procedure: Procedure): Unit = {
    env.declareProcedure(procedure)
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
      case AssignmentStmt(indexDesignator, exp) =>
        indexDesignator match {
          case ArrayAssignment(arrayExpression, indexExpression) =>
            env.reassignArray(arrayExpression.asInstanceOf[VarExpression].name, evalExpression(indexExpression).asInstanceOf[IntValue].value, evalExpression(exp))
          
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

      case NewStmt(name) =>
        env.setNewVariable(name, PointerAccessExpression(name))

      case IfElseStmt(condition, thenStmt, elseStmt) =>
        if (evalCondition(condition)) thenStmt.accept(this)
        else if (elseStmt.isDefined) elseStmt.get.accept(this)

      case WhileStmt(condition, whileStmt) =>
        while (evalCondition(condition) && exit == false )
          whileStmt.accept(this)
        exit = false

      case ExitStmt() =>
        exit = true

      case ReturnStmt(exp: Expression) =>
        setReturnExpression(evalExpression(exp))

      case MetaStmt(f) => f().accept(this)

      case ProcedureCallStmt(name, args) =>
        val actualArguments = args map (arg => arg -> evalExpression(arg)) toMap

        env.push() // after that, we can "push", to indicate a procedure call.
        visitProcedureCall(name, actualArguments) // then we execute the procedure.
        updateParameterByReferenceVariables(env.findProcedure(name)) // it updates the parameter by reference after
                                                                     // poping the stack
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

  def visitProcedureCall(name: String, args: Map[Expression, Expression]): Unit = {
    val procedure = env.findProcedure(name)
    updateEnvironmentWithProcedureCall(procedure, args)
    procedure.stmt.accept(this)
  }

  def updateEnvironmentWithProcedureCall(procedure: Procedure, args: Map[Expression, Expression]): Unit = {
    procedure.args.zip(args).foreach(pair => { pair._1 match {
      case ParameterByReference(name, _) => procedure.referenceMap += name -> pair._2._1.asInstanceOf[VarExpression].name
      case _ =>
    }
      env.setLocalVariable(pair._1.name, pair._2._2)})
    procedure.constants.foreach(c => env.setLocalVariable(c.name, c.exp))
    procedure.variables.foreach(v => env.setLocalVariable(v.name, Undef()))
  }

  def updateParameterByReferenceVariables(procedure: Procedure): Unit = {
    val auxMap = procedure.referenceMap map {case (reference, local) => (local, env.lookup(reference).get)}
    env.pop()
    auxMap foreach {case (local, value) => env.setVariable(local, value)}
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
    case NilValue => NilValue
    case Undef() => Undef()
    case VarExpression(name) => interpreter.env.lookup(name).get
    case ArraySubscript(arrayBase, index) => arrayBase match {
      case VarExpression(name) => interpreter.env.lookupArrayIndex(name, index.accept(this).asInstanceOf[Value]
        .value.asInstanceOf[Int]).get
    }
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
      val actualArguments = args map (arg => arg -> arg.accept(this)) toMap

      interpreter.env.push()
      val exp = visitFunctionCall(name, actualArguments)
      interpreter.updateParameterByReferenceVariables(interpreter.env.findProcedure(name))
      exp
    }

    //TODO FieldAccessExpression
    //TODO ArraySubscriptExpression
    //TODO PointerAccessExpression
  }

  def visitFunctionCall(name: String, args: Map[Expression, Expression]): Expression = {
    interpreter.visitProcedureCall(name, args)
    val returnValue = interpreter.env.lookup(Values.ReturnKeyWord)
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
  override def writeTo(o: OutputStream) {}
}
