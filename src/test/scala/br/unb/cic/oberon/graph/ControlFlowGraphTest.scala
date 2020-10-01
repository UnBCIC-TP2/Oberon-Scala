package br.unb.cic.oberon.graph

import br.unb.cic.oberon.ast.{AddExpression, AssignmentStmt, BoolValue, CaseStmt, GTExpression, IfElseStmt, IntValue, MultExpression, RangeCase, ReadIntStmt, SequenceStmt, SimpleCase, SubExpression, VarExpression, WriteStmt}
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, IntraProceduralGraphBuilder, SimpleNode, StartNode}
import org.scalatest.funsuite.AnyFunSuite
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc


class ControlFlowGraphTest extends AnyFunSuite {

  /**
   * This is the test case for a control-flow graph for the following Oberon Code:
   *
   * BEGIN
   *   readInt(x);
   *   readInt(max);
   *   IF(x > max) THEN
   *     max := x
   *   END;
   *   write(max)
   * END
   *
   */
  test("Test control flow graph for stmt16.oberon") {
    val s3_1 = AssignmentStmt("max", VarExpression("x"))
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("max")
    val s3 = IfElseStmt(GTExpression(VarExpression("x"), VarExpression("max")), s3_1 , None)
    val s4 = WriteStmt(VarExpression("max"))

    // we manually build the "expected" graph, to run the test case.
    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> SimpleNode(s3_1)
    expected += SimpleNode(s3_1) ~> SimpleNode(s4)
    expected += SimpleNode(s3) ~> SimpleNode(s4)
    expected += SimpleNode(s4) ~> EndNode()

    val stmts = List(s1, s2, s3, s4)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(stmts))

    assert( 7 == g.nodes.size)
    assert( 7 == g.edges.size)

    assert(expected == g)  // does the resulting control-flow graph match with the expected graph?
  }
  /*  BEGIN
    x: INTEGER;
    z: BOOLEAN;
    readInt(x);
    CASE x OF
      0: z := FALSE
      1: z := TRUE
      ELSE: z:= FALSE
    END
    write(z)
  END*/

  test("Test control flow graph of CaseStatement with 2 regular cases and one else case") {

    val case0_stmt = AssignmentStmt("z", BoolValue(false))
    val case1_stmt = AssignmentStmt("z", BoolValue(true))
    val caseE_stmt = AssignmentStmt("z", BoolValue(false))
    val case0 = SimpleCase(IntValue(0), case0_stmt)
    val case1 = SimpleCase(IntValue(1), case1_stmt)
    val cases = List(case0, case1)

    val stmt0 = ReadIntStmt("x")
    val stmt1 = CaseStmt(VarExpression("x"), cases, Some(caseE_stmt))
    val stmt2 = WriteStmt(VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(case0_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(caseE_stmt)
    expected += SimpleNode(case0_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(caseE_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(statements))

    assert( 8 == g.nodes.size)
    assert( 9 == g.edges.size)
    assert( expected == g)

  }
// Test control flow graph of CaseStatement with 3 regular cases and one else case
  test("Test control flow graph of CaseStatement with 3 regular cases and one else case") {

    val case0_stmt = AssignmentStmt("z", AddExpression(VarExpression("a"), VarExpression("b")))
    val case1_stmt = AssignmentStmt("z", SubExpression(VarExpression("a"), VarExpression("b")))
    val case2_stmt = AssignmentStmt("z", MultExpression(VarExpression("a"), VarExpression("b")))
    val caseE_stmt = AssignmentStmt("z", IntValue(0))

    val case0 = SimpleCase(IntValue(0), case0_stmt)
    val case1 = SimpleCase(IntValue(1), case1_stmt)
    val case2 = SimpleCase(IntValue(2), case2_stmt)
    val cases = List(case0, case1, case2)

    val stmt0 = ReadIntStmt("x")
    val stmt1 = CaseStmt(VarExpression("x"), cases, Some(caseE_stmt))
    val stmt2 = WriteStmt(VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(case0_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case2_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(caseE_stmt)
    expected += SimpleNode(case0_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case2_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(caseE_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(statements))

    assert( 9 == g.nodes.size)
    assert( 11 == g.edges.size)
    assert( expected == g)
  }

  test("Test control flow graph of CaseStatement with 2 regular cases, 1 range case, one else case") {

    val case0_stmt = AssignmentStmt("z", AddExpression(VarExpression("a"), VarExpression("b")))
    val case1_stmt = AssignmentStmt("z", SubExpression(VarExpression("a"), VarExpression("b")))
    val case2_stmt = AssignmentStmt("z", MultExpression(VarExpression("a"), VarExpression("b")))
    val caseE_stmt = AssignmentStmt("z", IntValue(0))

    val case0 = SimpleCase(IntValue(0), case0_stmt)
    val case1 = SimpleCase(IntValue(1), case1_stmt)
    val case2 = RangeCase(IntValue(2), IntValue(10), case2_stmt)
    val cases = List(case0, case1, case2)

    val stmt0 = ReadIntStmt("x")
    val stmt1 = CaseStmt(VarExpression("x"), cases, Some(caseE_stmt))
    val stmt2 = WriteStmt(VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(case0_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case2_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(caseE_stmt)
    expected += SimpleNode(case0_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case2_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(caseE_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(statements))

    assert( 9 == g.nodes.size)
    assert( 11 == g.edges.size)
    assert( expected == g)
  }

}
