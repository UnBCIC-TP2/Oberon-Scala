package br.unb.cic.oberon.tc

import java.nio.file.{Files, Paths}

import br.unb.cic.oberon.ast.{AddExpression, AssignmentStmt, BoolValue, BooleanType, ForStmt, IfElseStmt, IntValue, IntegerType, ReadIntStmt, SequenceStmt, Undef, VarExpression, WhileStmt, WriteStmt, CaseStmt, RangeCase, SimpleCase, RepeatUntilStmt, IfElseIfStmt, ElseIfStmt}
import br.unb.cic.oberon.ast.{LTExpression, LTEExpression, AndExpression, EQExpression, GTEExpression}
import br.unb.cic.oberon.parser.OberonParser.ReadIntStmtContext
import br.unb.cic.oberon.parser.ScalaParser
import org.scalatest.funsuite.AnyFunSuite

class TypeCheckerTestSuite  extends AnyFunSuite {

  test("Test read statement type checker") {
    val visitor = new TypeChecker()
    val read01 = ReadIntStmt(1, "x")
    val read02 = ReadIntStmt(2, "y")

    visitor.env.setGlobalVariable("x", IntegerType)

    assert(read01.accept(visitor) == List())
    assert(read02.accept(visitor).size == 1)
  }

  test("Test write statement type checker") {
    val visitor = new TypeChecker()
    val write01 = WriteStmt(1, IntValue(5))
    val write02 = WriteStmt(2, AddExpression(IntValue(5), BoolValue(false)))

    assert(write01.accept(visitor) == List())
    assert(write02.accept(visitor).size == 1)
  }

  test("Test assignment statement type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(10)) // invalid stmt
    val stmt03 = AssignmentStmt(3, "x", AddExpression(IntValue(5), BoolValue(false))) // invalid stmt

    visitor.env.setGlobalVariable("x", IntegerType)

    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor).size == 1)
    assert(stmt03.accept(visitor).size == 1)
  }

  test("Test a sequence of statements type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(10)) // invalid stmt
    val stmt03 = AssignmentStmt(3, "x", AddExpression(IntValue(5), BoolValue(false))) // invalid stmt
    val stmt04 = WriteStmt(4, VarExpression("x"))
    val stmt05 = WriteStmt(5, VarExpression("y"))

    visitor.env.setGlobalVariable("x", IntegerType)

    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor).size == 1)
    assert(stmt03.accept(visitor).size == 1)

    val seq1 = SequenceStmt(6, List(stmt01, stmt04))
    val seq2 = SequenceStmt(7, List(stmt01, stmt05))

    assert(seq1.accept(visitor).size == 0)
    assert(seq2.accept(visitor).size == 1)
  }

  test("Test if-else statement type checker (with invalid condition)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))

    visitor.env.setGlobalVariable("x", IntegerType)

    val stmt02 = IfElseStmt(2, IntValue(10), stmt01, None)
    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor).size == 1)
  }

  test("Test if-else statement type checker (with invalid then-stmt)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))

    val stmt02 = IfElseStmt(2, BoolValue(true), stmt01, None)
    assert(stmt01.accept(visitor).size == 1)
    assert(stmt02.accept(visitor).size == 1)
  }

  test("Test if-else statement type checker (with invalid then-stmt and else-stmt)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(10))
    val stmt03 = IfElseStmt(3, BoolValue(true), stmt01, Some(stmt02))

    assert(stmt01.accept(visitor).size == 1)
    assert(stmt02.accept(visitor).size == 1)
    assert(stmt03.accept(visitor).size == 2)
  }

  test("Test if-else statement type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(10))

    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("y", IntegerType)

    val stmt03 = IfElseStmt(3, BoolValue(true), stmt01, Some(stmt02))

    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 0)
    assert(stmt03.accept(visitor).size == 0)
  }
  
  test ("Test if-else-if statment type checker (invalid condition 'if')"){
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt(1, "x", IntValue(20))
    val stmt02 = AssignmentStmt(2, "z", IntValue(30))

    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("z", IntegerType)

    val stmt03 = ElseIfStmt(3, BoolValue(true), stmt02)
    val list1 = List(stmt03)
  
    val stmt04 = IfElseIfStmt(4, IntValue(34), stmt01, list1, None);
  
    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 0)
    assert(stmt04.accept(visitor).size == 1)
  }
  
  test ("Test else-if statment type checker (invalid condition 'else-if')"){
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt(1, "x", IntValue(40))
    val stmt02 = AssignmentStmt(2, "z", IntValue(100))

    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("z", IntegerType)
  
    val stmt03 = ElseIfStmt(3, IntValue(70), stmt02)
    val list1 = List(stmt03)
  
    val stmt04 = IfElseIfStmt(4, BoolValue(true), stmt01, list1, None);
    
    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 0)
    assert(stmt04.accept(visitor).size == 1)
  }

  test ("Test else-if statment type checker (invalid condition list 'else-if')"){
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt(1, "x", IntValue(40))
    val stmt02 = AssignmentStmt(2, "z", IntValue(100))

    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("z", IntegerType)

    val stmt03 = ElseIfStmt(3, BoolValue(true), stmt02)
    val stmt04 = ElseIfStmt(4, IntValue(73), stmt02)
    val stmt05 = ElseIfStmt(5, IntValue(58), stmt01)
    val stmt06 = ElseIfStmt(6, BoolValue(false), stmt01)
    val list1 = List(stmt03, stmt04, stmt05, stmt06)
    
    val stmt07 = IfElseIfStmt(7, BoolValue(true), stmt01, list1, None);
    
    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 0)
    assert(stmt07.accept(visitor).size == 2)
  }

  test ("Test else-if statment type checker (invalid then-stmt 'else-if')"){
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt(1, "x", IntValue(40))
    val stmt02 = AssignmentStmt(2, "z", IntValue(100))

    visitor.env.setGlobalVariable("x", IntegerType)

    val stmt03 = ElseIfStmt(3, BoolValue(true), stmt02)
    val list1 = List(stmt03)
  
    val stmt04 = IfElseIfStmt(4, BoolValue(true), stmt01, list1, None)
    
    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 1)
    assert(stmt04.accept(visitor).size == 1)
  }
  
  test("Test if-else-if statment type checker (invalid else-stmt)"){
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt(1, "x", IntValue(40))
    val stmt02 = AssignmentStmt(2, "z", IntValue(100))
    val stmt03 = AssignmentStmt(3, "w", IntValue(20))
    
    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("z", IntegerType)

    val stmt04 = ElseIfStmt(4, BoolValue(true), stmt02)
    val list1 = List(stmt04)

    val stmt05 = IfElseIfStmt(5, BoolValue(true), stmt01, list1, Some(stmt03))

    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 0)
    assert(stmt03.accept(visitor).size == 1)
    assert(stmt05.accept(visitor).size == 1)
  }

  test("Test if-else-if statment type checker (invalid then-stmt, 'else-if' then-stmt, 'else-if' invalid condition and else-stmt)"){
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt(1, "x", IntValue(40))
    val stmt02 = AssignmentStmt(2, "z", IntValue(100))
    val stmt03 = AssignmentStmt(3, "w", IntValue(20))

    val stmt04 = ElseIfStmt(4, IntValue(56), stmt02)
    val stmt05 = ElseIfStmt(5, IntValue(79), stmt01)
    val stmt06 = ElseIfStmt(6, BoolValue(true), stmt02)
    val list1 = List(stmt04, stmt05, stmt06)

    val stmt07 = IfElseIfStmt(7, BoolValue(true), stmt01, list1, Some(stmt03))

    assert(stmt01.accept(visitor).size == 1)
    assert(stmt02.accept(visitor).size == 1)
    assert(stmt03.accept(visitor).size == 1)
    assert(stmt07.accept(visitor).size == 5)
  }
  
  test("Test if-else-if statment type checker"){
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt(1, "x", IntValue(15))
    val stmt02 = AssignmentStmt(2, "y", IntValue(5))
    
    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("y", IntegerType)

    val stmt03 = ElseIfStmt(3, BoolValue(true), stmt02)
    val list1 = List(stmt03)
  
    val stmt04 = IfElseIfStmt(4, BoolValue(true), stmt01, list1, None);
  
    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 0)
    assert(stmt04.accept(visitor).size == 0)
    
  }

  test("Test while statement type checker (with invalid condition)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))

    visitor.env.setGlobalVariable("x", IntegerType)

    val stmt02 = WhileStmt(2, IntValue(10), stmt01)
    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor).size == 1)
  }

  test("Test while statement type checker (with invalid stmt)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))

    val stmt02 = WhileStmt(2, BoolValue(true), stmt01)
    assert(stmt01.accept(visitor).size == 1)
    assert(stmt02.accept(visitor).size == 1)
  }

  test("Test while statement type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))

    visitor.env.setGlobalVariable("x", IntegerType)

    val stmt02 = WhileStmt(2, BoolValue(true), stmt01)
    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 0)
  }

  test("Test for statement type checker (with invalid init)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(10))

    visitor.env.setGlobalVariable("y", IntegerType)

    val stmt03 = ForStmt(3, stmt01, BoolValue(true), stmt02)
    assert(stmt01.accept(visitor).size == 1)
    assert(stmt02.accept(visitor).size == 0)
    assert(stmt03.accept(visitor).size == 1)
  }

  test("Test for statement type checker (with invalid condition)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(10))

    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("y", IntegerType)

    val stmt03 = ForStmt(3, stmt01,IntValue(10), stmt02)
    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 0)
    assert(stmt03.accept(visitor).size == 1)
  }

  test("Test for statement type checker (with invalid stmt)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(100))

    visitor.env.setGlobalVariable("x", IntegerType)

    val stmt03 = ForStmt(3, stmt01, BoolValue(true), stmt02)
    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 1)
    assert(stmt03.accept(visitor).size == 1)
  }

  test("Test for statement type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt(1, "x", IntValue(0))
    val stmt02 = AssignmentStmt(2, "y", IntValue(10))

    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("y", IntegerType)

    val stmt03 = ForStmt(3, stmt01, BoolValue(true), stmt02)
    assert(stmt01.accept(visitor).size == 0)
    assert(stmt02.accept(visitor).size == 0)
    assert(stmt03.accept(visitor).size == 0)
  }

  test("Test switch-case statement type checker RangeCase (invalid case01 min expression) ") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(15))
    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt(3, "x", IntValue(20))


    val case01 = RangeCase(BoolValue(false), IntValue(20), stmt01)
    val case02 = RangeCase(IntValue(21), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(4, IntValue(11), cases, Some(caseElse))

    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor) == List())
    assert(caseElse.accept(visitor) == List())
    assert(stmt03.accept(visitor).size == 1)
  }

  test("Test switch-case statement type checker RangeCase (invalid case02 min expression) ") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(15))
    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt(3, "x", IntValue(20))


    val case01 = RangeCase(IntValue(21), IntValue(20), stmt01)
    val case02 = RangeCase(BoolValue(false), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(4, IntValue(11), cases, Some(caseElse))

    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor) == List())
    assert(caseElse.accept(visitor) == List())
    assert(stmt03.accept(visitor).size == 1)
  }

  test("Test switch-case statement type checker RangeCase (invalid case01 and case02 min expression) ") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(15))
    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt(3, "x", IntValue(20))


    val case01 = RangeCase(BoolValue(false), IntValue(20), stmt01)
    val case02 = RangeCase(BoolValue(false), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(4, IntValue(11), cases, Some(caseElse))

    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor) == List())
    assert(caseElse.accept(visitor) == List())
    assert(stmt03.accept(visitor).size == 2)
  }

  test("Test switch-case statement type checker RangeCase (invalid case01 and case02 max expression) ") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(15))
    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt(3, "x", IntValue(20))


    val case01 = RangeCase(IntValue(20),BoolValue(false), stmt01)
    val case02 = RangeCase(IntValue(30),BoolValue(false), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(4, IntValue(11), cases, Some(caseElse))

    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor) == List())
    assert(caseElse.accept(visitor) == List())
    assert(stmt03.accept(visitor).size == 2)
  }

  test("Test switch-case statement type checker RangeCase (invalid CaseStmt exp) ") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(15))
    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt(3, "x", IntValue(20))


    val case01 = RangeCase(IntValue(20),IntValue(30), stmt01)
    val case02 = RangeCase(IntValue(30),IntValue(40), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(4, Undef(), cases, Some(caseElse))

    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor) == List())
    assert(caseElse.accept(visitor) == List())
    assert(stmt03.accept(visitor).size == 1)
  }

  test("Test switch-case statement type checker SimpleCase (invalid CaseStmt condition)") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    visitor.env.setGlobalVariable("x", IntegerType)

    val caseElse = AssignmentStmt(2, "x", IntValue(20))


    val case01 = SimpleCase(BoolValue(true), stmt01)
    val case02 = SimpleCase(BoolValue(false), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(3, BoolValue(true), cases, Some(caseElse))

    assert(stmt01.accept(visitor) == List())
    assert(caseElse.accept(visitor) == List())
    assert(stmt02.accept(visitor).size ==  1)
  }

  test("Test switch-case statement type checker SimpleCase (invalid case02 condition)") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    visitor.env.setGlobalVariable("x", IntegerType)

    val caseElse = AssignmentStmt(2, "x", IntValue(20))


    val case01 = SimpleCase(IntValue(10), stmt01)
    val case02 = SimpleCase(BoolValue(false), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(3, IntValue(10), cases, Some(caseElse))

    assert(stmt01.accept(visitor) == List())
    assert(caseElse.accept(visitor) == List())
    assert(stmt02.accept(visitor).size ==  1)
  }

  test("Test switch-case statement type checker SimpleCase (invalid case01 and case02 condition)") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    visitor.env.setGlobalVariable("x", IntegerType)

    val caseElse = AssignmentStmt(2, "x", IntValue(20))


    val case01 = SimpleCase(Undef(), stmt01)
    val case02 = SimpleCase(Undef(), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(3, IntValue(10), cases, Some(caseElse))

    assert(stmt01.accept(visitor) == List())
    assert(caseElse.accept(visitor) == List())
    assert(stmt02.accept(visitor).size ==  2)
  }

  test("Test switch-case statement type checker RangeCase") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = AssignmentStmt(2, "y", IntValue(15))
    visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt(3, "x", IntValue(20))


    val case01 = RangeCase(IntValue(10), IntValue(20), stmt01)
    val case02 = RangeCase(IntValue(21), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(4, IntValue(11), cases, Some(caseElse))

    assert(stmt01.accept(visitor) == List())
    assert(stmt02.accept(visitor) == List())
    assert(caseElse.accept(visitor) == List())
    assert(stmt03.accept(visitor) == List())
  }

  test("Test switch-case statement type checker SimpleCase") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    visitor.env.setGlobalVariable("x", IntegerType)

    val caseElse = AssignmentStmt(2, "x", IntValue(20))


    val case01 = SimpleCase(IntValue(10), stmt01)
    val case02 = SimpleCase(IntValue(20), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(3, IntValue(10), cases, Some(caseElse))

    assert(stmt01.accept(visitor) == List())
    assert(caseElse.accept(visitor) == List())
    assert(stmt02.accept(visitor) == List())
  }

  /*
   * the following test cases read an oberon module with the
   * factorial procedure.
   */
  test("Test invalid procedure declaration") {
    val path = Paths.get(getClass.getClassLoader.getResource("procedures/procedure04.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module  = ScalaParser.parse(content)

    assert(module.name == "SimpleModule")

    assert(module.procedures.size == 2)
    assert(module.stmt.isDefined)

  }

  test("Test the type checker of a valid Repeat statement") {
    val visitor = new TypeChecker()
    
    val condition  = LTExpression(VarExpression("x"), IntValue(10))
    val stmt01     = ReadIntStmt(1, "x")
    val repeatStmt = RepeatUntilStmt(2, condition, stmt01)

    visitor.env.setGlobalVariable("x", IntegerType)

    assert(stmt01.accept(visitor) == List())
    assert(repeatStmt.accept(visitor) == List())    
  }


  test("Test the type checker of a valid Repeat statement 2"){
    val visitor = new TypeChecker()
    
    val condition  = EQExpression(VarExpression("x"), IntValue(0))
    val stmt01     = ReadIntStmt(1, "x")
    val repeatStmt = RepeatUntilStmt(2, condition, stmt01)
    
    visitor.env.setGlobalVariable("x", IntegerType)
    
    assert(stmt01.accept(visitor) == List())
    assert(repeatStmt.accept(visitor) == List())  
    
  }
  
  test("Test the type checker of a valid Repeat statement 3"){
    val visitor = new TypeChecker()
    val stmt01  =  AssignmentStmt(1, "x", IntValue(10))
    
    val stmt02  = RepeatUntilStmt(2, BoolValue(true), stmt01)

    assert(stmt01.accept(visitor).size == 1)
    assert(stmt02.accept(visitor).size == 1)
  }  

  test("Test a invalid Repeat statement in the type checker") {
    val visitor = new TypeChecker()
  
    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val stmt02 = ReadIntStmt(2, "x")
    val stmt03 = IfElseStmt(3, BoolValue(false), stmt01, Some(stmt02))
    val stmt04 = AssignmentStmt(4, "x", IntValue(20))
    val stmt05 = SequenceStmt(5, List(stmt01, stmt02, stmt03, stmt04))
    val stmt06 = RepeatUntilStmt(6, BoolValue(true), stmt05)

    assert(stmt01.accept(visitor).size == 1)
    assert(stmt02.accept(visitor).size == 1)
    assert(stmt03.accept(visitor).size == 2)
    assert(stmt04.accept(visitor).size == 1)
    assert(stmt05.accept(visitor).size == 5)
    assert(stmt06.accept(visitor).size == 5)
  }

  
  test("Test the type checker of a valid Repeat statement 4"){
  val visitor = new TypeChecker()
  
  val condition  = AndExpression(GTEExpression(VarExpression("x"), IntValue(1)),
    LTEExpression(VarExpression("x"), IntValue(10)))
  val stmt01     = ReadIntStmt(1, "x")
  val repeatStmt = RepeatUntilStmt(2, condition, stmt01)
  
  visitor.env.setGlobalVariable("x", IntegerType)
  
  assert(stmt01.accept(visitor) == List())
  assert(repeatStmt.accept(visitor) == List())  
  
  }

  test("Test a valid Repeat statement, with nested Repeat statements") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt(1, "x", IntValue(10))
    val repeatStmt01 = RepeatUntilStmt(2, BoolValue(true), stmt01)
    val repeatStmt02 = RepeatUntilStmt(3, BoolValue(true), repeatStmt01)
    val repeatStmt03 = RepeatUntilStmt(4, BoolValue(true), repeatStmt02)
    val repeatStmt04 = RepeatUntilStmt(5, BoolValue(true), repeatStmt03)
    
    visitor.env.setGlobalVariable("x", IntegerType)
    val allStmts = List(stmt01, repeatStmt01, repeatStmt02, repeatStmt03, repeatStmt04)

    allStmts.foreach(stmt => {
      assert(stmt.accept(visitor).size == 0)
    })
  }

  test("Test a invalid Repeat statement, with nested Repeat statements") {
    val visitor = new TypeChecker()

    val stmt01       = AssignmentStmt(1, "x", IntValue(10))
    val repeatStmt01 = RepeatUntilStmt(2, BoolValue(true), stmt01)
    val repeatStmt02 = RepeatUntilStmt(3, BoolValue(true), repeatStmt01)
    val repeatStmt03 = RepeatUntilStmt(4, BoolValue(true), repeatStmt02)
    val repeatStmt04 = RepeatUntilStmt(5, BoolValue(true), repeatStmt03)
    
    val allStmts = List(repeatStmt01, repeatStmt02, repeatStmt03, repeatStmt04)

    allStmts.foreach(stmt => {
      assert(stmt.accept(visitor).size == 1)
    })
  }

  test("Test a valid Repeat statement, with a boolean variable") {
    val visitor = new TypeChecker()

    val boolVar    = VarExpression("flag")
    val stmt01     = AssignmentStmt(1, boolVar.name, BoolValue(true))
    val repeatStmt = RepeatUntilStmt(2, boolVar, stmt01)

    visitor.env.setGlobalVariable("flag", BooleanType)

    assert(repeatStmt.accept(visitor).size == 0) 

  }

  test("Test a valid Repeat statement, with a sequence of statements") {
    val visitor = new TypeChecker()

    val stmt01     = AssignmentStmt(1, "x", BoolValue(false))
    val repeatStmt = RepeatUntilStmt(2, BoolValue(true), stmt01)
    val stmt02     = SequenceStmt(3, List(stmt01, repeatStmt, stmt01, repeatStmt))

    visitor.env.setGlobalVariable("x", IntegerType)

    assert(stmt02.accept(visitor).size == 0)
  }

  test("Test a invalid Repeat statement, with a sequence of statements") {
    val visitor = new TypeChecker()

    val stmt01     = AssignmentStmt(1, "x", BoolValue(false))
    val repeatStmt = RepeatUntilStmt(2, BoolValue(true), stmt01)
    val stmt02     = SequenceStmt(3, List(stmt01, repeatStmt, stmt01, repeatStmt))

    assert(stmt02.accept(visitor).size == 4)
  }

  test("Test a loop statement, from loop_stmt03") {
    val path = Paths.get(getClass.getClassLoader.getResource("stmts/loop_stmt03.oberon").toURI)

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = ScalaParser.parse(content)

    assert(module.name == "LoopStmt")

    val visitor = new TypeChecker()

    val errors = visitor.visit(module)

    assert(errors.size == 0)
  }

}
