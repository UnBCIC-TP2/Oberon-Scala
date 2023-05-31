package br.unb.cic.oberon.parser

import br.unb.cic.oberon.AbstractTestSuite
import br.unb.cic.oberon.ir.ast.{DivExpression, _}
import br.unb.cic.oberon.util.Resources
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable.Map
import java.beans.Expression


class ParserCombinatorTestSuite2 extends AbstractTestSuite with Oberon2ScalaParser {


  ignore("Testing the oberon stmt03 code. This module has IF-THEN statement") {
    val module = parseResource("stmts/stmt03.oberon")

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 4)
      case _ => fail("we are expecting three stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    assert(stmts.head == ReadIntStmt("x"))
    assert(stmts(1) == ReadIntStmt("max"))

    // the third stmt must be an IfElseStmt
    stmts(2) match {
      case IfElseStmt(cond, s1, s2) =>
        assert(cond == GTExpression(VarExpression("x"), VarExpression("max")))
        assert(s1 == AssignmentStmt("max", VarExpression("x")))
        assert(s2.isEmpty) // the else stmt is None
      case _ => fail("expecting an if-then stmt")
    }

    assert(stmts(3) == WriteStmt(VarExpression("max")))
  }

  ignore("Testing the oberon stmt04 code. This module has a While statement") {
    val module = parseResource("stmts/stmt04.oberon")

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 4)
      case _ => fail("we are expecting three stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    assert(stmts.head == ReadIntStmt("x"))
    assert(stmts(1) == ReadIntStmt("y"))

    // the third stmt must be an WhileStmt
    stmts(2) match {
      case WhileStmt(cond, stmt) =>
        assert(cond == LTExpression(VarExpression("x"), VarExpression("y")))
        assert(stmt == AssignmentStmt("x", MultExpression(VarExpression("x"), VarExpression("x"))))
      case _ => fail("expecting an if-then stmt")
    }
    assert(stmts(3) == WriteStmt(VarExpression("x")))
  }

  ignore("Testing the oberon stmt11 code. This module has a For statement") {
    val module = parseResource("stmts/stmt11.oberon")

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 2)
      case _ => fail("we are expecting three stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    val code1 = ReadIntStmt("z")
    val add = AddExpression(VarExpression("y"), IntValue(1))
    val code2 = AssignmentStmt("z", DivExpression(VarExpression("z"), add))
    val code3 = WriteStmt(VarExpression("z"))

    assert(stmts.head == ReadIntStmt("x"))

    // the third stmt must be an ForStmt
    stmts(1) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("y", IntValue(0)))
        assert(cond == LTExpression(VarExpression("y"), VarExpression("x")))
        assert(stmt == SequenceStmt(List(code1, code2, code3)))
      case _ => fail("expecting: SequenceStmt(List(ReadIntStmt(z), AssignmentStmt(z,DivExpression(VarExpression(z),AddExpression(VarExpression(y),IntValue(1)))), WriteStmt(VarExpression(z))))")
    }

  }
  ignore("Testing the oberon stmt12 code. This module has a For statement") {
    val module = parseResource("stmts/stmt12.oberon")

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 8)
      case _ => fail("we are expecting three stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]

    val stmts = sequence.stmts

    val multiplica = MultExpression(VarExpression("w"), AddExpression(VarExpression("y"), IntValue(1)))
    val codiguinho = AddExpression(VarExpression("v"), multiplica)
    val codee = ReadIntStmt("w");
    val code = AssignmentStmt("v", codiguinho);

    val code2 = AssignmentStmt("u", AddExpression(VarExpression("u"), VarExpression("w")));

    assert(stmts.head == ReadIntStmt("x"))
    assert(stmts(1) == AssignmentStmt("v", IntValue(0)))

    stmts(2) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("y", IntValue(0)))
        assert(cond == LTExpression(VarExpression("y"), VarExpression("x")))
        assert(stmt == SequenceStmt(List(codee, code)))
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }

    assert(stmts(3) == AssignmentStmt("v", DivExpression(VarExpression("v"), VarExpression("x"))))

    stmts(4) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("z", IntValue(0)))
        assert(cond == LTExpression(VarExpression("z"), VarExpression("x")))
        assert(stmt == SequenceStmt(List(codee, code2)))
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }
    assert(stmts(5) == AssignmentStmt("u", DivExpression(VarExpression("u"), VarExpression("x"))))

    assert(stmts(6) == WriteStmt(VarExpression("v")))
    assert(stmts(7) == WriteStmt(VarExpression("u")))
  }

  ignore("Testing the oberon stmt15 code. This module has a For statement") {
    val module = parseResource("stmts/stmt15.oberon")

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 3)
      case _ => fail("we are expecting three stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    val code = ReadIntStmt("z")
    val adicao = MultExpression(VarExpression("y"), VarExpression("x"))
    val code1 = AssignmentStmt("z", DivExpression(VarExpression("z"), adicao))

    assert(stmts.head == ReadIntStmt("x"))

    // the third stmt must be an ForStmt
    stmts(1) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("y", IntValue(0)))
        assert(cond == LTExpression(VarExpression("y"), VarExpression("x")))
        assert(stmt == SequenceStmt(List(code, code1)))
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }

    assert(stmts(2) == WriteStmt(VarExpression("z")))

  }

  test("Testing the oberon stmt16 code. This module has a For statement") {
    val module = parseResource("stmts/stmt16.oberon")

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 3)
      case _ => fail("we are expecting three stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    val code = AssignmentStmt("z", AddExpression(VarExpression("z"), DivExpression(VarExpression("z"), VarExpression("y"))))
    val code1 = AssignmentStmt("y", SubExpression(VarExpression("y"), IntValue(2)))

    assert(stmts.head == ReadIntStmt("x"))

    // the third stmt must be an ForStmt
    stmts(1) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("y", VarExpression("x")))
        assert(cond == GTExpression(VarExpression("y"), IntValue(0)))
        assert(stmt == SequenceStmt(List(code, code1)))
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }

    assert(stmts(2) == WriteStmt(VarExpression("z")))

  }

  ignore("Testing the oberon stmt20 code. This module implements the factorial function with a case statement") {
    val module = parseResource("stmts/stmt20.oberon")

    assert(module.name == "ProcedureCaseModule")

    // Verifying the factorial procedure
    assert(module.procedures.length == 1)
    val factorial = module.procedures.head

    assert(factorial.name == "factorial")
    assert(factorial.args.length == 1)
    assert(factorial.returnType.getOrElse(None) == IntegerType)

    val factorialStmt = factorial.stmt.asInstanceOf[SequenceStmt].stmts

    assert(factorialStmt.length == 2)

    val factorialCaseStmt = factorialStmt.head.asInstanceOf[CaseStmt]

    // Verifying the case stmt in the factorial procedure
    assert(factorialCaseStmt.exp == VarExpression("n"))
    assert(factorialCaseStmt.cases.length == 2)

    assert(factorialCaseStmt.cases.head == SimpleCase(IntValue(0), ReturnStmt(IntValue(1))))
    assert(factorialCaseStmt.cases(1) == SimpleCase(IntValue(1), ReturnStmt(IntValue(1))))

    factorialCaseStmt.elseStmt.getOrElse(None) match {
      case ReturnStmt(MultExpression(left, right)) => {
        assert(left == VarExpression("n"))

        assert(right == FunctionCallExpression("factorial", List(
          SubExpression(VarExpression("n"), IntValue(1))
        )))
      }

      case _ => fail("Missing an elseStmt in the factorial procedure case")
    }

    assert(factorialStmt(1) == ReturnStmt(MultExpression(VarExpression("n"),
      FunctionCallExpression("factorial", List(
        SubExpression(VarExpression("n"), IntValue(1)))))))
    // End of the factorial procedure verification

    // Verifying the body module statements
    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmts) => succeed
      case _ => fail("Expecting a sequence of statements!")
    }

  }

  ignore("Testing the oberon stmt21 code. This module tests if a number is even with a case statement") {
    val module = parseResource("stmts/stmt21.oberon")

    assert(module.name == "SimpleRangeCaseModule")

    // Verifying variables declarations
    assert(module.variables.length == 2)
    assert(module.variables.head == VariableDeclaration("x", IntegerType))
    assert(module.variables(1) == VariableDeclaration("aux", IntegerType))


    assert(module.stmt.nonEmpty);

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmt) => assert(stmt.length == 5)
      case _ => fail("This module should have 5 statements!")
    }

    // Verifying statements
    val sequenceStmts = module.stmt.get.asInstanceOf[SequenceStmt].stmts

    assert(sequenceStmts.head == ReadIntStmt("x"))
    assert(sequenceStmts(1) == AssignmentStmt("aux", DivExpression(VarExpression("x"), IntValue(2))))
    assert(sequenceStmts(2) == AssignmentStmt("aux", MultExpression(VarExpression("aux"), IntValue(2))))

    // Verifying the case statement

    val caseStmt = sequenceStmts(3).asInstanceOf[CaseStmt]
    val caseAlts = caseStmt.cases

    assert(caseAlts.length == 1)
    assert(caseAlts.head == SimpleCase(VarExpression("x"), AssignmentStmt("aux", IntValue(0))))

    caseStmt.elseStmt.getOrElse(None) match {
      case AssignmentStmt(VarAssignment(varName), exp) => {
        assert(varName == "aux")
        assert(exp == IntValue(1))
      }
      case None => fail("Expected an else on the case statement!")
    }

    // Verifying the write statement
    assert(sequenceStmts(4) == WriteStmt(VarExpression("aux")))

  }


  ignore("Testing the oberon stmt23 code. This module has a while with a case statement") {
    val module = parseResource("stmts/stmt23.oberon")

    assert(module.name == "WhileCaseModule")

    assert(module.variables.length == 2)

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmt) => assert(stmt.length == 3)
      case _ => fail("This module should have a sequence of 3 statements!")
    }

    val sequenceStmts = module.stmt.get.asInstanceOf[SequenceStmt].stmts

    assert(sequenceStmts(0) == AssignmentStmt("x", IntValue(0)))

    val myWhileStmt = sequenceStmts(1).asInstanceOf[WhileStmt];

    assert(myWhileStmt.condition == LTExpression(VarExpression("x"), IntValue(20)))

    myWhileStmt.stmt match {
      case SequenceStmt(stmts) => assert(stmts.length == 2)
      case _ => fail("Expected a sequence of statements in the while statement!")
    }

    val innerCase = myWhileStmt.stmt.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[CaseStmt]

    assert(innerCase.exp == VarExpression("x"))

    assert(innerCase.cases.head == SimpleCase(IntValue(0), AssignmentStmt("sum", IntValue(0))))

    assert(innerCase.cases(1) == RangeCase(IntValue(1), IntValue(9), AssignmentStmt("sum",
      AddExpression(VarExpression("sum"), VarExpression("x")))))

    assert(innerCase.cases(2) == SimpleCase(IntValue(10), SequenceStmt(List(WriteStmt(VarExpression("sum")),
      AssignmentStmt("sum", MultExpression(IntValue(2), IntValue(10)))))))

    assert(innerCase.cases(3) == RangeCase(IntValue(11), IntValue(20), AssignmentStmt("sum", AddExpression(
      VarExpression("sum"), MultExpression(IntValue(2), VarExpression("x"))))))

    assert(innerCase.elseStmt == None)

    assert(myWhileStmt.stmt.asInstanceOf[SequenceStmt].stmts(1).asInstanceOf[AssignmentStmt] ==
      AssignmentStmt("x", AddExpression(VarExpression("x"), IntValue(1))))

    assert(sequenceStmts(2) == WriteStmt(VarExpression("sum")))

  }


  ignore("Testing the oberon stmt24 code. This module has a while with a case statement") {
    val module = parseResource("stmts/stmt24.oberon")

    assert(module.name == "WhileCaseModule")

    assert(module.variables.length == 3)

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmt) => assert(stmt.length == 4)
      case _ => fail("This module should have a sequence of 4 statements!")
    }

    val sequenceStmts = module.stmt.get.asInstanceOf[SequenceStmt].stmts

    assert(sequenceStmts.head == AssignmentStmt("x", IntValue(0)))

    assert(sequenceStmts(1) == ReadIntStmt("lim"))

    val myWhileStmt = sequenceStmts(2).asInstanceOf[WhileStmt];

    assert(myWhileStmt.condition == LTExpression(VarExpression("x"), VarExpression("lim")))

    myWhileStmt.stmt match {
      case SequenceStmt(stmts) => assert(stmts.length == 2)
      case _ => fail("Expected a sequence of statements in the while statement!")
    }

    val innerCase = myWhileStmt.stmt.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[CaseStmt]

    assert(innerCase.exp == VarExpression("x"))

    assert(innerCase.cases(0) == SimpleCase(IntValue(0), AssignmentStmt("sum", IntValue(0))))

    assert(innerCase.elseStmt.getOrElse(None) == AssignmentStmt("sum", AddExpression(
      VarExpression("sum"), VarExpression("x"))))

    assert(myWhileStmt.stmt.asInstanceOf[SequenceStmt].stmts(1).asInstanceOf[AssignmentStmt] ==
      AssignmentStmt("x", AddExpression(VarExpression("x"), IntValue(1))))

    assert(sequenceStmts(3) == WriteStmt(VarExpression("sum")))

  }

}