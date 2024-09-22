package br.unb.cic.oberon.parser

import br.unb.cic.oberon.AbstractTestSuite
import br.unb.cic.oberon.ir.ast._
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ir.common._
import scala.collection.mutable.Map

class ParserCombinatorTestSuite
  extends AbstractTestSuite
    with Oberon2ScalaParser {
  test("Testing Int Parser") {
    assert(IntValue(123) == parseAbs(parse(int, "123"))) // positive number
    assert(IntValue(-321) == parseAbs(parse(int, "-321"))) // negative number
    val thrown = intercept[Exception] {
      parseAbs(parse(int, "abc 123")) // not accepted chars in the beginning
    }
    assert(thrown.getMessage.length > 0)
    assert(
      IntValue(123) == parseAbs(parse(int, "123 abc"))
    ) // accepted chars in the end
  }

  test("Testing Real Parser") {
    assert(RealValue(12.3) == parseAbs(parse(real, "12.3"))) // positive number
    assert(
      RealValue(-32.1) == parseAbs(parse(real, "-32.1"))
    ) // negative number
  }

  test("Testing Bool Parser") {
    assert(BoolValue(true) == parseAbs(parse(bool, "True")))
    assert(BoolValue(false) == parseAbs(parse(bool, "False")))
  }

  test("Testing String Parser") {
    assert(
      StringValue("teste") == parseAbs(parse(string, "\"teste\""))
    ) // double quotes
  }

  test("Testing identifier parser") {
    assert("teste" == parseAbs(parse(identifier, "teste")))
    assert("teste_" == parseAbs(parse(identifier, "teste_")))
    assert("teste1" == parseAbs(parse(identifier, "teste1")))
    ("teste_1abc" == parseAbs(parse(identifier, "teste_1abc")))
  }

  test("Testing type parser") {
    assert(IntegerType == parseAbs(parse(typeParser, "INTEGER")))
    assert(RealType == parseAbs(parse(typeParser, "REAL")))
    assert(CharacterType == parseAbs(parse(typeParser, "CHAR")))
    assert(BooleanType == parseAbs(parse(typeParser, "BOOLEAN")))
    assert(StringType == parseAbs(parse(typeParser, "STRING")))
    assert(NullType == parseAbs(parse(typeParser, "NIL")))
    assert(
      ReferenceToUserDefinedType("bolo") == parseAbs(parse(typeParser, "bolo"))
    )
  }

  test("Testing expValueParser Expression") {
    assert(IntValue(16) == parseAbs(parse(expValueParser, "16")))
    assert(RealValue(-35.2) == parseAbs(parse(expValueParser, "-35.2")))
    assert(CharValue('a') == parseAbs(parse(expValueParser, "'a'")))
    assert(StringValue("teste") == parseAbs(parse(expValueParser, "\"teste\"")))
    assert(BoolValue(true) == parseAbs(parse(expValueParser, "True")))
    assert(BoolValue(false) == parseAbs(parse(expValueParser, "False")))
    assert(NullValue == parseAbs(parse(expValueParser, "NIL")))
  }

  test("Testing expressionParser") {
    assert(IntValue(16) == parseAbs(parse(expressionParser, "16")))
    assert(RealValue(-35.2) == parseAbs(parse(expressionParser, "-35.2")))
    assert(CharValue('a') == parseAbs(parse(expressionParser, "'a'")))
    assert(
      StringValue("teste") == parseAbs(parse(expressionParser, "\"teste\""))
    )
    assert(BoolValue(true) == parseAbs(parse(expressionParser, "True")))
    assert(BoolValue(false) == parseAbs(parse(expressionParser, "False")))
    assert(NullValue == parseAbs(parse(expressionParser, "NIL")))
    assert(
      StringValue("testao") == parseAbs(
        parse(expressionParser, "(\"testao\")")
      )
    )

    var exp1 = IntValue(16)
    var exp2 = RealValue(-35.2)
    assert(
      MultExpression(exp1, exp2) == parseAbs(
        parse(expressionParser, "16 * -35.2")
      )
    )
    assert(
      DivExpression(exp1, exp2) == parseAbs(
        parse(expressionParser, "16 / -35.2")
      )
    )
    assert(
      AndExpression(exp1, exp2) == parseAbs(
        parse(expressionParser, "16 && -35.2")
      )
    )

    assert(
      AndExpression(
        DivExpression(
          DivExpression(
            MultExpression(
              DivExpression(IntValue(16), IntValue(4)),
              RealValue(-35.2)
            ),
            IntValue(-4)
          ),
          IntValue(3)
        ),
        IntValue(4)
      ) == parseAbs(parse(expressionParser, "(16 / 4) * -35.2 / -4 / 3 && 4"))
    )
    assert(
      AndExpression(
        MultExpression(
          MultExpression(
            DivExpression(IntValue(16), IntValue(4)),
            DivExpression(RealValue(-35.2), IntValue(-4))
          ),
          IntValue(3)
        ),
        IntValue(-66)
      ) == parseAbs(
        parse(expressionParser, "(16) / 4 * (-35.2 / -4) * 3 && -66")
      )
    )
  }
  test("Testing addExpParser") {
    assert(
      OrExpression(
        SubExpression(AddExpression(IntValue(2), IntValue(4)), IntValue(3)),
        IntValue(2)
      ) == parseAbs(parse(expressionParser, "2 + 4 - 3 || 2"))
    )
  }
  test("Testing mulExpParser") {
    assert(
      AndExpression(
        DivExpression(MultExpression(IntValue(2), IntValue(4)), IntValue(3)),
        IntValue(2)
      ) == parseAbs(parse(expressionParser, "2 * 4 / 3 && 2"))
    )
  }
  test("Testing relExpParser") {
    assert(
      GTEExpression(
        GTExpression(
          LTEExpression(
            LTExpression(
              NEQExpression(
                EQExpression(IntValue(2), IntValue(4)),
                IntValue(3)
              ),
              IntValue(2)
            ),
            IntValue(4)
          ),
          IntValue(1)
        ),
        IntValue(7)
      ) == parseAbs(parse(expressionParser, "2 = 4 # 3 < 2 <= 4 > 1 >= 7"))
    )
  }

  test("Testing Aritmetic operations") {
    assert(
      EQExpression(
        AddExpression(IntValue(25), IntValue(12)),
        IntValue(5)
      ) == parseAbs(parse(expressionParser, "25 + 12 = 5"))
    )
    assert(
      EQExpression(
        AddExpression(IntValue(25), MultExpression(IntValue(12), IntValue(3))),
        IntValue(5)
      ) == parseAbs(parse(expressionParser, "25 + 12 * 3 = 5"))
    )
  }

  test("Testing FieldAcces") {
    assert(
      FieldAccessExpression(VarExpression("abc"), "ab") == parseAbs(
        parse(expressionParser, "abc.ab")
      )
    )
  }
  test("Testing variable parser") {
    assert(VarExpression("abc") == parseAbs(parse(expressionParser, "abc")))
  }
  test("Testing function parser") {
    assert(
      FunctionCallExpression(
        "abc",
        List(IntValue(12), StringValue("oi"))
      ) == parseAbs(parse(expressionParser, "abc(12, \"oi\")"))
    )
  }
  test("Testing pointer parser") {
    assert(
      PointerAccessExpression("abc") == parseAbs(
        parse(expressionParser, "abc^")
      )
    )
  }
  test("Testing ArraySubscript parser") {
    assert(
      ArraySubscript(VarExpression("abc"), IntValue(3)) == parseAbs(
        parse(expressionParser, "abc[3]")
      )
    )
  }

  test("Testing Statement parser") {
    // identifier
    assert(
      AssignmentStmt("functionTest", IntValue(456)) == parseAbs(
        parse(multStatementParser, "functionTest := 456")
      )
    )
    // designator
    assert(
      new AssignmentStmt(
        ArrayAssignment(
          FunctionCallExpression("functionTest", List()),
          IntValue(123)
        ),
        IntValue(456)
      ) == parseAbs(parse(multStatementParser, "functionTest()[123] := 456"))
    )
    // readReal, readInt, readChar, write
    assert(
      ReadRealStmt("oi") == parseAbs(parse(multStatementParser, "readReal(oi)"))
    )
    assert(
      ReadIntStmt("oi") == parseAbs(parse(multStatementParser, "readInt(oi)"))
    )
    assert(
      ReadCharStmt("oi") == parseAbs(parse(multStatementParser, "readChar(oi)"))
    )
    assert(
      WriteStmt(StringValue("oi")) == parseAbs(
        parse(multStatementParser, "write(\"oi\")")
      )
    )
    // Procedure Call
    assert(
      ProcedureCallStmt("teste", List()) == parseAbs(
        parse(multStatementParser, "teste()")
      )
    )
    assert(
      ProcedureCallStmt("teste", List(IntValue(1), IntValue(2))) == parseAbs(
        parse(multStatementParser, "teste(1, 2)")
      )
    )
    assert(
      ProcedureCallStmt(
        "teste",
        List(StringValue("oi"), VarExpression("banana"), IntValue(12))
      ) == parseAbs(parse(multStatementParser, "teste(\"oi\", banana, 12)"))
    )
    // IF THEN ELSE
    assert(
      IfElseStmt(
          GTExpression(
            MultExpression(IntValue(2), IntValue(5)),
            FunctionCallExpression("teste", List(IntValue(1)))
        ),
        new AssignmentStmt(
          ArrayAssignment(
            FunctionCallExpression("functionTest", List()),
            IntValue(123)
          ),
          IntValue(456)
        ),
        Some(
          new AssignmentStmt(
            ArrayAssignment(
              FunctionCallExpression("testFunc", List()),
              IntValue(321)
            ),
            IntValue(567)
          )
        )
      ) == parseAbs(
        parse(
          multStatementParser,
          "IF (2 * 5 > teste(1)) THEN functionTest()[123] := 456 ELSE testFunc()[321] := 567 END"
        )
      )
    )
    // IF THEN ELSIF THEN ELSE END
    assert(
      IfElseIfStmt(
        GTExpression(IntValue(2), IntValue(5)),
        AssignmentStmt("functionTest", IntValue(456)),
        List(
          ElseIfStmt(
            LTExpression(IntValue(3), IntValue(5)),
            AssignmentStmt("functionTest", IntValue(567))
          )
        ),
        Some(AssignmentStmt("functionTest", IntValue(678)))
      ) == parseAbs(
        parse(
          multStatementParser,
          "IF 2 > 5 THEN functionTest := 456 ELSIF 3 < 5 THEN functionTest := 567 ELSE functionTest := 678 END"
        )
      )
    )
    // IF THEN ELSIF THEN ELSE END
    assert(
      IfElseIfStmt(
        GTExpression(IntValue(2), IntValue(5)),
        AssignmentStmt("functionTest", IntValue(456)),
        List(
          ElseIfStmt(
            LTExpression(IntValue(3), IntValue(5)),
            AssignmentStmt("functionTest", IntValue(567))
          )
        ),
        Some(AssignmentStmt("functionTest", IntValue(678)))
      ) == parseAbs(
        parse(
          multStatementParser,
          "IF 2 > 5 THEN functionTest := 456 ELSIF 3 < 5 THEN functionTest := 567 ELSE functionTest := 678 END"
        )
      )
    )
    // IF THEN ELSIF THEN END
    assert(
      IfElseIfStmt(
        GTExpression(IntValue(2), IntValue(5)),
        AssignmentStmt("functionTest", IntValue(456)),
        List(
          ElseIfStmt(
            LTExpression(IntValue(3), IntValue(5)),
            AssignmentStmt("functionTest", IntValue(567))
          )
        ),
        None: Option[Statement]
      ) == parseAbs(
        parse(
          multStatementParser,
          "IF 2 > 5 THEN functionTest := 456 ELSIF 3 < 5 THEN functionTest := 567 END"
        )
      )
    )
    // WHILE DO END
    assert(
      WhileStmt(
        GTExpression(IntValue(2), IntValue(5)),
        AssignmentStmt("functionTest", IntValue(456))
      ) == parseAbs(
        parse(multStatementParser, "WHILE 2 > 5 DO functionTest := 456 END")
      )
    )
    // REPEAT UNTIL
    assert(
      RepeatUntilStmt(
        GTExpression(IntValue(2), IntValue(5)),
        AssignmentStmt("functionTest", IntValue(456))
      ) == parseAbs(
        parse(multStatementParser, "REPEAT functionTest := 456 UNTIL 2 > 5")
      )
    )
    // FOR TO DO END
    assert(
      ForStmt(
        AssignmentStmt("functionTest", IntValue(456)),
        LTExpression(IntValue(3), IntValue(5)),
        AssignmentStmt("functionTest", IntValue(678))
      ) == parseAbs(
        parse(
          multStatementParser,
          "FOR functionTest := 456 TO 3 < 5 DO functionTest := 678 END"
        )
      )
    )
    // FOR IN DO END
    assert(
      ForStmt(
        AssignmentStmt("functionTest", IntValue(1)),
        LTEExpression(VarExpression("functionTest"), IntValue(5)),
        SequenceStmt(
          List(
            AssignmentStmt("functionTest0", IntValue(456)),
            AssignmentStmt(
              "functionTest",
              AddExpression(VarExpression("functionTest"), IntValue(1))
            )
          )
        )
      ) == parseAbs(
        parse(
          multStatementParser,
          "FOR functionTest IN 1..5 DO functionTest0 := 456 END"
        )
      )
    )
    // LOOP END
    assert(
      LoopStmt(
        AssignmentStmt("functionTest", IntValue(456))
      ) == parseAbs(parse(multStatementParser, "LOOP functionTest := 456 END"))
    )
    // RETURN
    assert(
      ReturnStmt(
        LTExpression(IntValue(3), IntValue(5))
      ) == parseAbs(parse(multStatementParser, "RETURN 3 < 5"))
    )
    // CASE OF ELSE END
    assert(
      CaseStmt(
        VarExpression("X"),
        List(
          SimpleCase(IntValue(0), AssignmentStmt("X", IntValue(2))),
          RangeCase(
            IntValue(123),
            IntValue(321),
            AssignmentStmt("X", IntValue(5))
          )
        ),
        Some(AssignmentStmt("functionTest", IntValue(678)))
      ) == parseAbs(
        parse(
          multStatementParser,
          "CASE X OF 0: X := 2 | 123..321: X := 5 ELSE functionTest := 678 END"
        )
      )
    )
    // EXIT
    assert(ExitStmt() == parseAbs(parse(multStatementParser, "EXIT")))
  }

  test("Testing Statement sequence parser") {
    assert(
      SequenceStmt(List(ReadRealStmt("oi"), ReadRealStmt("oi"))) == parseAbs(
        parse(multStatementParser, "readReal(oi);readReal(oi)")
      )
    )
  }

  test("Testing Procedure parser") {
    assert(
      Procedure(
        "addFunc",
        List(
          ParameterByValue("a", IntegerType),
          ParameterByValue("b", IntegerType)
        ),
        Option(IntegerType),
        List[Constant](),
        List[VariableDeclaration](),
        ReturnStmt(AddExpression(VarExpression("a"), VarExpression("b")))
      )
        == parseAbs(
        parse(
          procedureParser,
          """
        PROCEDURE addFunc (a, b: INTEGER): INTEGER;
        BEGIN
            RETURN a + b
        END
        addFunc
        """
        )
      )
    )

    val thrown = intercept[Exception] {
      parseAbs(
        parse(
          procedureParser,
          """
            PROCEDURE addFunc (a, b: INTEGER): INTEGER;
            BEGIN
                RETURN a + b
            END
            addFun
            """
        )
      )
    }
    assert(
      thrown.getMessage == "Procedure name (addFunc) doesn't match the end identifier (addFun)"
    )
  }

  test("Testing the oberon simple01 code") {
    val module = parseResource("simple/simple01.oberon")

    assert(module.name == "SimpleModule1")
    assert(module.constants.size == 1)
    assert(module.constants.head == Constant("x", IntValue(5)))
  }

  test(
    "Testing the oberon simple02 code. This module has one constants and two variables"
  ) {
    val module = parseResource("simple/simple02.oberon")

    assert(module.name == "SimpleModule2")
    assert(module.constants.size == 1)
    assert(module.constants.head == Constant("x", IntValue(5)))
    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }

  test(
    "Testing the oberon simple03 code. This module has three constants and two variables"
  ) {
    val module = parseResource("simple/simple03.oberon")

    assert(module.name == "SimpleModule3")
    assert(module.constants.size == 3)
    assert(module.constants.head == Constant("x", IntValue(5)))
    assert(module.constants(1) == Constant("y", IntValue(10)))
    assert(module.constants(2) == Constant("z", BoolValue(true)))

    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }

  test(
    "Testing the oberon simple04 code. This module has three constants, a sum, and two variables"
  ) {
    val module = parseResource("simple/simple04.oberon")
    assert(module.name == "SimpleModule4")
    assert(module.constants.size == 3)
    assert(module.constants.head == Constant("x", IntValue(5)))
    assert(module.constants(1) == Constant("y", IntValue(10)))
    assert(
      module.constants(2) == Constant(
        "z",
        AddExpression(IntValue(5), IntValue(10))
      )
    )

    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }

  test(
    "Testing the oberon simple05 code. This module has one constant, a multiplication, and two variables"
  ) {
    val module = parseResource("simple/simple05.oberon")

    assert(module.name == "SimpleModule5")
    assert(module.constants.size == 1)
    assert(
      module.constants.head == Constant(
        "z",
        MultExpression(IntValue(5), IntValue(10))
      )
    )

    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }

  test(
    "Testing the oberon simple06 code. This module has one constants, complex expression, and two variables"
  ) {
    val module = parseResource("simple/simple06.oberon")

    assert(module.name == "SimpleModule6")
    assert(module.constants.size == 1)
    assert(
      module.constants.head == Constant(
        "z",
        AddExpression(IntValue(5), MultExpression(IntValue(10), IntValue(3)))
      )
    )

    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }

  test(
    "Testing the oberon simple07 code. This module has two constants, a complex expression, and two variables"
  ) {
    val module = parseResource("simple/simple07.oberon")

    assert(module.name == "SimpleModule7")
    assert(module.constants.size == 2)
    assert(
      module.constants.head == Constant(
        "x",
        AddExpression(IntValue(5), MultExpression(IntValue(10), IntValue(3)))
      )
    )
    assert(
      module.constants(1) == Constant(
        "y",
        AddExpression(
          IntValue(5),
          DivExpression(MultExpression(IntValue(10), IntValue(3)), IntValue(5))
        )
      )
    )

    assert(module.variables.size == 2)
    assert(module.variables.head == VariableDeclaration("abc", IntegerType))
    assert(module.variables(1) == VariableDeclaration("def", BooleanType))
  }

  test(
    "Testing the oberon simple08 code. This module has three constants, a boolean expresson, and two variables"
  ) {
    val module = parseResource("simple/simple08.oberon")

    assert(module.name == "SimpleModule8")
    assert(module.constants.size == 3)
    assert(module.constants.head == Constant("x", BoolValue(false)))
    assert(module.constants(1) == Constant("y", BoolValue(true)))
    assert(
      module.constants(2) == Constant(
        "z",
        AndExpression(BoolValue(true), BoolValue(false))
      )
    )
  }

  test(
    "Testing the oberon simple09 code. This module has one constant and an expression involving both 'and' and 'or'"
  ) {
    val module = parseResource("simple/simple09.oberon")

    assert(module.name == "SimpleModule9")
    assert(module.constants.size == 1)
    assert(
      module.constants.head == Constant(
        "z",
        OrExpression(
          AndExpression(BoolValue(true), BoolValue(false)),
          BoolValue(false)
        )
      )
    )
  }

  test(
    "Testing the oberon stmt01 code. This module has a block of three statements"
  ) {
    val module = parseResource("stmts/stmt01.oberon")

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
    assert(
      stmts(2) == WriteStmt(
        AddExpression(VarExpression("x"), VarExpression("y"))
      )
    )
  }

  test("Testing the oberon arrayIndex01 module. This module has a ArrayIndex") {
    val module = parseResource("simple/arrayIndex01.oberon")

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
  }

  test("Testing the oberon arrayIndex02 code. This module has a ArrayIndex") {
    val module = parseResource("simple/arrayIndex02.oberon")

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

    assert(
      stmts.head == AssignmentStmt(
        "x",
        ArraySubscript(VarExpression("a"), IntValue(8))
      )
    )
    assert(stmts(1) == WriteStmt(VarExpression("x")))
  }

  test(
    "Testing the oberon stmt02 code. This module has a block of four statements"
  ) {
    val module = parseResource("stmts/stmt02.oberon")

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
    assert(
      stmts(2) == AssignmentStmt(
        "z",
        AddExpression(VarExpression("x"), VarExpression("y"))
      )
    )
    assert(stmts(3) == WriteStmt(VarExpression("z")))
  }

  test(
    "Testing the oberon stmt06 code. This module has a simple case statement"
  ) {
    val module = parseResource("stmts/stmt06.oberon")

    assert(module.name == "SimpleModule")

    assert(!module.stmt.isEmpty)

    module.stmt.getOrElse(false) match {
      case SequenceStmt(stmts) => assert(stmts.length == 3)
      case _                   => fail("Expected a sequence of 3 statements!!")
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
      case AssignmentStmt(VarAssignment(varName), exp) => {
        assert(varName == "xs")
        assert(exp == IntValue(0))
      }
      case _ => fail("Expected an else on the case statement!")
    }
    assert(sequenceStmts(2) == WriteStmt(VarExpression("xs")))

  }

  test("Testing the oberon stmt07 code. This module has a For statement") {
    val module = parseResource("stmts/stmt07.oberon")

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
        assert(
          stmt == AssignmentStmt(
            "z",
            AddExpression(VarExpression("z"), VarExpression("y"))
          )
        )
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }

    assert(stmts(2) == WriteStmt(VarExpression("z")))

  }

  test("Testing the oberon stmt08 code. This module has a For statement") {
    val module = parseResource("stmts/stmt08.oberon")

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

    val code =
      AssignmentStmt("k", AddExpression(VarExpression("z"), VarExpression("x")))

    assert(stmts.head == ReadIntStmt("y"))

    stmts(1) match {
      case ForStmt(init, cond, stmt) =>
        assert(init == AssignmentStmt("x", IntValue(0)))
        assert(cond == LTExpression(VarExpression("x"), VarExpression("y")))
        assert(
          stmt == ForStmt(
            AssignmentStmt("z", IntValue(0)),
            LTExpression(VarExpression("z"), VarExpression("y")),
            code
          )
        )
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }

    assert(stmts(2) == WriteStmt(VarExpression("k")))

  }

  test("Testing the oberon stmt09 code. This module has a For statement") {
    val module = parseResource("stmts/stmt09.oberon")

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
        assert(
          stmt == AssignmentStmt(
            "z",
            AddExpression(VarExpression("z"), VarExpression("y"))
          )
        )
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }

    assert(stmts(1) == WriteStmt(VarExpression("z")))

  }

  test("Testing the oberon stmt13 code. This module has a For statement") {
    val module = parseResource("stmts/stmt13.oberon")

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
        assert(
          stmt == AssignmentStmt(
            "y",
            MultExpression(VarExpression("y"), VarExpression("y"))
          )
        )
      case _ => fail("expecting an assigment stmt and if-then stmt")
    }

    assert(stmts(2) == WriteStmt(VarExpression("y")))
  }

  test(
    "Testing the oberon stmt17 code. This module has a case statement with range cases, two variabels, read and write statements"
  ) {
    val module = parseResource("stmts/stmt17.oberon")

    assert(module.name == "RangeCaseModule")

    // Verifying variables declarations
    assert(module.variables.length == 2)
    assert(module.variables.head == VariableDeclaration("x", IntegerType))
    assert(module.variables(1) == VariableDeclaration("y", IntegerType))

    assert(module.stmt.nonEmpty);

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmts) => assert(stmts.length == 4)
      case _                   => fail("This module should have 4 statements!")
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
    assert(
      caseAlts.head == RangeCase(
        IntValue(0),
        IntValue(9),
        AssignmentStmt("y", MultExpression(IntValue(2), VarExpression("x")))
      )
    )
    assert(
      caseAlts(1) == RangeCase(
        IntValue(10),
        IntValue(20),
        AssignmentStmt("y", MultExpression(IntValue(4), VarExpression("x")))
      )
    )

    // Verifying the last statement
    assert(sequenceStmts(3) == WriteStmt(VarExpression("y")))

  }

  test(
    "Testing the oberon stmt18 code. this module has a simple case statement with range case, reading min and max"
  ) {
    val module = parseResource("stmts/stmt18.oberon")
    assert(module.name == "SimpleRangeCaseModule")

    // Verifying variables declarations
    assert(module.variables.length == 3)
    assert(module.variables.head == VariableDeclaration("xs", IntegerType))
    assert(module.variables(1) == VariableDeclaration("min", IntegerType))
    assert(module.variables(2) == VariableDeclaration("max", IntegerType))

    assert(module.stmt.nonEmpty)

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmts) => assert(stmts.length == 5)
      case _                   => fail("This module should have 5 statements!")
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
    assert(
      caseAlts.head == SimpleCase(
        IntValue(1),
        AssignmentStmt("xs", IntValue(5))
      )
    )
    assert(
      caseAlts(1) == SimpleCase(IntValue(2), AssignmentStmt("xs", IntValue(10)))
    )
    assert(
      caseAlts(2) == RangeCase(
        VarExpression("min"),
        VarExpression("max"),
        AssignmentStmt("xs", IntValue(20))
      )
    )

    caseStmt.elseStmt.getOrElse(false) match {
      case AssignmentStmt(VarAssignment(varName), exp) => {
        assert(varName == "xs")
        assert(exp == IntValue(0))
      }
      case _ => fail("Expected an else on the case statement!")
    }
    assert(sequenceStmts(4) == WriteStmt(VarExpression("xs")))

  }

  test(
    "Testing the oberon stmt19 code. This module has three variabels, two case statements, an read and a write statements"
  ) {
    val module = parseResource("stmts/stmt19.oberon")
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
      case _                  => fail("This module should have 5 statements!")
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

      assert(
        miniCase == SimpleCase(
          IntValue(caseLabel),
          AssignmentStmt(
            "y",
            AddExpression(VarExpression("x"), IntValue(caseSum))
          )
        )
      )

      caseSum += 5
      caseLabel *= 2
    })

    caseStmt1.elseStmt.getOrElse(None) match {
      case AssignmentStmt(VarAssignment(varName), exp) => {
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

      assert(
        miniCase == RangeCase(
          IntValue(minCaseValue),
          IntValue(maxCaseValue),
          AssignmentStmt("z", IntValue(assignmentValue))
        )
      )

      maxCaseValue *= 2
      minCaseValue = maxCaseValue / 2 + 1
      assignmentValue *= 2
    })

    caseStmt2.elseStmt.getOrElse(None) match {
      case AssignmentStmt(VarAssignment(varName), exp) => {
        assert(varName == "z")
        assert(exp == IntValue(1))
      }
      case None => fail("Expected an else on the second case statement!")
    }

    assert(sequenceStmts(4) == WriteStmt(VarExpression("z")))

  }

  test(
    "Testing the oberon stmt22 code. This module implements a case statement inside a case statement"
  ) {
    val module = parseResource("stmts/stmt22.oberon")

    assert(module.name == "CaseCaseModule")

    // Verifying variables declarations
    assert(module.variables.length == 1)
    assert(module.variables.head == VariableDeclaration("x", IntegerType))

    assert(module.stmt.nonEmpty)

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmt) => assert(stmt.length == 3)
      case _                  => fail("This module should have 3 statements!")
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

    val innerCase = CaseStmt(
      VarExpression("x"),
      List(
        RangeCase(IntValue(1), IntValue(5), AssignmentStmt("x", IntValue(5))),
        RangeCase(IntValue(6), IntValue(10), AssignmentStmt("x", IntValue(10)))
      ),
      None
    )

    assert(caseAlts(0) == RangeCase(IntValue(1), IntValue(10), innerCase))

    assert(
      caseAlts(1) == RangeCase(
        IntValue(11),
        IntValue(20),
        AssignmentStmt("x", MultExpression(VarExpression("x"), IntValue(2)))
      )
    )

    // Verifying the write statement
    assert(sequenceStmts(2) == WriteStmt(VarExpression("x")))

  }

  test(
    "Testing the oberon stmt25 code. This module tests if a ForRange stmt is correctly converted to a For stmt"
  ) {
    val module = parseResource("stmts/stmt25.oberon")

    assert("ForRangeModule" == module.name)
    assert(1 == module.variables.length)

    assert(None != module.stmt.getOrElse(None))
    val forStmt = module.stmt.get.asInstanceOf[ForStmt]

    assert(AssignmentStmt("x", IntValue(0)) == forStmt.init)
    assert(LTEExpression(VarExpression("x"), IntValue(10)) == forStmt.condition)
    assert(
      SequenceStmt(
        List(
          WriteStmt(VarExpression("x")),
          AssignmentStmt("x", AddExpression(VarExpression("x"), IntValue(1)))
        )
      ) == forStmt.stmt
    )

  }

  test("Testing the oberon stmt26 code. This module has a ForRange stmt") {
    val module = parseResource("stmts/stmt26.oberon")

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
    val innerStmts = List(
      WriteStmt(VarExpression("x")),
      AssignmentStmt("x", AddExpression(VarExpression("x"), IntValue(1)))
    )

    val forStmts = forRangeStmt.stmt.asInstanceOf[SequenceStmt].stmts

    assert(innerInit == forRangeStmt.init)
    assert(innerCondition == forRangeStmt.condition)
    assert(innerStmts(0) == forStmts(0))
    assert(innerStmts(1) == forStmts(1))

  }

  test(
    "Testing the oberon stmt27 code. This module has a ForRange stmt nested with another ForRange stmt"
  ) {
    val module = parseResource("stmts/stmt27.oberon")

    assert("ForRangeModule" == module.name)
    assert(2 == module.variables.length)

    assert(None != module.stmt.getOrElse(None))

    val forRangeStmt = module.stmt.get.asInstanceOf[ForStmt]

    val initStmt1 = AssignmentStmt("x", IntValue(0))
    val condExpr1 = LTEExpression(VarExpression("x"), IntValue(20))

    assert(initStmt1 == forRangeStmt.init)
    assert(condExpr1 == forRangeStmt.condition)

    val stmts1 = forRangeStmt.stmt.asInstanceOf[SequenceStmt].stmts
    assert(
      AssignmentStmt(
        "x",
        AddExpression(VarExpression("x"), IntValue(1))
      ) == stmts1(1)
    )
    val nestedFor = stmts1.head.asInstanceOf[ForStmt]

    val initStmt2 = AssignmentStmt("y", VarExpression("x"))
    val condExpr2 = LTEExpression(VarExpression("y"), IntValue(20))
    val stmt2 = nestedFor.stmt.asInstanceOf[SequenceStmt].stmts

    assert(initStmt2 == nestedFor.init)
    assert(condExpr2 == nestedFor.condition)
    assert(WriteStmt(VarExpression("y")) == stmt2(0))
    assert(
      AssignmentStmt(
        "y",
        AddExpression(VarExpression("y"), IntValue(1))
      ) == stmt2(1)
    )

  }

  test(
    "Testing the oberon stmt28 code. This module has a ForRange stmt nested with another ForRange stmt"
  ) {
    val module = parseResource("stmts/stmt28.oberon")

    assert("ForRangeModule" == module.name)
    assert(4 == module.variables.length)

    module.stmt.getOrElse(None) match {
      case SequenceStmt(stmts) => assert(3 == stmts.length)
      case _                   => fail("Expected a sequence of 3 statements!!!")
    }

    val sequenceStmts =
      module.stmt.getOrElse(None).asInstanceOf[SequenceStmt].stmts
    assert(ReadIntStmt("min") == sequenceStmts.head)
    assert(ReadIntStmt("max") == sequenceStmts(1))

    val forRangeStmt = sequenceStmts(2).asInstanceOf[ForStmt]

    val initStmt1 = AssignmentStmt("x", VarExpression("min"))
    val condExpr1 = LTEExpression(VarExpression("x"), VarExpression("max"))

    assert(initStmt1 == forRangeStmt.init)
    assert(condExpr1 == forRangeStmt.condition)

    val stmts1 = forRangeStmt.stmt.asInstanceOf[SequenceStmt].stmts
    assert(
      AssignmentStmt(
        "x",
        AddExpression(VarExpression("x"), IntValue(1))
      ) == stmts1(1)
    )
    val nestedFor = stmts1(0).asInstanceOf[ForStmt]

    val initStmt2 = AssignmentStmt("y", VarExpression("x"))
    val condExpr2 = LTEExpression(VarExpression("y"), VarExpression("max"))
    val stmt2 = nestedFor.stmt.asInstanceOf[SequenceStmt].stmts

    assert(initStmt2 == nestedFor.init)
    assert(condExpr2 == nestedFor.condition)
    assert(WriteStmt(VarExpression("y")) == stmt2(0))
    assert(
      AssignmentStmt(
        "y",
        AddExpression(VarExpression("y"), IntValue(1))
      ) == stmt2(1)
    )

  }

}