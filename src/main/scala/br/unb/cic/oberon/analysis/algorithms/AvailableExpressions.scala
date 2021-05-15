package br.unb.cic.oberon.analysis.algorithms

import br.unb.cic.oberon.analysis.ControlFlowGraphAnalysis
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, SimpleNode, StartNode}
import scalax.collection.GraphEdge
import scalax.collection.mutable.Graph

import scala.annotation.tailrec
import scala.collection.immutable.{HashMap, Set}

case class AvailableExpressions() extends ControlFlowGraphAnalysis[HashMap[GraphNode, (Set[Expression], Set[Expression])], Set[Expression]] {
  type NodeAnalysis = Set[Expression]
  type AnalysisMapping = HashMap[GraphNode, (NodeAnalysis, NodeAnalysis)]

  def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge]): AnalysisMapping = {
    val initAvailExps: AnalysisMapping = initializeAvailableExpressions(cfg)

    analyse(cfg, initAvailExps, fixedPoint = false)
  }

  @tailrec
  private def analyse(cfg: Graph[GraphNode, GraphEdge.DiEdge],
                      prevAvailExps: AnalysisMapping,
                      fixedPoint: Boolean): AnalysisMapping = {
    if (fixedPoint) {
      prevAvailExps
    } else {
      var availExps: AnalysisMapping = prevAvailExps
      var predecessorsOut: NodeAnalysis = Set()

      cfg.edges.foreach(
        e => {
          val GraphEdge.DiEdge(prevNodeT, currNodeT) = e.edge
          val currNodeAnalysis: (GraphNode, (NodeAnalysis, NodeAnalysis)) = computeNodeInOutSets(cfg, currNodeT.value, availExps)
          availExps = availExps + currNodeAnalysis
          predecessorsOut = predecessorsOut & getNodeOut(availExps, prevNodeT.value)
        }
      )

      analyse(cfg, availExps, availExps == prevAvailExps)
    }
  }


  private def computeNodeInOutSets(cfg: Graph[GraphNode, GraphEdge.DiEdge],
                                   currNode: GraphNode,
                                   availExps: AnalysisMapping): (GraphNode, (NodeAnalysis, NodeAnalysis)) = {
    val currNodeIn: NodeAnalysis = computeNodeIn(cfg, availExps, currNode)
    val currNodeGen: NodeAnalysis = computeNodeGen(currNode)
    val currNodeKill: NodeAnalysis = computeNodeKill(currNode, cfg)

    // OUT(x) = In(x) + Gen(x) - Kill(x)
    val currNodeOut: NodeAnalysis =
      if (currNode != EndNode()) currNodeIn ++ currNodeGen -- currNodeKill else Set()

    currNode -> (currNodeIn, currNodeOut)
  }

  def getNodeIn(availExps: AnalysisMapping, Node: GraphNode): NodeAnalysis =
    availExps(Node)._1

  def getNodeOut(availExps: AnalysisMapping, Node: GraphNode): NodeAnalysis =
    availExps(Node)._2

  def computeNodeIn(cfg: Graph[GraphNode, GraphEdge.DiEdge],
                    availExps: AnalysisMapping,
                    currentNode: GraphNode): NodeAnalysis = {
    getNodePredecessors(cfg, currentNode)
      .map(predecessor => getNodeOut(availExps, predecessor))
      .reduceOption((acc, predecessorOut) => acc & predecessorOut)
      .getOrElse(Set())
  }

  def computeNodeGen(currNode: GraphNode): NodeAnalysis = currNode match {
    case SimpleNode(stmt) => getExpressions(stmt)
    case _ => Set()
  }

  def computeNodeKill(currNode: GraphNode, cfg: Graph[GraphNode, GraphEdge.DiEdge]): NodeAnalysis = {
    val u = buildAllExpressionsSet(cfg)

    currNode match {
      case SimpleNode(stmt) => stmt match {
        case AssignmentStmt (varName: String, _) => u.filter (exp => expressionUsesVariable (exp, varName) )
        case _ => Set ()
      }
      case _ => Set()
    }
  }

  private def expressionUsesVariable(exp: Expression, varName: String): Boolean = {
    exp match {
      case Brackets(exp: Expression) => expressionUsesVariable(exp, varName)
      //      case ArrayValue(v: List[Expression]) => v.flatMap(exp => expressionUsesVariable(exp, varName)).reduceOption((acc, bool) => acc || bool).getOrElse(false)
      case ArraySubscript(arrayBase: Expression, index: Expression) => expressionUsesVariable(arrayBase, varName) || expressionUsesVariable(index, varName)
      case FieldAccessExpression(exp: Expression, _: String) => expressionUsesVariable(exp, varName)
      //      case FunctionCallExpression(_: String, args: List[Expression]) => args.flatMap(exp => expressionUsesVariable(exp, varName))
      case EQExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case NEQExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case GTExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case LTExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case GTEExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case LTEExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case AddExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case SubExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case MultExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case DivExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case OrExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case AndExpression(left: Expression, right: Expression) => expressionUsesVariable(left, varName) || expressionUsesVariable(right, varName)
      case VarExpression(expVarName) => expVarName == varName
      case _ => false
    }
  }

  private def getExpressions(stmt: Statement): Set[Expression] = {
    val expressions: Set[Expression] = stmt match {
      case AssignmentStmt(_, exp: Expression) => Set(exp)
      case EAssignmentStmt(_, exp: Expression) => Set(exp)
      case SequenceStmt(stmts: List[Statement]) => stmts.flatMap(stmt => getExpressions(stmt)).toSet
      case WriteStmt(exp: Expression) => Set(exp)
      case IfElseStmt(condition: Expression, thenStmt: Statement, elseStmt: Option[Statement]) =>
        Set(condition) |
          getExpressions(thenStmt) |
          getOptionalStmtExpressions(elseStmt)

      case IfElseIfStmt(condition: Expression, thenStmt: Statement, elseifStmt: List[ElseIfStmt], elseStmt: Option[Statement]) =>
        Set(condition) |
          getExpressions(thenStmt) |
          elseifStmt.flatMap(stmt => getExpressions(stmt)).toSet |
          getOptionalStmtExpressions(elseStmt)

      case ElseIfStmt(condition: Expression, thenStmt: Statement) => Set(condition) | getExpressions(thenStmt)
      case WhileStmt(condition: Expression, stmt: Statement) => Set(condition) | getExpressions(stmt)
      case RepeatUntilStmt(condition: Expression, stmt: Statement) => Set(condition) | getExpressions(stmt)
      case ForStmt(init: Statement, condition: Expression, stmt: Statement) =>
        getExpressions(init) |
          Set(condition) |
          getExpressions(stmt)

      case LoopStmt(stmt: Statement) => getExpressions(stmt)
      case ReturnStmt(exp: Expression) => Set(exp)
      case CaseStmt(exp: Expression, cases: List[CaseAlternative], elseStmt: Option[Statement]) =>
        Set(exp) | cases.flatMap(altCase => getCaseAlternativeExpressions(altCase)).toSet | getOptionalStmtExpressions(elseStmt)

      case _ => Set()
    }

    expressions.filter(exp => exp match {
      case VarExpression(_) => false
      case _ => true
    })
  }
  //
  //  private def expressionHasVariable(exp: Expression): Boolean = {
  //    exp match {
  //      case Brackets (exp: Expression) =>
  //      case IntValue (v: Int) =>
  //      case BoolValue (v: Boolean) =>
  //      case ArrayValue (v: List[Expression] ) =>
  //      case ArraySubscript (arrayBase: Expression, index: Expression) =>
  //      case Undef () =>
  //      case FieldAccessExpression (exp: Expression, name: String) =>
  //      case VarExpression (name: String) =>
  //      case FunctionCallExpression (name: String, args: List[Expression] ) =>
  //      case EQExpression (left: Expression, right: Expression) =>
  //      case NEQExpression (left: Expression, right: Expression) =>
  //      case GTExpression (left: Expression, right: Expression) =>
  //      case LTExpression (left: Expression, right: Expression) =>
  //      case GTEExpression (left: Expression, right: Expression) =>
  //      case LTEExpression (left: Expression, right: Expression) =>
  //      case AddExpression (left: Expression, right: Expression) =>
  //      case SubExpression (left: Expression, right: Expression) =>
  //      case MultExpression (left: Expression, right: Expression) =>
  //      case DivExpression (left: Expression, right: Expression) =>
  //      case OrExpression (left: Expression, right: Expression) =>
  //      case AndExpression (left: Expression, right: Expression) =>
  //    }
  //  }

  private def getOptionalStmtExpressions(optionalStmt: Option[Statement]): Set[Expression] = {
    if (optionalStmt.isDefined) getExpressions(optionalStmt.get) else Set()
  }

  private def getCaseAlternativeExpressions(caseAlternative: CaseAlternative): Set[Expression] = {
    caseAlternative match {
      case SimpleCase(condition: Expression, stmt: Statement) => Set(condition) | getExpressions(stmt)
      case RangeCase(min: Expression, max: Expression, stmt: Statement) => Set(min, max) | getExpressions(stmt)
    }
  }

  private def initializeAvailableExpressions(cfg: Graph[GraphNode, GraphEdge.DiEdge]): AnalysisMapping = {
    var availableExps: AnalysisMapping = HashMap()
    val u: NodeAnalysis = buildAllExpressionsSet(cfg)

    cfg.edges.foreach(
      edge => edge.nodes.foreach(
        node => {
          availableExps = availableExps + (node.value -> (Set(), u))
        }
      )
    )

    availableExps
  }

  private def buildAllExpressionsSet(cfg: Graph[GraphNode, GraphEdge.DiEdge]): NodeAnalysis = {
    var u: NodeAnalysis = Set()

    cfg.edges.foreach(
      edge => edge.nodes.foreach(
        node => {
          Some(node.value) collect {
            case SimpleNode(stmt: Statement) =>
              val nodeGenExpressions = getExpressions(stmt)
              if (nodeGenExpressions.nonEmpty) u = u | nodeGenExpressions
          }
        }
      )
    )

    u
  }
}
