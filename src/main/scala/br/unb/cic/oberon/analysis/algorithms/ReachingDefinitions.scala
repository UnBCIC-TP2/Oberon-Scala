package br.unb.cic.oberon.analysis.algorithms

import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode}
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph
import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis
import br.unb.cic.oberon.ast.{AssignmentStmt, ReadIntStmt}

import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import scala.collection.immutable.Set
import scala.collection.mutable

case class ReachingDefinitions() extends ControlFlowGraphAnalysis[HashMap[GraphNode, (Set[(String, GraphNode)], Set[(String, GraphNode)])], Set[(String, GraphNode)]] {
  type NodeAnalysis = Set[(String, GraphNode)]
  type AnalysisMapping = HashMap[GraphNode, (NodeAnalysis, NodeAnalysis)]

  def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge]): AnalysisMapping = {
    val emptyReachDefs: AnalysisMapping = initializeReachingDefinitions(cfg)

    analyse(cfg, emptyReachDefs, fixedPoint = false)
  }

  def getNodeIn(reachDefs: AnalysisMapping, Node: GraphNode): NodeAnalysis =
    reachDefs(Node)._1

  def getNodeOut(reachDefs: AnalysisMapping, Node: GraphNode): NodeAnalysis =
    reachDefs(Node)._2

  def computeNodeIn(cfg: Graph[GraphNode, GraphEdge.DiEdge],
                    reachDefs: AnalysisMapping,
                    currentNode: GraphNode): NodeAnalysis = {
    getNodePredecessors(cfg, currentNode)
      .map(predecessor => getNodeOut(reachDefs, predecessor))
      .fold(Set())((acc, predecessorOut) => acc | predecessorOut)
  }

  private def getNodePredecessors(cfg: Graph[GraphNode, GraphEdge.DiEdge], node: GraphNode): mutable.Set[GraphNode] = {
    cfg.edges
      .filter(e => {
        val GraphEdge.DiEdge(_, nodeT) = e.edge
        nodeT.value == node
      }
      ).map(e => {
      val GraphEdge.DiEdge(prevNodeT, _) = e.edge
      prevNodeT.value
    })
  }

  def computeNodeKill(nodeIn: NodeAnalysis, nodeGen: NodeAnalysis): NodeAnalysis = {
    if (nodeGen.nonEmpty) nodeIn.filter(definition => definition._1 == nodeGen.head._1) else Set()
  }

  @tailrec
  private def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge],
                      prevReachDefs: AnalysisMapping,
                      fixedPoint: Boolean): AnalysisMapping = {
    if (fixedPoint) {
      prevReachDefs
    } else {
      var reachDefs: AnalysisMapping = prevReachDefs

      cfg.edges.foreach(
        e => {
          val GraphEdge.DiEdge(_, currNodeT) = e.edge
          reachDefs = reachDefs + computeNodeInOutSets(cfg, currNodeT.value, reachDefs)
        }
      )

      analyse(cfg, reachDefs, reachDefs == prevReachDefs)
    }
  }

  private def initializeReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): AnalysisMapping = {
    var reachDefs: AnalysisMapping = HashMap()

    cfg.edges.foreach(
      edge => edge.nodes.foreach(
        node => reachDefs = reachDefs + (node.value -> (Set(), Set()))
      )
    )

    reachDefs
  }

  private def computeNodeInOutSets(cfg: Graph[GraphNode, GraphEdge.DiEdge],
                                   currNode: GraphNode,
                                   reachDefs: AnalysisMapping): (GraphNode, (NodeAnalysis, NodeAnalysis)) = {
    val currNodeIn: NodeAnalysis = computeNodeIn(cfg, reachDefs, currNode)
    val currNodeGen: NodeAnalysis = computeNodeGenDefinitions(currNode)
    val currNodeKill: NodeAnalysis = computeNodeKill(currNodeIn, currNodeGen)

    // OUT(x) = In(x) + Gen(x) - Kill(x)
    val currNodeOut: NodeAnalysis =
      if (currNode != EndNode()) currNodeIn ++ currNodeGen -- currNodeKill else Set()

    currNode -> (currNodeIn, currNodeOut)
  }

  private def computeNodeGenDefinitions(currentNode: GraphNode): NodeAnalysis = currentNode match {
    case SimpleNode(AssignmentStmt(varName, _)) =>
      Set((varName, currentNode))
    case SimpleNode(ReadIntStmt(varName)) =>
      Set((varName, currentNode))
    case _ =>
      Set()
  }
}
