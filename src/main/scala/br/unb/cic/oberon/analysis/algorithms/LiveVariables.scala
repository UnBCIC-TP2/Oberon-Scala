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

		val initial_hash_map: HashMapStructure = initializeHashMap(graph)
		val backward_graph: GraphStructure = backwardGraph(graph)
		val live_variables_hash_map: HashMapStructure = create(backward_graph, initial_hash_map)

		return live_variables_hash_map
	}

	def initializeHashMap(graph: GraphStructure) = {

		var initial_hash_map: HashMapStructure
		
		graph.nodes.foreach(
			node => initial_hash_map += (node.value -> (SetStructure, SetStructure))
		)
		
		return initial_hash_map
	}

	def backwardGraph(graph: GraphStructure) = {
		
		var backward_graph: GraphStructure
		
		graph.edges.foreach(
			e => {
				val GraphEdge.DiEdge(currentNode, nextNode) = e.edge
				backward_graph += nextNode ~> currentNode
			}
		)
		
		return backward_graph
	}

	def create(graph: GraphStructure, hash_map: HashMapStructure) = {
		var live_variables_hash_map: HashMapStructure

		return live_variables_hash_map
	}
}