package br.unb.cic.oberon.cfg

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.ir.common._
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc

import scala.+:
import scala.collection.mutable.Stack

trait GraphNode

case class StartNode() extends GraphNode
case class SimpleNode(stmt: Statement) extends GraphNode
case class EndNode() extends GraphNode

//case class StartNode(label: Int) extends GraphNode
//case class SimpleNode(label: Int, stmt: Statement) extends GraphNode
//case class EndNode(label: Int) extends GraphNode

trait ControlFlowGraphBuilder {
  def createControlFlowGraph(
      procedure: Procedure[Statement]
  ): Graph[GraphNode, GraphEdge.DiEdge] // Graph
  def createControlFlowGraph(
      stmt: Statement
  ): Graph[GraphNode, GraphEdge.DiEdge] // Graph
}

class IntraProceduralGraphBuilder extends ControlFlowGraphBuilder {
  override def createControlFlowGraph(
      procedure: Procedure[Statement]
  ): Graph[GraphNode, GraphEdge.DiEdge] = {
    createControlFlowGraph(procedure.stmt)
  }

  override def createControlFlowGraph(
      stmt: Statement
  ): Graph[GraphNode, GraphEdge.DiEdge] = {
    var g = Graph[GraphNode, GraphEdge.DiEdge]()

    stmt match {
      case SequenceStmt(stmts) =>
        stmts.head match {
          case ForStmt(init, _, _) =>
            g += StartNode() ~> SimpleNode(init)
          case _ =>
            g += StartNode() ~> SimpleNode(stmts.head)
        }
        createControlFlowGraph(stmts, None, g)

      case ForStmt(init, _, forStmt) =>
        g += StartNode() ~> SimpleNode(init)
        g += SimpleNode(init) ~> SimpleNode(stmt)
        g += SimpleNode(stmt) ~> EndNode()
        processStmtNode(stmt, Some(forStmt), g)

      case _ =>
        g += StartNode() ~> SimpleNode(stmt)
        g += SimpleNode(stmt) ~> EndNode()
    }
  }

  /** A recursive definition that generates a graph from a list of stmts
    * @param stmts
    *   list of statements
    * @param g
    *   a graph used as accumulator
    * @return
    *   a new version of the graph
    */
  def createControlFlowGraph(
      stmts: List[Statement],
      end: Option[Statement],
      g: Graph[GraphNode, GraphEdge.DiEdge]
  ): Graph[GraphNode, GraphEdge.DiEdge] = {
    stmts match {
      case s1 :: s2 :: rest => // case the list has at least two elements. this is the recursive case
        s1 match {
          case IfElseStmt(
                _,
                _,
                Some(_)
              ) => // in this case, we do not create an edge from s1 -> s2
            s2 match {
              case ForStmt(init, _, _) =>
                processStmtNode(s1, Some(init), g)
              case _ =>
                processStmtNode(s1, Some(s2), g)
            }
            createControlFlowGraph(s2 :: rest, end, g)
          case CaseStmt(_, _, Some(_)) =>
            s2 match {
              case ForStmt(init, _, _) =>
                processStmtNode(s1, Some(init), g)
              case _ =>
                processStmtNode(s1, Some(s2), g)
            }
            createControlFlowGraph(s2 :: rest, end, g)
          case IfElseIfStmt(_, _, _, Some(_)) =>
            s2 match {
              case ForStmt(init, _, _) =>
                processStmtNode(s1, Some(init), g)
              case _ =>
                processStmtNode(s1, Some(s2), g)
            }
            createControlFlowGraph(s2 :: rest, end, g)
          case ForStmt(init, _, forStmt) =>
            g += SimpleNode(init) ~> SimpleNode(s1)
            g += SimpleNode(s1) ~> SimpleNode(s2)
            processStmtNode(s1, Some(forStmt), g)
            createControlFlowGraph(s2 :: rest, end, g)
          case _ =>
            s2 match {
              case ForStmt(init, _, _) =>
                g += SimpleNode(s1) ~> SimpleNode(init)
              case _ =>
                g += SimpleNode(s1) ~> SimpleNode(s2)
                processStmtNode(s1, Some(s2), g)
            }
            createControlFlowGraph(s2 :: rest, end, g)
        }
      case s1 :: List() => // case the list has just one element. this is the base case
        s1 match {
          case ForStmt(init, _, forStmt) =>
            g += SimpleNode(init) ~> SimpleNode(s1)
            processStmtNode(s1, Some(forStmt), g)
          case _ =>

        }

        if (!end.isDefined) {
          g += SimpleNode(s1) ~> EndNode()
          processStmtNode(
            s1,
            None,
            g
          ) // process the singleton node of the stmts list of statements
        }
        g
    }
  }

  /** Handle the specific logic for building a graph from the "from" stmt
    * @param from
    *   the from statement
    * @param target
    *   the optional target statement
    * @param g
    *   the cumulative graph
    * @return
    *   a new version of the graph
    */
  def processStmtNode(
      from: Statement,
      target: Option[Statement],
      g: Graph[GraphNode, GraphEdge.DiEdge]
  ): Graph[GraphNode, GraphEdge.DiEdge] = {
    from match {
      case IfElseStmt(_, thenStmt, optionalElseStmt) =>
        processStmtNode(from, thenStmt, target, g)
        if (optionalElseStmt.isDefined) {
          processStmtNode(from, optionalElseStmt.get, target, g)
        }
        g // returns g
      case WhileStmt(_, whileStmt) =>
        processStmtNode(
          from,
          whileStmt,
          Some(from),
          g
        ) // returns the recursive call
      case RepeatUntilStmt(_, repeatUntilStmt) =>
        processStmtNode(
          from,
          repeatUntilStmt,
          target,
          g
        ) // returns the recursive call
      case ForStmt(_, _, forStmt) =>
        processStmtNode(from, forStmt, Some(from), g)
      case IfElseIfStmt(_, thenStmt, elseIfStmt, optionalElseStmt) =>
        processStmtNode(from, thenStmt, target, g)
        elseIfStmt.foreach((ifElseIfBlock) => {
          ifElseIfBlock match {
            case ElseIfStmt(_, thenStmt) =>
              processStmtNode(from, thenStmt, target, g)
          }
        })
        if (optionalElseStmt.isDefined) {
          processStmtNode(from, optionalElseStmt.get, target, g)
        } else g += SimpleNode(from) ~> SimpleNode(target.get)
        g
      case CaseStmt(_, cases, optionalElseStmt) =>
        cases.foreach((caseBlock) => {
          caseBlock match {
            case SimpleCase(_, stmt) =>
              processStmtNode(from, stmt, target, g)
            case RangeCase(_, _, stmt) =>
              processStmtNode(from, stmt, target, g)
          }
        })
        if (optionalElseStmt.isDefined)
          processStmtNode(from, optionalElseStmt.get, target, g)
        else g += SimpleNode(from) ~> SimpleNode(target.get)
        g
      case _ =>
        g // if not a compound stmt (e.g., procedure call, assignment, ...), just return the graph g
    }
  }

  /** Deals with the cases where the "target" statement is a sequence stmt
    * @param from
    *   the from statement
    * @param target
    *   the target statement
    * @param end
    *   the possible "end" statement
    * @param g
    *   the cumulative graph
    * @return
    *   a new version of the graph.
    */
  def processStmtNode(
      from: Statement,
      target: Statement,
      end: Option[Statement],
      g: Graph[GraphNode, GraphEdge.DiEdge]
  ): Graph[GraphNode, GraphEdge.DiEdge] = {
    target match {
      case SequenceStmt(
            stmts
          ) => // if the target is a SequenceStmt, we have to create "sub-graph"
        g += SimpleNode(from) ~> SimpleNode(
          stmts.head
        ) // create an edge from "from" to the first elements of the list stmts
        if (end.isDefined) {
          g += SimpleNode(stmts.last) ~> SimpleNode(end.get)
          createControlFlowGraph(stmts, end, g)
        } else {
          createControlFlowGraph(stmts, None, g)
        }
      case _ =>
        g += SimpleNode(from) ~> SimpleNode(target)
        if (end.isDefined) {
          g += SimpleNode(target) ~> SimpleNode(end.get)
        }
        g // remember, the last statement corresponds to the "return statement"
    }
  }

}
