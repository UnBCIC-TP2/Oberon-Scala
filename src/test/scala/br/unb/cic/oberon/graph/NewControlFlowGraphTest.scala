package br.unb.cic.oberon.graph

import br.unb.cic.oberon.AbstractTestSuite
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.cfg.NewControlFlowGraph
import org.scalactic.source.Position
import org.scalatest.BeforeAndAfter

class NewControlFlowGraphTest extends AbstractTestSuite with BeforeAndAfter {

  ignore("Test the labels for statements") {
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

  test("Test new control flow graph for assignment stmt init function") {
    val firstAssigmentStmt = AssignmentStmt("x", IntValue(2))
    val grafo = new NewControlFlowGraph()

    val assignmentStmtInitTest = grafo.init(firstAssigmentStmt)
    assert(assignmentStmtInitTest == AssignmentStmt("x", IntValue(2)))

  }

  test("Test new control flow graph for sequence stmt init function") {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val secondAssignmentStmt = AssignmentStmt("y", IntValue(3))
    val thirdAssignmentStmt = AssignmentStmt("z", IntValue(4))
    val fourthAssignmentStmt = AssignmentStmt("w", IntValue(1))
    val grafo = new NewControlFlowGraph()

    val sequenceStmts: List[Statement] = List(
      firstAssignmentStmt,
      secondAssignmentStmt,
      thirdAssignmentStmt,
      fourthAssignmentStmt
    )

    val sequenceStmtInitTeste = grafo.init(SequenceStmt(sequenceStmts))
    assert(sequenceStmtInitTeste == firstAssignmentStmt)

  }

  test("Test new control flow graph for ifelse stmt init function") {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(1))
    val secondAssignmentStmt = Some(AssignmentStmt("x", IntValue(0)))
    val firstIfElseStmt = IfElseStmt(
      GTExpression(IntValue(3), IntValue(5)),
      firstAssignmentStmt,
      secondAssignmentStmt
    )

    val grafo = new NewControlFlowGraph()
    val ifElseStmtInitTeste = grafo.init(firstIfElseStmt)

    assert(ifElseStmtInitTeste == firstIfElseStmt)
  }

  test("Test new control flow graph for while stmt init function") {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(6))
    val firstWhileStmt =
      WhileStmt(GTExpression(IntValue(3), IntValue(5)), firstAssignmentStmt)

    val grafo = new NewControlFlowGraph()
    val whileStmtInitTeste = grafo.init(firstWhileStmt)

    assert(whileStmtInitTeste == firstWhileStmt)
  }

  test("Test new control flow graph for assignment stmt final function") {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val grafo = new NewControlFlowGraph()

    val assignmentStmtFinalTeste = grafo.finalFG(firstAssignmentStmt)
    assert(assignmentStmtFinalTeste == Set(firstAssignmentStmt))

  }

  test("Test new control flow graph for sequence stmt final function") {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val secondAssignmentStmt = AssignmentStmt("y", IntValue(3))
    val thirdAssignmentStmt = AssignmentStmt("z", IntValue(4))
    val fourthAssignmentStmt = AssignmentStmt("w", IntValue(1))
    val grafo = new NewControlFlowGraph()

    val sequenceStmts: List[Statement] = List(
      firstAssignmentStmt,
      secondAssignmentStmt,
      thirdAssignmentStmt,
      fourthAssignmentStmt
    )

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

  test("Test new control flow graph for IfElse stmt final function") {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(1))
    val secondAssignmentStmt = Some(AssignmentStmt("x", IntValue(0)))
    val firstIfElseStmt = IfElseStmt(
      GTExpression(IntValue(3), IntValue(5)),
      firstAssignmentStmt,
      secondAssignmentStmt
    )

    val grafo = new NewControlFlowGraph()
    val ifElseStmtFinalTeste = grafo.finalFG(firstIfElseStmt)

    assert(
      ifElseStmtFinalTeste == Set(firstAssignmentStmt, secondAssignmentStmt.get)
    )
  }

  test("Test new control flow graph for IfElseIF stmt final function") {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(1))
    val secondAssignmentStmt = Option(AssignmentStmt("x", IntValue(0)))
    val thirdAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val fourthAssignmentStmt = AssignmentStmt("x", IntValue(3))
    val fifthAssignmentStmt = AssignmentStmt("x", IntValue(4))

    val firstExpression = GTExpression(IntValue(3), IntValue(5))

    val firstElifStmt =
      ElseIfStmt(GTExpression(IntValue(1), IntValue(2)), thirdAssignmentStmt)
    val secondElifStmt =
      ElseIfStmt(LTExpression(IntValue(4), IntValue(5)), fourthAssignmentStmt)
    val thirdElifStmt =
      ElseIfStmt(GTExpression(IntValue(10), IntValue(5)), fifthAssignmentStmt)

    val elseIf: List[ElseIfStmt] =
      List(firstElifStmt, secondElifStmt, thirdElifStmt)

    val ifElif = IfElseIfStmt(
      firstExpression,
      firstAssignmentStmt,
      elseIf,
      secondAssignmentStmt
    )

    val grafo = new NewControlFlowGraph()
    val teste = grafo.finalFG(ifElif)

    assert(
      teste == Set(
        firstAssignmentStmt,
        thirdAssignmentStmt,
        fourthAssignmentStmt,
        fifthAssignmentStmt,
        secondAssignmentStmt.get
      )
    )
  }

  test("Test new control flow graph for while stmt final function") {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val secondAssignmentStmt = AssignmentStmt("y", IntValue(3))
    val thirdAssignmentStmt = AssignmentStmt("z", IntValue(4))
    val fourthAssignmentStmt = AssignmentStmt("w", IntValue(1))

    val sequenceStmts: List[Statement] = List(
      firstAssignmentStmt,
      secondAssignmentStmt,
      thirdAssignmentStmt,
      fourthAssignmentStmt
    )

    val whileStmt = WhileStmt(
      GTExpression(IntValue(3), IntValue(5)),
      SequenceStmt(sequenceStmts)
    )

    val grafo = new NewControlFlowGraph()
    val whileStmtFinalTest = grafo.finalFG(whileStmt)

    assert(whileStmtFinalTest == Set(whileStmt))
  }

  test("Test new control flow graph for case stmt final function") {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val secondAssignmentStmt = AssignmentStmt("y", IntValue(3))
    val thirdAssignmentStmt =
      SequenceStmt(List(firstAssignmentStmt, secondAssignmentStmt))
    val fourthAssignmentStmt = AssignmentStmt("a", IntValue(10))
    val fifthAssignmentStmt = AssignmentStmt("b", IntValue(5))
    val sixthAssignmentStmt = AssignmentStmt("z", IntValue(4))

    val firstCase = SimpleCase(IntValue(5), firstAssignmentStmt)
    val secondCase = SimpleCase(IntValue(2), secondAssignmentStmt)
    val thirdCase = SimpleCase(IntValue(4), thirdAssignmentStmt)
    val fourthCase = RangeCase(IntValue(1), IntValue(10), fourthAssignmentStmt)
    val fifthCase = RangeCase(IntValue(2), IntValue(5), fifthAssignmentStmt)

    val cases: List[CaseAlternative] =
      List(firstCase, secondCase, thirdCase, fourthCase, fifthCase)

    val caseStmt = CaseStmt(IntValue(2), cases, Option(sixthAssignmentStmt))

    val grafo = new NewControlFlowGraph()
    val caseStmtFinalTest = grafo.finalFG(caseStmt)

    assert(
      caseStmtFinalTest == Set(
        firstAssignmentStmt,
        secondAssignmentStmt,
        secondAssignmentStmt,
        fourthAssignmentStmt,
        fifthAssignmentStmt,
        sixthAssignmentStmt
      )
    )

  }

  test("Test new control flow graph for assignment stmt flow function") {
    val assignmentStmt = AssignmentStmt("x", IntValue(10))

    val grafo = new NewControlFlowGraph()
    val assignmentStmtFlowTest = grafo.flow(assignmentStmt)

    assert(assignmentStmtFlowTest == Set())
  }

  test("Test new control flow graph for ifelse stmt flow function") {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val secondAssignmentStmt = Option(AssignmentStmt("y", IntValue(3)))
    val ifElse = IfElseStmt(
      GTEExpression(IntValue(2), IntValue(5)),
      firstAssignmentStmt,
      secondAssignmentStmt
    )

    val grafo = new NewControlFlowGraph()
    val ifElseStmtFlowTest = grafo.flow(ifElse)

    assert(
      ifElseStmtFlowTest == Set(
        (ifElse, firstAssignmentStmt),
        (ifElse, secondAssignmentStmt.get)
      )
    )
  }

  test("Test new control flow graph for while stmt flow function") {
    val assignmentStmt = AssignmentStmt("x", IntValue(2))

    val whileStmt =
      WhileStmt(GTExpression(IntValue(3), IntValue(5)), assignmentStmt)

    val grafo = new NewControlFlowGraph()
    val whileStmtFlowTest = grafo.flow(whileStmt)

    assert(
      whileStmtFlowTest == Set(
        (whileStmt, assignmentStmt),
        (assignmentStmt, whileStmt)
      )
    )
  }

  test(
    "Test new control flow graph for sequence stmt flow function - Only Assignments"
  ) {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val secondAssignmentStmt = AssignmentStmt("y", IntValue(3))
    val thirdAssignmentStmt = AssignmentStmt("z", IntValue(4))
    val fourthAssignmentStmt = AssignmentStmt("w", IntValue(1))
    val grafo = new NewControlFlowGraph()

    val sequenceStmts: List[Statement] = List(
      firstAssignmentStmt,
      secondAssignmentStmt,
      thirdAssignmentStmt,
      fourthAssignmentStmt
    )

    val sequenceStmtFlowTeste = grafo.flow(SequenceStmt(sequenceStmts))
    assert(
      sequenceStmtFlowTeste == Set(
        (firstAssignmentStmt, secondAssignmentStmt),
        (secondAssignmentStmt, thirdAssignmentStmt),
        (thirdAssignmentStmt, fourthAssignmentStmt)
      )
    )

  }

  test("Test new control flow graph for sequence stmt flow function") {
    val firstAssignmentStmt = AssignmentStmt("x", IntValue(2))
    val secondAssignmentStmt = AssignmentStmt("y", IntValue(3))
    val thirdAssignmentStmt = AssignmentStmt("z", IntValue(4))
    val fourthAssignmentStmt = AssignmentStmt("w", IntValue(1))

    val thenIfStmt = AssignmentStmt("x", IntValue(2))
    val elseIfStmt = Option(AssignmentStmt("y", IntValue(3)))
    val ifElse = IfElseStmt(
      GTEExpression(IntValue(2), IntValue(5)),
      thenIfStmt,
      elseIfStmt
    )

    val grafo = new NewControlFlowGraph()

    val sequenceStmts: List[Statement] = List(
      firstAssignmentStmt,
      secondAssignmentStmt,
      thirdAssignmentStmt,
      fourthAssignmentStmt,
      ifElse
    )

    val sequenceStmtFlowTeste = grafo.flow(SequenceStmt(sequenceStmts))

    assert(
      sequenceStmtFlowTeste == Set(
        (firstAssignmentStmt, secondAssignmentStmt),
        (secondAssignmentStmt, thirdAssignmentStmt),
        (thirdAssignmentStmt, fourthAssignmentStmt),
        (ifElse, thenIfStmt),
        (ifElse, elseIfStmt.get),
        (fourthAssignmentStmt, ifElse)
      )
    )

  }

  override protected def before(fun: => Any)(implicit pos: Position): Unit =
    Statement.reset()
}
