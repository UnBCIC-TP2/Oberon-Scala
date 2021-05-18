package br.unb.cic.oberon.analysis.algorithms

import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc
import br.unb.cic.oberon.cfg.{GraphNode, StartNode, SimpleNode, EndNode}
import br.unb.cic.oberon.ast.{AssignmentStmt, EAssignmentStmt, ReadIntStmt, WriteStmt, IfElseStmt, IfElseIfStmt, ElseIfStmt, WhileStmt, RepeatUntilStmt, ForStmt, VarExpression, Brackets, EQExpression, NEQExpression, GTExpression, LTExpression, GTEExpression, LTEExpression, AddExpression, SubExpression, MultExpression, DivExpression, OrExpression, AndExpression}
import br.unb.cic.oberon.ast.Expression
import scala.collection.immutable.HashMap
import scala.collection.immutable.Set
import scala.collection.immutable.List
import scala.annotation.tailrec
import scala.collection.mutable

case class LiveVariables() extends ControlFlowGraphAnalysis[HashMap[GraphNode, (Set[String], Set[String])], Set[String]] {
	
	type SetStructure 		= Set[String]
	type HashMapStructure  	= HashMap[GraphNode, (SetStructure, SetStructure)]
	type GraphStructure 	= Graph[GraphNode, GraphEdge.DiEdge]

	def analyse(graph: GraphStructure): HashMapStructure = {
		val initial_hash_map: HashMapStructure 	= initializeHashMap(graph)
		val backward_graph: GraphStructure 		= backwardGraph(graph)
		val live_variables: HashMapStructure 	= createLiveVariables(backward_graph, initial_hash_map)
		live_variables
	}

	def initializeHashMap(graph: GraphStructure): HashMapStructure = {
		var initial_hash_map: HashMapStructure = HashMap()
		graph.nodes.foreach(node => initial_hash_map = initial_hash_map + (node.value -> (Set(), Set())))
		initial_hash_map
	}

	def backwardGraph(graph: Graph[GraphNode, GraphEdge.DiEdge]): Graph[GraphNode, GraphEdge.DiEdge] = {
		var backward_graph = Graph[GraphNode, GraphEdge.DiEdge]()
		graph.edges.foreach(
			e => {
				val GraphEdge.DiEdge(origin_node, target_node) = e.edge
				backward_graph += target_node.value ~> origin_node.value
			}
		)
		backward_graph
	}

	def createLiveVariables(graph: GraphStructure, initial_hash_map: HashMapStructure): HashMapStructure = {
		var live_variables: HashMapStructure = initial_hash_map
		graph.edges.foreach(
			e => {
				val GraphEdge.DiEdge(origin_node, target_node) = e.edge
				println(target_node)
				val node_output = nodeOutput(live_variables, origin_node.value)
				val node_input = nodeInput(live_variables, target_node.value)
				println(node_input)
				println(node_output)
				live_variables = live_variables + (target_node.value -> (live_variables(target_node.value)._1 ++ node_input, live_variables(target_node.value)._2 ++ node_output))
			}
		)
		live_variables
	}

	def nodeOutput(live_variables: HashMapStructure, origin_node: GraphNode): SetStructure = {
		val node_output: SetStructure = live_variables(origin_node)._1
		node_output
	}

	def nodeInput(live_variables: HashMapStructure, target_node: GraphNode): SetStructure = {
		val node_input: SetStructure = use(target_node) ++ (live_variables(target_node)._2 -- define(target_node))
		node_input
	}

	def use(node: GraphNode): SetStructure = node match {
		case SimpleNode(AssignmentStmt(_, expression)) 		=> getExpressionVariables(expression)
		case SimpleNode(EAssignmentStmt(_, expression)) 	=> getExpressionVariables(expression)
		case SimpleNode(WriteStmt(expression)) 				=> getExpressionVariables(expression)
		case SimpleNode(IfElseStmt(expression, _, _)) 		=> getExpressionVariables(expression)
		case SimpleNode(IfElseIfStmt(expression, _, _, _)) 	=> getExpressionVariables(expression)
		case SimpleNode(ElseIfStmt(expression, _)) 			=> getExpressionVariables(expression)
		case SimpleNode(WhileStmt(expression, _)) 			=> getExpressionVariables(expression)
		case SimpleNode(RepeatUntilStmt(expression, _)) 	=> getExpressionVariables(expression)
		case SimpleNode(ForStmt(_, expression, _)) 			=> getExpressionVariables(expression)
		case _ => Set()
	}

	def getExpressionVariables(expression: Expression): SetStructure = expression match {
		case VarExpression(variable) 		=> Set(variable)
		case Brackets(exp) 					=> getExpressionVariables(exp)
		case EQExpression(left, right) 		=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case NEQExpression(left, right) 	=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case GTExpression(left, right) 		=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case LTExpression(left, right) 		=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case GTEExpression(left, right) 	=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case LTEExpression(left, right) 	=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case AddExpression(left, right) 	=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case SubExpression(left, right) 	=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case MultExpression(left, right) 	=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case DivExpression(left, right) 	=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case OrExpression(left, right) 		=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case AndExpression(left, right) 	=> getExpressionVariables(left) ++ getExpressionVariables(right)
		case _ => Set()
	}

	def define(node: GraphNode): SetStructure = node match {
		case SimpleNode(AssignmentStmt(variable, _)) => Set(variable)
		case SimpleNode(ReadIntStmt(variable)) => Set(variable)
		case _ => Set()
	}
	
	def getNodeIn(hash_map: HashMapStructure, node: GraphNode): SetStructure = hash_map(node)._1

	def getNodeOut(hash_map: HashMapStructure, node: GraphNode): SetStructure = hash_map(node)._2

	def computeNodeIn(graph: GraphStructure, hash_map: HashMapStructure, node: GraphNode): SetStructure = Set()
}