package br.unb.cic.oberon.analysis_test

import br.unb.cic.oberon.analysis.algorithms.LiveVariables
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc
import br.unb.cic.oberon.cfg.{GraphNode, StartNode, SimpleNode, EndNode}
import br.unb.cic.oberon.ast.{IntValue, AssignmentStmt, EAssignmentStmt, ReadIntStmt, WriteStmt, IfElseStmt, IfElseIfStmt, ElseIfStmt, WhileStmt, RepeatUntilStmt, ForStmt, VarExpression, Brackets, EQExpression, NEQExpression, GTExpression, LTExpression, GTEExpression, LTEExpression, AddExpression, SubExpression, MultExpression, DivExpression, OrExpression, AndExpression}
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


	test("test Estevan"){
		/*
		* BEGIN
		*   readInt(x);
		*   max := x;
		*   readInt(max);
		* END
		*/
		
		val s1 = ReadIntStmt("x")
		val s2 = AssignmentStmt("max", VarExpression("x"))
		val s3 = ReadIntStmt("max")

		val graph = Graph[GraphNode, GraphEdge.DiEdge]()
		graph += StartNode() ~> SimpleNode(s1)
		graph += SimpleNode(s1) ~> SimpleNode(s2)
		graph += SimpleNode(s2) ~> SimpleNode(s3)
		graph += SimpleNode(s3) ~> EndNode()

		val hash_map_expected = Map(
			StartNode() -> (Set(), Set()),
			SimpleNode(s1) -> (Set(), Set("x")),
			SimpleNode(s2) ->(Set("x"), Set()),
			SimpleNode(s3) -> (Set(), Set()),
			EndNode() -> (Set(), Set())
		)

		val live_variables = new LiveVariables()
	 	val hash_map_received = live_variables.analyse(graph)
	 	assert(hash_map_expected == hash_map_received)
  
	}

	// test("teste cabuloso aaaaaaaaa") {
	// 	/*
	// 		BEGIN
	// 			x = input();
	// 			if(x>1){
	// 				if(x>10){
	// 					print(x);
	// 				}
	// 				y = input();
	// 				if(y < x){
	// 					print(y);
	// 				}
	// 				else{
	// 					print(x);
	// 				}
	// 			}

	// 		END
	// 	*/

	// 	val s1 		= ReadIntStmt("x")
	// 	val s2      = IfElseStmt(GTExpression(VarExpression("x"), IntValue(1)), s2_1 , None)
	// 	val s2_1    = IfElseStmt(GTExpression(VarExpression("x"), IntValue(10)), s2_1_1 , None)
	// 	val s2_1_1  = WriteStmt(VarExpression("x"))
	// 	val s2_2    = ReadIntStmt("y")
	// 	val s2_3    = IfElseStmt(LTExpression(VarExpression("y"), VarExpression("x")), s2_3_1 , [s2_3_2])
	// 	val s2_3_1  = WriteStmt(VarExpression("y"))
	// 	val s2_3_2  = WriteStmt(VarExpression("x"))

	// 	val graph: GraphStructure = Graph[GraphNode, GraphEdge.DiEdge](
	// 		StartNode()      	~> SimpleNode(s1),
	// 		SimpleNode(s1)   	~> SimpleNode(s2),
	// 		SimpleNode(s2)   	~> SimpleNode(s2_1),
	// 		SimpleNode(s2)   	~> EndNode(),
	// 		SimpleNode(s2_1)   	~> SimpleNode(s2_1_1),
	// 		SimpleNode(s2_1)   	~> SimpleNode(s2_2),
	// 		SimpleNode(s2_1_1)  ~> SimpleNode(s2_2),
	// 		SimpleNode(s2_2)   	~> SimpleNode(s2_3),
	// 		SimpleNode(s2_3) 	~> SimpleNode(s2_3_1),
	// 		SimpleNode(s2_3) 	~> SimpleNode(s2_3_2),
	// 		SimpleNode(s2_3_1)  ~> EndNode(),
	// 		SimpleNode(s2_3_2)  ~> EndNode()
	// 	)
	// 	val hash_map_expected = HashMap(
	// 		StartNode() 		-> (Set(), Set()),
	// 		SimpleNode(s1) 		-> (Set(), Set("x")),
	// 		SimpleNode(s2) 		-> (Set("x"), Set("x")),
	// 		SimpleNode(s2_1)    -> (Set("x"), Set("x")),
	// 		SimpleNode(s2_1_1) 	-> (Set("x"), Set("x")),
	// 		SimpleNode(s2_2) 	-> (Set("x"), Set("x", "y")),
	// 		SimpleNode(s2_3) 	-> (Set("x", "y"), Set("x", "y")),
	// 		SimpleNode(s2_3_1)	-> (Set("y"), Set()),
	// 		SimpleNode(s2_3_2)  -> (Set("x"), Set()),
	// 		EndNode() 			-> (Set(), Set())
    // 	)
	// 	val live_variables = new LiveVariables()
	// 	val hash_map_received = live_variables.analyse(graph)
	// 	assert(hash_map_expected == hash_map_received)
	// }
}