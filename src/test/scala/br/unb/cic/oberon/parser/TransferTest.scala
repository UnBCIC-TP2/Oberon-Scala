package br.unb.cic.oberon.parser

import br.unb.cic.oberon.AbstractTestSuite
import br.unb.cic.oberon.ir.ast.{DivExpression, _}
import br.unb.cic.oberon.util.Resources
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable.Map
import java.beans.Expression
import java.nio.file.{Files, Paths}

class ParserCombinatorTestSuite2 extends AbstractTestSuite with Oberon2ScalaParser {


  test("Testing the oberon stmt03 code. This module has IF-THEN statement") {
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
        assert(s1 == SequenceStmt(List(AssignmentStmt("max", VarExpression("x")),AssignmentStmt("x",IntValue(0)))))
        assert(s2.isEmpty) // the else stmt is None
      case _ => fail("expecting an if-then stmt")
    }

    assert(stmts(3) == WriteStmt(VarExpression("max")))
  }

  test("Testing the oberon stmt04 code. This module has a While statement") {
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

  test("Testing the oberon stmt11 code. This module has a For statement") {
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
  test("Testing the oberon stmt12 code. This module has a For statement") {
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

  test("Testing the oberon stmt15 code. This module has a For statement") {
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

  test("Testing the oberon stmt21 code. This module tests if a number is even with a case statement") {
    val module = parseResource("stmts/stmt21.oberon") //erro de comentario

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

  test("Testing the oberon stmt24 code. This module has a while with a case statement") {
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

  test("Testing the oberon stmt30 code. This module has IF-ELSIF statement") {
    val module = parseResource("stmts/stmt30.oberon")

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

    assert(stmts.head == ReadIntStmt("x"))

    stmts(1) match {
      case IfElseIfStmt(cond, thenStmt, elseIfs, elseStmt) =>
        assert(cond == LTExpression(VarExpression("x"), IntValue(5)))
        assert(thenStmt == AssignmentStmt("y", IntValue(1)))
        assert(elseIfs(0).condition == LTExpression(VarExpression("x"), IntValue(7)))
        assert(elseIfs(0).thenStmt == AssignmentStmt("y", IntValue(2)))
        assert(elseIfs(1).condition == LTExpression(VarExpression("x"), IntValue(9)))
        assert(elseIfs(1).thenStmt == AssignmentStmt("y", IntValue(3)))
        assert(elseStmt.contains(AssignmentStmt("y", IntValue(4))))
      case _ => fail("expecting an if-then stmt")
    }

    assert(stmts(2) == WriteStmt(VarExpression("y")))
  }

  test("Testing the oberon IfElseIfStmt09 code. This module has IF-ELSIF statement without ELSE stmt") {
    val module = parseResource("stmts/IfElseIfStmt09.oberon")

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

    assert(stmts.head == ReadIntStmt("x"))

    stmts(1) match {
      case IfElseIfStmt(cond, thenStmt, elseIfs, elseStmt) =>
        assert(cond == GTExpression(VarExpression("x"), IntValue(1)))
        assert(thenStmt == AssignmentStmt("y", IntValue(0)))
        assert(elseIfs(0).condition == LTExpression(VarExpression("x"), IntValue(3)))
        assert(elseIfs(0).thenStmt == AssignmentStmt("y", IntValue(2)))
        assert(elseStmt == None)
      case _ => fail("expecting an if-then stmt")
    }

    assert(stmts(2) == WriteStmt(VarExpression("y")))
  }


  test("Testing the oberon IfElseIfStmt10 code. This module has IF-ELSIF statement with ten ELSEIF stmts") {
    val module = parseResource("stmts/IfElseIfStmt10.oberon")

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

    assert(stmts.head == ReadIntStmt("x"))

    stmts(1) match {
      case IfElseIfStmt(cond, thenStmt, elseIfs, elseStmt) =>
        assert(cond == LTExpression(VarExpression("x"), IntValue(5)))
        assert(thenStmt == AssignmentStmt("y", IntValue(1)))
        assert(elseIfs(0).condition == LTExpression(VarExpression("x"), IntValue(7)))
        assert(elseIfs(0).thenStmt == AssignmentStmt("y", IntValue(2)))
        assert(elseIfs(1).condition == LTExpression(VarExpression("x"), IntValue(9)))
        assert(elseIfs(1).thenStmt == AssignmentStmt("y", IntValue(3)))
        assert(elseIfs(2).condition == LTExpression(VarExpression("x"), IntValue(11)))
        assert(elseIfs(2).thenStmt == AssignmentStmt("y", IntValue(4)))
        assert(elseIfs(3).condition == LTExpression(VarExpression("x"), IntValue(13)))
        assert(elseIfs(3).thenStmt == AssignmentStmt("y", IntValue(5)))
        assert(elseIfs(4).condition == LTExpression(VarExpression("x"), IntValue(15)))
        assert(elseIfs(4).thenStmt == AssignmentStmt("y", IntValue(6)))
        assert(elseIfs(5).condition == LTExpression(VarExpression("x"), IntValue(17)))
        assert(elseIfs(5).thenStmt == AssignmentStmt("y", IntValue(7)))
        assert(elseIfs(6).condition == LTExpression(VarExpression("x"), IntValue(19)))
        assert(elseIfs(6).thenStmt == AssignmentStmt("y", IntValue(8)))
        assert(elseIfs(7).condition == LTExpression(VarExpression("x"), IntValue(21)))
        assert(elseIfs(7).thenStmt == AssignmentStmt("y", IntValue(9)))
        assert(elseIfs(8).condition == LTExpression(VarExpression("x"), IntValue(23)))
        assert(elseIfs(8).thenStmt == AssignmentStmt("y", IntValue(10)))
        assert(elseIfs(9).condition == EQExpression(VarExpression("x"), IntValue(25)))
        assert(elseIfs(9).thenStmt == AssignmentStmt("y", IntValue(11)))
        assert(elseStmt == Some(AssignmentStmt("y", IntValue(12))))
      case _ => fail("expecting an if-then stmt")
    }

    assert(stmts(2) == WriteStmt(VarExpression("y")))
  }

  test("Testing the oberon procedure03 code. This module implements a fatorial function") {
    val module = parseResource("procedures/procedure03.oberon")

    assert(module.name == "Factorial")

    assert(module.procedures.size == 1)
    assert(module.stmt.isDefined)

    val procedure = module.procedures.head

    assert(procedure.name == "factorial")
    assert(procedure.args.size == 1)
    assert(procedure.returnType == Some(IntegerType))

    procedure.stmt match {
      case SequenceStmt(_) => succeed
      case _ => fail("expecting a sequence of stmts")
    }

    val SequenceStmt(stmts) = procedure.stmt // pattern matching...
    assert(stmts.size == 2)

    assert(stmts.head == IfElseStmt(EQExpression(VarExpression("i"), IntValue(1)), ReturnStmt(IntValue(1)), None))
    assert(stmts(1) == ReturnStmt(MultExpression(VarExpression("i"), FunctionCallExpression("factorial", List(SubExpression(VarExpression("i"), IntValue(1)))))))

    module.stmt.get match {
      case SequenceStmt(ss) => {
        assert(ss.head == AssignmentStmt("res", FunctionCallExpression("factorial", List(IntValue(5)))))
        assert(ss(1) == WriteStmt(VarExpression("res")))
      }
      case _ => fail("expecting a sequence of stmts: an assignment and a print stmt (Write)")
    }
  }


  test("Testing the oberon ArrayAssignmentStmt02 code. This module has an array assignment in IF-THEN") {
    val module = parseResource("stmts/ArrayAssignmentStmt02.oberon")

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 4)
      case _ => fail("we are expecting four stmts in the main block")
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
        assert(s1 == new AssignmentStmt(ArrayAssignment(VarExpression("array"), IntValue(0)), VarExpression("x")))
        assert(s2.isEmpty) // the else stmt is None
      case _ => fail("expecting an if-then stmt")
    }

    assert(stmts(3) == WriteStmt(VarExpression("max")))
  }

  test("Testing the oberon stmt32 code. This module has some user types declarations") {
    val module = parseResource("stmts/stmt32.oberon")
    assert(module.name == "UserTypeModule")
    assert(!module.stmt.isDefined)
    assert(module.userTypes.size == 4)
  }

  test("Testing the oberon stmt34 code. This module has a record and array type declarations") {
    val module = parseResource("stmts/stmt34.oberon")
    assert(module.name == "UserTypeModule")
    assert(!module.stmt.isDefined)
    assert(module.userTypes.size == 5)
    assert(module.userTypes(0).baseType == ArrayType(15, IntegerType))

  }

  test("Testing the oberon ExpressionNameParser2 code. This module tests if the parser can translate operations with expression name") {
    val module = parseResource("stmts/ExpressionNameParser2.oberon")
    assert(module.name == "ExpressionNameModule")
    assert(module.stmt.isDefined)
    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.size == 2)
    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].right.isInstanceOf[FieldAccessExpression])
    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].left.asInstanceOf[IntValue].value == 1)

  }


  test("Testing the oberon ExpressionNameParser3 code. This module tests if the parser can see expression name with more than two words") {
    val module = parseResource("stmts/ExpressionNameParser3.oberon")

    assert(module.name == "ExpressionNameModule")
    assert(module.stmt.isDefined)
    assert(module.stmt.get.asInstanceOf[WriteStmt].expression.isInstanceOf[FieldAccessExpression])
    assert(module.stmt.get.asInstanceOf[WriteStmt].expression.asInstanceOf[FieldAccessExpression].exp.isInstanceOf[FieldAccessExpression])
    assert(module.stmt.get.asInstanceOf[WriteStmt].expression.asInstanceOf[FieldAccessExpression].name.isInstanceOf[String])
  }
  
  test("Testing the oberon ExpressionNameParser4 code. This module tests if the parser can translate operations with two expression names") {
    val module = parseResource("stmts/ExpressionNameParser4.oberon")

    assert(module.name == "ExpressionNameModule")
    assert(module.stmt.isDefined)
    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.size == 2)
    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].left.isInstanceOf[FieldAccessExpression])
    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].right.isInstanceOf[FieldAccessExpression])

  }
  test("Testing the oberon ExpressionNameParser5 code. This module tests if the parser can translate different operations with type record declarations") {
    val module = parseResource("stmts/ExpressionNameParser5.oberon")

    assert(module.name == "ExpressionNameModule")

    assert(module.stmt.isDefined)

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.size == 2)

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].left.isInstanceOf[FieldAccessExpression])

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].right.asInstanceOf[MultExpression].left.isInstanceOf[FieldAccessExpression])

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].right.asInstanceOf[MultExpression].right.isInstanceOf[FieldAccessExpression])


  }

  test("Testing the oberon userTypeSimple01 code module. This module has a record type declaration, with invalid declarations") {
    val module = parseResource("simple/userTypeSimple01.oberon")

    assert(module.name == "UserTypeModule")

    assert(module.userTypes.length == 2)
  }

  test("Testing the oberon userTypeSimple03 code module. This module has a record type declaration, with invalid declarations") {
    val module = parseResource("simple/userTypeSimple03.oberon")

    assert(module.name == "test")

    assert(module.userTypes.length == 3)

    val typeList = module.userTypes

    val test_array = ArrayType(5, BooleanType)

    val tipo1 = RecordType(List(
      VariableDeclaration("num", IntegerType),
      VariableDeclaration("numum", ReferenceToUserDefinedType("test_array"))))

    val tipo2 = RecordType(List(VariableDeclaration("num_record", ReferenceToUserDefinedType("tipo1"))))

    val typetoTest = List(test_array, tipo1, tipo2)

    var it: Int = 0

    typeList.foreach(t => {
      assert(t.baseType == typetoTest(it))
      it += 1
    })

  }
  test("Testing the oberon userTypeSimple05 code module. This module has some user type declarations with a variables using theses types") {
    val module = parseResource("simple/userTypeSimple05.oberon")

    assert(module.name == "UserTypeModule")

    assert(module.userTypes.length == 2)

    assert(module.variables.length == 3)

    val varList = List(VariableDeclaration("x", ReferenceToUserDefinedType("simple")),
      VariableDeclaration("y", ReferenceToUserDefinedType("simple")),
      VariableDeclaration("z", ReferenceToUserDefinedType("complicated")))

    var it: Int = 0
    module.variables.foreach(v => {
      assert(v == varList(it))
      it += 1
    })
  }
  test("Testing the oberon userTypeSimple06 code module. This module has a record and array type declaration") {
    val module = parseResource("simple/userTypeSimple06.oberon")

    assert(module.name == "test_ando")

    assert(module.userTypes.length == 3)

    val typeList = module.userTypes

    val cheesewithbread = ArrayType(10, IntegerType)

    val cheesewithoutbread = RecordType(List(
      VariableDeclaration("var1", IntegerType),
      VariableDeclaration("var2", ReferenceToUserDefinedType("cheesewithbread"))))

    val cheesewithhalfabread = ArrayType(100000, ReferenceToUserDefinedType("cheesewithoutbread"))

    val typetoTest = List(cheesewithbread, cheesewithoutbread, cheesewithhalfabread)

    var it: Int = 0

    typeList.foreach(t => {
      assert(t.baseType == typetoTest(it))
      it += 1
    })

  }
  test("Testing the oberon userTypeSimple02 code module. This module has array and record type declarations") {
    val module = parseResource("simple/userTypeSimple02.oberon")
    // val sequenceStmts = module.stmt.get.asInstanceOf[SequenceStmt].stmts

    assert(module.name == "test")
    assert(module.stmt.get.asInstanceOf[ReadIntStmt] == ReadIntStmt("x"))

    assert(module.userTypes.length == 2)

    val typeList = module.userTypes

    val halls = RecordType(List(VariableDeclaration("integrante", BooleanType),
      VariableDeclaration("matricula", IntegerType)))

    val halls_array = ArrayType(9, ReferenceToUserDefinedType("HALLS"))

    val typetoTest = List(halls, halls_array)

    var it: Int = 0

    typeList.foreach(t => {
      assert(t.baseType == typetoTest(it))
      it += 1
    })

  }

  test("Testing module F oberon import module feature. Alias and module name") {
    val moduleF = parseResource("imports/F.oberon")

    val expectedSet = Set("A")
    assert(moduleF.submodules == expectedSet)
  }

  test("Testing module H oberon import module feature. A := aliasA, C := aliasC, D") {
    val moduleH = parseResource("imports/H.oberon")

    val expectedSet = Set("A", "C", "D")
    assert(expectedSet == moduleH.submodules)
  }

  test("Reading new types") {
    val path = Paths.get(getClass.getClassLoader.getResource("aritmetic/aritmetic35.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = parseAbs( parse(oberonParser ,content))

    assert(module.name == "SimpleModule")

    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts
    assert(stmts.head == ReadLongRealStmt("v"))
    assert(stmts(1) == ReadRealStmt("w"))
    assert(stmts(2) == ReadLongIntStmt("x"))
    assert(stmts(3) == ReadIntStmt("y"))
    assert(stmts(4) == ReadShortIntStmt("z"))
  }

  ignore("Testing the parser for simple expressions") {
    val intValue1 = "1"
    val sum = "x + 4"

    val exp1 = parseAbs(parse(int, intValue1))

    assert(exp1 == REPLExpression(IntValue(1)))

    val exp2 = ScalaParser.parserREPL(sum)
    assert(exp2 == REPLExpression(AddExpression(VarExpression("x"), IntValue(4))))
  }
}