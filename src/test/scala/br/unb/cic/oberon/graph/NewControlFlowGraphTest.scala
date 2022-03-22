package br.unb.cic.oberon.graph

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.cfg.{EndNode, GraphNode, IntraProceduralGraphBuilder, SimpleNode, StartNode, NewControlFlowGraph}
import org.scalatest.funsuite.AnyFunSuite
import scalax.collection.mutable.Graph
import scalax.collection.GraphEdge
import scalax.collection.GraphPredef.EdgeAssoc

class NewControlFlowGraphTest extends AnyFunSuite {

  ignore("Test the labels for statements"){
    val s1 = AssignmentStmt("x", IntValue(6))
    assert(1 == s1.label)
    val a1 = AssignmentStmt("x", IntValue(1))
    val a2 = Some(AssignmentStmt("x", IntValue(0)))
    val s2 = IfElseStmt(GTExpression(IntValue(3), IntValue(5)), a1, None)

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
	    x := 2 //(1 + 1)
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
    //val stmtSeq =

    val teste = grafo.init(SequenceStmt(stmts))
    assert(teste == AssignmentStmt("x", IntValue(2)))

  }

  test("Test new control flow graph for ifelse stmt init function"){
    val s1 = AssignmentStmt("x", IntValue(6))
    val a1 = AssignmentStmt("x", IntValue(1))
    val a2 = Some(AssignmentStmt("x", IntValue(0)))
    val s2 = IfElseStmt(GTExpression(IntValue(3), IntValue(5)), a1, None)

    val grafo = new NewControlFlowGraph()
    val teste = grafo.init(s2)

    assert(teste == a1)
  }

  test("Test new control flow graph for while stmt init function"){
    val s1 = AssignmentStmt("x", IntValue(6))
    val a1 = AssignmentStmt("x", IntValue(1))
    val a2 = Some(AssignmentStmt("x", IntValue(0)))
    val s2 = WhileStmt(GTExpression(IntValue(3), IntValue(5)), s1)

    val grafo = new NewControlFlowGraph()
    val teste = grafo.init(s2)

    assert(teste == s1)
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
    //val stmtSeq =

    val teste = grafo.finalFG(SequenceStmt(stmts))
    assert(teste == List(AssignmentStmt("w", IntValue(1))))

  }

  /*
  *   VAR
  *     x : INTEGER
  *   BEGIN
  *     x = 6
  *     IF (x > 5) THEN
  *       x = 1
  *     ELSE
  *       x = 0
  *   END
  *
  *
  * */

  test("Test new control flow graph for IfElse stmt final function"){
    val s1 = AssignmentStmt("x", IntValue(6))
    val a1 = AssignmentStmt("x", IntValue(1))
    val a2 = Some(AssignmentStmt("x", IntValue(0)))
    val s2 = IfElseStmt(GTExpression(IntValue(3), IntValue(5)), a1, None)

    val grafo = new NewControlFlowGraph()
    val teste = grafo.finalFG(s2)

    assert(teste == List(a1))
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

    assert(teste == List(s4))
  }


}
