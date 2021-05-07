package br.unb.cic.oberon.analysis.algorithms

import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis
import br.unb.cic.oberon.ast.{AssignmentStmt, ReadIntStmt}
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode}
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph

import scala.annotation.tailrec
import scala.collection.immutable.HashMap

case class AvailableExpressions() extends ControlFlowGraphAnalysis {
  type NodeDefinitionSet = NodeAnalysisSet
  type AvailableExpressionsMapping = AnalysisMapping

  def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge]): AvailableExpressionsMapping = {
    val emptyReachDefs: AvailableExpressionsMapping = initializeAvailableExpressions(cfg)

    analyse(cfg, emptyReachDefs, fixedPoint = false)
  }

  @tailrec
  private def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge],
                      prevReachDefs: AvailableExpressionsMapping,
                      fixedPoint: Boolean): AvailableExpressionsMapping = {
    if (fixedPoint) {
      prevReachDefs
    } else {
      var reachDefs: AvailableExpressionsMapping = prevReachDefs

      cfg.edges.foreach(
        e => {
          val GraphEdge.DiEdge(prevNodeT, currNodeT) = e.edge
          reachDefs = reachDefs + computeNodeInOutSets(prevNodeT.value, currNodeT.value, reachDefs)
        }
      )

      analyse(cfg, reachDefs, reachDefs == prevReachDefs)
    }
  }

  private def initializeAvailableExpressions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): AvailableExpressionsMapping = {
    var reachDefs: AvailableExpressionsMapping = HashMap()

    cfg.edges.foreach(
      edge => edge.nodes.foreach(
        node => reachDefs = reachDefs + (node.value -> (Set(), Set()))
      )
    )

    reachDefs
  }

  private def computeNodeInOutSets(prevNode: GraphNode,
                                   currNode: GraphNode,
                                   reachDefs: AvailableExpressionsMapping): (GraphNode, (NodeDefinitionSet, NodeDefinitionSet)) = {
    val currNodeIn: NodeDefinitionSet = computeNodeDefinitionIn(reachDefs, currNode, prevNode)
    val currNodeGen: NodeDefinitionSet = computeNodeGenDefinitions(currNode)
    val currNodeKill: NodeDefinitionSet = computeNodeKillDefinitions(currNodeIn, currNodeGen)

    // OUT(x) = In(x) + Gen(x) - Kill(x)
    val currNodeOut: NodeDefinitionSet =
      if (currNode != EndNode()) currNodeIn ++ currNodeGen -- currNodeKill else Set()

    currNode -> (currNodeIn, currNodeOut)
  }

  private def computeNodeGenDefinitions(currentNode: GraphNode): NodeAnalysisSet = currentNode match {
    case SimpleNode(AssignmentStmt(varName, _)) =>
      Set((varName, currentNode))
    case SimpleNode(ReadIntStmt(varName)) =>
      Set((varName, currentNode))
    case _ =>
      Set()
  }
}
