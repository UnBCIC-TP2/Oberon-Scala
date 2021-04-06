package br.unb.cic.oberon.transformations

import br.unb.cic.oberon.ast.{BoolValue, ForStmt, LoopStmt, Statement, WhileStmt, RepeatUntilStmt, CaseStmt, IfElseIfStmt}
import br.unb.cic.oberon.visitor.OberonVisitorAdapter

class CoreVisitor extends OberonVisitorAdapter {

    override type T = Statement

    override def visit(stmt: Statement): Statement = stmt match {
        case LoopStmt(stmt) => WhileStmt(BoolValue(true), stmt)
        case RepeatUntilStmt(condition, stmt) => stmt; WhileStmt(condition, stmt)
        case ForStmt(init, condition, block) => init; WhileStmt(condition, block)
        //case CaseStmt(exp, cases, elseStmt) => checkCaseStmt(exp, cases, elseStmt)
    }
/*
    private def checkCaseStmt(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement]): Unit = {
      var matched = false
      var i = 0
      while (i < cases.size && !matched) {
        cases(i) match {
            case RangeCase(min, max, stmt) =>
                if (exp >= min && exp <= max) stmt
                
            case SimpleCase(condition, stmt) =>
            
                
        }
        i += 1
      }
      if (!matched && elseStmt.isDefined) {
            
      }
    
  }*/
}

