package br.unb.cic.oberon.analysis

import br.unb.cic.oberon.ast.{AssignmentStmt, ReadIntStmt}
import br.unb.cic.oberon.cfg.{GraphNode, SimpleNode}
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph

class ReachingDefinition {
  def analyseReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): Map[GraphNode, (List[(String, GraphNode)], List[(String, GraphNode)])] = {
    // Inicializar In e Out vazio
    // Map[GraphNode => List[GraphNode]]
    // cfg.nodes.foreach(n => inicializar)
    // cfg.edges.foreach(e => e match {
    //    case n1 ~> n2 =>
    //      n2 "In" = n1 "Out"
    // })
    // 1ª Estratégia -> get StartNode(); obter todos os edges com StartNode(); travessia no grafo (BFS ou DFS)
    // 2ª Estratégia -> utilizar ordering http://www.scala-graph.org/guides/core-traversing.html
    // 1. Copiar todas as saídas do nó anterior para a entrada do nó atual
    // 2. GraphNode -> se for um AssingmentStatment, adicionamos no out uma tupla (var_name, GraphNode)

    var reachingDefinitions: Map[GraphNode, (List[(String, GraphNode)], List[(String, GraphNode)])] = initializeReachingDefinitions(cfg)
    var fixedPointReached = false
    var previousReachingDefinitions: Map[GraphNode, (List[(String, GraphNode)], List[(String, GraphNode)])] = reachingDefinitions
    while(!fixedPointReached) {
      cfg.edges.foreach(
        edge => edge.edge match {
          case GraphEdge.DiEdge(previousNodeT, currentNodeT) =>
            println("\n_______________________________\n")
            val previousNode = previousNodeT.toOuter
            val currentNode = currentNodeT.toOuter
            var previousNodeOut: List[(String, GraphNode)] = reachingDefinitions(previousNode)._2
            println(s"previousNode: $previousNode\npreviousNodeOut: $previousNodeOut\ncurrentNode: $currentNode\n")
            var currentNodeGen: (String, GraphNode) = currentNode match {
              case SimpleNode(AssignmentStmt(varName, _)) =>
                (varName, currentNode)
              case SimpleNode(ReadIntStmt(varName)) =>
                (varName, currentNode)
              case _ =>
                null
            }

            // OUT(x) = In(x) + gen(x) - kill(x)
//            var currentNodeOut: List[(String, GraphNode)] = (currentNodeGen :: previousNodeOut)
            var currentNodeOut: List[(String, GraphNode)] = if (currentNodeGen != null) (currentNodeGen :: previousNodeOut.filter(definition => definition._1 != currentNodeGen._1)) else previousNodeOut
            println(s"currentNodeOut: $currentNodeOut")
            reachingDefinitions = reachingDefinitions + (currentNode -> (previousNodeOut, currentNodeOut))
            println(s"reachingDefinitions: $reachingDefinitions")
        }
      )
      fixedPointReached = previousReachingDefinitions == reachingDefinitions
      previousReachingDefinitions = reachingDefinitions
    }


    reachingDefinitions
  }

  private def initializeReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): Map[GraphNode, (List[(String, GraphNode)], List[(String, GraphNode)])] = {
    var reachingDefinitions: Map[GraphNode, (List[(String, GraphNode)], List[(String, GraphNode)])] = Map()

    cfg.edges.foreach(
      edge => edge.nodes.foreach(
        node => reachingDefinitions = reachingDefinitions + (node.toOuter -> (List(), List()))
      )
    )

    reachingDefinitions
  }


}
