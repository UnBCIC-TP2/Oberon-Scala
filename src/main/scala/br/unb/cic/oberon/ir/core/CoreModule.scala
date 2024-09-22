package br.unb.cic.oberon.ir.core

import br.unb.cic.oberon.visitor.OberonVisitor
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon.ir.common._

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

/** Abstract representation of an Oberon Module
 *
 * Here we use an object-oriented decomposition to represent
 * Oberon concepts as Scala classes and traits.
 *
 * The use of case classes here are handy, mostly because we
 * can use case case classes in pattern matching, and they also
 * provide useful methods (such as equality).
 *
 * Traits offers an interesting approach for software composition,
 * besides OO inheritance.
 */
case class CoreModule(name: String,
                        submodules: Set[String],
                        constants: List[Constant],
                        variables: List[VariableDeclaration],
                        procedures: List[Procedure[Expression]],
                        tests: List[TestCore],
                        exp: Option[Expression]
                       ) {
  // def accept(v: OberonVisitor): v.T = v.visit(this)
}

case class TestCore(modifier: String,
                name: String,
                description: StringValue,
                constants: List[Constant],
                variables: List[VariableDeclaration],
                exp: Expression
                ) {
  //def accept(v: OberonVisitor): v.T = v.visit(this)
}

// SequenceStatement(List[Stmt]) extends Stmt
// alternativa: SequenceStatement(stmt, stmt) extends Stmt


case class AssignmentExp(designator: Designator, exp: Expression)
    extends Expression
case class SequenceExp(exps: (Expression,Expression)) extends Expression
case class ReadLongRealExp(varName: String) extends Expression
case class ReadRealExp(varName: String) extends Expression
case class ReadLongIntExp(varName: String) extends Expression
case class ReadIntExp(varName: String) extends Expression
case class ReadShortIntExp(varName: String) extends Expression
case class ReadCharExp(varName: String) extends Expression
case class WriteExp(Expression: Expression) extends Expression
case class ProcedureCallExp(name: String, args: List[Expression])
    extends Expression
case class TestCallExp(name: String) extends Expression
case class IfElseExp(
    condition: Expression,
    thenexp: Expression,
    elseexp: Option[Expression]
) extends Expression
case class WhileExp(condition: Expression, exp: Expression) extends Expression
case class ReturnExp(exp: Expression) extends Expression

/** LOW PRIORITY */
case class CaseExp(
    exp: Expression,
    cases: List[CaseAlternative],
    elseExp: Option[Expression]
) extends Expression

case class ExitExp() extends Expression
case class NewExp(varName: String) extends Expression
case class MetaExp(f: () => Expression) extends Expression
case class AssertTrueExp(exp: Expression) extends Expression
case class AssertEqualExp(left: Expression, right: Expression) extends Expression
case class AssertNotEqualExp(left: Expression, right: Expression) extends Expression
case class AssertErrorExp() extends Expression

case class SimpleCaseEx(condition: Expression, exp: Expression)
    extends CaseAlternative
case class RangeCaseEx(min: Expression, max: Expression, exp: Expression)
    extends CaseAlternative