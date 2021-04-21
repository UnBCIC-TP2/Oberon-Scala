package br.unb.cic.oberon.analysis

import br.unb.cic.oberon.ast.{AssignmentStmt, EAssignmentStmt, ReadIntStmt, Statement}
import scalax.collection.edge.Implicits._
import scalax.collection.edge.LBase._
import br.unb.cic.oberon.cfg.{GraphNode, SimpleNode}
import scalax.collection.GraphEdge
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.mutable.Graph

class ReachingDefinition {
  def analyseReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): Map[GraphNode, (List[(String, Int)], List[(String, Int)])] = {
    // Inicializar In e Out vazio
    // Map[GraphNode => List[GraphNode]]
    // cfg.nodes.foreach(n => inicializar)
    // cfg.edges.foreach(e => e match {
    //    case n1 ~> n2 =>
    //      n2 "In" = n1 "Out"
    // })
    // 1ª Estratégia -> get StartNode(); obter todos os edges com StartNode(); travessia no grafo (BFS ou DFS)
    // 2ª Estratégia -> utilizar ordering http://www.scala-graph.org/guides/core-traversing.html
    var reachingDefinitions: Map[GraphNode, (List[(String, Int)], List[(String, Int)])] = initializeReachingDefinitions(cfg)
    // 1. Copiar todas as saídas do nó anterior para a entrada do nó atual
    // 2. GraphNode -> se for um AssingmentStatment, adicionamos no out uma tupla (var_name, GraphNode)

    cfg.edges.foreach(
      edge => edge.edge match {
        case GraphEdge.DiEdge(n1, n2) =>
//          reachingDefinitions = reachingDefinitions + (n2 -> (reachingDefinitions(n1)._1, reachingDefinitions(n2)._2))
          n2.toOuter match {
            case SimpleNode(statement: AssignmentStmt) =>
              println(statement)
            case SimpleNode(statement: ReadIntStmt) =>
              println(statement)
            case SimpleNode(statement: EAssignmentStmt) =>
              println(statement)
            case _ =>
              println("Nao tem statement")
          }
      }
    )


    reachingDefinitions
  }

  private def initializeReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): Map[GraphNode, (List[(String, Int)], List[(String, Int)])] =
    cfg.edges.foreach(
      edge => edge.nodes.foreach(
        node => reachingDefinitions = reachingDefinitions + (node.toOuter -> (List(), List()))
      )
    )


}
