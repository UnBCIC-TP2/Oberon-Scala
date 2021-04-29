package br.unb.cic.oberon.cfg

import scala.collection.mutable.Map
import br.unb.cic.oberon.ast._
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc

import scala.+:
import scala.collection.mutable.Stack

trait GraphNode

case class StartNode() extends GraphNode
//case class SimpleNode(stmt: Statement) extends GraphNode
case class SimpleNode(stmt: Statement) extends GraphNode
case class EndNode() extends GraphNode

//case class StartNode(label: Int) extends GraphNode
//case class EndNode(label: Int) extends GraphNode

trait ControlFlowGraphBuilder {
  def createControlFlowGraph(procedure: Procedure): Graph[GraphNode, GraphEdge.DiEdge] // Graph
  def createControlFlowGraph(stmt: Statement): Graph[GraphNode, GraphEdge.DiEdge] // Graph
}

class IntraProceduralGraphBuilder extends ControlFlowGraphBuilder {
  // var vertices: Map.empty[Statement, GraphNode]
  var vertices = Map[Statement, GraphNode]()
  

  override def createControlFlowGraph(procedure: Procedure): Graph[GraphNode, GraphEdge.DiEdge] = {
    createControlFlowGraph(procedure.stmt)
  }

  override def createControlFlowGraph(stmt: Statement): Graph[GraphNode, GraphEdge.DiEdge] = {
    var g = Graph[GraphNode, GraphEdge.DiEdge]()
    // a partir do stmt, varrer todos os statements que são alcancaveis a partir dele
    // pra cada statement dentro dele, instanciar o no do grafo e adicionar no mapa o statement -> no correspondente que foi criado
    // a partir de todo algoritmo q cria o grafo, recuperar o no a partir do statement
    stmt match {
      case SequenceStmt(label, stmts) =>
        g += StartNode() ~> SimpleNode(stmts.head)
        createControlFlowGraph(stmts, g)
      case _ =>
        g += StartNode() ~> SimpleNode(stmt)
        g += SimpleNode(stmt) ~> EndNode()
    }
  }

  /**
   * A recursive definition that generates a graph from a list of stmts
   * @param stmts list of statements
   * @param g a graph used as accumulator
   * @return a new version of the graph
   */
  def createControlFlowGraph(stmts: List[Statement], g: Graph[GraphNode, GraphEdge.DiEdge]): Graph[GraphNode, GraphEdge.DiEdge] =
 stmts match {
    case s1 :: s2 :: rest => // case the list has at least two elements. this is the recursive case
      s1 match {
        case IfElseStmt(_, _, _, Some(_)) => // in this case, we do not create an edge from s1 -> s2
          processStmtNode(s1, Some(s2), g)
          createControlFlowGraph(s2 :: rest, g)
        case CaseStmt(_, _, _, Some(_)) =>
          processStmtNode(s1, Some(s2), g)
          createControlFlowGraph(s2 :: rest, g)
        case IfElseIfStmt(_, _, _, _, Some(_)) =>
          processStmtNode(s1, Some(s2), g)
          createControlFlowGraph(s2 :: rest, g)
        case _ =>
          g += SimpleNode(s1) ~> SimpleNode(s2)
          processStmtNode(s1, Some(s2), g)
          createControlFlowGraph(s2 :: rest, g)
      }
    case s1 :: List() => // case the list has just one element. this is the base case
      g += SimpleNode(s1) ~> EndNode()
      processStmtNode(s1, None, g) // process the singleton node of the stmts list of statements
      g
  }


  /**
   * Handle the specific logic for building a graph from the "from" stmt
   * @param from the from statement
   * @param target the optional target statement
   * @param g the cumulative graph
   * @return a new version of the graph
   */
  def processStmtNode(from: Statement, target: Option[Statement], g: Graph[GraphNode, GraphEdge.DiEdge]): Graph[GraphNode, GraphEdge.DiEdge] = {
    from match {
      case IfElseStmt(label, _, thenStmt, optionalElseStmt) =>
        processStmtNode(from, thenStmt, target, g)
        if (optionalElseStmt.isDefined) {
          processStmtNode(from, optionalElseStmt.get, target, g)
        }
        g // returns g
      case WhileStmt(label, _, whileStmt) =>
        processStmtNode(from, whileStmt, target, g) // returns the recursive call
      case RepeatUntilStmt(label, _, repeatUntilStmt) =>
        processStmtNode(from, repeatUntilStmt, target, g) // returns the recursive call
      case ForStmt(label, init,_ ,forStmt) =>
        processStmtNode(init, forStmt, target, g)
      case IfElseIfStmt(label, _, thenStmt, elseIfStmt, optionalElseStmt) =>
        processStmtNode(from, thenStmt, target, g)
          elseIfStmt.foreach((ifElseIfBlock) => {
            ifElseIfBlock match {
              case ElseIfStmt(label, _, thenStmt) =>
                processStmtNode(from, thenStmt, target, g)
            }
          })
          if (optionalElseStmt.isDefined){
            processStmtNode(from, optionalElseStmt.get, target, g)
          }
          else g += SimpleNode(from) ~> SimpleNode(target.get)
          g
      case CaseStmt(_, _, cases, optionalElseStmt) =>
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
      case _ => g // if not a compound stmt (e.g., procedure call, assignment, ...), just return the graph g
    }
  }
  /**
   * Deals with the cases where the "target" statement is a sequence stmt
   * @param from the from statement
   * @param target the target statement
   * @param end the possible "end" statement
   * @param g the cumulative graph
   * @return a new version of the graph.
   */
  def processStmtNode(from: Statement, target: Statement, end: Option[Statement], g: Graph[GraphNode, GraphEdge.DiEdge]): Graph[GraphNode, GraphEdge.DiEdge] = {
    target match {
      case SequenceStmt(label, stmts) => // if the target is a SequenceStmt, we have to create "sub-graph"
        g += SimpleNode(from) ~> SimpleNode(stmts.head) // create an edge from "from" to the first elements of the list stmts
        if(end.isDefined) {
          g += SimpleNode(stmts.last) ~> SimpleNode(end.get)
        }
        createControlFlowGraph(stmts, g)
      case _ =>
        g += SimpleNode(from) ~> SimpleNode(target)
        if(end.isDefined) {
          g += SimpleNode(target) ~> SimpleNode(end.get)
        }
        g // remember, the last statement corresponds to the "return statement"
    }
  }

}
