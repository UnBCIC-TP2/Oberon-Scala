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
    val s1 = AssignmentStmt("x", IntValue(6))
    val a1 = AssignmentStmt("x", IntValue(1))
    val a2 = Some(AssignmentStmt("x", IntValue(0)))

    val grafo = new NewControlFlowGraph()
    val teste = grafo.init(s1)

    assert(teste.label == s1.label)
    assert(1 == s1.label)
    assert(2 == a1.label)
    assert(3 == a2.get.label)
  }

  /*
  * VAR
      x : INTEGER;

    BEGIN
	    x := 2
    END
* */


  test("Test new control flow graph for assignment stmt init function"){
    val s1 = AssignmentStmt("x", IntValue(2))
    val grafo = new NewControlFlowGraph()

    val teste = grafo.init(s1)
    assert(teste == AssignmentStmt("x", IntValue(2)))

  }

  test("Test new control flow graph for sequence stmt init function"){
    val s1 = AssignmentStmt("x", IntValue(2))
    val s2 = AssignmentStmt("y", IntValue(3))
    val s3 = AssignmentStmt("z", IntValue(4))
    val s4 = AssignmentStmt("w", IntValue(1))
    val grafo = new NewControlFlowGraph()

    val stmts : List[Statement] = List(s1, s2, s3, s4)

    val teste = grafo.init(SequenceStmt(stmts))
    assert(teste == AssignmentStmt("x", IntValue(2)))

  }

  test("Test new control flow graph for ifelse stmt init function"){
    val a1 = AssignmentStmt("x", IntValue(1))
    val a2 = Some(AssignmentStmt("x", IntValue(0)))
    val s2 = IfElseStmt(GTExpression(IntValue(3), IntValue(5)), a1, a2)

    val grafo = new NewControlFlowGraph()
    val teste = grafo.init(s2)

    assert(teste == s2)
  }

  test("Test new control flow graph for while stmt init function"){
    val s1 = AssignmentStmt("x", IntValue(6))
    val s2 = WhileStmt(GTExpression(IntValue(3), IntValue(5)), s1)

    val grafo = new NewControlFlowGraph()
    val teste = grafo.init(s2)

    assert(teste == s2)
  }

  test("Test new control flow graph for assignment stmt final function"){
    val s1 = AssignmentStmt("x", IntValue(2))
    val grafo = new NewControlFlowGraph()

    val teste = grafo.finalFG(s1)
    assert(teste == List(AssignmentStmt("x", IntValue(2))))

  }

  test("Test new control flow graph for sequence stmt final function"){
    val s1 = AssignmentStmt("x", IntValue(2))
    val s2 = AssignmentStmt("y", IntValue(3))
    val s3 = AssignmentStmt("z", IntValue(4))
    val s4 = AssignmentStmt("w", IntValue(1))
    val grafo = new NewControlFlowGraph()

    val stmts : List[Statement] = List(s1, s2, s3, s4)

    val teste = grafo.finalFG(SequenceStmt(stmts))
    assert(teste == List(AssignmentStmt("w", IntValue(1))))

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
    val a1 = AssignmentStmt("x", IntValue(1))
    val a2 = Some(AssignmentStmt("x", IntValue(0)))
    val s2 = IfElseStmt(GTExpression(IntValue(3), IntValue(5)), a1, a2)

    val grafo = new NewControlFlowGraph()
    val teste = grafo.finalFG(s2)

    assert(teste == List(a1, a2.get))
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

    assert(teste == List(a1, a3, a4, a5, a2.get))
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

    assert(teste == List(s5))
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

    assert(teste == List(s1, s2, s2, s4, s5, s6))

  }

  override protected def before(fun: => Any)(implicit pos: Position): Unit = Statement.reset()
}
