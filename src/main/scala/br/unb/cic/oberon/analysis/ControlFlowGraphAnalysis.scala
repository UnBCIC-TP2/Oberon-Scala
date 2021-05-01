package br.unb.cic.oberon.analysis

import br.unb.cic.oberon.ast.{AssignmentStmt, ReadIntStmt}
import br.unb.cic.oberon.cfg.{GraphNode, SimpleNode}
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph

import scala.collection.immutable.HashMap

trait ControlFlowGraphAnalysis {
  type NodeAnalysisSet = Set[(Any, Any)]
  type AnalysisMapping = HashMap[GraphNode, (NodeAnalysisSet, NodeAnalysisSet)]

  def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge]): AnalysisMapping

  def getNodeDefinitionIn(reachingDefinitions: AnalysisMapping, Node: GraphNode): NodeAnalysisSet =
    reachingDefinitions(Node)._1

  def getNodeDefinitionOut(reachingDefinitions: AnalysisMapping, Node: GraphNode): NodeAnalysisSet =
    reachingDefinitions(Node)._2

  def computeNodeDefinitionIn(reachingDefinitions: AnalysisMapping, currentNode: GraphNode, previousNode: GraphNode): NodeAnalysisSet =
    getNodeDefinitionIn(reachingDefinitions, currentNode) | getNodeDefinitionOut(reachingDefinitions, previousNode)

  def computeNodeGenDefinitions(currentNode: GraphNode): NodeAnalysisSet = currentNode match {
    case SimpleNode(AssignmentStmt(varName, _)) =>
      Set((varName, currentNode))
    case SimpleNode(ReadIntStmt(varName)) =>
      Set((varName, currentNode))
    case _ =>
      Set()
  }

  def computeNodeKillDefinitions(nodeIn: NodeAnalysisSet, nodeGen: NodeAnalysisSet): NodeAnalysisSet = {
    if (nodeGen.nonEmpty) nodeIn.filter(definition => definition._1 == nodeGen.head._1) else Set()
  }
}
