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
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/stmt06.oberon").getFile);

    assert(path != null);

    val content = String.join("\n", Files.readAllLines(path));
    val module = ScalaParser.parse(content);

    assert(module.name == "SimpleModule");

    assert(!module.stmt.isEmpty);
    

    module.stmt.getOrElse(false) match {
      case SequenceStmt(stmts) => assert(stmts.length == 3);
      case _ => fail("Expected a sequence of 3 statements!!")
    }

    val sequenceStmts = module.stmt.get.asInstanceOf[SequenceStmt].stmts;

    assert(sequenceStmts(0) == ReadIntStmt("xs"));

    val myCaseStmt = sequenceStmts(1).asInstanceOf[CaseStmt];

    assert(myCaseStmt.exp == VarExpression("xs"));

    assert(myCaseStmt.cases.length == 4);

    var caseLabel = 1;
    var caseValAssigment = 5;
    myCaseStmt.cases.foreach(miniCase => {

      val _miniCase = miniCase.asInstanceOf[SimpleCase]

      assert(_miniCase.condition == IntValue(caseLabel))
      assert(_miniCase.stmt == AssignmentStmt("xs", IntValue(caseValAssigment)))
      caseLabel += 1
      caseValAssigment *= 2
    })

    myCaseStmt.elseStmt.getOrElse(false) match {
      case AssignmentStmt(varName, exp) => {
        assert(varName == "xs");
        assert(exp == IntValue(0));
      }
      case _ => fail("Expected an else on the case statement!");
    }
    assert(sequenceStmts(2) == WriteStmt(VarExpression("xs")));

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
  
  test("Testing the oberon stmt11 code. This module has a For statement") {
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
}
