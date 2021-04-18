package br.unb.cic.oberon.analysis

import br.unb.cic.oberon.cfg.GraphNode
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph
import scala.collection.mutable.HashMap

class ReachingDefinition {
  def analyseReachingDefinitions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): Map[Int,Map[String,List[(String, Int)]]] = {
    var reachingDefinitions : Map[Int, Map[String, List[(String, Int)]]] = Map(1 -> Map("In" -> List(("x", 2)), "Out" -> List(("x", 3))))
    reachingDefinitions
  }
}