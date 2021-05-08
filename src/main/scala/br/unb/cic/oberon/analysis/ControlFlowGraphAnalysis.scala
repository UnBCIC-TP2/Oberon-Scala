package br.unb.cic.oberon.analysis

import br.unb.cic.oberon.cfg.GraphNode
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph

abstract class ControlFlowGraphAnalysis[AnalysisMapping, NodeAnalysis] {
  def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge]): AnalysisMapping

  def getNodeIn(analysis: AnalysisMapping, Node: GraphNode): NodeAnalysis

  def getNodeOut(analysis: AnalysisMapping, Node: GraphNode): NodeAnalysis

  def computeNodeIn(analysis: AnalysisMapping, currentNode: GraphNode, previousNode: GraphNode): NodeAnalysis

  def computeNodeKill(nodeIn: NodeAnalysis, nodeGen: NodeAnalysis): NodeAnalysis
}
