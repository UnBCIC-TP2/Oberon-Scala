package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import scala.collection.mutable.ListBuffer
import br.unb.cic.oberon.ir.ast.Procedure

class CoreVisitor() {

  var addedVariables: List[VariableDeclaration] = Nil

  def visit(stmt: Statement): Statement = stmt match {
    case SequenceStmt(stmts) =>
      SequenceStmt(flatSequenceOfStatements(SequenceStmt(stmts.map((stmt) => visit(stmt))).stmts))

    case LoopStmt(stmt) =>
      WhileStmt(BoolValue(true), visit(stmt))

    case RepeatUntilStmt(condition, stmt) =>
      WhileStmt(BoolValue(true), SequenceStmt(List(visit(stmt), visit(IfElseStmt(condition, ExitStmt(), None)))))

    case ForStmt(initStmt, condition, block) =>
      SequenceStmt(List(visit(initStmt), WhileStmt(condition, visit(block))))

    case IfElseIfStmt (condition, thenStmt, elsifStmt, elseStmt) =>
      IfElseStmt (condition, visit(thenStmt), Some(transformElsif(elsifStmt, elseStmt)))

    case CaseStmt(exp, cases, elseStmt) => transformCase(exp, cases, elseStmt)

    case WhileStmt(condition, stmt) =>
      WhileStmt(condition, visit(stmt))

    case _ => stmt
  }

  private val caseIdGenerator: Iterator[Int] = Iterator.from(0)

  private def transformCase(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement]): Statement = {
    val coreElseStmt = elseStmt.map((stmt) => visit(stmt))

    // TODO corrigir comportamento para outras expressões

    val caseExpressionId = exp match {
      case VarExpression(name) => name
      case _ => f"case_exp#${caseIdGenerator.next()}"
    }
    val caseExpressionEvaluation = AssignmentStmt(VarAssignment(caseExpressionId), exp)

    def casesToIfElseStmt(cases: List[CaseAlternative]): IfElseStmt =
      cases match {
        case SimpleCase(condition, stmt) :: Nil =>
          val newCondition =
            EQExpression(VarExpression(caseExpressionId), condition)
          IfElseStmt(newCondition, visit(stmt), coreElseStmt)

        case SimpleCase(condition, stmt) :: tailCases =>
          val newCondition =
            EQExpression(VarExpression(caseExpressionId), condition)
          val newElse = Some(casesToIfElseStmt(tailCases))
          IfElseStmt(newCondition, visit(stmt), newElse)

        case RangeCase(min, max, stmt) :: Nil =>
          val newCondition = AndExpression(
            LTEExpression(min, VarExpression(caseExpressionId)),
            LTEExpression(VarExpression(caseExpressionId), max)
          )
          IfElseStmt(newCondition, visit(stmt), coreElseStmt)

        case RangeCase(min, max, stmt) :: tailCases =>
          val newCondition = AndExpression(
            LTEExpression(min, VarExpression(caseExpressionId)),
            LTEExpression(VarExpression(caseExpressionId), max)
          )
          val newElse = Some(casesToIfElseStmt(tailCases))
          IfElseStmt(newCondition, visit(stmt), newElse)

        case _ => throw new RuntimeException("Invalid CaseStmt without cases")
      }
    
    exp match {
      case VarExpression(_) => casesToIfElseStmt(cases)
      case _ =>
        addedVariables = VariableDeclaration(caseExpressionId, IntegerType) :: addedVariables
        SequenceStmt(List(caseExpressionEvaluation, casesToIfElseStmt(cases)))
    }
  }

  private def transformElsif(elsifStmts: List[ElseIfStmt], elseStmt: Option[Statement]): Statement =
    elsifStmts match {
      case currentElsif :: Nil =>
        IfElseStmt(currentElsif.condition, visit(currentElsif.thenStmt), elseStmt.map((stmt) => visit(stmt)))
      case currentElsif :: tail =>
        IfElseStmt(currentElsif.condition, visit(currentElsif.thenStmt), Some(transformElsif(tail, elseStmt.map((stmt) => visit(stmt)))))
      case Nil =>
        throw new IllegalArgumentException("elsifStmts cannot be empty.")
    }

  private def transformProcedureStatement(procedure: Procedure): Procedure = {
      Procedure(
      name = procedure.name,
      args = procedure.args,
      returnType = procedure.returnType,
      constants = procedure.constants,
      variables = procedure.variables,
      stmt = visit(procedure.stmt))
    }

  def flatSequenceOfStatements(stmts: List[Statement]): List[Statement] = stmts.flatMap {
    case SequenceStmt(ss) => flatSequenceOfStatements(ss)
    case s => List(s)
  }

  def transformModule(module: OberonModule): OberonModule = {
    // É possível remover essa val?
    val stmtcore = visit(module.stmt.get)

     OberonModule(
      name = module.name,
      submodules = module.submodules,
      userTypes = module.userTypes,
      constants = module.constants,
      variables = module.variables ++ addedVariables,
      procedures = module.procedures.map(transformProcedureStatement(_)),
      stmt = Some(stmtcore)
    )
  }
}

object CoreChecker {
  
  def stmtCheck(stmt: Statement): Boolean = {
    stmt match {
      case SequenceStmt(stmts) => stmts.map(s => stmtCheck(s)).foldLeft(true)(_ && _)

      // Laços
      case LoopStmt(stmt) => false
      case RepeatUntilStmt(condition, stmt) => false
      case ForStmt(initStmt, condition, block) => false
      case WhileStmt(_, whileStmt) => stmtCheck(whileStmt)
      
      // Condicionais
      case CaseStmt(exp, cases, elseStmt) => false  
      case IfElseIfStmt (condition, thenStmt, elsifStmt, elseStmt) => false
      case IfElseStmt(_, thenStmt, Some(elseStmt)) => stmtCheck(thenStmt) && stmtCheck(elseStmt)
      
      case _ => true
    }
  }

  def isModuleCore(module: OberonModule): Boolean = {
    stmtCheck(module.stmt.get) && module.procedures.map(p => stmtCheck(p.stmt)).foldLeft(true)(_ && _)
  }
}