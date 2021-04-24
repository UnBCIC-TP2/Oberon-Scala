package br.unb.cic.oberon.analysis

import br.unb.cic.oberon.ast.{AssignmentStmt, ReadIntStmt}
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode}
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph

class ReachingDefinition {
  def analyseReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): Map[GraphNode, (Set[(String, GraphNode)], Set[(String, GraphNode)])] = {
    var reachingDefinitions: Map[GraphNode, (Set[(String, GraphNode)], Set[(String, GraphNode)])] = initializeReachingDefinitions(cfg)
    var fixedPointReached: Boolean = false
    var previousReachingDefinitions: Map[GraphNode, (Set[(String, GraphNode)], Set[(String, GraphNode)])] = reachingDefinitions

    while(!fixedPointReached) {
      cfg.edges.foreach(
        edge => edge.edge match {
          case GraphEdge.DiEdge(previousNodeT, currentNodeT) =>
            val previousNode: GraphNode = previousNodeT.toOuter
            val currentNode: GraphNode = currentNodeT.toOuter
            val previousNodeOut: Set[(String, GraphNode)] = reachingDefinitions(previousNode)._2
            val currentNodeIn: Set[(String, GraphNode)] = reachingDefinitions(currentNode)._1 | previousNodeOut

            val currentNodeGen: (String, GraphNode) = currentNode match {
              case SimpleNode(AssignmentStmt(varName, _)) =>
                (varName, currentNode)
              case SimpleNode(ReadIntStmt(varName)) =>
                (varName, currentNode)
              case _ =>
                null
            }

            val currentNodeKill: Set[(String, GraphNode)] = if(currentNodeGen != null) currentNodeIn.filter(definition => definition._1 == currentNodeGen._1) else Set()
            // OUT(x) = In(x) + gen(x) - kill(x)
            val currentNodeOut: Set[(String, GraphNode)] = if(currentNodeGen != null) currentNodeIn ++ Set(currentNodeGen) -- currentNodeKill else currentNodeIn

            reachingDefinitions = reachingDefinitions + (currentNode -> (currentNodeIn, currentNodeOut))
        }
      )
      fixedPointReached = previousReachingDefinitions == reachingDefinitions
      previousReachingDefinitions = reachingDefinitions
    }

    reachingDefinitions = reachingDefinitions + (EndNode() -> (reachingDefinitions(EndNode())._1, Set()))

    reachingDefinitions
  }

  private def initializeReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): Map[GraphNode, (Set[(String, GraphNode)], Set[(String, GraphNode)])] = {
    var reachingDefinitions: Map[GraphNode, (Set[(String, GraphNode)], Set[(String, GraphNode)])] = Map()

    cfg.edges.foreach(
      edge => edge.nodes.foreach(
        node => reachingDefinitions = reachingDefinitions + (node.toOuter -> (Set(), Set()))
      )
    )

    reachingDefinitions
  }
}
