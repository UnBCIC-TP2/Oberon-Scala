package br.unb.cic.oberon.graph

import br.unb.cic.oberon.ast.{AssignmentStmt, GTExpression, IfElseStmt, ReadIntStmt, SequenceStmt, VarExpression, WriteStmt}
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

}
