package br.unb.cic.oberon.analysis.algorithms

//import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc
import br.unb.cic.oberon.cfg.{GraphNode, StartNode, SimpleNode, EndNode}
import br.unb.cic.oberon.ast.{AssignmentStmt, EAssignmentStmt, ReadIntStmt, WriteStmt, IfElseStmt, IfElseIfStmt, ElseIfStmt, WhileStmt, RepeatUntilStmt, ForStmt, VarExpression, Brackets, EQExpression, NEQExpression, GTExpression, LTExpression, GTEExpression, LTEExpression, AddExpression, SubExpression, MultExpression, DivExpression, OrExpression, AndExpression}
import br.unb.cic.oberon.ast.Expression
//import br.unb.cic.oberon.ast.Boolean
import scala.collection.immutable.HashMap
import scala.collection.immutable.Set
//import scala.annotation.tailrec
//import scala.collection.mutable

class LiveVariables() {
	
	type SetStructure = Set[String]
	type HashMapStructure = HashMap[GraphNode, (SetStructure, SetStructure)]
	type GraphStructure = Graph[GraphNode, GraphEdge.DiEdge]

	def analyse(graph: GraphStructure): HashMapStructure = {
		val initial_hash_map = initializeHashMap(graph)
		val backward_graph = backwardGraph(graph)
		val live_variables = createLiveVariables(backward_graph, initial_hash_map)
		live_variables
	}

	def initializeHashMap(graph: GraphStructure): HashMapStructure = {
		var initial_hash_map: HashMapStructure = HashMap()
		graph.nodes.foreach(node => initial_hash_map = initial_hash_map + (node.value -> (Set(), Set())))
		initial_hash_map
	}

	def backwardGraph(graph: GraphStructure): GraphStructure = {
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
		var live_variables = initial_hash_map
		var fixed_point = false
		while (!fixed_point) {
			fixed_point = true
			graph.edges.foreach(
				e => {
					val GraphEdge.DiEdge(origin_node, target_node) = e.edge
					val node_output = nodeOutput(live_variables, origin_node.value)
					val node_input = nodeInput(live_variables, target_node.value, node_output)
					val is_different = isDifferent(live_variables, target_node, node_input, node_output)
					if (is_different) {
						live_variables = live_variables + (target_node.value -> (live_variables(target_node.value)._1 ++ node_input, live_variables(target_node.value)._2 ++ node_output))
						fixed_point = false
					}
					
				}
			)
		}
		live_variables
	}

	def nodeOutput(live_variables: HashMapStructure, origin_node: GraphNode): SetStructure = {
		val node_output: SetStructure = live_variables(origin_node)._1
		node_output
	}

	def nodeInput(_live_variables: HashMapStructure, target_node: GraphNode, node_output: SetStructure): SetStructure = {
		val node_input: SetStructure = use(target_node) ++ (node_output -- define(target_node))
		node_input
	}

	def isDifferent(live_variables: HashMapStructure, node: GraphNode, input: SetStructure, output: SetStructure): Boolean = {
		var is_different = false
		if ((live_variables(node)._1 != live_variables(node)._1 ++ input) || (live_variables(node)._2 != live_variables(node)._2 ++ output)) {
			is_different = true
		}
		is_different
	}

	def use(node: GraphNode): SetStructure = node match {
		case SimpleNode(AssignmentStmt(_, expression)) => getExpressionVariables(expression)
		case SimpleNode(EAssignmentStmt(_, expression)) => getExpressionVariables(expression)
		case SimpleNode(WriteStmt(expression)) => getExpressionVariables(expression)
		case SimpleNode(IfElseStmt(expression, _, _)) => getExpressionVariables(expression)
		case SimpleNode(IfElseIfStmt(expression, _, _, _)) => getExpressionVariables(expression)
		case SimpleNode(ElseIfStmt(expression, _)) => getExpressionVariables(expression)
		case SimpleNode(WhileStmt(expression, _)) => getExpressionVariables(expression)
		case SimpleNode(RepeatUntilStmt(expression, _)) => getExpressionVariables(expression)
		case SimpleNode(ForStmt(_, expression, _)) => getExpressionVariables(expression)
		case _ 	=> Set()
	}

	private def getExpressionVariables(expression: Expression): SetStructure = expression match {
		case VarExpression(variable) => Set(variable)
		case Brackets(exp) => getExpressionVariables(exp)
		case EQExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case NEQExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case GTExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case LTExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case GTEExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case LTEExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case AddExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case SubExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case MultExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case DivExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case OrExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case AndExpression(left, right) => getExpressionVariables(left) ++ getExpressionVariables(right)
		case _ => Set()
	}

	private def define(node: GraphNode): SetStructure = node match {
		case SimpleNode(AssignmentStmt(variable, _)) => Set(variable)
		case SimpleNode(ReadIntStmt(variable)) => Set(variable)
		case _ => Set()
	}
	
	def getNodeIn(hash_map: HashMapStructure, node: GraphNode): SetStructure = hash_map(node)._1

	def getNodeOut(hash_map: HashMapStructure, node: GraphNode): SetStructure = hash_map(node)._2

	def computeNodeIn(_graph: GraphStructure, _hash_map: HashMapStructure, _node: GraphNode): SetStructure = Set()
}