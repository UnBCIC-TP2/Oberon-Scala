package br.unb.cic.oberon.analysis

import br.unb.cic.oberon.ast.{AssignmentStmt, ReadIntStmt}
import br.unb.cic.oberon.cfg.{GraphNode, SimpleNode}
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph

class ReachingDefinition {
  def analyseReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): Map[GraphNode, (Set[(String, GraphNode)], Set[(String, GraphNode)])] = {
    // Inicializar In e Out vazio
    // Map[GraphNode => Set[GraphNode]]
    // cfg.nodes.foreach(n => inicializar)
    // cfg.edges.foreach(e => e match {
    //    case n1 ~> n2 =>
    //      n2 "In" = n1 "Out"
    // })
    // 1ª Estratégia -> get StartNode(); obter todos os edges com StartNode(); travessia no grafo (BFS ou DFS)
    // 2ª Estratégia -> utilizar ordering http://www.scala-graph.org/guides/core-traversing.html
    // 1. Copiar todas as saídas do nó anterior para a entrada do nó atual
    // 2. GraphNode -> se for um AssingmentStatment, adicionamos no out uma tupla (var_name, GraphNode)

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

            val currentNodeGen: (String, GraphNode) = currentNode match {
              case SimpleNode(AssignmentStmt(varName, _)) =>
                (varName, currentNode)
              case SimpleNode(ReadIntStmt(varName)) =>
                (varName, currentNode)
              case _ =>
                null
            }

            // OUT(x) = In(x) + gen(x) - kill(x)
            val currentNodeOut: Set[(String, GraphNode)] = if(currentNodeGen != null) {
              currentNodeGen :: previousNodeOut.filter(definition => definition._1 != currentNodeGen._1)
            } else previousNodeOut

            reachingDefinitions = reachingDefinitions + (currentNode -> (previousNodeOut, currentNodeOut))
        }
      )
      fixedPointReached = previousReachingDefinitions == reachingDefinitions
      previousReachingDefinitions = reachingDefinitions
    }

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
