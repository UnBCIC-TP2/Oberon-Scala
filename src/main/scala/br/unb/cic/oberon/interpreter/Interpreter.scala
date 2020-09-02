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

  override def visit(stmt: Statement): Unit = stmt match {
    case AssignmentStmt(name, exp) => env.setVariable(name, exp)
    case SequenceStmt(stmts) => stmts.foreach(s => s.accept(this))
    case ReadIntStmt(name) => env.setVariable(name, IntValue(StdIn.readLine().toInt))
    case WriteStmt(exp) => println(exp)
    case IfElseStmt(condition, thenStmt, elseStmt) => if(evalCondition(condition)) thenStmt.accept(this) else if(elseStmt.isDefined) elseStmt.get.accept(this)
    case WhileStmt(condition, whileStmt) => while(evalCondition(condition)) whileStmt.accept(this)
    case ReturnStmt(exp: Expression) => setReturnExpression(exp)
    case ProcedureCallStmt(name, args) => {
      env.push()
      visitProcedureCall(name, args)
      env.pop()
    }
  }

  /*
   * process the ReturnStmt(exp) statement.
   * In this case, we just create a new entry
   * in the local variables, assigning
   * "return" -> exp.
   */
  private def setReturnExpression(exp: Expression): Unit = env.setLocalVariable(Values.ReturnKeyWord, exp)

  def visitProcedureCall(name: String, args: List[Expression]) = {
    val procedure = env.findProcedure(name)
    updateEnvironmentWithProcedureCall(procedure, args)
    procedure.stmt.accept(this)
  }

  def updateEnvironmentWithProcedureCall(procedure: Procedure, args: List[Expression]): Unit = {
    procedure.args.map(formal => formal.name).zip(args).foreach(pair => env.setLocalVariable(pair._1, pair._2))
    procedure.constants.foreach(c => env.setLocalVariable(c.name, c.exp))
    procedure.variables.foreach(v => env.setLocalVariable(v.name, Undef()))
  }

  def evalCondition(expression: Expression) : Boolean = {
    val evalVisitor = new EvalExpressionVisitor(this)

    expression.accept(evalVisitor)

    evalVisitor.result.asInstanceOf[BoolValue].value
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

  override def visit(exp: Expression): Unit = exp match {
    case Brackets(expression) => expression.accept(this)
    case IntValue(v) => result = IntValue(v)
    case BoolValue(v) => result = BoolValue(v)
    case Undef() => result = Undef()
    case VarExpression(name) => result = interpreter.env.lookup(name).get
    case EQExpression(left, right) => binIntExpression(left, right, (v1: Integer, v2: Integer) => BoolValue(v1 == v2))
    case NEQExpression(left, right) => binIntExpression(left, right, (v1: Integer, v2: Integer) => BoolValue(v1 != v2))
    case GTExpression(left, right) => binIntExpression(left, right, (v1: Integer, v2: Integer) => BoolValue(v1 > v2))
    case LTExpression(left, right) => binIntExpression(left, right, (v1: Integer, v2: Integer) => BoolValue(v1 < v2))
    case GTEExpression(left, right) => binIntExpression(left, right, (v1: Integer, v2: Integer) => BoolValue(v1 >= v2))
    case LTEExpression(left, right) => binIntExpression(left, right, (v1: Integer, v2: Integer) => BoolValue(v1 <= v2))
    case AddExpression(left, right) => binIntExpression(left, right, (v1: Integer, v2: Integer) => IntValue(v1 + v2))
    case SubExpression(left, right) => binIntExpression(left, right, (v1: Integer, v2: Integer) => IntValue(v1 - v2))
    case MultExpression(left, right) => binIntExpression(left, right, (v1: Integer, v2: Integer) => IntValue(v1 * v2))
    case DivExpression(left, right) => binIntExpression(left, right, (v1: Integer, v2: Integer) => IntValue(v1 / v2))
    case AndExpression(left, right) => binBoolExpression(left, right, (v1: Boolean, v2: Boolean) => BoolValue(v1 && v2))
    case OrExpression(left, right) => binBoolExpression(left, right, (v1: Boolean, v2: Boolean) => BoolValue(v1 || v2))
    case FunctionCallExpression(name, args) => {
      interpreter.env.push()
      visitFunctionCall(name, args)
      interpreter.env.pop()
    }
  }

  def visitFunctionCall(name: String, args: List[Expression]): Unit = {
    interpreter.visitProcedureCall(name, args)

    val returnValue = interpreter.env.lookup(Values.ReturnKeyWord)

    assert(returnValue.isDefined) // a function call must set a local variable with the "return" expression

    result = interpreter.env.lookup(Values.ReturnKeyWord).get
  }


  def binIntExpression(left: Expression, right: Expression, fn: (Integer, Integer) => Expression): Unit = {
    // reduce the left expression to a normal form
    left.accept(this)
    val v1 = result.asInstanceOf[IntValue].value

    // reduce the right expression to a normal form
    right.accept(this)
    val v2 = result.asInstanceOf[IntValue].value

    // applies the high order function fn.
    result = fn(v1, v2)
  }

  def binBoolExpression(left: Expression, right: Expression, fn: (Boolean, Boolean) => Expression): Unit = {
    // reduce the left expression to a normal form
    left.accept(this)
    val v1 = result.asInstanceOf[BoolValue].value

    // reduce the right expression to a normal form
    right.accept(this)
    val v2 = result.asInstanceOf[BoolValue].value

    // applies the high order function fn.
    result = fn(v1, v2)
  }
}
