package br.unb.cic.oberon.analysis_test

import br.unb.cic.oberon.analysis.algorithms.AvailableExpressions
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode, StartNode}
import org.scalatest.funsuite.AnyFunSuite
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc
import scalax.collection.mutable.Graph

class AvailableExpressionsTest extends AnyFunSuite {
  /**
   * BEGIN
   *   readInt(x);
   *   readInt(y);
   *   max := x + y
   * END
   */
  test("return a map with number of keys equal to number of graph nodes") {
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
   *   readInt(x);
   *   readInt(y);
   *   max := x + y
   * END
   */
  test("return a map with reaching definitions for each graph node (example 1)") {
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("y")
    val s3 = AssignmentStmt("max", AddExpression(VarExpression("x"), VarExpression("y")))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> EndNode()

    val expected = Map(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set((Set(AddExpression(VarExpression("x"), VarExpression("y"))), SimpleNode(s3)))),
      SimpleNode(s2) -> (Set(), Set((Set(AddExpression(VarExpression("x"), VarExpression("y"))), SimpleNode(s3)))),
      SimpleNode(s3) -> (Set(), Set((Set(AddExpression(VarExpression("x"), VarExpression("y"))), SimpleNode(s3)))),
      EndNode() -> (Set(), Set((Set(AddExpression(VarExpression("x"), VarExpression("y"))), SimpleNode(s3)))),
    )

    val availableExps = new AvailableExpressions()
    val availableExpsAnalysis = availableExps.analyse(cfg)

    assert(expected == availableExpsAnalysis)
  }

  /**
   * BEGIN
   *   readInt(x);
   *   readInt(max);
   *   IF(x > max) THEN
   *     max := x
   *   END;
   *   write(max)
   * END
   */
  test("return a map with reaching definitions for each graph node (example 2)") {
    val s3_1 = AssignmentStmt("max", VarExpression("x"))
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("max")
    val s3 = IfElseStmt(GTExpression(VarExpression("x"), VarExpression("max")), s3_1 , None)
    val s4 = WriteStmt(VarExpression("max"))

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> SimpleNode(s3_1)
    cfg += SimpleNode(s3_1) ~> SimpleNode(s4)
    cfg += SimpleNode(s3) ~> SimpleNode(s4)
    cfg += SimpleNode(s4) ~> EndNode()

    val expected = Map(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3)))),
      SimpleNode(s2) -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3)))),
      SimpleNode(s3) -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3)))),
      SimpleNode(s3_1) -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3)))),
      SimpleNode(s4) -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3)))),
      EndNode() -> (Set(), Set((Set(GTExpression(VarExpression("x"), VarExpression("max"))), SimpleNode(s3))))
    )

    val availableExps = new AvailableExpressions()
    val availableExpsAnalysis = availableExps.analyse(cfg)

    assert(expected == availableExpsAnalysis)
  }
}
