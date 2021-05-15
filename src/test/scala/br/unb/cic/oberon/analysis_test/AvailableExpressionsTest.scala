package br.unb.cic.oberon.analysis_test

import br.unb.cic.oberon.analysis.algorithms.AvailableExpressions
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode, StartNode}
import org.scalatest.funsuite.AnyFunSuite
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc
import scalax.collection.mutable.Graph

import scala.collection.immutable.HashMap

class AvailableExpressionsTest extends AnyFunSuite {
  /**
   * BEGIN
   * y := 2 + 1
   * END
   */
  test("getNodeIn returns given node IN set on AnalysisMapping") {
    val s1 = AssignmentStmt("max", AddExpression(IntValue(1), IntValue(2)))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> EndNode()

    val analysisMapping: HashMap[GraphNode, (Set[Expression], Set[Expression])] = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set(AddExpression(IntValue(1), IntValue(2)))),
      EndNode() -> (Set(AddExpression(IntValue(1), IntValue(2))), Set(AddExpression(IntValue(1), IntValue(2)))),
    )

    val endNodeIn = Set(AddExpression(IntValue(1), IntValue(2)))
    val simpleNodeIn = Set()

    val availableExps = new AvailableExpressions

    assert(availableExps.getNodeIn(analysisMapping, EndNode()) == endNodeIn)
    assert(availableExps.getNodeIn(analysisMapping, SimpleNode(s1)) == simpleNodeIn)
  }

  /**
   * BEGIN
   * y := 2 + 1
   * END
   */
  test("getNodeOut returns given node OUT set on AnalysisMapping") {
    val s1 = AssignmentStmt("max", AddExpression(IntValue(1), IntValue(2)))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> EndNode()

    val analysisMapping: HashMap[GraphNode, (Set[Expression], Set[Expression])] = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set(AddExpression(IntValue(1), IntValue(2)))),
      EndNode() -> (Set(AddExpression(IntValue(1), IntValue(2))), Set(AddExpression(IntValue(1), IntValue(2)))),
    )

    val endNodeOut = Set(AddExpression(IntValue(1), IntValue(2)))
    val startNodeOut = Set()

    val availableExps = new AvailableExpressions

    assert(availableExps.getNodeOut(analysisMapping, EndNode()) == endNodeOut)
    assert(availableExps.getNodeOut(analysisMapping, StartNode()) == startNodeOut)
  }

  /**
   * BEGIN
   * y := 2 + 1
   * END
   */
  test("computeNodeIn returns given node IN set correctly computed on AnalysisMapping (example 0)") {
    val s1 = AssignmentStmt("max", AddExpression(IntValue(1), IntValue(2)))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> EndNode()

    val analysisMapping: HashMap[GraphNode, (Set[Expression], Set[Expression])] = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set(AddExpression(IntValue(1), IntValue(2)))),
      EndNode() -> (Set(), Set()),
    )

    val endNodeIn = Set(AddExpression(IntValue(1), IntValue(2)))
    val startNodeIn = Set()

    val availableExps = new AvailableExpressions

    assert(availableExps.computeNodeIn(cfg, analysisMapping, EndNode()) == endNodeIn)
    assert(availableExps.computeNodeIn(cfg, analysisMapping, StartNode()) == startNodeIn)
  }

  /**
   * BEGIN
   * readInt(x);
   * readInt(y);
   * max := x + y
   * END
   */
  test("computeNodeIn returns given node IN set correctly computed on AnalysisMapping (example 1)") {
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("y")
    val s3 = AssignmentStmt("max", AddExpression(VarExpression("x"), VarExpression("y")))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> EndNode()

    val xPlusYExpression = AddExpression(VarExpression("x"), VarExpression("y"))

    val analysisMapping: HashMap[GraphNode, (Set[Expression], Set[Expression])] = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set()),
      SimpleNode(s2) -> (Set(), Set()),
      SimpleNode(s3) -> (Set(), Set(xPlusYExpression)),
      EndNode() -> (Set(), Set()),
    )

    val endNodeIn = Set(xPlusYExpression)
    val simpleNodeIn = Set()

    val availableExps = new AvailableExpressions

    assert(availableExps.computeNodeIn(cfg, analysisMapping, EndNode()) == endNodeIn)
    assert(availableExps.computeNodeIn(cfg, analysisMapping, SimpleNode(s3)) == simpleNodeIn)
  }

  /**
   * BEGIN
   * readInt(x);
   * readInt(y);
   * max := x + y
   * END
   */
  test("computeNodeGen returns given node generated expressions set") {
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("y")
    val s3 = AssignmentStmt("max", AddExpression(VarExpression("x"), VarExpression("y")))
    val s4 = AssignmentStmt("x", MultExpression(VarExpression("x"), IntValue(1)))
//    val s5 = AssignmentStmt("x", AndExpression(VarExpression("x"), VarExpression("y")))


    val availableExps = new AvailableExpressions

    assert(availableExps.computeNodeGen(SimpleNode(s1)) == Set())
    assert(availableExps.computeNodeGen(SimpleNode(s2)) == Set())
    assert(availableExps.computeNodeGen(SimpleNode(s3)) == Set(AddExpression(VarExpression("x"), VarExpression("y"))))
    assert(availableExps.computeNodeGen(SimpleNode(s4)) == Set(MultExpression(VarExpression("x"), IntValue(1))))
//    assert(availableExps.computeNodeGen(SimpleNode(s5)) == Set(AndExpression(VarExpression("x"), VarExpression("y"))))
  }

  test("computeNodeGen returns given node generated expressions set even when expressions are deeply nested") {
    val x = VarExpression("x")
    val y = VarExpression("y")
    val add_1 = AddExpression(x, y)
    val mult_1 = MultExpression(add_1, x)
    val add_2 = AddExpression(mult_1, y)
    val mult_2 = MultExpression(add_2, x)
    val stmt = AssignmentStmt("x", mult_2)



    val availableExps = new AvailableExpressions

    assert(availableExps.computeNodeGen(SimpleNode(stmt)) == Set(mult_2, add_2, mult_1, add_1))
  }


  /**
   * BEGIN
   * readInt(x);
   * readInt(y);
   * max := x + y
   * END
   */
  test("computeNodeKill returns given node killed expressions set when expressions are simple") {
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("y")
    val s3 = AssignmentStmt("max", AddExpression(VarExpression("x"), VarExpression("y")))
    val s4 = AssignmentStmt("x", MultExpression(VarExpression("x"), IntValue(1)))
    val s5 = AssignmentStmt("y", AndExpression(VarExpression("x"), VarExpression("y")))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> SimpleNode(s4)
    cfg += SimpleNode(s4) ~> SimpleNode(s5)
    cfg += SimpleNode(s5) ~> EndNode()


    val availableExps = new AvailableExpressions

    assert(availableExps.computeNodeKill(SimpleNode(s1), cfg) == Set())
    assert(availableExps.computeNodeKill(SimpleNode(s2), cfg) == Set())
    assert(availableExps.computeNodeKill(SimpleNode(s3), cfg) == Set())

    assert(availableExps.computeNodeKill(SimpleNode(s4), cfg) != Set(
      AddExpression(VarExpression("x"), VarExpression("y")),
      MultExpression(VarExpression("x"), IntValue(1)),
      AndExpression(VarExpression("x"), VarExpression("y"))))
    assert(availableExps.computeNodeKill(SimpleNode(s4), cfg) == Set(
      AddExpression(VarExpression("x"), VarExpression("y")),
      MultExpression(VarExpression("x"), IntValue(1))))

    assert(availableExps.computeNodeKill(SimpleNode(s5), cfg) != Set(
      AddExpression(VarExpression("x"), VarExpression("y")),
      AndExpression(VarExpression("x"), VarExpression("y"))))
    assert(availableExps.computeNodeKill(SimpleNode(s5), cfg) == Set(
      AddExpression(VarExpression("x"), VarExpression("y"))))
  }

  /**
   * BEGIN
   * readInt(x);
   * readInt(y);
   * max := (x + y) + 1
   * x := x * 1
   * y := ((x + 1) + 1) * x
   * END
   */
  test("computeNodeKill returns given node killed expressions set when expressions are nested") {
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("y")
    val s3 = AssignmentStmt("max", AddExpression(AddExpression(VarExpression("x"), VarExpression("y")), IntValue(1)))
    val s4 = AssignmentStmt("x", MultExpression(VarExpression("x"), IntValue(1)))
    val s5 = AssignmentStmt("y", MultExpression(AddExpression(AddExpression(VarExpression("x"), VarExpression("y")), IntValue(1)), VarExpression("x")))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> SimpleNode(s4)
    cfg += SimpleNode(s4) ~> SimpleNode(s5)
    cfg += SimpleNode(s5) ~> EndNode()


    val availableExps = new AvailableExpressions

    assert(availableExps.computeNodeKill(SimpleNode(s1), cfg) == Set())
    assert(availableExps.computeNodeKill(SimpleNode(s2), cfg) == Set())
    assert(availableExps.computeNodeKill(SimpleNode(s3), cfg) == Set())

    assert(availableExps.computeNodeKill(SimpleNode(s4), cfg) == Set(
      MultExpression(AddExpression(AddExpression(VarExpression("x"), VarExpression("y")), IntValue(1)), VarExpression("x")),
      AddExpression(AddExpression(VarExpression("x"), VarExpression("y")), IntValue(1)),
      AddExpression(VarExpression("x"), VarExpression("y")),
      MultExpression(VarExpression("x"), IntValue(1))
    ))

    assert(availableExps.computeNodeKill(SimpleNode(s5), cfg) == Set(
      MultExpression(AddExpression(AddExpression(VarExpression("x"), VarExpression("y")), IntValue(1)), VarExpression("x")),
      AddExpression(AddExpression(VarExpression("x"), VarExpression("y")), IntValue(1)),
      AddExpression(VarExpression("x"), VarExpression("y"))
    ))
  }

  /**
   * BEGIN
   * readInt(x);
   * readInt(y);
   * max := x + y
   * END
   */
  ignore("analyse returns a map with number of keys equal to number of graph nodes") {
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("y")
    val s3 = AssignmentStmt("max", AddExpression(VarExpression("x"), VarExpression("y")))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> EndNode()

    val numberOfNodes = 5

    val algorithm = new AvailableExpressions
    val algorithmAnalysis = algorithm.analyse(cfg)

    val numberOfMapKeys = algorithmAnalysis.keySet.size

    assert(numberOfNodes == numberOfMapKeys)
  }

  /**
   * BEGIN
   * y := 2 + 1
   * END
   */
  ignore("analyse returns a map with available expressions for each graph node (example 0)") {
    val s1 = AssignmentStmt("max", AddExpression(IntValue(1), IntValue(2)))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> EndNode()

    val expected = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set(AddExpression(IntValue(1), IntValue(2)))),
      EndNode() -> (Set(AddExpression(IntValue(1), IntValue(2))), Set())
    )

    val availableExps = new AvailableExpressions
    val availableExpsAnalysis = availableExps.analyse(cfg)

    assert(availableExpsAnalysis == expected)
  }

  /**
   * BEGIN
   * readInt(x);
   * readInt(y);
   * max := x + y
   * END
   */
  ignore("analyse returns a map with available expressions for each graph node (example 1)") {
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("y")
    val s3 = AssignmentStmt("max", AddExpression(VarExpression("x"), VarExpression("y")))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> EndNode()

    val expected = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set((Set(AddExpression(VarExpression("x"), VarExpression("y"))), SimpleNode(s3)))),
      SimpleNode(s2) -> (Set(), Set((Set(AddExpression(VarExpression("x"), VarExpression("y"))), SimpleNode(s3)))),
      SimpleNode(s3) -> (Set(), Set((Set(AddExpression(VarExpression("x"), VarExpression("y"))), SimpleNode(s3)))),
      EndNode() -> (Set(), Set((Set(AddExpression(VarExpression("x"), VarExpression("y"))), SimpleNode(s3)))),
    )

    val availableExps = new AvailableExpressions
    val availableExpsAnalysis = availableExps.analyse(cfg)

    assert(expected == availableExpsAnalysis)
  }

  /**
   * BEGIN
   * readInt(x);
   * readInt(max);
   * IF(x > max) THEN
   * max := x
   * END;
   * write(max)
   * END
   */
  ignore("analyse returns a map with available expressions for each graph node (example 2)") {
    val s3_1 = AssignmentStmt("max", VarExpression("x"))
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("max")
    val s3 = IfElseStmt(GTExpression(VarExpression("x"), VarExpression("max")), s3_1, None)
    val s4 = WriteStmt(VarExpression("max"))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> SimpleNode(s3_1)
    cfg += SimpleNode(s3_1) ~> SimpleNode(s4)
    cfg += SimpleNode(s3) ~> SimpleNode(s4)
    cfg += SimpleNode(s4) ~> EndNode()

    val expected = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3)))),
      SimpleNode(s2) -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3)))),
      SimpleNode(s3) -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3)))),
      SimpleNode(s3_1) -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3)))),
      SimpleNode(s4) -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3)))),
      EndNode() -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3))))
    )

    val availableExps = new AvailableExpressions
    val availableExpsAnalysis = availableExps.analyse(cfg)

    assert(expected == availableExpsAnalysis)
  }
}
