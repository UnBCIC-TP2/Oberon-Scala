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

	def analyse(graph: GraphStructure): HashMapStructure = {
		val initial_hash_map: HashMapStructure = initializeHashMap(graph)
		val backward_graph: GraphStructure = backwardGraph(graph)
		val live_variables: HashMapStructure = createLiveVariables(backward_graph, initial_hash_map)
		return live_variables
	}

	def initializeHashMap(graph: GraphStructure): HashMapStructure = {
		var initial_hash_map: HashMapStructure
		graph.nodes.foreach(node => initial_hash_map += (node.value -> (SetStructure, SetStructure)))
		return initial_hash_map
	}

	def backwardGraph(graph: GraphStructure): GraphStructure = {
		var backward_graph: GraphStructure
		graph.edges.foreach(
			e => {
				val GraphEdge.DiEdge(origin_node, target_node) = e.edge
				backward_graph += target_node ~> origin_node
			}
		)
		return backward_graph
	}

	def createLiveVariables(graph: GraphStructure, live_variables: HashMapStructure): HashMapStructure = {
		graph.edges.foreach(
			e => {
				val GraphEdge.DiEdge(origin_node, target_node) = e.edge
				live_variables(target_node.value)._2 += live_variables(origin_node.value)._1 // "output do destino" = "input da origem"
				live_variables(target_node.value)._1 += generate(targetNode) ++ (live_variables(target_node.value)._2 -- kill(targetNode)) // "input" = "variaveis usadas" ++ ("output" -- "o que ele mata")
			}
		)
		return live_variables
	}

	def getNodeInput(live_variables: HashMapStructure, node: GraphNode): SetStructure	= live_variables(node.value)._1

	def getNodeOutput(live_variables: HashMapStructure, node: GraphNode): SetStructure = live_variables(node.value)._2

	def generate(node: GraphNode): SetStructure = node match {
		case SimpleNode(AssignmentStmt(_, expression)) => return Set(expression, node)
		case SimpleNode(EAssignmentStmt(_, expression)) => return Set(expression, node)
		case SimpleNode(WriteStmt(expression)) => return Set(expression, node)
	}

	def kill(node: GraphNode): SetStructure = node match {
		case SimpleNode(AssignmentStmt(variable, _)) => return Set(variable, node)
		case SimpleNode(ReadIntStmt(variable)) => return Set(variable, node)
		case _ => return Set()
	}
}