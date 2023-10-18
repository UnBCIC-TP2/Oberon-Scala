package br.unb.cic.oberon.graph

import org.scalatest.funsuite.AnyFunSuite
import scalax.collection.GraphEdge
import scalax.collection.GraphEdge.{DiEdge, ~>}
import scalax.collection.GraphPredef.EdgeAssoc
import scalax.collection.mutable.Graph // mutable because we "increase" the graph after its construction

class SimpleGraphs extends AnyFunSuite {
  test("Test a really simple graph") {

    var g =
      Graph[
        Int,
        GraphEdge.DiEdge
      ]() // init an empty, DiEdge graph whose nodes are of type Int

    g += 1 ~> 2 // add the edge from 1 -> 2
    g += 2 ~> 3 // add the edge from 2 -> 3
    g += 2 ~> 4 // add the edge from 2 -> 4
    g += 4 ~> 5 // add the edge from 4 -> 5

    assert(5 == g.nodes.size) // assert that we have five vertices (nodes)
    assert(4 == g.edges.size) // assert that we have four edges
  }

  test("Graph traversal with pattern matching") {
    var g =
      Graph[
        Int,
        GraphEdge.DiEdge
      ]() // init an empty, DiEdge graph whose nodes are of type Int

    g += 1 ~> 2 // add the edge from 1 -> 2
    g += 2 ~> 3 // add the edge from 2 -> 3
    g += 2 ~> 4 // add the edge from 2 -> 4
    g += 4 ~> 5 // add the edge from 4 -> 5

    var sum = 0
    g.edges.foreach(e =>
      e.edge match {
        case n1 ~> n2 => sum += n1.value * n2.value
      }
    )
    assert(36 == sum)
  }
}
