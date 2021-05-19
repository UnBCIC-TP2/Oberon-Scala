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

	val live_variables = new LiveVariables()

	test("TEST 1") {

		//	BEGIN
		// 		readInt(x)
		// 		readInt(max)
		// 		IF (x > max) THEN
		// 			max := x
		// 		END
		// 		write(max)
		// 	END

		val s1 		= ReadIntStmt("x")
		val s2      = ReadIntStmt("max")
		val s3      = IfElseStmt(GTExpression(VarExpression("x"), VarExpression("max")), s3_1 , None)
		val s3_1    = AssignmentStmt("max", VarExpression("x"))
		val s4      = WriteStmt(VarExpression("max"))

		val graph = Graph[GraphNode, GraphEdge.DiEdge](
			StartNode()      ~> SimpleNode(s1),
			SimpleNode(s1)   ~> SimpleNode(s2),
			SimpleNode(s2)   ~> SimpleNode(s3),
			SimpleNode(s3)   ~> SimpleNode(s3_1),
			SimpleNode(s3)   ~> SimpleNode(s4),
			SimpleNode(s3_1) ~> SimpleNode(s4),
			SimpleNode(s4)   ~> EndNode()
		)

		val hash_map_expected = HashMap(
			StartNode() 		-> (Set(), 				Set()),
			SimpleNode(s1) 		-> (Set(), 				Set("x")),
			SimpleNode(s2) 		-> (Set("x"), 			Set("max", "x")),
			SimpleNode(s3) 		-> (Set("max", "x"), 	Set("max", "x")),
			SimpleNode(s3_1) 	-> (Set("x"), 			Set("max")),
			SimpleNode(s4) 		-> (Set("max"), 		Set()),
			EndNode() 			-> (Set(), 				Set()),
		)
		
		val hash_map_received = live_variables.analyse(graph)

		assert(hash_map_expected == hash_map_received)
	}

	test("TEST 2"){
		
		// 	BEGIN
		// 		readInt(x)
		// 		max := x
		// 		readInt(max)
		// 	END
		
		val s1 = ReadIntStmt("x")
		val s2 = AssignmentStmt("max", VarExpression("x"))
		val s3 = ReadIntStmt("max")

		val graph = Graph[GraphNode, GraphEdge.DiEdge](
			StartNode() 	~> SimpleNode(s1),
			SimpleNode(s1) 	~> SimpleNode(s2),
			SimpleNode(s2) 	~> SimpleNode(s3),
			SimpleNode(s3) 	~> EndNode()
		)

		val hash_map_expected = HashMap(
			StartNode() 	-> (Set(), 		Set()),
			SimpleNode(s1) 	-> (Set(), 		Set("x")),
			SimpleNode(s2) 	-> (Set("x"), 	Set()),
			SimpleNode(s3) 	-> (Set(), 		Set()),
			EndNode() 		-> (Set(), 		Set())
		)

	 	val hash_map_received = live_variables.analyse(graph)
		
		assert(hash_map_expected == hash_map_received)
	}

	test("TEST 3") {

		// 	BEGIN
		// 		readInt(x)
		// 		IF (x > 1) THEN
		// 			IF (x > 10) THEN
		// 				write(x)
		// 			END
		// 			readInt(y)
		// 			IF (y < x) THEN
		// 				write(y)
		// 			ELSE
		// 				write(x)
		// 			END
		// 	END

		val s1 		= ReadIntStmt("x")
		val s2      = IfElseStmt(GTExpression(VarExpression("x"), IntValue(1)), s2_1 , None)
		val s2_1    = IfElseStmt(GTExpression(VarExpression("x"), IntValue(10)), s2_1_1 , None)
		val s2_1_1  = WriteStmt(VarExpression("x"))
		val s2_2    = ReadIntStmt("y")
		val s2_3    = IfElseStmt(LTExpression(VarExpression("y"), VarExpression("x")), s2_3_1 , s2_3_2)
		val s2_3_1  = WriteStmt(VarExpression("y"))
		val s2_3_2  = WriteStmt(VarExpression("x"))

		val graph = Graph[GraphNode, GraphEdge.DiEdge](
			StartNode()      	~> SimpleNode(s1),
			SimpleNode(s1)   	~> SimpleNode(s2),
			SimpleNode(s2)   	~> SimpleNode(s2_1),
			SimpleNode(s2)   	~> EndNode(),
			SimpleNode(s2_1)   	~> SimpleNode(s2_1_1),
			SimpleNode(s2_1)   	~> SimpleNode(s2_2),
			SimpleNode(s2_1_1)  ~> SimpleNode(s2_2),
			SimpleNode(s2_2)   	~> SimpleNode(s2_3),
			SimpleNode(s2_3) 	~> SimpleNode(s2_3_1),
			SimpleNode(s2_3) 	~> SimpleNode(s2_3_2),
			SimpleNode(s2_3_1)  ~> EndNode(),
			SimpleNode(s2_3_2)  ~> EndNode()
		)

		val hash_map_expected = HashMap(
			StartNode() 		-> (Set(), 			Set()),
			SimpleNode(s1) 		-> (Set(), 			Set("x")),
			SimpleNode(s2) 		-> (Set("x"), 		Set("x")),
			SimpleNode(s2_1)    -> (Set("x"), 		Set("x")),
			SimpleNode(s2_1_1) 	-> (Set("x"), 		Set("x")),
			SimpleNode(s2_2) 	-> (Set("x"), 		Set("x", "y")),
			SimpleNode(s2_3) 	-> (Set("x", "y"), 	Set("x", "y")),
			SimpleNode(s2_3_1)	-> (Set("y"), 		Set()),
			SimpleNode(s2_3_2)  -> (Set("x"), 		Set()),
			EndNode() 			-> (Set(), 			Set())
    	)

		val hash_map_received = live_variables.analyse(graph)

		assert(hash_map_expected == hash_map_received)
	}

	test("TEST 4") {

		// 	BEGIN
		// 		readInt(a)
		// 		readInt(b)
		// 		write(b)
		// 		readInt(c)
		// 		readInt(c)
		// 		b := c
		// 		write(b)
		// 		readInt(b)
		// 		c := b
		// 		write(c)
		// 		write(a)
		// 	END

		val s1 	= ReadIntStmt("a")
		val s2 	= ReadIntStmt("b")
		val s3 	= WriteStmt("b")
		val s4 	= ReadIntStmt("c")
		val s5 	= ReadIntStmt("c")
		val s6 	= AssignmentStmt("b", VarExpression("c"))
		val s7 	= WriteStmt("b")
		val s8 	= ReadIntStmt("b")
		val s9 	= AssignmentStmt("c", VarExpression("b"))
		val s10 = WriteStmt("c")
		val s11 = WriteStmt("a")

		val graph = Graph[GraphNode, GraphEdge.DiEdge](
			StartNode() 	~> SimpleNode(s1),
			SimpleNode(s1) 	~> SimpleNode(s2),
			SimpleNode(s2) 	~> SimpleNode(s3),
			SimpleNode(s3) 	~> SimpleNode(s4),
			SimpleNode(s4) 	~> SimpleNode(s5),
			SimpleNode(s5) 	~> SimpleNode(s6),
			SimpleNode(s6) 	~> SimpleNode(s7),
			SimpleNode(s7) 	~> SimpleNode(s8),
			SimpleNode(s8) 	~> SimpleNode(s9),
			SimpleNode(s9) 	~> SimpleNode(s10),
			SimpleNode(s10) ~> SimpleNode(s11),
			SimpleNode(s11) ~> EndNode()
		)

		val hash_map_expected = HashMap(
			StartNode() 	-> (Set(), 				Set()),
			SimpleNode(s1) 	-> (Set(), 				Set("a")),
			SimpleNode(s2) 	-> (Set("a"), 			Set("a", "b")),
			SimpleNode(s3) 	-> (Set("a", "b"), 		Set("a")),
			SimpleNode(s4) 	-> (Set("a"), 			Set("a")),
			SimpleNode(s5) 	-> (Set("a"), 			Set("a", "c")),
			SimpleNode(s6) 	-> (Set("a", "c"), 		Set("a", "b", "c")),
			SimpleNode(s7) 	-> (Set("a", "b", "c"), Set("a", "c")),
			SimpleNode(s8) 	-> (Set("a", "c"), 		Set("a", "b", "c")),
			SimpleNode(s9) 	-> (Set("a", "b", "c"), Set("a", "c")),
			SimpleNode(s10) -> (Set("a", "c"), 		Set("a")),
			SimpleNode(s11) -> (Set("a"), 			Set()),
			EndNode() 		-> (Set(), 				Set())
		)

		val hash_map_received = live_variables.analyse(graph)

		assert(hash_map_expected == hash_map_received)
	}
}