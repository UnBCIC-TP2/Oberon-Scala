package br.unb.cic.oberon.interpreter

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

  val env = new Environment[Expression]()

  override def visit(module: OberonModule): Unit = {
    // set up the global declarations
    module.constants.foreach(c => c.accept(this))
    module.variables.foreach(v => v.accept(this))
    module.procedures.foreach(p => p.accept(this))

    // execute the statment, if it is defined. remember,
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

  override def visit(procedure: Procedure): Unit = {
    env.declareProcedure(procedure)
  }

  override def visit(stmt: Statement): Unit = {
    // we first check if we achieved a return stmt.
    // if so, we should not execute any other statement
    // of a sequence of stmts. Whenever we achieve a
    // return stmt, we associate in the local variables
    // the name "return" to the return value.
    if (env.lookup(Values.ReturnKeyWord).isDefined) {
      return
    }
    // otherwise, we pattern-match on the current stmt.
    stmt match {
      case AssignmentStmt(name, exp) => env.setVariable(name, evalExpression(exp))
      case SequenceStmt(stmts) => stmts.foreach(s => s.accept(this))
      case ReadIntStmt(name) => env.setVariable(name, IntValue(StdIn.readLine().toInt))
      case WriteStmt(exp) => println(evalExpression(exp))
      case IfElseStmt(condition, thenStmt, elseStmt) => if (evalCondition(condition)) thenStmt.accept(this) else if (elseStmt.isDefined) elseStmt.get.accept(this)
      case WhileStmt(condition, whileStmt) => while (evalCondition(condition)) whileStmt.accept(this)
      case ForStmt(init, condition, block) => init.accept(this); while (evalCondition(condition)) block.accept(this)
      case ReturnStmt(exp: Expression) => setReturnExpression(evalExpression(exp))
      case ProcedureCallStmt(name, args) =>
        // we evaluate the "args" in the current
        // environment.
        val actualArguments = args.map(a => evalExpression(a))
        env.push()  // after that, we can "push", to indicate a procedure call.
        visitProcedureCall(name, actualArguments) // then we execute the procedure.
        env.pop() // and we pop, to indicate that a procedure finished its execution.
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

  def evalCondition(expression: Expression) : Boolean = {
    val evalVisitor = new EvalExpressionVisitor(this)
    expression.accept(evalVisitor).asInstanceOf[BoolValue].value
  }

  def evalExpression(expression: Expression) : Expression = {
    val evalVisitor = new EvalExpressionVisitor(this)
    expression.accept(evalVisitor)
  }

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
  def setLocalVariable(name: String, exp: Expression) : Unit = {
    env.setLocalVariable(name, exp)
  }
}

class EvalExpressionVisitor(val interpreter: Interpreter) extends OberonVisitorAdapter {
  type T = Expression

  override def visit(exp: Expression): Expression = exp match {
    case Brackets(expression) => expression.accept(this)
    case IntValue(v) => IntValue(v)
    case BoolValue(v) => BoolValue(v)
    case Undef() => Undef()
    case VarExpression(name) => interpreter.env.lookup(name).get
    case EQExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value == v2.value))
    case NEQExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value != v2.value))
    case GTExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value > v2.value))
    case LTExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value < v2.value))
    case GTEExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value >= v2.value))
    case LTEExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => BoolValue(v1.value <= v2.value))
    case AddExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => IntValue(v1.value + v2.value))
    case SubExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => IntValue(v1.value - v2.value))
    case MultExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => IntValue(v1.value * v2.value))
    case DivExpression(left, right) => binExpression(left, right, (v1: Value[Int], v2: Value[Int]) => IntValue(v1.value / v2.value))
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

  /**
   * Eval a binary expression.
   *
   * @param left the left expression
   * @param right the right expression
   * @param fn a function that constructs an expression. Here we
   *           are using again a high-order function. We assign to
   *           the "result" visitor attribute the value we compute
   *           after applying this function.
   *
   * @tparam T a type parameter to set the function fn correctly.
   */
  def binExpression[T](left: Expression, right: Expression, fn: (Value[T], Value[T]) => Expression) : Expression = {
    val v1 = left.accept(this).asInstanceOf[Value[T]]
    val v2 = right.accept(this).asInstanceOf[Value[T]]
    fn(v1, v2)
  }
}
