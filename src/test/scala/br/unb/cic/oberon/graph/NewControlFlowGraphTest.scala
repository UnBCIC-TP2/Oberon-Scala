package br.unb.cic.oberon.graph

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, IntraProceduralGraphBuilder, NewControlFlowGraph, SimpleNode, StartNode}
import org.scalactic.source.Position
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc

class NewControlFlowGraphTest extends AnyFunSuite with BeforeAndAfter{


  test("Test the labels for statements"){
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(6))
    val secondAssignmentStmt = AssignmentStmt("x", IntValue(1))
    val thirdAssignmentStmt = Some(AssignmentStmt("x", IntValue(0)))

    val grafo = new NewControlFlowGraph()
    val labelTeste = grafo.init(firstAssignmentStmt)

    assert(labelTeste.label == firstAssignmentStmt.label)
    assert(1 == firstAssignmentStmt.label)
    assert(2 == secondAssignmentStmt.label)
    assert(3 == thirdAssignmentStmt.get.label)
  }

  /*
  * VAR
      x : INTEGER;

    BEGIN
	    x := 2
    END
* */


  test("Test new control flow graph for assignment stmt init function"){
    val firstAssigmentStmt = AssignmentStmt("x", IntValue(2))
    val grafo = new NewControlFlowGraph()

    val assignmentStmtInitTest = grafo.init(firstAssigmentStmt)
    assert(assignmentStmtInitTest == AssignmentStmt("x", IntValue(2)))

  }

  test("Test new control flow graph for sequence stmt init function"){
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val secondAssignmentStmt = AssignmentStmt("y", IntValue(3))
    val thirdAssignmentStmt = AssignmentStmt("z", IntValue(4))
    val fourthAssignmentStmt = AssignmentStmt("w", IntValue(1))
    val grafo = new NewControlFlowGraph()

    val sequenceStmts : List[Statement] = List(firstAssignmentStmt, secondAssignmentStmt, thirdAssignmentStmt,
      fourthAssignmentStmt)

    val sequenceStmtInitTeste = grafo.init(SequenceStmt(sequenceStmts))
    assert(sequenceStmtInitTeste == firstAssignmentStmt)

  }

  test("Test new control flow graph for ifelse stmt init function"){
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(1))
    val secondAssignmentStmt = Some(AssignmentStmt("x", IntValue(0)))
    val firstIfElseStmt = IfElseStmt(GTExpression(IntValue(3), IntValue(5)), firstAssignmentStmt, secondAssignmentStmt)

    val grafo = new NewControlFlowGraph()
    val ifElseStmtInitTeste = grafo.init(firstIfElseStmt)

    assert(ifElseStmtInitTeste == firstIfElseStmt)
  }

  test("Test new control flow graph for while stmt init function"){
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(6))
    val firstWhileStmt = WhileStmt(GTExpression(IntValue(3), IntValue(5)), firstAssignmentStmt)

    val grafo = new NewControlFlowGraph()
    val whileStmtInitTeste = grafo.init(firstWhileStmt)

    assert(whileStmtInitTeste == firstWhileStmt)
  }

  test("Test new control flow graph for assignment stmt final function"){
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val grafo = new NewControlFlowGraph()

    val assignmentStmtFinalTeste = grafo.finalFG(firstAssignmentStmt)
    assert(assignmentStmtFinalTeste == Set(firstAssignmentStmt))

  }

  test("Test new control flow graph for sequence stmt final function"){
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val secondAssignmentStmt = AssignmentStmt("y", IntValue(3))
    val thirdAssignmentStmt = AssignmentStmt("z", IntValue(4))
    val fourthAssignmentStmt = AssignmentStmt("w", IntValue(1))
    val grafo = new NewControlFlowGraph()

    val sequenceStmts : List[Statement] = List(firstAssignmentStmt, secondAssignmentStmt, thirdAssignmentStmt,
      fourthAssignmentStmt)

    val sequenceStmtFinalTeste = grafo.finalFG(SequenceStmt(sequenceStmts))
    assert(sequenceStmtFinalTeste == Set(fourthAssignmentStmt))

  }

  /*
  *   VAR
  *     x : INTEGER
  *   BEGIN
  *     IF (3 > 5) THEN
  *       x = 1
  *     ELSE
  *       x = 0
  *   END
  *
  *
  * */

  test("Test new control flow graph for IfElse stmt final function"){
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(1))
    val secondAssignmentStmt = Some(AssignmentStmt("x", IntValue(0)))
    val firstIfElseStmt = IfElseStmt(GTExpression(IntValue(3), IntValue(5)), firstAssignmentStmt, secondAssignmentStmt)

    val grafo = new NewControlFlowGraph()
    val ifElseStmtFinalTeste = grafo.finalFG(firstIfElseStmt)

    assert(ifElseStmtFinalTeste == Set(firstAssignmentStmt, secondAssignmentStmt.get))
  }

  test("Test new control flow graph for IfElseIF stmt final function"){
    val a1 = AssignmentStmt("x", IntValue(1))
    val a2 = Option(AssignmentStmt("x", IntValue(0)))
    val a3 = AssignmentStmt("x", IntValue(2))
    val a4 = AssignmentStmt("x", IntValue(3))
    val a5 = AssignmentStmt("x", IntValue(4))

    val t1 = GTExpression(IntValue(3), IntValue(5))

    val e1 = ElseIfStmt(GTExpression(IntValue(1), IntValue(2)), a3)
    val e2 = ElseIfStmt(LTExpression(IntValue(4), IntValue(5)), a4)
    val e3 = ElseIfStmt(GTExpression(IntValue(10), IntValue(5)), a5)

    val elseIf : List[ElseIfStmt] = List(e1, e2, e3)

    val s1 = IfElseIfStmt(t1, a1, elseIf, a2)

    val grafo = new NewControlFlowGraph()
    val teste = grafo.finalFG(s1)

    assert(teste == Set(a1, a3, a4, a5, a2.get))
  }



  test("Test new control flow graph for while stmt final function"){
    val s1 = AssignmentStmt("x", IntValue(2))
    val s2 = AssignmentStmt("y", IntValue(3))
    val s3 = AssignmentStmt("z", IntValue(4))
    val s4 = AssignmentStmt("w", IntValue(1))

    val stmts : List[Statement] = List(s1, s2, s3, s4)
    val s5 = WhileStmt(GTExpression(IntValue(3), IntValue(5)), SequenceStmt(stmts))

    val grafo = new NewControlFlowGraph()
    val teste = grafo.finalFG(s5)

    assert(teste == Set(s5))
  }

  test("Test new control flow graph for case stmt final function"){
    val s1 = AssignmentStmt("x", IntValue(2))
    val s2 = AssignmentStmt("y", IntValue(3))
    val s3 = SequenceStmt(List(s1, s2))
    val s4 = AssignmentStmt("a", IntValue(10))
    val s5 = AssignmentStmt("b", IntValue(5))
    val s6 = AssignmentStmt("z", IntValue(4))

    val c1 = SimpleCase(IntValue(5), s1)
    val c2 = SimpleCase(IntValue(2), s2)
    val c3 = SimpleCase(IntValue(4), s3)
    val c4 = RangeCase(IntValue(1), IntValue(10), s4)
    val c5 = RangeCase(IntValue(2), IntValue(5), s5)

    val cas : List[CaseAlternative] = List(c1, c2, c3, c4, c5)

    val r1 = CaseStmt(IntValue(2), cas, Option(s6))

    val grafo = new NewControlFlowGraph()
    val teste = grafo.finalFG(r1)

    assert(teste == Set(s1, s2, s2, s4, s5, s6))

  }

  test("Test new control flow graph for ifelse stmt flow function"){
    val s1 = AssignmentStmt("x", IntValue(2))
    val s2 = Option(AssignmentStmt("y", IntValue(3)))
    val ifElse = IfElseStmt(GTEExpression(IntValue(2), IntValue(5)), s1, s2)

    val grafo = new NewControlFlowGraph()
    val ifElseStmtFlowTest = grafo.flow(ifElse)

    assert(ifElseStmtFlowTest == Set((ifElse, s1), (ifElse, s2.get)))
  }

  test("Test new control flow graph for while stmt flow function"){
    val s1 = AssignmentStmt("x", IntValue(2))
    //val s2 = AssignmentStmt("y", IntValue(3))
    //val s3 = AssignmentStmt("z", IntValue(4))
    //val s4 = AssignmentStmt("w", IntValue(1))

    //val stmts : List[Statement] = List(s1, s2, s3, s4)
    val s5 = WhileStmt(GTExpression(IntValue(3), IntValue(5)), s1)

    val grafo = new NewControlFlowGraph()
    val teste = grafo.flow(s5)

    assert(teste == Set((s5, s1), (s1, s5)))
  }

  test("Test new control flow graph for sequence stmt flow function - Only Assignments"){
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val secondAssignmentStmt = AssignmentStmt("y", IntValue(3))
    val thirdAssignmentStmt = AssignmentStmt("z", IntValue(4))
    val fourthAssignmentStmt = AssignmentStmt("w", IntValue(1))
    val grafo = new NewControlFlowGraph()

    val sequenceStmts : List[Statement] = List(firstAssignmentStmt, secondAssignmentStmt, thirdAssignmentStmt,
      fourthAssignmentStmt)

    val sequenceStmtFinalTeste = grafo.flow(SequenceStmt(sequenceStmts))
    assert(sequenceStmtFinalTeste == Set((firstAssignmentStmt, secondAssignmentStmt),
      (secondAssignmentStmt, thirdAssignmentStmt), (thirdAssignmentStmt, fourthAssignmentStmt)))

  }

  test("Test new control flow graph for sequence stmt flow function"){
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val secondAssignmentStmt = AssignmentStmt("y", IntValue(3))
    val thirdAssignmentStmt = AssignmentStmt("z", IntValue(4))
    val fourthAssignmentStmt = AssignmentStmt("w", IntValue(1))

    val s1 = AssignmentStmt("x", IntValue(2))
    val s2 = Option(AssignmentStmt("y", IntValue(3)))
    val ifElse = IfElseStmt(GTEExpression(IntValue(2), IntValue(5)), s1, s2)

    val grafo = new NewControlFlowGraph()

    val sequenceStmts : List[Statement] = List(firstAssignmentStmt, secondAssignmentStmt, thirdAssignmentStmt,
      fourthAssignmentStmt, ifElse)

    val sequenceStmtFinalTeste = grafo.flow(SequenceStmt(sequenceStmts))

    assert(sequenceStmtFinalTeste == Set((firstAssignmentStmt, secondAssignmentStmt),
      (secondAssignmentStmt, thirdAssignmentStmt), (thirdAssignmentStmt, fourthAssignmentStmt), (ifElse, s1),
      (ifElse, s2.get), (fourthAssignmentStmt, ifElse)))

  }

  override protected def before(fun: => Any)(implicit pos: Position): Unit = Statement.reset()
}
