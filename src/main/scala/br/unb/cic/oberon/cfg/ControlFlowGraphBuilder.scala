package br.unb.cic.oberon.cfg

import br.unb.cic.oberon.ast._
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc

abstract class StmtHolder[+A] {
  def get: A
}
case object NoStmt extends StmtHolder[Nothing] {
  def get: Nothing = throw new NoSuchElementException("NoStmt.get")
}
case class FullStmt(stmt: Statement) extends StmtHolder[Statement] {
  def get: Statement = stmt
}
case class IgnoreInitStmt(stmt: Statement) extends StmtHolder[Statement] {
  def get: Statement = stmt
}

trait GraphNode

case class StartNode() extends GraphNode
case class SimpleNode(stmt: Statement) extends GraphNode
case class EndNode() extends GraphNode

//case class StartNode(label: Int) extends GraphNode
//case class SimpleNode(label: Int, stmt: Statement) extends GraphNode
//case class EndNode(label: Int) extends GraphNode

trait ControlFlowGraphBuilder {
  type CFGtype = Graph[GraphNode, GraphEdge.DiEdge]
  def createControlFlowGraph(procedure: Procedure): CFGtype // Graph
  def createControlFlowGraph(stmt: Statement): CFGtype // Graph
}

class IntraProceduralGraphBuilder extends ControlFlowGraphBuilder {
  override def createControlFlowGraph(procedure: Procedure): CFGtype = {
    createControlFlowGraph(procedure.stmt)
  }

  override def createControlFlowGraph(stmt: Statement): CFGtype = {
    var g = Graph[GraphNode, GraphEdge.DiEdge]()

    stmt match {
      case SequenceStmt(stmts) =>
        addEdge(None, stmts.head, g)
        createControlFlowGraph(stmts, NoStmt, g)
      case _ =>
        addEdge(None, stmt, g)
        g += SimpleNode(stmt) ~> EndNode()
        processStmtNode(stmt, NoStmt, g)
    }
  }

  /**
   * A recursive definition that generates a graph from a list of stmts
   * @param stmts list of statements
   * @param g a graph used as accumulator
   * @return a new version of the graph
   */
  def createControlFlowGraph(stmts: List[Statement], end: StmtHolder[Statement], g: CFGtype): CFGtype =
   stmts match {
    case s1 :: s2 :: rest => // case the list has at least two elements. this is the recursive case
      s1 match {
        case IfElseStmt(_, _, _) => // in this case, we do not create an edge from s1 -> s2
          addEdge(Some(s1), s2, g)
          processStmtNode(s1, FullStmt(s2), g)
          createControlFlowGraph(s2 :: rest, end, g)
        case CaseStmt(_, _, _) =>
          processStmtNode(s1, FullStmt(s2), g)
          createControlFlowGraph(s2 :: rest, end, g)
        case IfElseIfStmt(_, _, _, _) =>
          processStmtNode(s1, FullStmt(s2), g)
          createControlFlowGraph(s2 :: rest, end, g)
        case _ =>
          // g += SimpleNode(s1) ~> SimpleNode(s2)
          addEdge(Some(s1), s2, g)
          processStmtNode(s1, FullStmt(s2), g)
          createControlFlowGraph(s2 :: rest, end, g)
      }
    case s1 :: List() => // case the list has just one element. this is the base case
      end match {
        case NoStmt               => g += SimpleNode(s1) ~> EndNode()
        case FullStmt(stmt)       => addEdge(Some(s1), stmt, g)
        case IgnoreInitStmt(stmt) => g += SimpleNode(s1) ~> SimpleNode(stmt)
      }

      processStmtNode(s1, NoStmt, g) // process the singleton node of the stmts list of statements
      g
  }


  /**
   * Handle the specific logic for building a graph from the "from" stmt
   * @param from the from statement
   * @param target the optional target statement
   * @param g the cumulative graph
   * @return a new version of the graph
   */
  def processStmtNode(from: Statement, target: StmtHolder[Statement], g: CFGtype): CFGtype = {
    from match {
      case IfElseStmt(_, thenStmt, optionalElseStmt) =>
        processStmtNode(from, thenStmt, target, g)
        if (optionalElseStmt.isDefined) {
          processStmtNode(from, optionalElseStmt.get, target, g)
        }
        g // returns g
      case WhileStmt(_, whileStmt) =>
        processStmtNode(from, whileStmt, IgnoreInitStmt(from), g) // returns the recursive call
      case RepeatUntilStmt(_, repeatUntilStmt) =>
        processStmtNode(from, repeatUntilStmt, IgnoreInitStmt(from), g) // returns the recursive call
      case ForStmt(_, _, forStmt) =>
        processStmtNode(from, forStmt, IgnoreInitStmt(from), g)
      case IfElseIfStmt(_, thenStmt, elseIfStmt, optionalElseStmt) =>
        processStmtNode(from, thenStmt, target, g)
          elseIfStmt.foreach((ifElseIfBlock) => {
            ifElseIfBlock match {
              case ElseIfStmt(_, thenStmt) =>
                processStmtNode(from, thenStmt, target, g)
            }
          })

          if (optionalElseStmt.isDefined){
            processStmtNode(from, optionalElseStmt.get, target, g)
          }
          else addEdge(Some(from), target.get, g)
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
        else addEdge(Some(from), target.get, g)
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
  def processStmtNode(from: Statement, target: Statement, end: StmtHolder[Statement], g: CFGtype): CFGtype = {
    target match {
      case SequenceStmt(stmts) => // if the target is a SequenceStmt, we have to create "sub-graph"
        addEdge(Some(from), stmts.head, g) // create an edge from "from" to the first elements of the list stmts

        createControlFlowGraph(stmts, end, g)
      case _ =>
        addEdge(Some(from), target, g)

        end match {
          case NoStmt => g
          case FullStmt(stmt) => addEdge(Some(target), stmt, g)
          case IgnoreInitStmt(stmt) => g += SimpleNode(target) ~> SimpleNode(stmt)
        }

        g
    }
  }

  def addEdge(from: Option[Statement], target: Statement, g: CFGtype): CFGtype = {
    val fromNode = from.map(SimpleNode).getOrElse(StartNode())

    target match {
      case ForStmt(init, _, _) =>
        g += fromNode ~> SimpleNode(init)
        g += SimpleNode(init) ~> SimpleNode(target)
      case RepeatUntilStmt(_, stmt) =>
        stmt match {
          case SequenceStmt(stmts) => 
            addEdge(from, stmts.head, g)
          case _ =>
            addEdge(from, stmt, g)
        }
      case _ =>
        g += fromNode ~> SimpleNode(target)
    }
  }
}


