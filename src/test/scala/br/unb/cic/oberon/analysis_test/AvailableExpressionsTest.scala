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
  private val x = "x"
  private val xVarExp: VarExpression = VarExpression(x)
  private val y = "y"
  private val yVarExp: VarExpression = VarExpression(y)
  private val max = "max"
  private val maxVarExp: VarExpression = VarExpression(max)


  /**
   * BEGIN
   * y := 2 + 1
   * END
   */
  test("getNodeIn returns given node IN set on AnalysisMapping") {
    val onePlus2 = AddExpression(IntValue(1), IntValue(2))

    val s1 = AssignmentStmt(max, onePlus2)

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> EndNode()

    val analysisMapping: HashMap[GraphNode, (Set[Expression], Set[Expression])] = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set(onePlus2)),
      EndNode() -> (Set(onePlus2), Set(onePlus2)),
    )

    val endNodeIn = Set(onePlus2)
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
    val onePlus2 = AddExpression(IntValue(1), IntValue(2))

    val s1 = AssignmentStmt(max, onePlus2)

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> EndNode()

    val analysisMapping: HashMap[GraphNode, (Set[Expression], Set[Expression])] = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set(onePlus2)),
      EndNode() -> (Set(onePlus2), Set(onePlus2)),
    )

    val endNodeOut = Set(onePlus2)
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
    val onePlus2 = AddExpression(IntValue(1), IntValue(2))

    val s1 = AssignmentStmt(max, AddExpression(IntValue(1), IntValue(2)))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> EndNode()

    val analysisMapping: HashMap[GraphNode, (Set[Expression], Set[Expression])] = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set(onePlus2)),
      EndNode() -> (Set(), Set()),
    )

    val endNodeIn = Set(onePlus2)
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
    val xPlusY = AddExpression(xVarExp, yVarExp)

    val s1 = ReadIntStmt(x)
    val s2 = ReadIntStmt(y)
    val s3 = AssignmentStmt(max, xPlusY)

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> EndNode()

    val analysisMapping: HashMap[GraphNode, (Set[Expression], Set[Expression])] = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set()),
      SimpleNode(s2) -> (Set(), Set()),
      SimpleNode(s3) -> (Set(), Set(xPlusY)),
      EndNode() -> (Set(), Set()),
    )

    val endNodeIn = Set(xPlusY)
    val simpleNodeIn = Set()

    val availableExps = new AvailableExpressions

    assert(availableExps.computeNodeIn(cfg, analysisMapping, EndNode()) == endNodeIn)
    assert(availableExps.computeNodeIn(cfg, analysisMapping, SimpleNode(s3)) == simpleNodeIn)
  }

  // TODO consider including boolean expressions
  /**
   * BEGIN
   * readInt(x);
   * readInt(y);
   * max := x + y
   * END
   */
  test("computeNodeGen returns given node generated expressions set") {
    val xPlusY = AddExpression(xVarExp, yVarExp)
    val xTimes1 = MultExpression(xVarExp, IntValue(1))
    val xAndY = AndExpression(xVarExp, yVarExp)

    val s1 = ReadIntStmt(x)
    val s2 = ReadIntStmt(y)
    val s3 = AssignmentStmt(max, xPlusY)
    val s4 = AssignmentStmt(x, xTimes1)
    val s5 = AssignmentStmt(x, xAndY)


    val availableExps = new AvailableExpressions

    assert(availableExps.computeNodeGen(SimpleNode(s1)) == Set())
    assert(availableExps.computeNodeGen(SimpleNode(s2)) == Set())
    assert(availableExps.computeNodeGen(SimpleNode(s3)) == Set(xPlusY))
    assert(availableExps.computeNodeGen(SimpleNode(s4)) == Set(xTimes1))
    assert(availableExps.computeNodeGen(SimpleNode(s5)) != Set(xAndY))
  }

  test("computeNodeGen returns given node generated expressions set even when expressions are deeply nested") {
    val xPlusY = AddExpression(xVarExp, yVarExp)
    val xPlusYTimesX = MultExpression(xPlusY, xVarExp)
    val xPlusYTimesXPlusY = AddExpression(xPlusYTimesX, yVarExp)
    val xPlusYTimesXPlusYTimesX = MultExpression(xPlusYTimesXPlusY, xVarExp)

    val stmt = AssignmentStmt(x, xPlusYTimesXPlusYTimesX)

    val availableExps = new AvailableExpressions

    assert(availableExps.computeNodeGen(SimpleNode(stmt)) == Set(xPlusYTimesXPlusYTimesX, xPlusYTimesXPlusY, xPlusYTimesX, xPlusY))
  }


  // TODO consider including boolean expressions
  /**
   * BEGIN
   * readInt(x);
   * readInt(y);
   * max := x + y
   * x := x * 1
   * y := x && y
   * END
   */
  test("computeNodeKill returns given node killed expressions set when expressions are simple") {
    val xPlusY = AddExpression(xVarExp, yVarExp)
    val xTimes1 = MultExpression(xVarExp, IntValue(1))
    val xAndY = AndExpression(xVarExp, yVarExp)

    val s1 = ReadIntStmt(x)
    val s2 = ReadIntStmt(y)
    val s3 = AssignmentStmt(max, xPlusY)
    val s4 = AssignmentStmt(x,xTimes1)
    val s5 = AssignmentStmt(y, xAndY)

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> SimpleNode(s4)
    cfg += SimpleNode(s4) ~> SimpleNode(s5)
    cfg += SimpleNode(s5) ~> EndNode()


    val availableExps = new AvailableExpressions
    val analysisMapping = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set()),
      SimpleNode(s2) -> (Set(), Set()),
      SimpleNode(s3) -> (Set(), Set(xPlusY)),
      SimpleNode(s4) -> (Set(xPlusY), Set()),
      SimpleNode(s5) -> (Set(), Set()),
      EndNode() -> (Set(), Set())
    )

    assert(availableExps.computeNodeKill(SimpleNode(s1), Set()) == Set())
    assert(availableExps.computeNodeKill(SimpleNode(s2), Set()) == Set())
    assert(availableExps.computeNodeKill(SimpleNode(s3), Set(xPlusY)) == Set())
    assert(availableExps.computeNodeKill(SimpleNode(s4), Set(xPlusY, xTimes1)) == Set(xPlusY, xTimes1))
    assert(availableExps.computeNodeKill(SimpleNode(s5), Set()) == Set())
  }

  /**
   * BEGIN
   * readInt(x);
   * readInt(y);
   * max := (x + y) + 1
   * x := x * 1
   * y := ((x + y) + 1) * x
   * END
   */
  test("computeNodeKill returns given node killed expressions set when expressions are nested") {
    val xPlusY = AddExpression(xVarExp, yVarExp)
    val xPlusYPlus1 = AddExpression(xPlusY, IntValue(1))
    val xPlusYPlus1TimesX = MultExpression(xPlusYPlus1, xVarExp)
    val xTimes1 = MultExpression(xVarExp, IntValue(1))

    val s1 = ReadIntStmt(x)
    val s2 = ReadIntStmt(y)
    val s3 = AssignmentStmt(max, xPlusYPlus1)
    val s4 = AssignmentStmt(x, xTimes1)
    val s5 = AssignmentStmt(y, xPlusYPlus1TimesX)

    val availableExps = new AvailableExpressions

    assert(availableExps.computeNodeKill(SimpleNode(s1), Set()) == Set())
    assert(availableExps.computeNodeKill(SimpleNode(s2), Set()) == Set())
    assert(availableExps.computeNodeKill(SimpleNode(s3), Set(xPlusYPlus1, xPlusY)) == Set())
    assert(availableExps.computeNodeKill(SimpleNode(s4), Set(xPlusYPlus1, xPlusY, xTimes1)) ==
      Set(xPlusYPlus1, xPlusY, xTimes1))

    assert(availableExps.computeNodeKill(SimpleNode(s5), Set(xPlusYPlus1TimesX, xPlusYPlus1, xPlusY)) ==
      Set(xPlusYPlus1TimesX, xPlusYPlus1, xPlusY))
  }

  /**
   * BEGIN
   * readInt(x);
   * readInt(y);
   * max := x + y
   * END
   */
  test("analyse returns a map with number of keys equal to number of graph nodes") {
    val xPlusY = AddExpression(xVarExp, yVarExp)

    val s1 = ReadIntStmt(x)
    val s2 = ReadIntStmt(y)
    val s3 = AssignmentStmt(max, xPlusY)

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
  test("analyse returns a map with available expressions for each graph node (example 0)") {
    val onePlus2 = AddExpression(IntValue(1), IntValue(2))

    val s1 = AssignmentStmt(max, onePlus2)

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> EndNode()

    val expected = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set(onePlus2)),
      EndNode() -> (Set(onePlus2), Set())
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
  test("analyse returns a map with available expressions for each graph node (example 1)") {
    val xPlusY = AddExpression(xVarExp, yVarExp)

    val s1 = ReadIntStmt(x)
    val s2 = ReadIntStmt(y)
    val s3 = AssignmentStmt(max, xPlusY)

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> EndNode()

    val expected = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set()),
      SimpleNode(s2) -> (Set(), Set()),
      SimpleNode(s3) -> (Set(), Set(xPlusY)),
      EndNode() -> (Set(xPlusY), Set()),
    )

    val availableExps = new AvailableExpressions
    val availableExpsAnalysis = availableExps.analyse(cfg)

    assert(expected == availableExpsAnalysis)
  }

  /**
   * BEGIN
   * readInt(x);
   * readInt(max);
   * IF(x > max + 1) THEN
   * max := x
   * END;
   * write(max)
   * END
   */
  test("analyse returns a map with available expressions for each graph node (example 2)") {
    val maxPlus1 = AddExpression(maxVarExp, IntValue(1))

    val s3_1 = AssignmentStmt(max, xVarExp)
    val s1 = ReadIntStmt(x)
    val s2 = ReadIntStmt(max)
    val s3 = IfElseStmt(GTExpression(xVarExp, maxPlus1), s3_1, None)
    val s4 = WriteStmt(maxVarExp)

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
      SimpleNode(s1) -> (Set(), Set()),
      SimpleNode(s2) -> (Set(), Set()),
      SimpleNode(s3) -> (Set(), Set(maxPlus1)),
      SimpleNode(s3_1) -> (Set(maxPlus1), Set()),
      SimpleNode(s4) -> (Set(), Set()),
      EndNode() -> (Set(), Set())
    )

    val availableExps = new AvailableExpressions
    val availableExpsAnalysis = availableExps.analyse(cfg)

    assert(availableExpsAnalysis == expected)
  }

  /**
   * BEGIN
   * readInt(x);
   * readInt(max);
   * IF(x > max + 1) THEN
   * max := (x + 3) * 2
   *   IF(max > (x * 2) + 3 + 4)
   *     max := x - 5 - 3
   *   END
   * END;
   * x := x + 1
   * write(max)
   * END
   */
  test("analyse returns a map with available expressions for each graph node (example 3)") {
    val xPlus3 = AddExpression(xVarExp, IntValue(3))
    val xPlus3Times2 = MultExpression(xPlus3, IntValue(2))
    val xMinus5 = AddExpression(xVarExp, IntValue(-5))
    val xMinus5Minus3 = AddExpression(xMinus5, IntValue(-3))
    val maxPlus1 = AddExpression(maxVarExp, IntValue(1))
    val xTimes2 = MultExpression(xVarExp, IntValue(2))
    val xTimes2Plus3 = AddExpression(xTimes2, IntValue(3))
    val xTimes2Plus3Plus4 = AddExpression(xTimes2Plus3, IntValue(4))
    val xPlus1 = AddExpression(xVarExp, IntValue(1))

    val s3_1 = AssignmentStmt(max, xPlus3Times2)
    val s4_1 = AssignmentStmt(max, xMinus5Minus3)
    val s1 = ReadIntStmt(x)
    val s2 = ReadIntStmt(max)
    val s3 = IfElseStmt(GTExpression(xVarExp, maxPlus1), s3_1, None)
    val s4 = IfElseStmt(GTExpression(maxVarExp, xTimes2Plus3Plus4), s4_1, None)
    val s5 = AssignmentStmt(x, xPlus1)
    val s6 = WriteStmt(maxVarExp)

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> SimpleNode(s5)
    cfg += SimpleNode(s3) ~> SimpleNode(s3_1)
    cfg += SimpleNode(s3_1) ~> SimpleNode(s4)
    cfg += SimpleNode(s4) ~> SimpleNode(s4_1)
    cfg += SimpleNode(s4) ~> SimpleNode(s5)
    cfg += SimpleNode(s4_1) ~> SimpleNode(s5)
    cfg += SimpleNode(s5) ~> SimpleNode(s6)
    cfg += SimpleNode(s6) ~> EndNode()

    val expected = HashMap(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set()),
      SimpleNode(s2) -> (Set(), Set()),
      SimpleNode(s3) -> (Set(), Set(maxPlus1)),
      SimpleNode(s3_1) -> (Set(maxPlus1), Set(xPlus3Times2, xPlus3)),
      SimpleNode(s4) -> (
        Set(xPlus3Times2, xPlus3),
        Set(xPlus3Times2, xPlus3, xTimes2Plus3Plus4, xTimes2, xTimes2Plus3)
      ),
      SimpleNode(s4_1) -> (
        Set(xPlus3Times2, xPlus3, xTimes2Plus3Plus4, xTimes2, xTimes2Plus3),
        Set(xPlus3Times2, xPlus3, xTimes2Plus3Plus4, xTimes2, xTimes2Plus3, xMinus5Minus3, xMinus5)
      ),
      SimpleNode(s5) -> (Set(), Set(xPlus1)),
      SimpleNode(s6) -> (Set(xPlus1), Set(xPlus1)),
      EndNode() -> (Set(xPlus1), Set())
    )



    val availableExps = new AvailableExpressions
    val availableExpsAnalysis = availableExps.analyse(cfg)

    assert(availableExpsAnalysis == expected)
  }
}
