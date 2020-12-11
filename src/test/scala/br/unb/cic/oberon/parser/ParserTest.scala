package br.unb.cic.oberon.parser

import java.nio.file.{Files, Paths}

import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ast._

class ParserTestSuite extends AnyFunSuite {

  test("Testing the oberon simple01 code") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 1)
    assert(module.constants.head == Constant("x", IntValue(5)))
  }

  test("Testing the oberon simple02 code. This module has one constants and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 1)
    assert(module.constants.head == Constant("x", IntValue(5)))
    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }

  test("Testing the oberon simple03 code. This module has three constants and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple03.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 3)
    assert(module.constants.head == Constant("x", IntValue(5)))
    assert(module.constants(1) == Constant("y", IntValue(10)))
    assert(module.constants(2) == Constant("z", BoolValue(true)))

    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }

  test("Testing the oberon simple04 code. This module has three constants, a sum, and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple04.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 3)
    assert(module.constants.head == Constant("x", IntValue(5)))
    assert(module.constants(1) == Constant("y", IntValue(10)))
    assert(module.constants(2) == Constant("z", AddExpression(IntValue(5), IntValue(10))))


    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }

  test("Testing the oberon simple05 code. This module has one constant, a multiplication, and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple05.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 1)
    assert(module.constants.head == Constant("z", MultExpression(IntValue(5), IntValue(10))))


    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }


  test("Testing the oberon simple06 code. This module has one constants, complex expression, and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple06.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 1)
    assert(module.constants.head == Constant("z", AddExpression(IntValue(5), MultExpression(IntValue(10), IntValue(3)))))


    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }

  test("Testing the oberon simple07 code. This module has two constants, a complex expression, and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple07.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 2)
    assert(module.constants.head == Constant("x", AddExpression(IntValue(5), MultExpression(IntValue(10), IntValue(3)))))
      assert(module.constants(1) == Constant("y",
        AddExpression(IntValue(5),
         DivExpression(
           MultExpression(IntValue(10), IntValue(3)),
           IntValue(5)))))

    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }

  test("Testing the oberon simple08 code. This module has three constants, a boolean expresson, and two variables") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple08.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 3)
    assert(module.constants.head == Constant("x", BoolValue(false)))
    assert(module.constants(1) == Constant("y", BoolValue(true)))
    assert(module.constants(2) == Constant("z", AndExpression(BoolValue(true), BoolValue(false))))
  }

  test("Testing the oberon simple09 code. This module has one constant and an expression involving both 'and' and 'or'") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple09.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")
    assert(module.constants.size == 1)
    assert(module.constants.head == Constant("z", OrExpression(AndExpression(BoolValue(true), BoolValue(false)), BoolValue(false))))
  }

    test("Testing the oberon arrayIndex01 module. This module has a ArrayIndex") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/arrayIndex01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.variables.size == 1)
    assert(module.variables.head == VariableDeclaration("x", IntegerType))

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 2)
      case _ => fail("we are expecting two stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    assert(stmts.head == AssignmentStmt("x", ArraySubscript(VarExpression("a"),AddExpression(IntValue(2), IntValue(6)))))
    assert(stmts(1) == WriteStmt(VarExpression("x")))
  }

  test("Testing the oberon arrayIndex02 code. This module has a ArrayIndex") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/arrayIndex02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.variables.size == 1)
    assert(module.variables.head == VariableDeclaration("x", IntegerType))

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 2)
      case _ => fail("we are expecting two stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    assert(stmts.head == AssignmentStmt("x", ArraySubscript(VarExpression("a"),IntValue(8))))
    assert(stmts(1) == WriteStmt(VarExpression("x")))
  }

  test("Testing the oberon stmt01 code. This module has a block of three statements") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
    assert(stmts(1) == ReadIntStmt("y"))
    assert(stmts(2) == WriteStmt(AddExpression(VarExpression("x"), VarExpression("y"))))
  }

  test("Testing the oberon stmt02 code. This module has a block of four statements") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
    assert(stmts(2) == AssignmentStmt("z", AddExpression(VarExpression("x"), VarExpression("y"))))
    assert(stmts(3) == WriteStmt(VarExpression("z")))
  }

  test("Testing the oberon stmt03 code. This module has IF-THEN statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt03.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
        assert(cond == GTExpression(VarExpression("x"),VarExpression("max")))
        assert(s1 == AssignmentStmt("max",VarExpression("x")))
        assert(s2.isEmpty) // the else stmt is None
      case _ => fail("expecting an if-then stmt")
    }

    assert(stmts(3) == WriteStmt(VarExpression("max")))
  }

  test("Testing the oberon stmt04 code. This module has a While statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt04.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
        assert(cond == LTExpression(VarExpression("x"),VarExpression("y")))
        assert(stmt == AssignmentStmt("x", MultExpression(VarExpression("x"), VarExpression("x"))))
      case _ => fail("expecting an if-then stmt")
    }
    assert(stmts(3) == WriteStmt(VarExpression("x")))
  }

  test("Testing the oberon stmt06 code. This module has a simple case statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt06.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(!module.stmt.isEmpty)
    

    module.stmt.getOrElse(false) match {
      case SequenceStmt(stmts) => assert(stmts.length == 3)
      case _ => fail("Expected a sequence of 3 statements!!")
    }

    val sequenceStmts = module.stmt.get.asInstanceOf[SequenceStmt].stmts;

    assert(sequenceStmts(0) == ReadIntStmt("xs"))

    val myCaseStmt = sequenceStmts(1).asInstanceOf[CaseStmt]

    assert(myCaseStmt.exp == VarExpression("xs"))

    assert(myCaseStmt.cases.length == 4)

    var caseLabel = 1
    var caseValAssigment = 5
    myCaseStmt.cases.foreach(miniCase => {

      val _miniCase = miniCase.asInstanceOf[SimpleCase]

      assert(_miniCase.condition == IntValue(caseLabel))
      assert(_miniCase.stmt == AssignmentStmt("xs", IntValue(caseValAssigment)))
      caseLabel += 1
      caseValAssigment *= 2
    })

    myCaseStmt.elseStmt.getOrElse(false) match {
      case AssignmentStmt(varName, exp) => {
        assert(varName == "xs")
        assert(exp == IntValue(0))
      }
      case _ => fail("Expected an else on the case statement!")
    }
    assert(sequenceStmts(2) == WriteStmt(VarExpression("xs")))

  }
  
  test("Testing the oberon stmt07 code. This module has a For statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt07.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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

    // the third stmt must be an ForStmt
    stmts(1) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("y", IntValue(0)))
        assert(cond == LTExpression(VarExpression("y"), VarExpression("x")))
        assert(stmt == AssignmentStmt("z", AddExpression(VarExpression("z"), VarExpression("y"))))
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }

    assert(stmts(2) == WriteStmt(VarExpression("z")))

  }

  test("Testing the oberon stmt08 code. This module has a For statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt08.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
    
    val code = AssignmentStmt("k", AddExpression(VarExpression("z"), VarExpression("x")))

    assert(stmts.head == ReadIntStmt("y"))

    stmts(1) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("x", IntValue(0)))
        assert(cond == LTExpression(VarExpression("x"),VarExpression("y")))
        assert(stmt == ForStmt(AssignmentStmt("z", IntValue(0)), LTExpression(VarExpression("z"),VarExpression("y")), code))
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }
 
    
    assert(stmts(2) == WriteStmt(VarExpression("k")))

  }

  test("Testing the oberon stmt09 code. This module has a For statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt09.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 2)
      case _ => fail("we are expecting two stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]

    val stmts = sequence.stmts

    // the third stmt must be an ForStmt
    stmts.head match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("y", IntValue(0)))
        assert(cond == LTExpression(VarExpression("y"), IntValue(10)))
        assert(stmt == AssignmentStmt("z", AddExpression(VarExpression("z"), VarExpression("y"))))
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }

    assert(stmts(1) == WriteStmt(VarExpression("z")))

  }
  
  test("Testing the oberon stmt10 code. This module has a For statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt10.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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

    val code = AssignmentStmt("y", AddExpression(VarExpression("y"), IntValue(2)));
    val code2 = AssignmentStmt("z", AddExpression(VarExpression("z"), VarExpression("y")));

    assert(stmts.head == ReadIntStmt("x"))

    // the third stmt must be an ForStmt
    stmts(1) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("y", IntValue(0)))
        assert(cond == LTExpression(VarExpression("y"),VarExpression("x")))
        assert(stmt == SequenceStmt(List(code, code2)))
      case _ => fail("expecting an assigment stmt and if-then stmt") 
    }

    assert(stmts(2) == WriteStmt(VarExpression("z")))

  }
  
  ignore("Testing the oberon stmt11 code. This module has a For statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt11.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
    val adicao = AddExpression(VarExpression("y"), IntValue(1))
    val code1 = AssignmentStmt("z", DivExpression(VarExpression("z"), adicao))
    val code2 = WriteStmt(VarExpression("z"))

    assert(stmts.head == ReadIntStmt("x"))

    // the third stmt must be an ForStmt
    stmts(1) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("y", IntValue(0)))
        assert(cond == LTExpression(VarExpression("y"),VarExpression("x")))
        assert(stmt == SequenceStmt(List(code, code1, code2)))
      case _ => fail("expecting an assigment stmt and if-then stmt") 
    }

  }

  test("Testing the oberon stmt12 code. This module has a For statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt12.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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

    val code2 = AssignmentStmt("u", AddExpression(VarExpression("u"),VarExpression("w")));

    assert(stmts.head == ReadIntStmt("x"))
    assert(stmts(1) == AssignmentStmt("v", IntValue(0)))

    stmts(2) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("y", IntValue(0)))
        assert(cond == LTExpression(VarExpression("y"),VarExpression("x")))
        assert(stmt == SequenceStmt(List(codee, code)))
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }
    
    assert(stmts(3) == AssignmentStmt("v", DivExpression(VarExpression("v"), VarExpression("x"))))

    stmts(4) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("z", IntValue(0)))
        assert(cond == LTExpression(VarExpression("z"),VarExpression("x")))
        assert(stmt == SequenceStmt(List(codee, code2)))
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }
    assert(stmts(5) == AssignmentStmt("u", DivExpression(VarExpression("u"), VarExpression("x")))) 

    assert(stmts(6) == WriteStmt(VarExpression("v")))
    assert(stmts(7) == WriteStmt(VarExpression("u")))
  }

  test("Testing the oberon stmt13 code. This module has a For statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt13.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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

    // the third stmt must be an ForStmt
    stmts(1) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("y", VarExpression("x")))
        assert(cond == LTExpression(VarExpression("y"), IntValue(100)))
        assert(stmt == AssignmentStmt("y", MultExpression(VarExpression("y"), VarExpression("y"))))
      case _ => fail("expecting an assigment stmt and if-then stmt") 
    }

    assert(stmts(2) == WriteStmt(VarExpression("y")))

  }

  test("Testing the oberon stmt14 code. This module has a For statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt14.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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

    val code = AssignmentStmt("y", SubExpression(VarExpression("y"), IntValue(2)));
    val code2 = WriteStmt(VarExpression("y"));

    assert(stmts.head == ReadIntStmt("x"))

    // the third stmt must be an ForStmt
    stmts(1) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("y", VarExpression("x")))
        assert(cond == GTExpression(VarExpression("y"), IntValue(0)))
        assert(stmt == SequenceStmt(List(code, code2)))
      case _ => fail("expecting an assigment stmt and if-then stmt") 
    }

  }

   test("Testing the oberon stmt15 code. This module has a For statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt15.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
        assert(cond == LTExpression(VarExpression("y"),VarExpression("x")))
        assert(stmt == SequenceStmt(List(code, code1)))
      case _ => fail("expecting an assigment stmt and if-then stmt") 
    }

     assert(stmts(2) == WriteStmt(VarExpression("z")))

  }

  test("Testing the oberon stmt16 code. This module has a For statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt16.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
        assert(cond == GTExpression(VarExpression("y"),IntValue(0)))
        assert(stmt == SequenceStmt(List(code, code1)))
      case _ => fail("expecting an assigment stmt and if-then stmt") 
    }

     assert(stmts(2) == WriteStmt(VarExpression("z")))

  }

  test("Testing the oberon stmt17 code. This module has a case statement with range cases, two variabels, read and write statements") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt17.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "RangeCaseModule")

    // Verifying variables declarations
    assert(module.variables.length == 2)
    assert(module.variables.head == VariableDeclaration("x", IntegerType))
    assert(module.variables(1) == VariableDeclaration("y", IntegerType))

    assert(module.stmt.nonEmpty);

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmts) => assert(stmts.length == 4)
      case _ => fail("This module should have 4 statements!")
    }

    // Verifying the statements
    val sequenceStmts = module.stmt.get.asInstanceOf[SequenceStmt].stmts

    assert(sequenceStmts.head == AssignmentStmt("y", IntValue(0)))
    assert(sequenceStmts(1) == ReadIntStmt("x"))

    val caseStmt = sequenceStmts(2).asInstanceOf[CaseStmt]

    // Verifying the caseStmt properties
    assert(caseStmt.exp == VarExpression("x"))
    assert(caseStmt.cases.length == 2)
    assert(caseStmt.elseStmt.isEmpty)

    // Verifying the caseAlternatives in the case
    val caseAlts = caseStmt.cases

    assert(caseAlts.length == 2)
    assert(caseAlts.head == RangeCase(IntValue(0), IntValue(9),
      AssignmentStmt("y", MultExpression(IntValue(2), VarExpression("x")))))
    assert(caseAlts(1) == RangeCase(IntValue(10), IntValue(20),
      AssignmentStmt("y", MultExpression(IntValue(4), VarExpression("x")))))

    // Verifying the last statement
    assert(sequenceStmts(3) == WriteStmt(VarExpression("y")))

  } 

  test("Testing the oberon stmt18 code. this module has a simple case statement with range case, reading min and max") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt18.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleRangeCaseModule")

    // Verifying variables declarations
    assert(module.variables.length == 3)
    assert(module.variables.head == VariableDeclaration("xs", IntegerType))
    assert(module.variables(1) == VariableDeclaration("min", IntegerType))
    assert(module.variables(2) == VariableDeclaration("max", IntegerType))
    
    assert(module.stmt.nonEmpty)

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmts) => assert(stmts.length == 5)
      case _ => fail("This module should have 5 statements!")
    }

    // Verifying the statements
    val sequenceStmts = module.stmt.get.asInstanceOf[SequenceStmt].stmts

    assert(sequenceStmts.head == ReadIntStmt("xs"))
    assert(sequenceStmts(1) == ReadIntStmt("min"))
    assert(sequenceStmts(2) == ReadIntStmt("max"))

    val caseStmt = sequenceStmts(3).asInstanceOf[CaseStmt]

    // Verifying the caseStmt properties
    assert(caseStmt.exp == VarExpression("xs"))
    
    assert(caseStmt.cases.length == 3)

    // Verifying the caseAlternatives in the case
    val caseAlts = caseStmt.cases

    assert(caseAlts.length == 3)
    assert(caseAlts.head == SimpleCase(IntValue(1), AssignmentStmt("xs",IntValue(5))))
    assert(caseAlts(1) == SimpleCase(IntValue(2), AssignmentStmt("xs", IntValue(10))))
    assert(caseAlts(2) == RangeCase(VarExpression("min"), VarExpression("max"), AssignmentStmt("xs", IntValue(20))))
      
    caseStmt.elseStmt.getOrElse(false) match {
      case AssignmentStmt(varName, exp) => {
        assert(varName == "xs")
        assert(exp == IntValue(0))
      }
      case _ => fail("Expected an else on the case statement!")
    }
    assert(sequenceStmts(4) == WriteStmt(VarExpression("xs")))
  
  }

  test("Testing the oberon stmt19 code. This module has three variabels, two case statements, an read and a write statements") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt19.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "CaseModule")

    // Verifying variables declarations
    assert(module.variables.length == 3)
    assert(module.variables(0) == VariableDeclaration("x", IntegerType))
    assert(module.variables(1) == VariableDeclaration("y", IntegerType))
    assert(module.variables(2) == VariableDeclaration("z", IntegerType))

    // Verifying statements 
    assert(module.stmt.nonEmpty)

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmt) => assert(stmt.length == 5)
      case _ => fail("This module should have 5 statements!")
    }

    val sequenceStmts = module.stmt.get.asInstanceOf[SequenceStmt].stmts

    assert(sequenceStmts(0) == ReadIntStmt("x"))
    assert(sequenceStmts(1) == AssignmentStmt("y", IntValue(1)))

    // Verifying the first case statement
    val caseStmt1 = sequenceStmts(2).asInstanceOf[CaseStmt]
    assert(caseStmt1.exp == VarExpression("x"))

    var caseSum = 5
    var caseLabel = 5
    caseStmt1.cases.foreach(_miniCase => {
      val miniCase = _miniCase.asInstanceOf[SimpleCase]
      
      assert(miniCase == SimpleCase(IntValue(caseLabel),
        AssignmentStmt("y", AddExpression(VarExpression("x"), IntValue(caseSum)))))
        
      caseSum += 5
      caseLabel *= 2
    })

    caseStmt1.elseStmt.getOrElse(None) match {
      case AssignmentStmt(varName, exp) => {
        assert(varName == "y")
        assert(exp == IntValue(41))
      }
      case None => fail("Expected an else on the first case statement!") 
    }

    // Verifying the second case statement
    val caseStmt2 = sequenceStmts(3).asInstanceOf[CaseStmt]
    assert(caseStmt2.exp == VarExpression("y"))

    var minCaseValue = 1; var maxCaseValue = 5; var assignmentValue = 5
    caseStmt2.cases.foreach(_miniCase => {
      val miniCase = _miniCase.asInstanceOf[RangeCase]

      assert(miniCase == RangeCase(IntValue(minCaseValue), IntValue(maxCaseValue),
        AssignmentStmt("z", IntValue(assignmentValue))))

      maxCaseValue *= 2
      minCaseValue = maxCaseValue / 2 + 1
      assignmentValue *= 2
    })

    caseStmt2.elseStmt.getOrElse(None) match {
      case AssignmentStmt(varName, exp) => {
        assert(varName == "z")
        assert(exp == IntValue(1))
      }
      case None => fail("Expected an else on the second case statement!") 
    }

    assert(sequenceStmts(4) == WriteStmt(VarExpression("z")))

  }

  test("Testing the oberon stmt20 code. This module implements the factorial function with a case statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt20.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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

  test("Testing the oberon stmt21 code. This module tests if a number is even with a case statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt21.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
    assert(caseAlts.head == SimpleCase(VarExpression("x"), AssignmentStmt("aux",IntValue(0))))

    caseStmt.elseStmt.getOrElse(None) match {
      case AssignmentStmt(varName, exp) => {
        assert(varName == "aux")
        assert(exp == IntValue(1))
      }
      case None => fail("Expected an else on the case statement!") 
    }

    // Verifying the write statement
    assert(sequenceStmts(4) == WriteStmt(VarExpression("aux")))

  }

  test("Testing the oberon stmt22 code. This module implements a case statement inside a case statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt22.oberon").getFile)
    
    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "CaseCaseModule")
    
    // Verifying variables declarations
    assert(module.variables.length == 1)
    assert(module.variables.head == VariableDeclaration("x", IntegerType))

    assert(module.stmt.nonEmpty)

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmt) => assert(stmt.length == 3)
      case _ => fail("This module should have 3 statements!")
    }

    // Verifying the statements
    val sequenceStmts = module.stmt.get.asInstanceOf[SequenceStmt].stmts;
    assert(sequenceStmts.head == ReadIntStmt("x"));

    val caseStmt1 = sequenceStmts(1).asInstanceOf[CaseStmt]
    

    // Verifying the caseStmt properties
    assert(caseStmt1.exp == VarExpression("x"))
    
    assert(caseStmt1.cases.length == 2)

    // Verifying the caseAlternatives in the case
    val caseAlts = caseStmt1.cases

    assert(caseAlts.length == 2)
    assert(caseStmt1.elseStmt.isEmpty)

    val innerCase = CaseStmt(VarExpression("x"), List(RangeCase(IntValue(1), IntValue(5),
      AssignmentStmt("x", IntValue(5))), RangeCase(IntValue(6), IntValue(10), AssignmentStmt("x", IntValue(10)))), 
      None)

    assert(caseAlts(0) == RangeCase(IntValue(1), IntValue(10), innerCase))
    
    assert(caseAlts(1) == RangeCase(IntValue(11), IntValue(20), AssignmentStmt("x",MultExpression(VarExpression("x"), IntValue(2)))))
    
    // Verifying the write statement
    assert(sequenceStmts(2) == WriteStmt(VarExpression("x")))

  }

  test("Testing the oberon stmt23 code. This module has a while with a case statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt23.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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

  test("Testing the oberon stmt24 code. This module has a while with a case statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt24.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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

  test("Testing the oberon stmt25 code. This module tests if a ForRange stmt is correctly converted to a For stmt") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt25.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert("ForRangeModule" == module.name)
    assert(1 == module.variables.length)

    assert(None != module.stmt.getOrElse(None))
    val forStmt = module.stmt.get.asInstanceOf[ForStmt]

    assert(AssignmentStmt("x", IntValue(0)) == forStmt.init)
    assert(LTEExpression(VarExpression("x"), IntValue(10)) == forStmt.condition)
    assert(SequenceStmt(List(WriteStmt(VarExpression("x")),
      AssignmentStmt("x", AddExpression(VarExpression("x"), IntValue(1))))) == forStmt.stmt)

  }

  test("Testing the oberon stmt26 code. This module has a ForRange stmt") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt26.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert("ForRangeModule" == module.name)
    assert(3 == module.variables.length)

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmts) => assert(3 == stmts.length)
      case _ => fail("Error! Expected a sequence stmt with 3 statements!!!")
    }
    val sequenceStmts = module.stmt.get.asInstanceOf[SequenceStmt].stmts

    assert(ReadIntStmt("min") == sequenceStmts.head)
    assert(ReadIntStmt("max") == sequenceStmts(1))

    val forRangeStmt = sequenceStmts(2).asInstanceOf[ForStmt]

    val innerInit = AssignmentStmt("x", VarExpression("min"))
    val innerCondition = LTEExpression(VarExpression("x"), VarExpression("max"))
    val innerStmts = List(WriteStmt(VarExpression("x")), AssignmentStmt("x", AddExpression(
      VarExpression("x"), IntValue(1))))

    val forStmts = forRangeStmt.stmt.asInstanceOf[SequenceStmt].stmts
    
    assert(innerInit == forRangeStmt.init)
    assert(innerCondition == forRangeStmt.condition)
    assert(innerStmts(0) == forStmts(0))
    assert(innerStmts(1) == forStmts(1))

  }

  test("Testing the oberon stmt27 code. This module has a ForRange stmt nested with another ForRange stmt") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt27.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert("ForRangeModule" == module.name)
    assert(2 == module.variables.length)

    assert(None != module.stmt.getOrElse(None))

    val forRangeStmt = module.stmt.get.asInstanceOf[ForStmt]

    val initStmt1 = AssignmentStmt("x", IntValue(0))
    val condExpr1 = LTEExpression(VarExpression("x"), IntValue(20))

    assert(initStmt1 == forRangeStmt.init)
    assert(condExpr1 == forRangeStmt.condition)

    val stmts1 = forRangeStmt.stmt.asInstanceOf[SequenceStmt].stmts
    assert(AssignmentStmt("x", AddExpression(VarExpression("x"), IntValue(1))) == stmts1(1))
    val nestedFor = stmts1.head.asInstanceOf[ForStmt]

    val initStmt2 = AssignmentStmt("y", VarExpression("x"))
    val condExpr2 = LTEExpression(VarExpression("y"), IntValue(20))
    val stmt2 = nestedFor.stmt.asInstanceOf[SequenceStmt].stmts

    assert(initStmt2 == nestedFor.init)
    assert(condExpr2 == nestedFor.condition)
    assert(WriteStmt(VarExpression("y")) == stmt2(0))
    assert(AssignmentStmt("y", AddExpression(VarExpression("y"), IntValue(1))) == stmt2(1))

  }

    test("Testing the oberon stmt28 code. This module has a ForRange stmt nested with another ForRange stmt") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt28.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert("ForRangeModule" == module.name)
    assert(4 == module.variables.length)

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmts) => assert(3 == stmts.length)
      case _ => fail("Expected a sequence of 3 statements!!!")
    }
  
    val sequenceStmts = module.stmt.getOrElse(None).asInstanceOf[SequenceStmt].stmts
    assert(ReadIntStmt("min") == sequenceStmts.head)
    assert(ReadIntStmt("max") == sequenceStmts(1))

    val forRangeStmt = sequenceStmts(2).asInstanceOf[ForStmt]

    val initStmt1 = AssignmentStmt("x", VarExpression("min"))
    val condExpr1 = LTEExpression(VarExpression("x"), VarExpression("max"))

    assert(initStmt1 == forRangeStmt.init)
    assert(condExpr1 == forRangeStmt.condition)

    val stmts1 = forRangeStmt.stmt.asInstanceOf[SequenceStmt].stmts
    assert(AssignmentStmt("x", AddExpression(VarExpression("x"), IntValue(1))) == stmts1(1))
    val nestedFor = stmts1(0).asInstanceOf[ForStmt]

    val initStmt2 = AssignmentStmt("y", VarExpression("x"))
    val condExpr2 = LTEExpression(VarExpression("y"), VarExpression("max"))
    val stmt2 = nestedFor.stmt.asInstanceOf[SequenceStmt].stmts

    assert(initStmt2 == nestedFor.init)
    assert(condExpr2 == nestedFor.condition)
    assert(WriteStmt(VarExpression("y")) == stmt2(0))
    assert(AssignmentStmt("y", AddExpression(VarExpression("y"), IntValue(1))) == stmt2(1))

  }

    test("Testing the oberon stmt29 code. This module has a ForRange with a procedure") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt29.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert("ForRangeModule" == module.name)
    assert(1 == module.variables.length)

    assert(None != module.stmt.getOrElse(None))
    val forStmt = module.stmt.get.asInstanceOf[ForStmt]

    val initStmt = AssignmentStmt("x", IntValue(0))
    val condExpr = LTEExpression(VarExpression("x"), IntValue(10))
    val stmts = forStmt.stmt.asInstanceOf[SequenceStmt].stmts

    assert(initStmt == forStmt.init)
    assert(condExpr == forStmt.condition)
    assert(WriteStmt(FunctionCallExpression("squareOf", List(VarExpression("x")))) == stmts(0))
    assert(AssignmentStmt("x", AddExpression(VarExpression("x"), IntValue(1))) == stmts(1))
  }

  test("Testing the oberon stmt30 code. This module has IF-ELSIF statement") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt30.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt09.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/IfElseIfStmt10.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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

  test("Testing the oberon procedure01 code. This module has a procedure") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/procedure01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.procedures.size == 1)
    assert(module.stmt.isDefined)

    val procedure = module.procedures.head

    assert(procedure.name == "sum")
    assert(procedure.args.size == 2)
    assert(procedure.returnType == Some(IntegerType))

    procedure.stmt match {
      case ReturnStmt(AddExpression(VarExpression("v1"), VarExpression("v2"))) => succeed
      case _ => fail("expecting a return stmt")
    }

    assert(module.stmt.get.isInstanceOf[SequenceStmt])

    val stmt = module.stmt.get.asInstanceOf[SequenceStmt]

    assert(stmt.stmts.head == ReadIntStmt("x"))
    assert(stmt.stmts(1) == ReadIntStmt("y"))
    assert(stmt.stmts(2) == WriteStmt(FunctionCallExpression("sum", List(VarExpression("x"), VarExpression("y")))))
  }

  test("Testing the oberon procedure02 code. This module resembles the code of the LDTA challenge") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/procedure02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "Multiples")

    assert(module.procedures.size == 1)
    assert(module.stmt.isDefined)

    val procedure = module.procedures.head

    assert(procedure.name == "calcmult")
    assert(procedure.args.size == 2)
    assert(procedure.returnType == Some(IntegerType))

    procedure.stmt match {
      case ReturnStmt(MultExpression(VarExpression("i"), VarExpression("base"))) => succeed
      case _ => fail("expecting a return i * base stmt")
    }

    assert(module.stmt.get.isInstanceOf[SequenceStmt])

    val stmt = module.stmt.get.asInstanceOf[SequenceStmt]

    assert(stmt.stmts.head == ReadIntStmt("base"))
  }

  test("Testing the oberon procedure03 code. This module implements a fatorial function") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/procedure03.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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

  test("Testing the oberon stmt31 module. This module has a RepeatUntil") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt31.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined && module.stmt.get.isInstanceOf[SequenceStmt])

    val stmt = module.stmt.get.asInstanceOf[SequenceStmt]

    assert(stmt.stmts.size == 4)

    assert(stmt.stmts(2).isInstanceOf[RepeatUntilStmt])
  }

  // Parser doesn't create Bracket Expression objects.
  ignore("Testing simple10.oberon - Bracket Expression") {
    val path = Paths.get(getClass.getClassLoader.getResource("simple/simple10.oberon")
      .getFile
      .replaceFirst("\\/(.:\\/)", "$1"))

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined && module.stmt.get.isInstanceOf[AssignmentStmt])

    val stmt = module.stmt.get.asInstanceOf[AssignmentStmt]

    assert(stmt.exp.isInstanceOf[Brackets])
  }


  test("Testing the oberon ArrayAssignmentStmt01 code. This module has a simple array assignment") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/ArrayAssignmentStmt01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 2)
      case _ => fail("we are expecting two stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts
	
    assert(stmts.head == ReadIntStmt("x"))
    assert(stmts(1) == EAssignmentStmt(ArrayAssignment(VarExpression("array"), IntValue(0)), VarExpression("x")))

  }
  
  test("Testing the oberon ArrayAssignmentStmt02 code. This module has an array assignment in IF-THEN") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/ArrayAssignmentStmt02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

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
        assert(cond == GTExpression(VarExpression("x"),VarExpression("max")))
		assert(s1 == EAssignmentStmt(ArrayAssignment(VarExpression("array"), IntValue(0)), VarExpression("x")))
        assert(s2.isEmpty) // the else stmt is None
      case _ => fail("expecting an if-then stmt")
    }

    assert(stmts(3) == WriteStmt(VarExpression("max")))
  }
  
  ignore("Testing the oberon stmt32 code. This module has some user types declarations") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt32.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "UserTypeModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 5)
      case _ => fail("we are expecting five stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    assert(stmts.head == EAssignmentStmt(ArrayAssignment(VarExpression("array"), IntValue(0)), IntValue(10)))
    assert(stmts(1) == EAssignmentStmt(ArrayAssignment(VarExpression("array"), IntValue(1)), IntValue(20)))
    assert(stmts(2) == EAssignmentStmt(ArrayAssignment(VarExpression("array"), IntValue(2)), IntValue(30)))
    assert(stmts(3) == EAssignmentStmt(ArrayAssignment(VarExpression("outroarray"), IntValue(0)), IntValue(1)))
    assert(stmts(4) == EAssignmentStmt(ArrayAssignment(VarExpression("outroarray"), IntValue(1)), IntValue(5)))
  }

  test("Testing the oberon ArrayAssignmentStmt04 code. This module has two array assignments") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/ArrayAssignmentStmt04.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 3)
      case _ => fail("we are expecting four stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

	  assert(stmts.head == EAssignmentStmt(ArrayAssignment(VarExpression("v"), IntValue(2)), IntValue(3)))
	  assert(stmts(1) == AssignmentStmt(("sum"), AddExpression(IntValue(9),IntValue(2))))
	  assert(stmts(2) == EAssignmentStmt(ArrayAssignment(VarExpression("v"), IntValue(2)), VarExpression("sum")))
  }

  ignore("Testing the oberon ArrayAssignmentStmt05 code. This module has an assignmet array with sum in the index") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/ArrayAssignmentStmt05.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.userTypes.length == 4)

    val typeList = module.userTypes

    val type1 = ArrayType("type1", 10, IntegerType)

    val type2 = RecordType("type2", List(VariableDeclaration("lista",  ReferenceToUserDefinedType("type1")),
      VariableDeclaration("inteiro", IntegerType)))

    val type3 = RecordType("type3", List(
      VariableDeclaration("mylist", ReferenceToUserDefinedType("type2")),
      VariableDeclaration("age", IntegerType),
      VariableDeclaration("check", BooleanType),
      VariableDeclaration("kleber", ReferenceToUserDefinedType("type1")),
      VariableDeclaration("bolo", ReferenceToUserDefinedType("type1"))))

    val type4 = RecordType("type4", List(
      VariableDeclaration("v1", IntegerType),
      VariableDeclaration("v2", IntegerType)))

    val typetoTest = List(type1, type2, type3, type4)

    var it: Int = 0

    typeList.foreach(t => {
      assert(t == typetoTest(it))
      it += 1
    })

  }

  ignore("Testing the oberon stmt33 code. This module has a record and array type declarations"){
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt33.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)


    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 3)
      case _ => fail("we are expecting four stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    assert(stmts.head == AssignmentStmt(("n"), IntValue(5)))
    assert(stmts(1) == AssignmentStmt(("value"), IntValue(12)))
    assert(stmts(2) == EAssignmentStmt(ArrayAssignment(VarExpression("array"), AddExpression(VarExpression("n"),IntValue(1))), VarExpression("value")))
  }

  test("Testing the oberon recordAssignmentStmt01 code") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/recordAssignmentStmt01.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 2)
      case _ => fail("we are expecting 2 stmts in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    assert(stmts.head == EAssignmentStmt(RecordAssignment(VarExpression("d1"), "day"), IntValue(5)))
  }

  ignore("Testing the oberon recordAssignmentStmt02 code") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/recordAssignmentStmt02.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.userTypes.length == 3)

    val typeList = module.userTypes

    val tysize = ArrayType("tysize", 5, IntegerType)

    val shoes = RecordType("shoes", List(VariableDeclaration("id", IntegerType),
      VariableDeclaration("sizes", ReferenceToUserDefinedType("tysize")),
      VariableDeclaration("price", IntegerType),
      VariableDeclaration("stock", IntegerType),
      VariableDeclaration("limited", BooleanType)))

    val shoes_array =  ArrayType("shoes_array", 50, ReferenceToUserDefinedType("shoes"))

    val typetoTest = List(tysize, shoes, shoes_array)

    var it: Int = 0

    typeList.foreach(t => {
      assert(t == typetoTest(it))
      it += 1
    })
  }

  ignore("Testing the oberon stmt34 code. This module has a record and array type declarations"){
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt34.oberon").getFile)


    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)


    assert(module.name == "UserTypeModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 4)
      case _ => fail("we are expecting 4 stmt in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    assert(stmts.head == EAssignmentStmt(RecordAssignment(VarExpression("year"), "last"), IntValue(2019)))
    assert(stmts(1) == EAssignmentStmt(RecordAssignment(VarExpression("year"), "actual"), IntValue(2020)))
    assert(stmts(2) == EAssignmentStmt(RecordAssignment(VarExpression("year"), "next"), IntValue(2021)))
  }

  test("Testing the oberon recordAssignmentStmt03 code") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/recordAssignmentStmt03.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 5)
      case _ => fail("we are expecting 5 stmt in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    assert(stmts.head == EAssignmentStmt(RecordAssignment(VarExpression("vagas"), "salaA"), IntValue(20)))
    assert(stmts(1) == EAssignmentStmt(RecordAssignment(VarExpression("vagas"), "salaB"), IntValue(30)))
    assert(stmts(2) == EAssignmentStmt(RecordAssignment(VarExpression("matricula"), "alunoA"), IntValue(180047205)))
    assert(stmts(3) == EAssignmentStmt(RecordAssignment(VarExpression("matricula"), "alunoB"), IntValue(180108531)))
  }

  test("Testing the oberon recordAssignmentStmt04 code") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/recordAssignmentStmt04.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 5)
      case _ => fail("we are expecting 5 stmt in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    assert(stmts.head == AssignmentStmt(("n"), IntValue(1)))
    assert(stmts(1) == EAssignmentStmt(RecordAssignment(VarExpression("passageiros"), "A"), IntValue(1)))
    assert(stmts(2) == EAssignmentStmt(RecordAssignment(VarExpression("passageiros"), "B"), IntValue(3)))
    assert(stmts(3) == EAssignmentStmt(RecordAssignment(VarExpression("passageiros"), "C"), IntValue(2)))
    assert(stmts(4) == EAssignmentStmt(ArrayAssignment(VarExpression("fila"), VarExpression("n")), VarExpression("A")))


  }

  test("Testing the oberon recordAssignmentStmt05 code") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/recordAssignmentStmt05.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.stmt.isDefined)

    // assert that the main block contains a sequence of statements
    module.stmt.get match {
      case SequenceStmt(stmts) => assert(stmts.length == 7)
      case _ => fail("we are expecting one stmt in the main block")
    }

    // now we can assume that the main block contains a sequence of stmts
    val sequence = module.stmt.get.asInstanceOf[SequenceStmt]
    val stmts = sequence.stmts

    assert(stmts.head == AssignmentStmt(("n"), IntValue(1)))
    assert(stmts(1) == EAssignmentStmt(RecordAssignment(VarExpression("passageiros"), "A"), IntValue(1)))
    assert(stmts(2) == EAssignmentStmt(RecordAssignment(VarExpression("passageiros"), "B"), IntValue(3)))
    assert(stmts(3) == EAssignmentStmt(RecordAssignment(VarExpression("passageiros"), "C"), IntValue(2)))
    stmts(4) match {
      case IfElseStmt(cond, s1, s2) =>
        assert(cond == GTExpression(VarExpression("n"), IntValue(2)))
		assert(s1 == EAssignmentStmt(ArrayAssignment(VarExpression("fila"), VarExpression("n")), VarExpression("A")))
        assert(s2.isEmpty) // the else stmt is None
      case _ => fail("expecting an if-then stmt")
    }
    assert(stmts(5) == WriteStmt(VarExpression("A")))


  }

//
//
//    assert(module.name == "UserTypeModule")
//
//    assert(module.userTypes.length == 5)
//
//    val typeList = module.userTypes
//
//    val m_id = ArrayType("m_id", 15, IntegerType)
//    val team = RecordType("team", List(
//      VariableDeclaration("t_id", IntegerType),
//      VariableDeclaration("members", ReferenceToUserDefinedType("m_id")),
//      VariableDeclaration("n_members", IntegerType),
//      VariableDeclaration("full", BooleanType)))
//
//    val team_array = ArrayType("team_array", 20, ReferenceToUserDefinedType("team"))
//    val test_array = ArrayType("test_array", 20, IntegerType)
//    val second_test = ArrayType("second_test", 20, ReferenceToUserDefinedType("test_array"))
//    val typetoTest = List(m_id, team, team_array, test_array, second_test)
//
//    var it: Int = 0
//
//    typeList.foreach(t => {
//      assert(t == typetoTest(it))
//      it += 1
//    })
//  }

  test("Testing the oberon ExpressionNameParser1 code. This module tests if the parser can see expression name access"){
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/ExpressionNameParser1.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "ExpressionNameModule")
    assert(module.stmt.isDefined)

    assert(module.stmt.get.asInstanceOf[WriteStmt].expression.isInstanceOf[FieldAccessExpression])

    assert(module.stmt.get.asInstanceOf[WriteStmt].expression.asInstanceOf[FieldAccessExpression].exp.isInstanceOf[VarExpression])

    assert(module.stmt.get.asInstanceOf[WriteStmt].expression.asInstanceOf[FieldAccessExpression].name.isInstanceOf[String])
  }

  test("Testing the oberon ExpressionNameParser2 code. This module tests if the parser can translate operations with expression name"){
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/ExpressionNameParser2.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "ExpressionNameModule")

    assert(module.stmt.isDefined)

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.size == 2)

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].right.isInstanceOf[FieldAccessExpression])

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].left.asInstanceOf[IntValue].value == 1)


  }

  test("Testing the oberon ExpressionNameParser3 code. This module tests if the parser can see expression name with more than two words"){
  	val path = Paths.get(getClass.getClassLoader.getResource("stmts/ExpressionNameParser3.oberon").getFile)

  	assert(path != null)

  	val content = String.join("\n", Files.readAllLines(path))
  	val module = ScalaParser.parse(content)

  	assert(module.name == "ExpressionNameModule")

  	assert(module.stmt.isDefined)

  	assert(module.stmt.get.asInstanceOf[WriteStmt].expression.isInstanceOf[FieldAccessExpression])

    assert(module.stmt.get.asInstanceOf[WriteStmt].expression.asInstanceOf[FieldAccessExpression].exp.isInstanceOf[FieldAccessExpression])

    assert(module.stmt.get.asInstanceOf[WriteStmt].expression.asInstanceOf[FieldAccessExpression].name.isInstanceOf[String])
  }

  test("Testing the oberon ExpressionNameParser4 code. This module tests if the parser can translate operations with two expression names"){
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/ExpressionNameParser4.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "ExpressionNameModule")

    assert(module.stmt.isDefined)

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.size == 2)

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].left.isInstanceOf[FieldAccessExpression])

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].right.isInstanceOf[FieldAccessExpression])

  }

  test("Testing the oberon ExpressionNameParser5 code. This module tests if the parser can translate different operations with type record declarations"){
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/ExpressionNameParser5.oberon").getFile)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "ExpressionNameModule")

    assert(module.stmt.isDefined)

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.size == 2)

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].left.isInstanceOf[FieldAccessExpression])

    assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].right.asInstanceOf[MultExpression].left.isInstanceOf[FieldAccessExpression])

	assert(module.stmt.get.asInstanceOf[SequenceStmt].stmts.head.asInstanceOf[AssignmentStmt].exp.asInstanceOf[AddExpression].right.asInstanceOf[MultExpression].right.isInstanceOf[FieldAccessExpression])


  }

}

//test("Testing the oberon ArrayAssignmentStmt03 code. This module has five array assignments") {
//val path = Paths.get(getClass.getClassLoader.getResource("stmts/ArrayAssignmentStmt03.oberon").getFile)

