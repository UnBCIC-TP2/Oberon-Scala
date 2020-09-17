package br.unb.cic.oberon.cfg

import br.unb.cic.oberon.ast.Procedure

trait ControlFlowGraphBuilder {
  def createControlFlowGraph(procedure: Procedure): Unit // Graph
}

class IntraProceduralGraphBuilder extends ControlFlowGraphBuilder {
  override def createControlFlowGraph(procedure: Procedure): Unit = ???
}
