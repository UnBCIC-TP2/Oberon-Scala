package br.unb.cic.oberon.cfg

import br.unb.cic.oberon.ast._
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc

trait NewGraphNode

case class NewStartNode() extends NewGraphNode
case class NewSimpleNode(stmt: Statement) extends NewGraphNode
case class NewEndNode() extends NewGraphNode

//case class StartNode(label: Int) extends GraphNode
//case class SimpleNode(label: Int, stmt: Statement) extends GraphNode
//case class EndNode(label: Int) extends GraphNode

trait NewControlFlowGraphBuilder {
  def createControlFlowGraph(procedure: Procedure): Graph[GraphNode, GraphEdge.DiEdge] // Graph
  def createControlFlowGraph(stmt: Statement): Graph[GraphNode, GraphEdge.DiEdge] // Graph
}

class NewControlFlowGraph {
  def init(stmet : Statement) : Statement = {
    stmet match {
      case SequenceStmt(stmts) =>  init(stmts.head)
      case _ => stmet
    }
  }

  def finalFG(stmet: Statement) : List[Statement] = {
    stmet match {
      case AssignmentStmt(name, exp) => List(AssignmentStmt(name, exp))
      case SequenceStmt(stmts) => finalFG(stmts.last)
      case IfElseStmt(condition, thenStmt, elseStmt) =>
        if (elseStmt.isDefined){
          finalFG(thenStmt) concat finalFG(elseStmt.get)
        }
        else {
          finalFG(thenStmt)
        }
      case IfElseIfStmt(condition, thenStmt, elseifStmt, elseStmt) =>
        var result = finalFG(thenStmt)

        for(elseIf <- elseifStmt){
          result = result concat finalFG(elseIf)
        }

        if(elseStmt.isDefined)
          result concat finalFG(elseStmt.get)
        else
          result
      case ElseIfStmt(condition, thenStmt) => finalFG(thenStmt)
      case CaseStmt(exp, cases, elseStmt) =>
        var result : List[Statement] = List()
        for (caso <- cases){
          var teste = caso  match {
            case SimpleCase(condition, stmt) => finalFG(stmt)
            case RangeCase(min, max, stmt) => finalFG(stmt)
          }
          result = result concat teste
        }

        if (elseStmt.isDefined){
          result = result concat finalFG(elseStmt.get)
        }

        result

      case _ => List(stmet)
    }
  }
}
