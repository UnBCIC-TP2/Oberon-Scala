package br.unb.cic.oberon.ir.ast

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
case class OberonModule(name: String,
                        submodules: Set[String],
                        userTypes: List[UserDefinedType],
                        constants: List[Constant],
                        variables: List[VariableDeclaration],
                        procedures: List[Procedure[Statement]],
                        tests: List[Test],
                        stmt: Option[Statement]
                       ) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

// SequenceStatement(List[Stmt]) extends Stmt
// alternativa: SequenceStatement(stmt, stmt) extends Stmt

/* procedure declaration definition */
/*
class Procedure(name: String,
    args: List[FormalArg],
    returnType: Option[Type],
    constants: List[Constant],
    variables: List[VariableDeclaration],
    stmt: Statement) extends ProcedureBase[Statement](name,args,returnType,constants,variables,stmt)

class ProcedureS(
    name: String,
    args: List[FormalArg],
    returnType: Option[Type],
    constants: List[Constant],
    variables: List[VariableDeclaration],
    stmt: Statement
)
*/
/* test declaration definition*/
case class Test(modifier: String,
                name: String,
                description: StringValue,
                constants: List[Constant],
                variables: List[VariableDeclaration],
                stmt: Statement
                ) {
  def accept(v: OberonVisitor): v.T = v.visit(this)
}

case class AssignmentStmt(designator: Designator, exp: Expression)
    extends Statement
case class SequenceStmt(stmts: List[Statement]) extends Statement
case class ReadLongRealStmt(varName: String) extends Statement
case class ReadRealStmt(varName: String) extends Statement
case class ReadLongIntStmt(varName: String) extends Statement
case class ReadIntStmt(varName: String) extends Statement
case class ReadShortIntStmt(varName: String) extends Statement
case class ReadCharStmt(varName: String) extends Statement
case class WriteStmt(expression: Expression) extends Statement
case class ProcedureCallStmt(name: String, args: List[Expression])
    extends Statement
case class TestCallStmt(name: String) extends Statement
case class IfElseStmt(
    condition: Expression,
    thenStmt: Statement,
    elseStmt: Option[Statement]
) extends Statement
case class IfElseIfStmt(
    condition: Expression,
    thenStmt: Statement,
    elseifStmt: List[ElseIfStmt],
    elseStmt: Option[Statement]
) extends Statement
case class ElseIfStmt(condition: Expression, thenStmt: Statement)
    extends Statement
case class WhileStmt(condition: Expression, stmt: Statement) extends Statement
case class RepeatUntilStmt(condition: Expression, stmt: Statement)
    extends Statement
case class ForStmt(init: Statement, condition: Expression, stmt: Statement)
    extends Statement
case class ForEachStmt(varName: String, exp: Expression, stmt: Statement)
    extends Statement
case class LoopStmt(stmt: Statement) extends Statement
case class ReturnStmt(exp: Expression) extends Statement
case class CaseStmt(
    exp: Expression,
    cases: List[CaseAlternative],
    elseStmt: Option[Statement]
) extends Statement
case class ExitStmt() extends Statement
case class NewStmt(varName: String) extends Statement
case class MetaStmt(f: () => Statement) extends Statement
case class AssertTrueStmt(exp: Expression) extends Statement
case class AssertEqualStmt(left: Expression, right: Expression) extends Statement
case class AssertNotEqualStmt(left: Expression, right: Expression) extends Statement
case class AssertError() extends Statement

case class SimpleCase(condition: Expression, stmt: Statement)
    extends CaseAlternative
case class RangeCase(min: Expression, max: Expression, stmt: Statement)
    extends CaseAlternative
