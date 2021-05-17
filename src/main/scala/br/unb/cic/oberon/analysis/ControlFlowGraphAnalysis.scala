package br.unb.cic.oberon.analysis

import br.unb.cic.oberon.cfg.GraphNode
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph

import scala.collection.mutable

abstract class ControlFlowGraphAnalysis[AnalysisMapping, NodeAnalysis] {
  def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge]): AnalysisMapping

  def getNodeIn(analysis: AnalysisMapping, Node: GraphNode): NodeAnalysis

  def getNodeOut(analysis: AnalysisMapping, Node: GraphNode): NodeAnalysis


  def getNodePredecessors(cfg: Graph[GraphNode, GraphEdge.DiEdge],
                          node: GraphNode): mutable.Set[GraphNode] = {
    cfg.edges
      .filter(e => {
        val GraphEdge.DiEdge(_, nodeT) = e.edge
        nodeT.value == node
      })
      .map(e => {
        val GraphEdge.DiEdge(prevNodeT, _) = e.edge
        prevNodeT.value
      })
  }

  def computeNodeIn(cfg: Graph[GraphNode, GraphEdge.DiEdge],
                    reachDefs: AnalysisMapping,
                    currentNode: GraphNode): NodeAnalysis

//  TODO uncomment and make available on interface
//  def computeNodeKill[A, B](a: A, b: B): NodeAnalysis
}
