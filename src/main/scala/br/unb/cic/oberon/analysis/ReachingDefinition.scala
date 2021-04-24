package br.unb.cic.oberon.analysis

import br.unb.cic.oberon.cfg.{EndNode, GraphNode}
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph
import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis._

class ReachingDefinition {
  def analyseReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): ReachingDefinitionMapping = {
    var reachingDefinitions: ReachingDefinitionMapping = initializeReachingDefinitions(cfg)
    var fixedPointReached: Boolean = false
    var previousReachingDefinitions: ReachingDefinitionMapping = reachingDefinitions

    while(!fixedPointReached) {
      cfg.edges.foreach(
        edge => edge.edge match {
          case GraphEdge.DiEdge(previousNodeT, currentNodeT) =>
            val previousNode: GraphNode = previousNodeT.toOuter
            val currentNode: GraphNode = currentNodeT.toOuter
            val currentNodeIn: NodeDefinitionSet = calculateNodeDefinitionIn(reachingDefinitions, currentNode, previousNode)
            val currentNodeGen: NodeDefinitionSet = calculateNodeGenDefinitions(currentNode)
            val currentNodeKill: NodeDefinitionSet = calculateNodeKillDefinitions(currentNodeIn, currentNodeGen)

            // OUT(x) = In(x) + gen(x) - kill(x)
            val currentNodeOut: NodeDefinitionSet =
              if(currentNode != EndNode())
                currentNodeIn ++ currentNodeGen -- currentNodeKill
              else
                Set()

            reachingDefinitions = reachingDefinitions + (currentNode -> (currentNodeIn, currentNodeOut))
        }
      )
      fixedPointReached = previousReachingDefinitions == reachingDefinitions
      previousReachingDefinitions = reachingDefinitions
    }

    reachingDefinitions
  }

  private def initializeReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): ReachingDefinitionMapping = {
    var reachingDefinitions: ReachingDefinitionMapping = Map()

    cfg.edges.foreach(
      edge => edge.nodes.foreach(
        node => reachingDefinitions = reachingDefinitions + (node.toOuter -> (Set(), Set()))
      )
    )

    reachingDefinitions
  }
}
