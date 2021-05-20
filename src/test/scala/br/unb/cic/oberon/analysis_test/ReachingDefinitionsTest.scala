package br.unb.cic.oberon.analysis_test

import br.unb.cic.oberon.analysis.algorithms.ReachingDefinitions
import br.unb.cic.oberon.ast.{AssignmentStmt, GTExpression, IfElseStmt, ReadIntStmt, VarExpression, WriteStmt}
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode, StartNode}
import org.scalatest.funsuite.AnyFunSuite
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc
import scalax.collection.mutable.Graph

class ReachingDefinitionsTest extends AnyFunSuite {
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
  test("return a map with number of keys equal to number of graph nodes") {
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

    val numberOfNodes = 7

    val reachingDefinitions = new ReachingDefinitions
    val reachingDefinitionsAnalysis = reachingDefinitions.analyse(cfg)

    val numberOfMapKeys = reachingDefinitionsAnalysis.keySet.size

    assert(numberOfNodes == numberOfMapKeys)
  }

  /*
  * BEGIN
  *   readInt(x);
  *   max := x;
  *   readInt(max);
  * END
  */
  test("return a map with reaching definitions for each graph node (example 1)") {
    val s1 = ReadIntStmt("x")
    val s2 = AssignmentStmt("max", VarExpression("x"))
    val s3 = ReadIntStmt("max")

    val cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> EndNode()

    val expected = Map(
      StartNode() -> (Set(), Set()),
      SimpleNode(s1) -> (Set(), Set(("x", SimpleNode(s1)))),
      SimpleNode(s2) -> (Set(("x", SimpleNode(s1))), Set(("max", SimpleNode(s2)), ("x", SimpleNode(s1)))),
      SimpleNode(s3) -> (Set(("max", SimpleNode(s2)), ("x", SimpleNode(s1))), Set(("x", SimpleNode(s1)), ("max", SimpleNode(s3)))),
      EndNode() -> (Set(("x", SimpleNode(s1)), ("max", SimpleNode(s3))), Set()),
    )

    val reachingDefinitions = new ReachingDefinitions
    val reachingDefinitionsAnalysis = reachingDefinitions.analyse(cfg)

    assert(reachingDefinitionsAnalysis == expected)
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
      SimpleNode(s1) -> (Set(), Set(("x", SimpleNode(s1)))),
      SimpleNode(s2) -> (Set(("x", SimpleNode(s1))), Set(("x", SimpleNode(s1)), ("max", SimpleNode(s2)))),
      SimpleNode(s3) -> (Set(("x", SimpleNode(s1)), ("max", SimpleNode(s2))), Set(("x", SimpleNode(s1)), ("max", SimpleNode(s2)))),
      SimpleNode(s3_1) -> (Set(("x", SimpleNode(s1)), ("max", SimpleNode(s2))), Set(("x", SimpleNode(s1)), ("max", SimpleNode(s3_1)))),
      SimpleNode(s4) -> (Set(("x", SimpleNode(s1)), ("max", SimpleNode(s2)), ("max", SimpleNode(s3_1))), Set(("x", SimpleNode(s1)), ("max", SimpleNode(s2)), ("max", SimpleNode(s3_1)))),
      EndNode() -> (Set(("x", SimpleNode(s1)), ("max", SimpleNode(s2)), ("max", SimpleNode(s3_1))), Set())
    )

    val reachingDefinitions = new ReachingDefinitions
    val reachingDefinitionsAnalysis = reachingDefinitions.analyse(cfg)

    assert(reachingDefinitionsAnalysis == expected)
  }
}
