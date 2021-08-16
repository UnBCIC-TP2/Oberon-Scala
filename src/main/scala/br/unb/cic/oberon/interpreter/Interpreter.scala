package br.unb.cic.oberon.interpreter

import java.io.{ByteArrayOutputStream, OutputStream, PrintStream}

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.util.Values
import br.unb.cic.oberon.visitor.OberonVisitorAdapter


import scala.io.StdIn

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

  var printStream : PrintStream = new PrintStream(System.out)

  override def visit(module: OberonModule): Unit = {
    // set up the global declarations
    module.constants.foreach(c => c.accept(this))
    module.variables.foreach(v => v.accept(this))
    module.procedures.foreach(p => p.accept(this))
    module.userTypes.foreach(userType => userType.accept(this))

    // execute the statement, if it is defined. remember,
    // module.stmt is an Option[Statement].
    if (module.stmt.isDefined) {
      module.stmt.get.accept(this)
    }
  }

  override def visit(constant: Constant): Unit = {
    env.setGlobalVariable(constant.name, constant.exp)
  }

  override def visit(variable: VariableDeclaration): Unit = {
    env.setGlobalVariable(variable.name, Undef())
  }

  override def visit(userType: UserDefinedType): Unit = {
    env.addUserDefinedType(userType)
  }

  override def visit(procedure: Procedure): Unit = {
    env.declareProcedure(procedure)
  }

  // 	assert(stmts(1) == EAssignmentStmt(ArrayAssignment(VarExpression("array"), IntValue(0)), VarExpression("x")))


  override def visit(stmt: Statement): Unit = {
    // we first check if we achieved a return stmt.
    // if so, we should not execute any other statement
    // of a sequence of stmts. Whenever we achieve a
    // return stmt, we associate in the local variables
    // the name "return" to the return value.
    if (exit || env.lookup(Values.ReturnKeyWord).isDefined) {
      return
    }
    // otherwise, we pattern-match on the current stmt.
    stmt match {
      case EAssignmentStmt(indexDesignator, exp) => {
        indexDesignator match {
          case ArrayAssignment(arrayExpression, indexExpression) => {
            env.reassignArray(arrayExpression.asInstanceOf[VarExpression].name, evalExpression(indexExpression).asInstanceOf[IntValue].value, evalExpression(exp))
          }
          case RecordAssignment(record, atrib) => ???
          case _ => ???
        }
      }
      case AssignmentStmt(name, exp) => env.setVariable(name, evalExpression(exp))
      case SequenceStmt(stmts) => stmts.foreach(s => s.accept(this))
      case ReadLongRealStmt(name) => env.setVariable(name, LongRealValue(StdIn.readLine().toDouble))
      case ReadRealStmt(name) => env.setVariable(name, RealValue(StdIn.readLine().toFloat))
      case ReadLongIntStmt(name) => env.setVariable(name, LongValue(StdIn.readLine().toLong))
      case ReadIntStmt(name) => env.setVariable(name, IntValue(StdIn.readLine().toInt))
      case ReadShortIntStmt(name) => env.setVariable(name, ShortValue(StdIn.readLine().toShort))
      case ReadCharStmt(name) => env.setVariable(name, CharValue(StdIn.readLine().charAt(0)))
      case WriteStmt(exp) => printStream.println(evalExpression(exp))
      case IfElseStmt(condition, thenStmt, elseStmt) => if (evalCondition(condition)) thenStmt.accept(this) else if (elseStmt.isDefined) elseStmt.get.accept(this)
      case IfElseIfStmt(condition, thenStmt, listOfElseIf, elseStmt) => checkIfElseIfStmt(condition, thenStmt, listOfElseIf, elseStmt)
      case WhileStmt(condition, whileStmt) => while (evalCondition(condition)) whileStmt.accept(this)
      case RepeatUntilStmt(condition, repeatUntilStmt) => do (repeatUntilStmt.accept(this)) while (!evalCondition(condition))
      case ForStmt(init, condition, block) => init.accept(this); while (evalCondition(condition)) block.accept(this)
      case LoopStmt(stmt) => while(!exit) { stmt.accept(this) }; exit = false
      case ExitStmt() => exit = true
      case CaseStmt(exp, cases, elseStmt) => checkCaseStmt(exp, cases, elseStmt)
      case ReturnStmt(exp: Expression) => setReturnExpression(evalExpression(exp))
      case ProcedureCallStmt(name, args) =>
        // we evaluate the "args" in the current
        // environment.
        val actualArguments = args.map(a => evalExpression(a))
        env.push() // after that, we can "push", to indicate a procedure call.
        visitProcedureCall(name, actualArguments) // then we execute the procedure.
        env.pop() // and we pop, to indicate that a procedure finished its execution.
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

  private def checkCaseStmt(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement]): Unit = {
    var v = evalExpression(exp)
    var matched = false
    var i = 0
    while (i < cases.size && !matched) {
      cases(i) match {
        case RangeCase(min, max, stmt) =>
          if ((evalCaseAlt(v) >= evalCaseAlt(min)) && (evalCaseAlt(v) <= evalCaseAlt(max))) {
            stmt.accept(this)
            matched = true
          }
        case SimpleCase(condition, stmt) =>
          if (v == evalExpression(condition)) {
            stmt.accept(this)
            matched = true
          }
      }
      i += 1
    }
    if (!matched && elseStmt.isDefined) {
      elseStmt.get.accept(this)
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

  def visitProcedureCall(name: String, args: List[Expression]): Unit = {
    val procedure = env.findProcedure(name)
    updateEnvironmentWithProcedureCall(procedure, args)
    procedure.stmt.accept(this)
  }

  def updateEnvironmentWithProcedureCall(procedure: Procedure, args: List[Expression]): Unit = {
    procedure.args.map(formal => formal.name)
      .zip(args)
      .foreach(pair => env.setLocalVariable(pair._1, pair._2))

    procedure.constants.foreach(c => env.setLocalVariable(c.name, c.exp))
    procedure.variables.foreach(v => env.setLocalVariable(v.name, Undef()))
  }

  def evalCondition(expression: Expression): Boolean = {
    val evalVisitor = new EvalExpressionVisitor(this)
    expression.accept(evalVisitor).asInstanceOf[BoolValue].value
  }

  def evalExpression(expression: Expression): Expression = {
    val evalVisitor = new EvalExpressionVisitor(this)
    expression.accept(evalVisitor)
  }

  def evalCaseAlt(expression: Expression): Integer = {
    val evalVisitor = new EvalExpressionVisitor(interpreter = this)
    expression.accept(evalVisitor).asInstanceOf[IntValue].value
  }
  
  // def evalCaseAlt(expression: Expression): Float = {
  //   val evalVisitor = new EvalExpressionVisitor(interpreter = this)
  //   expression.accept(evalVisitor).asInstanceOf[RealValue].value
  // }

  /*
   * This method is mostly useful for testing purpose.
   * That is, here we are considering testability as a
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
    case LongRealValue(v) => LongRealValue(v)
    case LongValue(v) => LongValue(v)
    case ShortValue(v) => ShortValue(v)
    case CharValue(v) => CharValue(v)
    case BoolValue(v) => BoolValue(v)
    case Undef() => Undef()
    case VarExpression(name) => interpreter.env.lookup(name).get
    case AddExpression(left, right) => aritmeticLogicExpression(left, right, 1) //(v1: Value[Number], v2: Value[Number])  => sum(v1, v2)
    case SubExpression(left, right) => aritmeticLogicExpression(left, right, 2) //(v1: Value[Number], v2: Value[Number])  => sum(v1, v2)
    case MultExpression(left, right) => aritmeticLogicExpression(left, right, 3)
    case DivExpression(left, right) => aritmeticLogicExpression(left, right, 4)
    case EQExpression(left, right) => aritmeticLogicExpression(left, right, 5)
    case NEQExpression(left, right) => aritmeticLogicExpression(left, right, 6)
    case GTExpression(left, right) => aritmeticLogicExpression(left, right, 7)
    case LTExpression(left, right) => aritmeticLogicExpression(left, right, 8)
    case GTEExpression(left, right) => aritmeticLogicExpression(left, right, 9)
    case LTEExpression(left, right) => aritmeticLogicExpression(left, right, 10)
    case AndExpression(left, right) => binExpression(left, right, (v1: Value[Boolean], v2: Value[Boolean]) => BoolValue(v1.value && v2.value))
    case OrExpression(left, right) => binExpression(left, right, (v1: Value[Boolean], v2: Value[Boolean]) => BoolValue(v1.value || v2.value))
    case FunctionCallExpression(name, args) => {
      val actualArguments = args.map(a => a.accept(this))
      interpreter.env.push()
      val exp = visitFunctionCall(name, actualArguments)
      interpreter.env.pop()
      exp
    }
  }

  def visitFunctionCall(name: String, args: List[Expression]): Expression = {
    interpreter.visitProcedureCall(name, args)
    val returnValue = interpreter.env.lookup(Values.ReturnKeyWord)
    assert(returnValue.isDefined) // a function call must set a local variable with the "return" expression
    returnValue.get
  }

  def aritmeticLogicExpression(left: Expression, right: Expression, op: Int) : Expression = {
    var vl = left.accept(this).asInstanceOf[Primitive[T]]
    var vr = right.accept(this).asInstanceOf[Primitive[T]]

    /*
      Array to detect the least restrictive type in the expression
      Least restrictive order: [long real, real, long, int, short, char]
      With this we are able to do the appropriate operator overload
    */
    var types = Array(
      vl.isInstanceOf[LongRealValue] || vr.isInstanceOf[LongRealValue],
      vl.isInstanceOf[RealValue]     || vr.isInstanceOf[RealValue],
      vl.isInstanceOf[LongValue]     || vr.isInstanceOf[LongValue],
      vl.isInstanceOf[IntValue]      || vr.isInstanceOf[IntValue],
      vl.isInstanceOf[ShortValue]    || vr.isInstanceOf[ShortValue]
    )

    op match {
      case 1 => {
        if (types(0) == true) LongRealValue((vl.toDouble + vr.toDouble).toDouble)
        else if (types(1) == true) RealValue((vl.toFloat + vr.toFloat).toFloat)
        else if (types(2) == true) LongValue((vl.toLong + vr.toLong).toLong)
        else if (types(3) == true) IntValue((vl.toInt + vr.toInt).toInt)
        else if (types(4) == true) ShortValue((vl.toShort + vr.toShort).toShort)
        else CharValue((vl.toInt + vr.toInt).toChar)
      }

      case 2 => {
        if (types(0) == true) LongRealValue((vl.toDouble - vr.toDouble).toDouble)
        else if (types(1) == true) RealValue((vl.toFloat - vr.toFloat).toFloat)
        else if (types(2) == true) LongValue((vl.toLong - vr.toLong).toLong)
        else if (types(3) == true) IntValue((vl.toInt - vr.toInt).toInt)
        else if (types(4) == true) ShortValue((vl.toShort - vr.toShort).toShort)
        else CharValue((vl.toInt + vr.toInt).toChar)
      }

      case 3 => {
        if (types(0) == true) LongRealValue((vl.toDouble * vr.toDouble).toDouble)
        else if (types(1) == true) RealValue((vl.toFloat * vr.toFloat).toFloat)
        else if (types(2) == true) LongValue((vl.toLong * vr.toLong).toLong)
        else if (types(3) == true) IntValue((vl.toInt * vr.toInt).toInt)
        else if (types(4) == true) ShortValue((vl.toShort * vr.toShort).toShort)
        else CharValue((vl.toInt + vr.toInt).toChar)
      }

      case 4 => {
        if (types(0) == true) LongRealValue((vl.toDouble / vr.toDouble).toDouble)
        else if (types(1) == true) RealValue((vl.toFloat / vr.toFloat).toFloat)
        else if (types(2) == true) LongValue((vl.toLong / vr.toLong).toLong)
        else if (types(3) == true) IntValue((vl.toInt / vr.toInt).toInt)
        else if (types(4) == true) ShortValue((vl.toShort / vr.toShort).toShort)
        else CharValue((vl.toInt + vr.toInt).toChar)
      }

      case 5 => {
        if (types(0) == true || types(1) == true) BoolValue(vl.toDouble == vr.toDouble)
        else BoolValue(vl.toLong == vr.toLong)
      }

      case 6 => {
        if (types(0) == true || types(1) == true) BoolValue(vl.toDouble != vr.toDouble)
        else BoolValue(vl.toLong != vr.toLong)
      }

      case 7 => {
        if (types(0) == true || types(1) == true) BoolValue(vl.toDouble > vr.toDouble)
        else BoolValue(vl.toLong > vr.toLong)
      }

      case 8 => {
        if (types(0) == true || types(1) == true) BoolValue(vl.toDouble < vr.toDouble)
        else BoolValue(vl.toLong < vr.toLong)
      }

      case 9 => {
        if (types(0) == true || types(1) == true) BoolValue(vl.toDouble >= vr.toDouble)
        else BoolValue(vl.toLong >= vr.toLong)
      }

      case 10 => {
        if (types(0) == true || types(1) == true) BoolValue(vl.toDouble <= vr.toDouble)
        else BoolValue(vl.toLong <= vr.toLong)
      }
    }
  }

  /**
   * Eval a binary expression.
   *
   * @param left  the left expression
   * @param right the right expression
   * @param fn    a function that constructs an expression. Here we
   *              are using again a high-order function. We assign to
   *              the "result" visitor attribute the value we compute
   *              after applying this function.
   * @tparam T a type parameter to set the function fn correctly.
   */
  def binExpression[T](left: Expression, right: Expression, fn: (Value[T], Value[T]) => Expression): Expression = {
    val v1 = left.accept(this).asInstanceOf[Value[T]]
    val v2 = right.accept(this).asInstanceOf[Value[T]]

    fn(v1, v2)
  }

  // def sum2 (v1: IntValue, v2: IntValue): Expression = IntValue(v1 + v2)

}

class NullPrintStream extends PrintStream(new NullByteArrayOutputStream) { }

class NullByteArrayOutputStream extends ByteArrayOutputStream {
  override def writeTo(o: OutputStream) {}
}