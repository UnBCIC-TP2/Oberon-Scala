package br.unb.cic.oberon.analysis_test

import br.unb.cic.oberon.analysis.algorithms.LiveVariables
import br.unb.cic.oberon.ast.{AssignmentStmt, GTExpression, IfElseStmt, ReadIntStmt, VarExpression, WriteStmt}
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode, StartNode}
import org.scalatest.funsuite.AnyFunSuite
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc
import scalax.collection.mutable.Graph

class LiveVariablesTest extends AnyFunSuit {

    type SetStructure = Set[(String, GraphNode)]
    type HashMapStructure  = HashMap[GraphNode, (SetStructure, SetStructure)]
    type GraphStructure = Graph[GraphNode, GraphEdge.DiEdge]

	//	BEGIN
	// 		readInt(x);
	// 		readInt(max);
	// 		IF(x > max) THEN
	// 			max := x
	// 		END;
	// 		write(max)
	// 	END

	val s1 		= ReadIntStmt("x")
	val s2      = ReadIntStmt("max")
	val s3      = IfElseStmt(GTExpression(VarExpression("x"), VarExpression("max")), s3_1 , None)
	val s3_1    = AssignmentStmt("max", VarExpression("x"))
	val s4      = WriteStmt(VarExpression("max"))

	val graph = GraphStructure
	graph += StartNode()      ~> SimpleNode(s1)
	graph += SimpleNode(s1)   ~> SimpleNode(s2)
	graph += SimpleNode(s2)   ~> SimpleNode(s3)
	graph += SimpleNode(s3)   ~> SimpleNode(s3_1)
	graph += SimpleNode(s3_1) ~> SimpleNode(s4)
	graph += SimpleNode(s3)   ~> SimpleNode(s4)
	graph += SimpleNode(s4)   ~> EndNode()

	val live_variables = new LiveVariables()
	val backward_graph_received = live_variables.backwardGraph(graph)
	val hash_map_received = live_variables.initializeHashMap(graph)

    test("CREATE BACKWARD GRAPH") {

		val backward_graph_expected = GraphStructure
		backward_graph_expected += EndNode()		~> SimpleNode(s4)
		backward_graph_expected += SimpleNode(s4) 	~> SimpleNode(s3_1)
		backward_graph_expected += SimpleNode(s4) 	~> SimpleNode(s3)
		backward_graph_expected += SimpleNode(s3_1)	~> SimpleNode(s3)
		backward_graph_expected += SimpleNode(s3) 	~> SimpleNode(s2)
		backward_graph_expected += SimpleNode(s4) 	~> SimpleNode(s1)
		backward_graph_expected += SimpleNode(s1) 	~> StartNode()

		assert(backward_graph_expected == backward_graph_received)
	}

	test("INITIALIZE HASH MAP") {

		val hash_map_expected = HashMapStructure
		hash_map_expected += StartNode() 		~> (SetStructure, SetStructure)
		hash_map_expected += SimpleNode(s1) 	~> (SetStructure, SetStructure)
		hash_map_expected += SimpleNode(s2) 	~> (SetStructure, SetStructure)
		hash_map_expected += SimpleNode(s3) 	~> (SetStructure, SetStructure)
		hash_map_expected += SimpleNode(s3_1) 	~> (SetStructure, SetStructure)
		hash_map_expected += SimpleNode(s4) 	~> (SetStructure, SetStructure)
		hash_map_expected += EndNode() 			~> (SetStructure, SetStructure)

		assert(hash_map_expected == hash_map_received)
	}
}