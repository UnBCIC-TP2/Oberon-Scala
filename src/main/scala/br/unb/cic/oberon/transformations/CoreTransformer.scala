package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import scala.collection.mutable.ListBuffer
import br.unb.cic.oberon.ir.ast.Procedure

class CoreVisitor() {

  //var addedVariables: List[VariableDeclaration] = Nil

  def visit(stmt: Statement, caseIdGenerator: Int = 0, addedVariables: List[VariableDeclaration] = Nil): Statement = stmt match {
    case SequenceStmt(stmts) =>
      SequenceStmt(flatSequenceOfStatements(SequenceStmt(stmts.map((stmt) => visit(stmt, caseIdGenerator, addedVariables))).stmts))

    case LoopStmt(stmt) =>
      WhileStmt(BoolValue(true), visit(stmt, caseIdGenerator, addedVariables))

    case RepeatUntilStmt(condition, stmt) =>
      WhileStmt(BoolValue(true), visit(SequenceStmt(List(visit(stmt, caseIdGenerator, addedVariables), visit(IfElseStmt(condition, ExitStmt(), None), caseIdGenerator, addedVariables)))))

    case ForStmt(initStmt, condition, block) =>
      SequenceStmt(List(visit(initStmt, caseIdGenerator, addedVariables), WhileStmt(condition, visit(block, caseIdGenerator, addedVariables))))

    case IfElseIfStmt (condition, thenStmt, elsifStmt, elseStmt) =>
      IfElseStmt (condition, visit(thenStmt, caseIdGenerator, addedVariables), Some(transformElsif(elsifStmt, elseStmt, caseIdGenerator, addedVariables)))

    case CaseStmt(exp, cases, elseStmt) => transformCase(exp, cases, elseStmt, caseIdGenerator, addedVariables)

    case WhileStmt(condition, stmt) =>
      WhileStmt(condition, visit(stmt, caseIdGenerator, addedVariables))

    case _ => stmt
  }

  //private val caseIdGenerator: Iterator[Int] = Iterator.from(0)

  private def transformCase(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement], caseIdGenerator: Int, addedVariables: List[VariableDeclaration]): Statement = {
    val coreElseStmt = elseStmt.map((stmt) => visit(stmt, caseIdGenerator, addedVariables))

    // TODO corrigir comportamento para outras expressões

    val caseExpressionId = exp match {
      case VarExpression(name) => name
      case _ => f"case_exp#${caseIdGenerator + 1}"
    }
    val caseExpressionEvaluation = AssignmentStmt(VarAssignment(caseExpressionId), exp)

    def casesToIfElseStmt(cases: List[CaseAlternative]): IfElseStmt =
      cases match {
        case SimpleCase(condition, stmt) :: Nil =>
          val newCondition =
            EQExpression(VarExpression(caseExpressionId), condition)
          IfElseStmt(newCondition, visit(stmt, caseIdGenerator, VariableDeclaration(caseExpressionId, IntegerType) :: addedVariables), coreElseStmt)

        case SimpleCase(condition, stmt) :: tailCases =>
          val newCondition =
            EQExpression(VarExpression(caseExpressionId), condition)
          val newElse = Some(casesToIfElseStmt(tailCases))
          IfElseStmt(newCondition, visit(stmt, caseIdGenerator, VariableDeclaration(caseExpressionId, IntegerType) :: addedVariables), newElse)

        case RangeCase(min, max, stmt) :: Nil =>
          val newCondition = AndExpression(
            LTEExpression(min, VarExpression(caseExpressionId)),
            LTEExpression(VarExpression(caseExpressionId), max)
          )
          IfElseStmt(newCondition, visit(stmt, caseIdGenerator, VariableDeclaration(caseExpressionId, IntegerType) :: addedVariables), coreElseStmt)

        case RangeCase(min, max, stmt) :: tailCases =>
          val newCondition = AndExpression(
            LTEExpression(min, VarExpression(caseExpressionId)),
            LTEExpression(VarExpression(caseExpressionId), max)
          )
          val newElse = Some(casesToIfElseStmt(tailCases))
          IfElseStmt(newCondition, visit(stmt, caseIdGenerator, VariableDeclaration(caseExpressionId, IntegerType) :: addedVariables), newElse)

        case _ => throw new RuntimeException("Invalid CaseStmt without cases")
      }
    
    exp match {
      case VarExpression(_) => casesToIfElseStmt(cases)
      case _ =>
        //VariableDeclaration(caseExpressionId, IntegerType) +: addedVariables
        //addedVariables = VariableDeclaration(caseExpressionId, IntegerType) :: addedVariables
        SequenceStmt(List(caseExpressionEvaluation, casesToIfElseStmt(cases)))
    }
  }

  private def transformElsif(elsifStmts: List[ElseIfStmt], elseStmt: Option[Statement], caseIdGenerator: Int, addedVariables: List[VariableDeclaration]): Statement =
    elsifStmts match {
      case currentElsif :: Nil =>
        IfElseStmt(currentElsif.condition, visit(currentElsif.thenStmt, caseIdGenerator, addedVariables), elseStmt.map((stmt) => visit(stmt, caseIdGenerator, addedVariables)))
      case currentElsif :: tail =>
        IfElseStmt(currentElsif.condition, visit(currentElsif.thenStmt, caseIdGenerator, addedVariables), Some(transformElsif(tail, elseStmt.map((stmt) => visit(stmt, caseIdGenerator, addedVariables)), caseIdGenerator, addedVariables)))
      case Nil =>
        throw new IllegalArgumentException("elsifStmts cannot be empty.")
    }

  private def transformProcedureStatement(procedure: Procedure, caseIdGenerator: Int, addedVariables: List[VariableDeclaration]): Procedure = {
      Procedure(
      name = procedure.name,
      args = procedure.args,
      returnType = procedure.returnType,
      constants = procedure.constants,
      variables = procedure.variables,
      stmt = visit(procedure.stmt, caseIdGenerator, addedVariables))
    }

  def flatSequenceOfStatements(stmts: List[Statement]): List[Statement] = stmts.flatMap {
    case SequenceStmt(ss) => flatSequenceOfStatements(ss)
    case s => List(s)
  }

  def transformModule(module: OberonModule, caseIdGenerator: Int = 0, addedVariables: List[VariableDeclaration] = Nil): OberonModule = {
    // É possível remover essa val?
    val stmtcore = visit(module.stmt.get, caseIdGenerator, addedVariables)

     OberonModule(
      name = module.name,
      submodules = module.submodules,
      userTypes = module.userTypes,
      constants = module.constants,
      variables = module.variables ++ addedVariables,
      procedures = module.procedures.map(transformProcedureStatement(_, caseIdGenerator, addedVariables)),
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