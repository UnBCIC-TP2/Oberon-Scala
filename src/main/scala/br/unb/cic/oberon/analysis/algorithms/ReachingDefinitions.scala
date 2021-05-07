package br.unb.cic.oberon.analysis.algorithms

import br.unb.cic.oberon.cfg.{EndNode, GraphNode}
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph
import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis

import scala.annotation.tailrec
import scala.collection.immutable.HashMap

case class ReachingDefinitions() extends ControlFlowGraphAnalysis {
  type NodeDefinitionSet = NodeAnalysisSet
  type ReachingDefinitionsMapping = AnalysisMapping

  def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge]): ReachingDefinitionsMapping = {
    val emptyReachDefs: ReachingDefinitionsMapping = initializeReachingDefinitions(cfg)

    analyse(cfg, emptyReachDefs, fixedPoint = false)
  }

  @tailrec
  private def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge],
                      prevReachDefs: ReachingDefinitionsMapping,
                      fixedPoint: Boolean): ReachingDefinitionsMapping = {
    if (fixedPoint) {
      prevReachDefs
    } else {
      var reachDefs: ReachingDefinitionsMapping = prevReachDefs

      cfg.edges.foreach(
        e => {
          val GraphEdge.DiEdge(prevNodeT, currNodeT) = e.edge
          reachDefs = reachDefs + computeNodeInOutSets(prevNodeT.value, currNodeT.value, reachDefs)
        }
      )

      analyse(cfg, reachDefs, reachDefs == prevReachDefs)
    }
  }

  private def initializeReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): ReachingDefinitionsMapping = {
    var reachDefs: ReachingDefinitionsMapping = HashMap()

    cfg.edges.foreach(
      edge => edge.nodes.foreach(
        node => reachDefs = reachDefs + (node.value -> (Set(), Set()))
      )
    )

    reachDefs
  }

  private def computeNodeInOutSets(prevNode: GraphNode,
                                   currNode: GraphNode,
                                   reachDefs: ReachingDefinitionsMapping): (GraphNode, (NodeDefinitionSet, NodeDefinitionSet)) = {
    val currNodeIn: NodeDefinitionSet = computeNodeDefinitionIn(reachDefs, currNode, prevNode)
    val currNodeGen: NodeDefinitionSet = computeNodeGenDefinitions(currNode)
    val currNodeKill: NodeDefinitionSet = computeNodeKillDefinitions(currNodeIn, currNodeGen)

    // OUT(x) = In(x) + Gen(x) - Kill(x)
    val currNodeOut: NodeDefinitionSet =
      if (currNode != EndNode()) currNodeIn ++ currNodeGen -- currNodeKill else Set()

    currNode -> (currNodeIn, currNodeOut)
  }
}
