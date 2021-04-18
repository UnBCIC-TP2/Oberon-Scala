package br.unb.cic.oberon.analysis

import br.unb.cic.oberon.ast.{AssignmentStmt, GTExpression, IfElseStmt, ReadIntStmt, VarExpression, WriteStmt}
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode, StartNode}
import org.scalatest.funsuite.AnyFunSuite
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc
import scalax.collection.mutable.Graph

class ReachingDefinitionTest extends AnyFunSuite {

  test("return a map with fixed point for reaching definitions") {
    val s3_1 = AssignmentStmt("max", VarExpression("x"))
    val s1 = ReadIntStmt("x")
    val s2 = ReadIntStmt("max")
    val s3 = IfElseStmt(GTExpression(VarExpression("x"), VarExpression("max")), s3_1 , None)
    val s4 = WriteStmt(VarExpression("max"))

    var cfg = Graph[GraphNode, GraphEdge.DiEdge]()
    cfg += StartNode() ~> SimpleNode(s1)
    cfg += SimpleNode(s1) ~> SimpleNode(s2)
    cfg += SimpleNode(s2) ~> SimpleNode(s3)
    cfg += SimpleNode(s3) ~> SimpleNode(s3_1)
    cfg += SimpleNode(s3_1) ~> SimpleNode(s4)
    cfg += SimpleNode(s3) ~> SimpleNode(s4)
    cfg += SimpleNode(s4) ~> EndNode()

    val expected = Map(
      1 -> Map("In" -> List(), "Out" -> List(("x", 1))),
      2 -> Map("In" -> List(("x", 1)), "Out" -> List(("x", 1), ("max", 2))),
      3 -> Map("In" -> List(("x", 1), ("max", 2)), "Out" -> List(("x", 1), ("max", 2))),
      4 -> Map("In" -> List(("x", 1), ("max", 2)), "Out" -> List(("x", 1), ("max", 4))),
      5 -> Map("In" -> List(("x", 1), ("max", 2), ("max", 4)), "Out" -> List(("x", 1), ("max", 2), ("max", 4)))
    )

    val reachingDefinition = new ReachingDefinition()
    val reachingDefinitionAnalysis = reachingDefinition.analyseReachingDefinitions(cfg)

    assert(expected == reachingDefinitionAnalysis)
  }

}
