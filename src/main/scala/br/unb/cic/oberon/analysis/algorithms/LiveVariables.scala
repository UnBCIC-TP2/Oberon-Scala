package br.unb.cic.oberon.analysis.algorithms

import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis
import br.unb.cic.oberon.ast.{AssignmentStmt, ReadIntStmt}
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode}
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge

case class LiveVariables() extends ControlFlowGraphAnalysis[HashMap[GraphNode, (Set[(String, GraphNode)], Set[(String, GraphNode)])], Set[(String, GraphNode)]] {
  type SetStructure = Set[(String, GraphNode)]
  type HashMapStructure  = HashMap[GraphNode, (SetStructure, SetStructure)]
  type GraphStructure = Graph[GraphNode, GraphEdge.DiEdge]

  def analyse(graph: GraphStructure) = {
    val backwardGraph: GraphStructure = backwardGraph(graph)
    val initialHashMap: HashMapStructure = initializeHashMap(backwardGraph)
    val liveVariableHashMap: HashMapStructure = create(backwardGraph, initialHashMap)
    liveVariableHashMap
  }

  // @tailrec
  private def backwardGraph(graph: GraphStructure) = {
    var backwardGraph: GraphStructure
    graph.edges.foreach(
      e => {
        val GraphEdge.DiEdge(previousNode, currentNode) = e.edge
        backwardGraph += currentNode ~> previousNode
      }
    )
    backwardGraph
  }

  private def initializeHashMap(graph: GraphStructure) = {
    var initialHashMap: HashMapStructure = HashMap()
    graph.edges.foreach(
      edge => edge.nodes.foreach(
        node => initialHashMap = initialHashMap + (node.value -> (SetStructure, SetStructure))
      )
    )
    initialHashMap
  }

  private def create(graph: GraphStructure, hashmap: HashMapStructure) = {
    var liveVariableHashMap: HashMapStructure
  }
}