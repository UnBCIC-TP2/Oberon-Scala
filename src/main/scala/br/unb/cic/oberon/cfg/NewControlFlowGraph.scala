package br.unb.cic.oberon.cfg

import br.unb.cic.oberon.ast._
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc

class NewControlFlowGraph {
  def init(stmet : Statement) : Statement = {
    stmet match {
      //case AssignmentStmt(name, exp) => AssignmentStmt(name, exp)
      case SequenceStmt(stmts) =>  init(stmts.head)
      case IfElseStmt(condition, thenStmt, elseStmt) => init(thenStmt)
      case WhileStmt(condition, stmt) => init(stmt)
      case _ => stmet

    }

  }

  def finalFG(stmet: Statement) : List[Statement] = {
    stmet match {
      case AssignmentStmt(name, exp) => List(AssignmentStmt(name, exp))
      case SequenceStmt(stmts) => finalFG(stmts.last)
      case IfElseStmt(condition, thenStmt, elseStmt) =>
        if (elseStmt != None){
          finalFG(thenStmt) concat finalFG(elseStmt.get)
        }
        else {
          finalFG(thenStmt)
        }
      case WhileStmt(condition, stmt) => finalFG(stmt)
    }
  }
}
