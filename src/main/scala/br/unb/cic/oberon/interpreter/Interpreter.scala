package br.unb.cic.oberon.interpreter

import br.unb.cic.oberon.ast.{AddExpression, AndExpression, BoolValue, Brackets, Constant, DivExpression, EQExpression, Expression, FormalArg, GTEExpression, GTExpression, IntValue, LTEExpression, LTExpression, MultExpression, NEQExpression, OberonModule, OrExpression, Procedure, Statement, SubExpression, Type, Undef, VarExpression, VariableDeclaration}
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.visitor.{OberonVisitor, OberonVisitorAdapter}
import com.sun.org.apache.xpath.internal.operations.Mult

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
    env.declareGlobal(constant.name, constant.exp)
  }

  override def visit(variable: VariableDeclaration): Unit = {
    env.declareGlobal(variable.name, Undef())
  }

  override def visit(procedure: Procedure): Unit = {
    env.declareProcedure(procedure)
  }

  override def visit(stmt: Statement): Unit = ???

}
class EvalExpressionVisitor(val env: Environment[Expression]) extends OberonVisitorAdapter {
  type T = Expression

  override def visit(exp: Expression): Unit = exp match {
    case Brackets(expression) => expression.accept(this)
    case IntValue(v) => result = IntValue(v)
    case BoolValue(v) => result = BoolValue(v)
    case Undef() => result = Undef()
    case VarExpression(name) => result = env.lookup(name).get
    // TODO: function call expression
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
