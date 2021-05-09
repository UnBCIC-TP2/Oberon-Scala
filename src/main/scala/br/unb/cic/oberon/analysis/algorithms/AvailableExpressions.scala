package br.unb.cic.oberon.analysis.algorithms

import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode, StartNode}
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph

import scala.annotation.tailrec
import scala.collection.immutable.HashMap

case class AvailableExpressions() extends ControlFlowGraphAnalysis[HashMap[GraphNode, (Set[(Set[Expression], GraphNode)], Set[(Set[Expression], GraphNode)])], Set[(Set[Expression], GraphNode)]] {
  type NodeAnalysis = Set[(Set[Expression], GraphNode)]
  type AnalysisMapping = HashMap[GraphNode, (NodeAnalysis, NodeAnalysis)]

  def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge]): AnalysisMapping = {
    initializeAvailableExpressions(cfg)
  }
//
//  @tailrec
//  private def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge],
//              prevAvailExps: AnalysisMapping,
//              fixedPoint: Boolean): AnalysisMapping = {
//    if (fixedPoint) {
//      prevAvailExps
//    } else {
//      var availExps: AnalysisMapping = prevAvailExps
//
//      cfg.edges.foreach(
//        e => {
//          val GraphEdge.DiEdge(prevNodeT, currNodeT) = e.edge
//          availExps = availExps + computeNodeInOutSets(prevNodeT.value, currNodeT.value, availExps)
//        }
//      )
//
//      analyse(cfg, availExps, availExps == prevAvailExps)
//    }
//  }
//
//  private def computeNodeInOutSets(prevNode: GraphNode,
//                                   currNode: GraphNode,
//                                   reachDefs: AnalysisMapping): (GraphNode, (NodeAnalysis, NodeAnalysis)) = {
//    val currNodeIn: NodeAnalysis = computeNodeIn(reachDefs, currNode, prevNode)
//    val currNodeGen: NodeAnalysis = computeNodeGenDefinitions(currNode)
//    val currNodeKill: NodeAnalysis = computeNodeKill(currNodeIn, currNodeGen)
//
//    // OUT(x) = In(x) + Gen(x) - Kill(x)
//    val currNodeOut: NodeAnalysis =
//      if (currNode != EndNode()) currNodeIn ++ currNodeGen -- currNodeKill else Set()
//
//    currNode -> (currNodeIn, currNodeOut)
//  }

  def getNodeIn(reachingDefinitions: AnalysisMapping, Node: GraphNode): NodeAnalysis =
    reachingDefinitions(Node)._1

  def getNodeOut(reachingDefinitions: AnalysisMapping, Node: GraphNode): NodeAnalysis =
    reachingDefinitions(Node)._2

  def computeNodeIn(reachingDefinitions: AnalysisMapping, currentNode: GraphNode, previousNode: GraphNode): NodeAnalysis =
    getNodeIn(reachingDefinitions, currentNode) & getNodeOut(reachingDefinitions, previousNode)

  def computeNodeKill(nodeIn: NodeAnalysis, nodeGen: NodeAnalysis): NodeAnalysis = {
    if (nodeGen.nonEmpty) nodeIn.filter(definition => definition._1 == nodeGen.head._1) else Set()
  }

  private def getStmtExpressions(stmt: Statement): Set[Expression] = {
    val expressions: Set[Expression] = stmt match {
      case AssignmentStmt(_, exp: Expression) => Set(exp)
      case EAssignmentStmt(_, exp: Expression) => Set(exp)
      case SequenceStmt(stmts: List[Statement] ) => stmts.flatMap(stmt => getStmtExpressions(stmt)).toSet
      case WriteStmt(exp: Expression) => Set(exp)
      case IfElseStmt(condition: Expression, thenStmt: Statement, elseStmt: Option[Statement] ) =>
        Set(condition) |
          getStmtExpressions(thenStmt) |
          getOptionalStmtExpressions(elseStmt)

      case IfElseIfStmt(condition: Expression, thenStmt: Statement, elseifStmt: List[ElseIfStmt], elseStmt: Option[Statement] ) =>
        Set(condition) |
          getStmtExpressions(thenStmt) |
          elseifStmt.flatMap(stmt => getStmtExpressions(stmt)).toSet |
          getOptionalStmtExpressions(elseStmt)

      case ElseIfStmt(condition: Expression, thenStmt: Statement) => Set(condition) | getStmtExpressions(thenStmt)
      case WhileStmt(condition: Expression, stmt: Statement) => Set(condition) | getStmtExpressions(stmt)
      case RepeatUntilStmt(condition: Expression, stmt: Statement) => Set(condition) | getStmtExpressions(stmt)
      case ForStmt(init: Statement, condition: Expression, stmt: Statement) =>
        getStmtExpressions(init) |
          Set(condition) |
          getStmtExpressions(stmt)

      case LoopStmt(stmt: Statement) => getStmtExpressions(stmt)
      case ReturnStmt(exp: Expression) => Set(exp)
      case CaseStmt(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement] ) =>
        Set(exp) | cases.flatMap(altCase => getCaseAlternativeExpressions(altCase)).toSet | getOptionalStmtExpressions(elseStmt)

      case _ => Set()
    }

    expressions.filter(exp => exp match {
      case VarExpression(_) => false
      case _ => true
    })
  }

  private def getOptionalStmtExpressions(optionalStmt: Option[Statement]): Set[Expression] = {
    if(optionalStmt.isDefined) getStmtExpressions(optionalStmt.get) else Set()
  }

  private def getCaseAlternativeExpressions(caseAlternative: CaseAlternative): Set[Expression] = {
    caseAlternative match {
      case SimpleCase (condition: Expression, stmt: Statement) => Set(condition) | getStmtExpressions(stmt)
      case RangeCase (min: Expression, max: Expression, stmt: Statement) => Set(min, max) | getStmtExpressions(stmt)
    }
  }

  private def initializeAvailableExpressions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): AnalysisMapping = {
    var availableExps: AnalysisMapping = HashMap()
    val u: NodeAnalysis = buildAllExpressionsSet(cfg)

    cfg.edges.foreach(
      edge => edge.nodes.foreach(
        node => {
          node.value match {
            case SimpleNode(_) | EndNode() => availableExps = availableExps + (node.value -> (Set(), u))
            case StartNode() => availableExps = availableExps + (node.value -> (Set(), Set()))
          }
        }
      )
    )

    availableExps
  }

  private def buildAllExpressionsSet(cfg: Graph[GraphNode, GraphEdge.DiEdge]): NodeAnalysis = {
    var u: NodeAnalysis = Set()

    cfg.edges.foreach(
      edge => edge.nodes.foreach(
        node => {
          Some(node.value) collect {
            case SimpleNode(stmt: Statement) =>
              val nodeGenExpressions = getStmtExpressions(stmt)
              if(nodeGenExpressions.nonEmpty) u = u + Tuple2(nodeGenExpressions, node)
          }
        }
      )
    )

    u
  }

//  private def computeNodeInOutSets(prevNode: GraphNode,
//                                   currNode: GraphNode,
//                                   reachDefs: AnalysisMapping): (GraphNode, (NodeAnalysis, NodeAnalysis)) = {
//    val currNodeIn: NodeAnalysis = computeNodeIn(reachDefs, currNode, prevNode)
//    val currNodeGen: NodeAnalysis = computeNodeGenDefinitions(currNode)
//    val currNodeKill: NodeAnalysis = computeNodeKill(currNodeIn, currNodeGen)
//
//    // OUT(x) = In(x) + Gen(x) - Kill(x)
//    val currNodeOut: NodeAnalysis =
//      if (currNode != EndNode()) currNodeIn ++ currNodeGen -- currNodeKill else Set()
//
//    currNode -> (currNodeIn, currNodeOut)
//  }
//
//  private def computeNodeGenDefinitions(currentNode: GraphNode): NodeAnalysis = currentNode match {
//    case SimpleNode(AssignmentStmt(varName, _)) =>
//      Set((varName, currentNode))
//    case SimpleNode(ReadIntStmt(varName)) =>
//      Set((varName, currentNode))
//    case _ =>
//      Set()
//  }
}
