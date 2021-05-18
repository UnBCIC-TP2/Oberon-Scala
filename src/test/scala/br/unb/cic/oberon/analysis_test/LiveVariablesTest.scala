package br.unb.cic.oberon.analysis_test

import br.unb.cic.oberon.analysis.algorithms.LiveVariables
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc
import br.unb.cic.oberon.cfg.{GraphNode, StartNode, SimpleNode, EndNode}
import br.unb.cic.oberon.ast.{AssignmentStmt, EAssignmentStmt, ReadIntStmt, WriteStmt, IfElseStmt, IfElseIfStmt, ElseIfStmt, WhileStmt, RepeatUntilStmt, ForStmt, VarExpression, Brackets, EQExpression, NEQExpression, GTExpression, LTExpression, GTEExpression, LTEExpression, AddExpression, SubExpression, MultExpression, DivExpression, OrExpression, AndExpression}
import scala.collection.immutable.HashMap
import scala.collection.immutable.Set

class LiveVariablesTest extends AnyFunSuite {

    type SetStructure 		= Set[String]
    type HashMapStructure  	= HashMap[GraphNode, (SetStructure, SetStructure)]
    type GraphStructure 	= Graph[GraphNode, GraphEdge.DiEdge]

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

	val graph: GraphStructure = Graph[GraphNode, GraphEdge.DiEdge](
		StartNode()      ~> SimpleNode(s1),
		SimpleNode(s1)   ~> SimpleNode(s2),
		SimpleNode(s2)   ~> SimpleNode(s3),
		SimpleNode(s3)   ~> SimpleNode(s3_1),
		SimpleNode(s3)   ~> SimpleNode(s4),
		SimpleNode(s3_1) ~> SimpleNode(s4),
		SimpleNode(s4)   ~> EndNode()
	)

	val live_variables = new LiveVariables()
	val graph_received = live_variables.backwardGraph(graph)
	val hash_map_received = live_variables.initializeHashMap(graph)
	val live_variables_received = live_variables.analyse(graph)

    test("BACKWARD GRAPH") {

		val graph_expected = Graph[GraphNode, GraphEdge.DiEdge](
		 	EndNode()			~> SimpleNode(s4),
		 	SimpleNode(s4) 		~> SimpleNode(s3_1),
		 	SimpleNode(s4) 		~> SimpleNode(s3),
		 	SimpleNode(s3_1)	~> SimpleNode(s3),
		 	SimpleNode(s3) 		~> SimpleNode(s2),
		 	SimpleNode(s2) 		~> SimpleNode(s1),
		 	SimpleNode(s1) 		~> StartNode(),
		)

		assert(graph_expected == graph_received)
	}

	test("INITIALIZE HASH MAP") {

		val hash_map_expected = HashMap(
			StartNode() 		-> (Set(), Set()),
			SimpleNode(s1) 		-> (Set(), Set()),
			SimpleNode(s2) 		-> (Set(), Set()),
			SimpleNode(s3) 		-> (Set(), Set()),
			SimpleNode(s3_1)	-> (Set(), Set()),
			SimpleNode(s4) 	    -> (Set(), Set()),
			EndNode() 			-> (Set(), Set()),	
    	)

		assert(hash_map_expected == hash_map_received)
	}

	test("CREATE LIVE VARIABLES HASH MAP") {

		val live_variables_expected = HashMap(
			StartNode() 		-> (Set(), Set()),
			SimpleNode(s1) 		-> (Set(), Set("x")),
			SimpleNode(s2) 		-> (Set("x"), Set("max", "x")),
			SimpleNode(s3) 		-> (Set("max", "x"), Set("max", "x")),
			SimpleNode(s3_1) 	-> (Set("x"), Set("max")),
			SimpleNode(s4) 		-> (Set("max"), Set()),
			EndNode() 			-> (Set(), Set()),
		)

		assert(live_variables_expected == live_variables_received)

	}
}