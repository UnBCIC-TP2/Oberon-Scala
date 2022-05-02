package br.unb.cic.oberon.cfg

import br.unb.cic.oberon.ast._
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc

trait NewGraphNode

case class NewStartNode() extends NewGraphNode
case class NewSimpleNode(stmt: Statement) extends NewGraphNode
case class NewEndNode() extends NewGraphNode

//case class StartNode(label: Int) extends GraphNode
//case class SimpleNode(label: Int, stmt: Statement) extends GraphNode
//case class EndNode(label: Int) extends GraphNode

trait NewControlFlowGraphBuilder {
  def createControlFlowGraph(procedure: Procedure): Graph[GraphNode, GraphEdge.DiEdge] // Graph
  def createControlFlowGraph(stmt: Statement): Graph[GraphNode, GraphEdge.DiEdge] // Graph
}

class NewControlFlowGraph() {
  var nomesProcedures = Map[String, Procedure]()
  var startProc = Map[String, StartProcStmt]()
  var endProc = Map[String, EndProcStmt]()

  def setProcedures(procedures: List[Procedure]): Unit ={
    for (procedure <- procedures){
      nomesProcedures += (procedure.name -> procedure)
      startProc += (procedure.name -> StartProcStmt(procedure))
      endProc += (procedure.name -> EndProcStmt(procedure))
    }
  }

  def init(stmtFG : Statement) : Statement = {
    stmtFG match {
      case SequenceStmt(stmts) =>  init(stmts.head)
      case ProcedureCallStmt(name, args) => CallStmt(stmtFG)
      case _ => stmtFG
    }
  }

  def finalFG(stmtFG: Statement) : Set[Statement] = {
    stmtFG match {
      case SequenceStmt(stmts) => finalFG(stmts.last)
      case IfElseStmt(condition, thenStmt, elseStmt) =>
        if (elseStmt.isDefined){
          finalFG(thenStmt) concat finalFG(elseStmt.get)
        }
        else {
          finalFG(thenStmt)
        }
      case IfElseIfStmt(condition, thenStmt, elseifStmt, elseStmt) =>
        var stmtsIfElseIf = finalFG(thenStmt)

        for(elseIf <- elseifStmt){
          stmtsIfElseIf = stmtsIfElseIf concat finalFG(elseIf)
        }

        if(elseStmt.isDefined) {
          stmtsIfElseIf concat finalFG(elseStmt.get)
        }
        else {
          stmtsIfElseIf
        }
      case ElseIfStmt(condition, thenStmt) => finalFG(thenStmt)
      case CaseStmt(exp, cases, elseStmt) =>
        var stmtsCase : Set[Statement] = Set()
        for (caseItem <- cases){
          var caseType = caseItem  match {
            case SimpleCase(condition, stmt) => finalFG(stmt)
            case RangeCase(min, max, stmt) => finalFG(stmt)
          }
          stmtsCase = stmtsCase concat caseType
        }

        if (elseStmt.isDefined){
          stmtsCase = stmtsCase concat finalFG(elseStmt.get)
        }

        stmtsCase

      case ProcedureCallStmt(name, args) => Set(ReturnFromProcStmt(stmtFG))
      case _ => Set(stmtFG)
    }
  }

  def flow(stmtFG : Statement) : Set[(Statement, Statement)] = {
    stmtFG match {
      case IfElseStmt(condition, thenStmt, elseStmt) =>
        if (elseStmt.isDefined)
          flow(thenStmt) concat flow(elseStmt.get) concat Set((stmtFG, init(thenStmt)),
            (stmtFG, init(elseStmt.get)))
        else
          flow(thenStmt) concat Set((stmtFG, init(thenStmt)))
      case WhileStmt(condition, stmt) =>
        var result = flow(stmt) concat Set((stmtFG, init(stmt)))
        val finalWhile = finalFG(stmt)
        for (stmtFinal <- finalWhile){
          result = result concat Set((stmtFinal, stmtFG))
        }

        result
      case SequenceStmt(stmts) =>
        var result : Set[(Statement, Statement)] = Set()

        for (stmt <- stmts){
          result = result concat flow(stmt)
        }

        for (i <- 0 until stmts.length - 1 ){
          var stmtAtual = stmts(i)
          var stmtProximo = stmts(i + 1)
          for (stmtFinal <- finalFG(stmtAtual)){
            result = result concat Set((stmtFinal, init(stmtProximo)))
          }
        }

        result
      case ProcedureCallStmt(name, args) =>
        var result : Set[(Statement, Statement)] = Set()
        if (nomesProcedures contains name){
          var call = init(stmtFG)
          var rtrn = finalFG(stmtFG)
          result = Set((call, startProc(name)), (endProc(name), rtrn.head))
        }
        result

      case _ => Set()
    }

  }

  def flow(procedureFG : Procedure) : Set[(Statement, Statement)] = {
    var result : Set[(Statement, Statement)] = Set()
    result = result concat Set((startProc(procedureFG.name), init(procedureFG.stmt)))
    result = result concat flow(procedureFG.stmt)

    for (finalStmt <- finalFG(procedureFG.stmt)){
      result = result concat Set((finalStmt, endProc(procedureFG.name)))
    }

    result
  }

  def flow(moduleFG : OberonModule) : Set[(Statement, Statement)] = {
    setProcedures(moduleFG.procedures)
    var result : Set[(Statement, Statement)] = Set()
    for (procedure <- moduleFG.procedures) {
      result = result concat flow(procedure)
    }

    result = result concat flow(moduleFG.stmt.get)

    result
  }
}
