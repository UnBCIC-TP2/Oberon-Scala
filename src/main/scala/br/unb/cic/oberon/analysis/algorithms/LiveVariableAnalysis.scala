package br.unb.cic.oberon.analysis.algorithms

import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis
import br.unb.cic.oberon.ast.{AssignmentStmt, ReadIntStmt}
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode}
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge

case class LiveVariableAnaylisis() extends ControlFlowGraphAnalysis {

    private def analyse(cgf: Graph[GraphNode, GraphEdge.DiEdge])

    private def backwards(cgf: Graph[GraphNode, GraphEdge.DiEdge]) = {
        backward = Graph
        e => {
          val GraphEdge.DiEdge(previousNode, currentNode) = e.edge
          backward += currentNode ~> previousNode
        }
      )
    }
}]