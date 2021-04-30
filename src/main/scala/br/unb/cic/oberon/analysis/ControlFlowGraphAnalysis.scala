package br.unb.cic.oberon.analysis

import br.unb.cic.oberon.ast.{AssignmentStmt, ReadIntStmt}
import br.unb.cic.oberon.cfg.{GraphNode, SimpleNode}

trait ControlFlowGraphAnalysis {
  type NodeDefinitionSet = Set[(String, GraphNode)]
  type ReachingDefinitionMapping = Map[GraphNode, (Set[(String, GraphNode)], Set[(String, GraphNode)])]

  def getNodeDefinitionIn(reachingDefinitions: ReachingDefinitionMapping, Node: GraphNode): NodeDefinitionSet =
    reachingDefinitions(Node)._1

  def getNodeDefinitionOut(reachingDefinitions: ReachingDefinitionMapping, Node: GraphNode): NodeDefinitionSet =
    reachingDefinitions(Node)._2

  def calculateNodeDefinitionIn(reachingDefinitions: ReachingDefinitionMapping, currentNode: GraphNode, previousNode: GraphNode): NodeDefinitionSet =
    getNodeDefinitionIn(reachingDefinitions, currentNode) | getNodeDefinitionOut(reachingDefinitions, previousNode)

  def calculateNodeGenDefinitions(currentNode: GraphNode): NodeDefinitionSet = currentNode match {
    case SimpleNode(AssignmentStmt(varName, _)) =>
      Set((varName, currentNode))
    case SimpleNode(ReadIntStmt(varName)) =>
      Set((varName, currentNode))
    case _ =>
      Set()
  }

  def calculateNodeKillDefinitions(nodeIn: NodeDefinitionSet, nodeGen: NodeDefinitionSet): NodeDefinitionSet = {
    if (nodeGen.nonEmpty) nodeIn.filter(definition => definition._1 == nodeGen.head._1) else Set()
  }
}
