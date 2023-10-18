package br.unb.cic.oberon.graph

import br.unb.cic.oberon.AbstractTestSuite
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.cfg.{
  EndNode,
  GraphNode,
  IntraProceduralGraphBuilder,
  SimpleNode,
  StartNode
}
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc

class ControlFlowGraphTest extends AbstractTestSuite {

  /** This is the test case for a control-flow graph for the following Oberon
    * Code:
    *
    * BEGIN readInt(x); readInt(max); IF(x > max) THEN max := x END; write(max)
    * END
    */
  test("Test control flow graph for stmt16.oberon") {
    val s3_1 = AssignmentStmt("max", VarExpression("x"))
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("max")
    val s3 = IfElseStmt(
      GTExpression(VarExpression("x"), VarExpression("max")),
      s3_1,
      None
    )
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

    assert(7 == g.nodes.size)
    assert(7 == g.edges.size)

    assert(
      expected == g
    ) // does the resulting control-flow graph match with the expected graph?
  }

  test("Test control flow graph for stmt01.oberon") {
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("y")
    val s3 = WriteStmt(AddExpression(VarExpression("x"), VarExpression("y")))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> EndNode()

    val stmts = List(s1, s2, s3)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(stmts))

    assert(5 == g.nodes.size)
    assert(4 == g.edges.size)

    assert(expected == g)
  }

  test("Test control flow graph for stmt02.oberon") {
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("y")
    val s3 =
      AssignmentStmt("z", AddExpression(VarExpression("x"), VarExpression("y")))
    val s4 = WriteStmt(VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> SimpleNode(s4)
    expected += SimpleNode(s4) ~> EndNode()

    val stmts = List(s1, s2, s3, s4)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(stmts))

    assert(6 == g.nodes.size)
    assert(5 == g.edges.size)

    assert(expected == g)
  }

  /** Whilestmt test */
  test("Test control flow graph for stmt04.oberon") {
    val s3_1 = AssignmentStmt(
      "x",
      MultExpression(VarExpression("x"), VarExpression("x"))
    )
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("y")
    val s3 =
      WhileStmt(LTExpression(VarExpression("x"), VarExpression("y")), s3_1)
    val s4 = WriteStmt(VarExpression("x"))

    // we manually build the "expected" graph, to run the test case.
    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> SimpleNode(s3_1)
    expected += SimpleNode(s3_1) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> SimpleNode(s4)
    expected += SimpleNode(s4) ~> EndNode()

    val stmts = List(s1, s2, s3, s4)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(stmts))

    assert(7 == g.nodes.size)
    assert(7 == g.edges.size)

    assert(
      expected == g
    ) // does the resulting control-flow graph match with the expected graph?
  }

  /** Forstmt Test */
  test("Test control flow graph for stmt11.oberon") {

    /** BEGIN readInt(x);
      *
      * FOR y := 0 TO y < x DO readInt(z); z := z/(y+1); write(z) END;
      */

    val s1 = ReadIntStmt("x")

    val s2_0 = AssignmentStmt("y", IntValue(0))
    val s2_1 = ReadIntStmt("z")
    val s2_2 = AssignmentStmt(
      "z",
      DivExpression(
        VarExpression("z"),
        AddExpression(VarExpression("y"), IntValue(1))
      )
    )
    val s2_3 = WriteStmt(VarExpression("z"))
    val s2 = ForStmt(
      s2_0,
      LTExpression(VarExpression("y"), VarExpression("x")),
      SequenceStmt(List(s2_1, s2_2, s2_3))
    )

    // we manually build the "expected" graph, to run the test case.
    var expected = Graph[GraphNode, GraphEdge.DiEdge]() // Expected: 7 nodes

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2_0)
    expected += SimpleNode(s2_0) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s2_1)
    expected += SimpleNode(s2_1) ~> SimpleNode(s2_2)
    expected += SimpleNode(s2_2) ~> SimpleNode(s2_3)
    expected += SimpleNode(s2_3) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> EndNode()

    val stmts = List(s1, s2)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(stmts))

    assert(8 == g.nodes.size)
    assert(8 == g.edges.size)

    assert(
      expected == g
    ) // does the resulting control-flow graph match with the expected graph?
  }

  test("Test control flow graph for stmt12.oberon") {

    /** BEGIN readInt(x); v := 0;
      *
      * FOR y := 0 TO y < x DO readInt(w); v := v + (w * (y+1)) END; v := v / x;
      *
      * FOR z:= 0 TO z < x DO readInt(w); u := u + w END; u := u / x;
      *
      * write(v); write(u)
      *
      * END
      */

    val s1 = ReadIntStmt("x")
    val s2 = AssignmentStmt("v", IntValue(0))

    val s3_0 = AssignmentStmt("y", IntValue(0))
    val s3_1 = ReadIntStmt("w")
    val s3_2 = AssignmentStmt(
      "v",
      AddExpression(
        VarExpression("v"),
        MultExpression(
          VarExpression("w"),
          AddExpression(VarExpression("y"), IntValue(1))
        )
      )
    )
    val s3 = ForStmt(
      s3_0,
      LTExpression(VarExpression("y"), VarExpression("x")),
      SequenceStmt(List(s3_1, s3_2))
    )

    val s4 =
      AssignmentStmt("v", DivExpression(VarExpression("v"), VarExpression("x")))

    val s5_0 = AssignmentStmt("z", IntValue(0))
    val s5_1 = ReadIntStmt("w")
    val s5_2 =
      AssignmentStmt("u", AddExpression(VarExpression("u"), VarExpression("w")))
    val s5 = ForStmt(
      s5_0,
      LTExpression(VarExpression("z"), VarExpression("x")),
      SequenceStmt(List(s5_1, s5_2))
    )

    val s6 =
      AssignmentStmt("u", DivExpression(VarExpression("u"), VarExpression("x")))
    val s7 = WriteStmt(VarExpression("v"))
    val s8 = WriteStmt(VarExpression("u"))

    // we manually build the "expected" graph, to run the test case.
    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3_0)
    expected += SimpleNode(s3_0) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> SimpleNode(s3_1)
    expected += SimpleNode(s3_1) ~> SimpleNode(s3_2)
    expected += SimpleNode(s3_2) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> SimpleNode(s4)

    expected += SimpleNode(s4) ~> SimpleNode(s5_0)
    expected += SimpleNode(s5_0) ~> SimpleNode(s5)
    expected += SimpleNode(s5) ~> SimpleNode(s5_1)
    expected += SimpleNode(s5_1) ~> SimpleNode(s5_2)
    expected += SimpleNode(s5_2) ~> SimpleNode(s5)

    expected += SimpleNode(s5) ~> SimpleNode(s6)
    expected += SimpleNode(s6) ~> SimpleNode(s7)
    expected += SimpleNode(s7) ~> SimpleNode(s8)

    expected += SimpleNode(s8) ~> EndNode()

    val stmts = List(s1, s2, s3, s4, s5, s6, s7, s8)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(stmts))

    assert(15 == g.nodes.size)
    assert(17 == g.edges.size)
    assert(
      expected == g
    ) // does the resulting control-flow graph match with the expected graph?
  }

  test("Test control flow graph for stmt13.oberon") {

    /** BEGIN readInt(x);
      *
      * FOR y := x TO y < 100 DO y := y * y
      *
      * END;
      *
      * write(y)
      *
      * END
      */

    val s1 = ReadIntStmt("x")

    val s2_0 = AssignmentStmt("y", VarExpression("x"))
    val s2_1 = AssignmentStmt(
      "y",
      MultExpression(VarExpression("y"), VarExpression("y"))
    )
    val s2 =
      ForStmt(s2_0, LTExpression(VarExpression("y"), IntValue(100)), s2_1)

    val s3 = WriteStmt(VarExpression("y"))

    // we manually build the "expected" graph, to run the test case.
    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(s1)
    expected += SimpleNode(s1) ~> SimpleNode(s2_0)
    expected += SimpleNode(s2_0) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s2_1)
    expected += SimpleNode(s2_1) ~> SimpleNode(s2)
    expected += SimpleNode(s2) ~> SimpleNode(s3)
    expected += SimpleNode(s3) ~> EndNode()

    val stmts = List(s1, s2, s3)

    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(stmts))

    assert(7 == g.nodes.size)
    assert(7 == g.edges.size)

    assert(
      expected == g
    ) // does the resulting control-flow graph match with the expected graph?
  }

  ignore("Simple control flow graph with repeated statements") {

    val stmt0 = ReadIntStmt("x")
    val stmt1 = ReadIntStmt("y")
    val stmt2 = ReadIntStmt("z")
    val stmt3 = ReadIntStmt("x")

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()

    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> SimpleNode(stmt3)
    expected += SimpleNode(stmt3) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2, stmt3)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(statements))

    assert(expected.nodes.size == 6)
    assert(expected.edges.size == 5)
    assert(g.nodes.size == 6)
    assert(g.edges.size == 5)
    assert(g == expected)

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

  test(
    "Test control flow graph of CaseStatement with 2 regular cases and NO else case"
  ) {

    val case0_stmt = AssignmentStmt("z", IntValue(1))
    val case1_stmt = AssignmentStmt("z", IntValue(2))
    val case0 = SimpleCase(IntValue(0), case0_stmt)
    val case1 = SimpleCase(IntValue(1), case1_stmt)
    val cases = List(case0, case1)

    val stmt0 = ReadIntStmt("x")
    val stmt1 = CaseStmt(VarExpression("x"), cases, None)
    val stmt2 = WriteStmt(VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(case0_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(case1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(stmt2)
    expected += SimpleNode(case0_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(case1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(statements))

    assert(expected.nodes.size == 7)
    assert(expected.edges.size == 8)
    assert(g.nodes.size == 7)
    assert(g.edges.size == 8)
    assert(g == expected)
  }

  test(
    "Test control flow graph of CaseStatement with 2 regular cases and one else case"
  ) {

    val case0_stmt = AssignmentStmt("z", IntValue(1))
    val case1_stmt = AssignmentStmt("z", IntValue(2))
    val caseE_stmt = AssignmentStmt("z", IntValue(3))
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

    assert(g.nodes.size == 8)
    assert(g.edges.size == 9)
    assert(g == expected)

  }
// Test control flow graph of CaseStatement with 3 regular cases and one else case
  test(
    "Test control flow graph of CaseStatement with 3 regular cases and one else case"
  ) {

    val case0_stmt =
      AssignmentStmt("z", AddExpression(VarExpression("a"), VarExpression("b")))
    val case1_stmt =
      AssignmentStmt("z", SubExpression(VarExpression("a"), VarExpression("b")))
    val case2_stmt = AssignmentStmt(
      "z",
      MultExpression(VarExpression("a"), VarExpression("b"))
    )
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

    assert(9 == g.nodes.size)
    assert(11 == g.edges.size)
    assert(expected == g)
  }

  test(
    "Test control flow graph of CaseStatement with 2 regular cases, 1 range case, one else case"
  ) {

    val case0_stmt =
      AssignmentStmt("z", AddExpression(VarExpression("a"), VarExpression("b")))
    val case1_stmt =
      AssignmentStmt("z", SubExpression(VarExpression("a"), VarExpression("b")))
    val case2_stmt = AssignmentStmt(
      "z",
      MultExpression(VarExpression("a"), VarExpression("b"))
    )
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

    assert(9 == g.nodes.size)
    assert(11 == g.edges.size)
    assert(expected == g)
  }

  test(
    "Test control flow graph RepeatUntilStmt 03 - 1 Expression and 1 Condition "
  ) {
    val stmt00 = AssignmentStmt("x", IntValue(3))
    val stmt01 = AssignmentStmt("y", IntValue(4))
    val stmt02 = AssignmentStmt(
      "z",
      MultExpression(VarExpression("x"), VarExpression("y"))
    )
    val stmt03 =
      AssignmentStmt("z", AddExpression(VarExpression("z"), IntValue(5)))
    val stmt04 =
      RepeatUntilStmt(LTExpression(VarExpression("z"), IntValue(50)), stmt03)
    val stmt05 = WriteStmt(VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt00)
    expected += SimpleNode(stmt00) ~> SimpleNode(stmt01)
    expected += SimpleNode(stmt01) ~> SimpleNode(stmt02)
    expected += SimpleNode(stmt02) ~> SimpleNode(stmt03)
    expected += SimpleNode(stmt03) ~> SimpleNode(stmt04)
    expected += SimpleNode(stmt03) ~> SimpleNode(stmt05)
    expected += SimpleNode(stmt04) ~> SimpleNode(stmt03)
    expected += SimpleNode(stmt04) ~> SimpleNode(stmt05)
    expected += SimpleNode(stmt05) ~> EndNode()

    val statements = List(stmt00, stmt01, stmt02, stmt03, stmt04, stmt05)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(statements))

    assert(8 == g.nodes.size)
    assert(9 == g.edges.size)
    assert(expected == g)
  }

  test(
    "Test control flow graph of RepeatUntil 05 with 2 expression and 1 condition "
  ) {
    val stmt2_1 = AssignmentStmt("max", VarExpression("x"))
    val stmt0 = ReadIntStmt("x")
    val stmt1 = ReadIntStmt("max")
    val stmt2 = IfElseStmt(
      GTExpression(VarExpression("x"), VarExpression("max")),
      stmt2_1,
      None
    )
    val stmt3 =
      AssignmentStmt("x", SubExpression(VarExpression("x"), IntValue(1)))
    val stmt4 =
      RepeatUntilStmt(LTExpression(VarExpression("x"), IntValue(10)), stmt3)
    val stmt5 = WriteStmt(VarExpression("x"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> SimpleNode(stmt2_1)
    expected += SimpleNode(stmt2_1) ~> SimpleNode(stmt3)
    expected += SimpleNode(stmt2_1) ~> SimpleNode(stmt2_1) // gerado
    //    expected += SimpleNode(stmt2) ~> SimpleNode(stmt3)
    expected += SimpleNode(stmt3) ~> SimpleNode(stmt4)
    expected += SimpleNode(stmt3) ~> SimpleNode(stmt5)
    //    expected += SimpleNode(stmt4) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt4) ~> SimpleNode(stmt3) // gerado
    expected += SimpleNode(stmt4) ~> SimpleNode(stmt5)
    expected += SimpleNode(stmt5) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2, stmt2_1, stmt3, stmt4, stmt5)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(statements))

    assert(9 == g.nodes.size)
    assert(11 == g.edges.size)
    assert(expected == g)
  }

  /*
  MODULE SimpleModule;

    VAR
      x, y : INTEGER;
    BEGIN
      readInt(x);
      IF(x < 5) THEN
        y := 1
      ELSIF(x < 7) THEN
        y := 2
      ELSIF(x < 9) THEN
        y := 3
      END;
      write(y)
    END

  END SimpleModule.
   */

  test(
    "Test control flow graph of IfElseIfStmt with 3 if/else if and NO else case"
  ) {

    val if_stmt = AssignmentStmt("y", IntValue(1))
    val elsif1_stmt = AssignmentStmt("y", IntValue(2))
    val elsif_case_1 =
      ElseIfStmt(LTExpression(VarExpression("x"), IntValue(7)), elsif1_stmt)
    val elsif2_stmt = AssignmentStmt("y", IntValue(3))
    val elsif_case_2 =
      ElseIfStmt(LTExpression(VarExpression("x"), IntValue(9)), elsif2_stmt)

    val elsif = List(elsif_case_1, elsif_case_2)

    val stmt0 = ReadIntStmt("x")
    val stmt1 = IfElseIfStmt(
      LTExpression(VarExpression("x"), IntValue(5)),
      if_stmt,
      elsif,
      None
    )
    val stmt2 = WriteStmt(VarExpression("y"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(if_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(elsif1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(elsif2_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(stmt2)
    expected += SimpleNode(if_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(elsif1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(elsif2_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(statements))

    assert(g.nodes.size == 8)
    assert(g.edges.size == 10)
    assert(g == expected)
  }

  /*
  MODULE SimpleModule;

    VAR
      x, y : INTEGER;
    BEGIN
      readInt(x);
      IF(x < 5) THEN
        y := 1
      ELSIF(x < 7) THEN
        y := 2
      ELSIF(x < 9) THEN
        y := 3
      ELSE
        y := 4
      END;
      write(y)
    END

  END SimpleModule.
   */

  test(
    "Test control flow graph of IfElseIfStmt with 3 if/else if and 1 else case"
  ) {

    val if_stmt = AssignmentStmt("y", IntValue(1))
    val elsif1_stmt = AssignmentStmt("y", IntValue(2))
    val elsif_case_1 =
      ElseIfStmt(LTExpression(VarExpression("x"), IntValue(7)), elsif1_stmt)
    val elsif2_stmt = AssignmentStmt("y", IntValue(3))
    val elsif_case_2 =
      ElseIfStmt(LTExpression(VarExpression("x"), IntValue(9)), elsif2_stmt)
    val else_stmt = AssignmentStmt("y", IntValue(4))

    val elsif = List(elsif_case_1, elsif_case_2)

    val stmt0 = ReadIntStmt("x")
    val stmt1 = IfElseIfStmt(
      LTExpression(VarExpression("x"), IntValue(5)),
      if_stmt,
      elsif,
      Some(else_stmt)
    )
    val stmt2 = WriteStmt(VarExpression("y"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt0)
    expected += SimpleNode(stmt0) ~> SimpleNode(stmt1)
    expected += SimpleNode(stmt1) ~> SimpleNode(if_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(elsif1_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(elsif2_stmt)
    expected += SimpleNode(stmt1) ~> SimpleNode(else_stmt)
    expected += SimpleNode(if_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(elsif1_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(elsif2_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(else_stmt) ~> SimpleNode(stmt2)
    expected += SimpleNode(stmt2) ~> EndNode()

    val statements = List(stmt0, stmt1, stmt2)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(statements))

    assert(g.nodes.size == 9)
    assert(g.edges.size == 11)
    assert(g == expected)
  }

  /*
    MODULE SimpleModule;

      VAR
        x, y : INTEGER;
      BEGIN
        readInt(x);
        readInt(y);
        IF(x < 5) THEN
          IF(y < 3) THEN
            y := 0
          ELSIF(y = 3) THEN
            y := 1
          ELSIF(y > 3) THEN
            y := 2
        ELSIF(x = 5) THEN
          y := 10
        ELSE
          y := 90
        END;
        write(y)
      END

    END SimpleModule.
   */

  test(
    "Test control flow graph RepeatUntilStmt 02 - 1 Expression and 1 Condition "
  ) {
    val stmt00 = AssignmentStmt("x", IntValue(30))
    val stmt01 = AssignmentStmt("y", IntValue(2))
    val stmt02 =
      AssignmentStmt("z", DivExpression(VarExpression("x"), VarExpression("y")))
    val stmt03 =
      AssignmentStmt("z", AddExpression(VarExpression("z"), IntValue(2)))
    val stmt04 =
      RepeatUntilStmt(LTExpression(VarExpression("z"), IntValue(20)), stmt03)
    val stmt05 = WriteStmt(VarExpression("z"))

    var expected = Graph[GraphNode, GraphEdge.DiEdge]()
    expected += StartNode() ~> SimpleNode(stmt00)
    expected += SimpleNode(stmt00) ~> SimpleNode(stmt01)
    expected += SimpleNode(stmt01) ~> SimpleNode(stmt02)
    expected += SimpleNode(stmt02) ~> SimpleNode(stmt03)
    expected += SimpleNode(stmt03) ~> SimpleNode(stmt04)
    expected += SimpleNode(stmt03) ~> SimpleNode(stmt05)
    expected += SimpleNode(stmt04) ~> SimpleNode(stmt03)
    expected += SimpleNode(stmt04) ~> SimpleNode(stmt05)
    expected += SimpleNode(stmt05) ~> EndNode()

    val statements = List(stmt00, stmt01, stmt02, stmt03, stmt04, stmt05)
    val builder = new IntraProceduralGraphBuilder()
    val g = builder.createControlFlowGraph(SequenceStmt(statements))

    assert(8 == g.nodes.size)
    assert(9 == g.edges.size)
    assert(expected == g)
  }
}
