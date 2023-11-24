package br.unb.cic.oberon.tc

import br.unb.cic.oberon.AbstractTestSuite

import java.nio.file.{Files, Paths}
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.ir.ast.{
  AndExpression,
  EQExpression,
  GTEExpression,
  LTEExpression,
  LTExpression,
}
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.parser.Oberon2ScalaParser
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.transformations.{CoreChecker, CoreTransformer}
import br.unb.cic.oberon.ir.ast.{OberonModule, VariableDeclaration}
import br.unb.cic.oberon.environment.Environment
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

class TypeCheckerTestSuite  extends AbstractTestSuite with Oberon2ScalaParser {


  test("Test read int statement type checker") {
    val visitor = new TypeChecker()
    val read01 = ReadIntStmt("x")
    val read02 = ReadIntStmt("y")
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    assert(visitor.checkStmt(read01) == List())
    assert(visitor.checkStmt(read02).isEmpty)
  }

  test("Test read real statement type checker") {
    val visitor = new TypeChecker()
    val read01 = ReadRealStmt("x")
    val read02 = ReadRealStmt("y")

    visitor.env = visitor.env.setGlobalVariable("x", RealType)
    assert(visitor.checkStmt(read01) == List())
    assert(visitor.checkStmt(read02).size == 1)
  }

  test("Test write statement type checker") {
    val visitor = new TypeChecker()
    val write01 = WriteStmt(IntValue(5))
    val write02 = WriteStmt(AddExpression(IntValue(5), BoolValue(false)))

    assert(visitor.checkStmt(write01) == List())
    assert(visitor.checkStmt(write02).size == 1)
  }

  test("Test assignment statement type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(10)) // invalid stmt
    val stmt03 = AssignmentStmt(
      "x",
      AddExpression(IntValue(5), BoolValue(false))
    ) 
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(stmt02).size == 1)
    assert(visitor.checkStmt(stmt03).size == 1)
  }

  test("Test a sequence of statements type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(10)) // invalid stmt
    val stmt03 = AssignmentStmt(
      "x",
      AddExpression(IntValue(5), BoolValue(false))
    ) // invalid stmt
    val stmt04 = WriteStmt(VarExpression("x"))
    val stmt05 = WriteStmt(VarExpression("y"))


    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(stmt02).size == 1)
    assert(visitor.checkStmt(stmt03).size == 1)

    val seq1 = SequenceStmt(List(stmt01, stmt04))
    val seq2 = SequenceStmt(List(stmt01, stmt05))

    assert(visitor.checkStmt(seq1).size == 0)
    assert(visitor.checkStmt(seq2).size == 1)
  }

  test("Test if-else statement type checker (with invalid condition)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = IfElseStmt(IntValue(10), stmt01, None)

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(stmt02).size == 1)
  }

  test("Test if-else statement type checker (with invalid then-stmt)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))

    val stmt02 = IfElseStmt(BoolValue(true), stmt01, None)
    assert(visitor.checkStmt(stmt01).size == 1)
    assert(visitor.checkStmt(stmt02).size == 1)
  }

  test(
    "Test if-else statement type checker (with invalid then-stmt and else-stmt)"
  ) {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(10))
    val stmt03 = IfElseStmt(BoolValue(true), stmt01, Some(stmt02))

    assert(visitor.checkStmt(stmt01).size == 1)
    assert(visitor.checkStmt(stmt02).size == 1)
    assert(visitor.checkStmt(stmt03).size == 2)
  }

  test("Test if-else statement type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(10))


    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    val stmt03 = IfElseStmt(BoolValue(true), stmt01, Some(stmt02))

    assert(visitor.checkStmt(stmt01).size == 0)
    assert(visitor.checkStmt(stmt02).size == 0)
    assert(visitor.checkStmt(stmt03).size == 0)
  }

  test("Test if-else-if statment type checker (invalid condition 'if')") {
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt("x", IntValue(20))
    val stmt02 = AssignmentStmt("z", IntValue(30))


    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("z", IntegerType)

    val stmt03 = ElseIfStmt(BoolValue(true), stmt02)
    val list1 = List(stmt03)

    val stmt04 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(IntValue(34), stmt01, list1, None)
    )

    assert(visitor.checkStmt(stmt01).size == 0)
    assert(visitor.checkStmt(stmt02).size == 0)
    assert(visitor.checkStmt(stmt04).size == 1)
  }

  test("Test else-if statment type checker (invalid condition 'else-if')") {
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt("x", IntValue(40))
    val stmt02 = AssignmentStmt("z", IntValue(100))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("z", IntegerType)

    val stmt03 = ElseIfStmt(IntValue(70), stmt02)
    val list1 = List(stmt03)

    val stmt04 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, None)
    )

    assert(visitor.checkStmt(stmt01).size == 0)
    assert(visitor.checkStmt(stmt02).size == 0)
    assert(visitor.checkStmt(stmt04).size == 1)
  }

  test(
    "Test else-if statment type checker (invalid condition list 'else-if')"
  ) {
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt("x", IntValue(40))
    val stmt02 = AssignmentStmt("z", IntValue(100))


    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("z", IntegerType)

    val stmt03 = ElseIfStmt(BoolValue(true), stmt02)
    val stmt04 = ElseIfStmt(IntValue(73), stmt02)
    val stmt05 = ElseIfStmt(IntValue(58), stmt01)
    val stmt06 = ElseIfStmt(BoolValue(false), stmt01)
    val list1 = List(stmt03, stmt04, stmt05, stmt06)

    val stmt07 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, None)
    )

    assert(visitor.checkStmt(stmt01).size == 0)
    assert(visitor.checkStmt(stmt02).size == 0)
    assert(visitor.checkStmt(stmt07).size == 2)
  }

  test("Test else-if statment type checker (invalid then-stmt 'else-if')") {
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt("x", IntValue(40))
    val stmt02 = AssignmentStmt("z", IntValue(100))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    val stmt03 = ElseIfStmt(BoolValue(true), stmt02)
    val list1 = List(stmt03)

    val stmt04 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, None)
    )

    assert(visitor.checkStmt(stmt01).size == 0)
    assert(visitor.checkStmt(stmt02).size == 1)
    assert(visitor.checkStmt(stmt04).size == 1)
  }

  test("Test if-else-if statment type checker (invalid else-stmt)") {
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt("x", IntValue(40))
    val stmt02 = AssignmentStmt("z", IntValue(100))
    val stmt03 = AssignmentStmt("w", IntValue(20))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("z", IntegerType)

    val stmt04 = ElseIfStmt(BoolValue(true), stmt02)
    val list1 = List(stmt04)

    val stmt05 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, Some(stmt03))
    )

    assert(visitor.checkStmt(stmt01).size == 0)
    assert(visitor.checkStmt(stmt02).size == 0)
    assert(visitor.checkStmt(stmt03).size == 1)
    assert(visitor.checkStmt(stmt05).size == 1)
  }

  test(
    "Test if-else-if statment type checker (invalid then-stmt, 'else-if' then-stmt, 'else-if' invalid condition and else-stmt)"
  ) {
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt("x", IntValue(40))
    val stmt02 = AssignmentStmt("z", IntValue(100))
    val stmt03 = AssignmentStmt("w", IntValue(20))

    val stmt04 = ElseIfStmt(IntValue(56), stmt02)
    val stmt05 = ElseIfStmt(IntValue(79), stmt01)
    val stmt06 = ElseIfStmt(BoolValue(true), stmt02)
    val list1 = List(stmt04, stmt05, stmt06)

    val stmt07 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, Some(stmt03))
    )

    assert(visitor.checkStmt(stmt01).size == 1)
    assert(visitor.checkStmt(stmt02).size == 1)
    assert(visitor.checkStmt(stmt03).size == 1)
    assert(visitor.checkStmt(stmt07).size == 7)
  }

  test("Test if-else-if statment type checker") {
    val visitor = new TypeChecker
    val stmt01 = AssignmentStmt("x", IntValue(15))
    val stmt02 = AssignmentStmt("y", IntValue(5))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    val stmt03 = ElseIfStmt(BoolValue(true), stmt02)
    val list1 = List(stmt03)

    val stmt04 = CoreTransformer.reduceToCoreStatement(
      IfElseIfStmt(BoolValue(true), stmt01, list1, None)
    )

    assert(visitor.checkStmt(stmt01).size == 0)
    assert(visitor.checkStmt(stmt02).size == 0)
    assert(visitor.checkStmt(stmt04).size == 0)

  }

  test("Test while statement type checker (with invalid condition)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    val stmt02 = WhileStmt(IntValue(10), stmt01)
    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(stmt02).size == 1)
  }

  test("Test while statement type checker (with invalid stmt)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))

    val stmt02 = WhileStmt(BoolValue(true), stmt01)
    assert(visitor.checkStmt(stmt01).size == 1)
    assert(visitor.checkStmt(stmt02).size == 1)
  }

  test("Test while statement type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    val stmt02 = WhileStmt(BoolValue(true), stmt01)
    assert(visitor.checkStmt(stmt01).size == 0)
    assert(visitor.checkStmt(stmt02).size == 0)
  }

  test("Test for statement type checker (with invalid init)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(10))

    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    val stmt03 = CoreTransformer.reduceToCoreStatement(
      ForStmt(stmt01, BoolValue(true), stmt02)
    )
    assert(visitor.checkStmt(stmt01).size == 1)
    assert(visitor.checkStmt(stmt02).size == 0)
    assert(visitor.checkStmt(stmt03).size == 1)
  }

  test("Test for statement type checker (with invalid condition)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(10))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    val stmt03 = CoreTransformer.reduceToCoreStatement(
      ForStmt(stmt01, IntValue(10), stmt02)
    )
    assert(visitor.checkStmt(stmt01).size == 0)
    assert(visitor.checkStmt(stmt02).size == 0)
    assert(visitor.checkStmt(stmt03).size == 1)
  }

  test("Test for statement type checker (with invalid stmt)") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(100))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    val stmt03 = CoreTransformer.reduceToCoreStatement(
      ForStmt(stmt01, BoolValue(true), stmt02)
    )
    assert(visitor.checkStmt(stmt01).size == 0)
    assert(visitor.checkStmt(stmt02).size == 1)
    assert(visitor.checkStmt(stmt03).size == 1)
  }

  test("Test for statement type checker") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(0))
    val stmt02 = AssignmentStmt("y", IntValue(10))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    val stmt03 = CoreTransformer.reduceToCoreStatement(
      ForStmt(stmt01, BoolValue(true), stmt02)
    )
    assert(visitor.checkStmt(stmt01).size == 0)
    assert(visitor.checkStmt(stmt02).size == 0)
    assert(visitor.checkStmt(stmt03).size == 0)
  }

  test(
    "Test switch-case statement type checker RangeCase (invalid case01 min expression) "
  ) {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(15))
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt("x", IntValue(20))

    val case01 = RangeCase(BoolValue(false), IntValue(20), stmt01)
    val case02 = RangeCase(IntValue(21), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(IntValue(11), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(stmt02) == List())
    assert(visitor.checkStmt(caseElse) == List())
    assert(visitor.checkModule(testModuleCore).size == 1)
  }

  test(
    "Test switch-case statement type checker RangeCase (invalid case02 min expression) "
  ) {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(15))
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt("x", IntValue(20))

    val case01 = RangeCase(IntValue(21), IntValue(20), stmt01)
    val case02 = RangeCase(BoolValue(false), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(IntValue(11), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(stmt02) == List())
    assert(visitor.checkStmt(caseElse) == List())
    assert(visitor.checkModule(testModuleCore).size == 1)
  }

  test(
    "Test switch-case statement type checker RangeCase (invalid case01 and case02 min expression) "
  ) {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(15))
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt("x", IntValue(20))

    val case01 = RangeCase(BoolValue(false), IntValue(20), stmt01)
    val case02 = RangeCase(BoolValue(false), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(IntValue(11), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(stmt02) == List())
    assert(visitor.checkStmt(caseElse) == List())
    assert(visitor.checkModule(testModuleCore).size == 2)
  }

  test(
    "Test switch-case statement type checker RangeCase (invalid case01 and case02 max expression) "
  ) {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(15))
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt("x", IntValue(20))

    val case01 = RangeCase(IntValue(20), BoolValue(false), stmt01)
    val case02 = RangeCase(IntValue(30), BoolValue(false), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(IntValue(11), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(stmt02) == List())
    assert(visitor.checkStmt(caseElse) == List())
    assert(visitor.checkModule(testModuleCore).size == 2)
  }

  test(
    "Test switch-case statement type checker RangeCase (invalid CaseStmt exp) "
  ) {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(15))
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt("x", IntValue(20))

    val case01 = RangeCase(IntValue(20), IntValue(30), stmt01)
    val case02 = RangeCase(IntValue(30), IntValue(40), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(Undef(), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(stmt02) == List())
    assert(visitor.checkStmt(caseElse) == List())
    assert(visitor.checkModule(testModuleCore).size == 1)
  }

  ignore("Test switch-case statement type checker SimpleCase (Boolean cases)") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    val caseElse = AssignmentStmt("x", IntValue(20))

    val case01 = SimpleCase(BoolValue(true), stmt01)
    val case02 = SimpleCase(BoolValue(false), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(BoolValue(true), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(VariableDeclaration("x", IntegerType)),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt02)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(caseElse) == List())
    assert(visitor.checkModule(testModuleCore) == List())
  }

  test(
    "Test switch-case statement type checker SimpleCase (invalid case02 condition)"
  ) {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    val caseElse = AssignmentStmt("x", IntValue(20))

    val case01 = SimpleCase(IntValue(10), stmt01)
    val case02 = SimpleCase(BoolValue(false), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(IntValue(10), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(VariableDeclaration("x", IntegerType)),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt02)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(caseElse) == List())
    assert(visitor.checkModule(testModuleCore).size == 1)
  }

  test(
    "Test switch-case statement type checker SimpleCase (invalid case01 and case02 condition)"
  ) {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    val caseElse = AssignmentStmt("x", IntValue(20))

    val case01 = SimpleCase(Undef(), stmt01)
    val case02 = SimpleCase(Undef(), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(IntValue(10), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(VariableDeclaration("x", IntegerType)),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt02)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(caseElse) == List())
    assert(visitor.checkModule(testModuleCore).size == 2)
  }

  test("Test switch-case statement type checker RangeCase") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = AssignmentStmt("y", IntValue(15))
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("y", IntegerType)

    val caseElse = AssignmentStmt("x", IntValue(20))

    val case01 = RangeCase(IntValue(10), IntValue(20), stmt01)
    val case02 = RangeCase(IntValue(21), IntValue(30), stmt02)

    val cases = List(case01, case02)

    val stmt03 = CaseStmt(IntValue(11), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(
        VariableDeclaration("x", IntegerType),
        VariableDeclaration("y", IntegerType)
      ),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt03)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(stmt02) == List())
    assert(visitor.checkStmt(caseElse) == List())
    assert(visitor.checkModule(testModuleCore) == List())
  }

  test("Test switch-case statement type checker SimpleCase") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    val caseElse = AssignmentStmt("x", IntValue(20))

    val case01 = SimpleCase(IntValue(10), stmt01)
    val case02 = SimpleCase(IntValue(20), stmt01)
    val cases = List(case01, case02)

    val stmt02 = CaseStmt(IntValue(10), cases, Some(caseElse))

    val testModule = OberonModule(
      name = "switch-case-test",
      submodules = Set(),
      userTypes = Nil,
      constants = Nil,
      variables = List(VariableDeclaration("x", IntegerType)),
      procedures = Nil,
      tests = Nil,
      stmt = Some(stmt02)
    )

    val testModuleCore = CoreTransformer.reduceOberonModule(testModule)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(caseElse) == List())
    assert(visitor.checkModule(testModuleCore) == List())
  }

  /*
   * the following test cases read an oberon module with the
   * factorial procedure.
   */
  test("Test invalid procedure declaration") {
    val module = parseResource("procedures/procedure04.oberon")

    assert(module.name == "SimpleModule")

    assert(module.procedures.size == 2)
    assert(module.stmt.isDefined)

  }

  test("Test the type checker of a valid Repeat statement") {
    val visitor = new TypeChecker()

    val condition = LTExpression(VarExpression("x"), IntValue(10))
    val stmt01 = ReadIntStmt("x")
    val repeatStmt =
      CoreTransformer.reduceToCoreStatement(RepeatUntilStmt(condition, stmt01))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(repeatStmt) == List())
  }

  test("Test the type checker of a valid Repeat statement 2") {
    val visitor = new TypeChecker()

    val condition = EQExpression(VarExpression("x"), IntValue(0))
    val stmt01 = ReadIntStmt("x")
    val repeatStmt =
      CoreTransformer.reduceToCoreStatement(RepeatUntilStmt(condition, stmt01))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(repeatStmt) == List())
  }

  test("Test the type checker of a valid Repeat statement 3") {
    val visitor = new TypeChecker()
    val stmt01 = AssignmentStmt("x", IntValue(10))

    val stmt02 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt01)
    )

    assert(visitor.checkStmt(stmt01).size == 1)
    assert(visitor.checkStmt(stmt02).size == 1)
  }

  test("Test a invalid Repeat statement in the type checker") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    val stmt02 = ReadIntStmt("x")
    val stmt03 = IfElseStmt(BoolValue(false), stmt01, Some(stmt02))
    val stmt04 = AssignmentStmt("x", IntValue(20))
    val stmt05 = SequenceStmt(List(stmt01, stmt02, stmt03, stmt04))
    val stmt06 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt05)
    )

    assert(visitor.checkStmt(stmt01).size == 1)
    assert(visitor.checkStmt(stmt02).size == 1)
    assert(visitor.checkStmt(stmt03).size == 2)
    assert(visitor.checkStmt(stmt04).size == 1)
    assert(visitor.checkStmt(stmt05).size == 5)
    assert(visitor.checkStmt(stmt06).size == 5)
  }

  test("Test the type checker of a valid Repeat statement 4") {
    val visitor = new TypeChecker()

    val condition = AndExpression(
      GTEExpression(VarExpression("x"), IntValue(1)),
      LTEExpression(VarExpression("x"), IntValue(10))
    )
    val stmt01 = ReadIntStmt("x")
    val repeatStmt = CoreTransformer.reduceToCoreStatement(RepeatUntilStmt(condition, stmt01))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    assert(visitor.checkStmt(stmt01) == List())
    assert(visitor.checkStmt(repeatStmt) == List())

  }

  test("Test a valid Repeat statement, with nested Repeat statements") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    val repeatStmt01 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt01)
    )
    val repeatStmt02 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt01)
    )
    val repeatStmt03 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt02)
    )
    val repeatStmt04 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt03)
    )

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    val allStmts =
      List(stmt01, repeatStmt01, repeatStmt02, repeatStmt03, repeatStmt04)

    allStmts.foreach(stmt => {
      assert(visitor.checkStmt(stmt).size == 0)
    })
  }

  test("Test a invalid Repeat statement, with nested Repeat statements") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", IntValue(10))
    val repeatStmt01 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt01)
    )
    val repeatStmt02 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt01)
    )
    val repeatStmt03 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt02)
    )
    val repeatStmt04 = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), repeatStmt03)
    )

    val allStmts = List(repeatStmt01, repeatStmt02, repeatStmt03, repeatStmt04)

    allStmts.foreach(stmt => {
      assert(visitor.checkStmt(stmt).size == 1)
    })
  }

  test("Test a valid Repeat statement, with a boolean variable") {
    val visitor = new TypeChecker()

    val boolVar = VarExpression("flag")
    val stmt01 = AssignmentStmt(boolVar.name, BoolValue(true))
    val repeatStmt =
      CoreTransformer.reduceToCoreStatement(RepeatUntilStmt(boolVar, stmt01))

    visitor.env = visitor.env.setGlobalVariable("flag", BooleanType)

    assert(visitor.checkStmt(repeatStmt).size == 0)

  }

  test("Test a valid Repeat statement, with a sequence of statements") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", BoolValue(false))
    val repeatStmt = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt01)
    )
    val stmt02 = SequenceStmt(List(stmt01, repeatStmt, stmt01, repeatStmt))

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    assert(visitor.checkStmt(stmt02).size == 0)
  }

  test("Test a invalid Repeat statement, with a sequence of statements") {
    val visitor = new TypeChecker()

    val stmt01 = AssignmentStmt("x", BoolValue(false))
    val repeatStmt = CoreTransformer.reduceToCoreStatement(
      RepeatUntilStmt(BoolValue(true), stmt01)
    )
    val stmt02 = SequenceStmt(List(stmt01, repeatStmt, stmt01, repeatStmt))

    assert(visitor.checkStmt(stmt02).size == 4)
  }

  test("Test a loop statement, from loop_stmt03") {
    val path = Paths.get(
      getClass.getClassLoader.getResource("stmts/loop_stmt03.oberon").toURI
    )

    assert(path != null)

    val content = String.join("\n", Files.readAllLines(path))
    val module = parseAbs(parse(oberonParser,content))

    assert(module.name == "LoopStmt")

    val visitor = new TypeChecker()

    val errors = visitor.checkModule(CoreTransformer.reduceOberonModule(module))

    assert(errors.size == 0)
  }

  test("Test assignment to pointer value") {
    val module = parseResource("stmts/tc_PointerAccessStmt.oberon")
    val visitor = new TypeChecker()
    val errors = visitor.checkModule(module)

    assert(errors.size == 0)
  }

  test("Test arithmetic operation with pointers") {
    val module = parseResource("stmts/tc_PointerOperation.oberon")
    val visitor = new TypeChecker()
    val errors = visitor.checkModule(module)

    assert(errors.size == 0)
  }

  test("Test incorrect assignment between pointer and simple type variable") {
    val module = parseResource("stmts/tc_PointerAssignmentWrong.oberon")
    val visitor = new TypeChecker()
    val errors = visitor.checkModule(module)

    val erro1 = (
      AssignmentStmt("x", VarExpression("p")),
      "Assignment between different types: x, VarExpression(p)"
    )
    val erro2 = (
      AssignmentStmt("p", VarExpression("x")),
      "Assignment between different types: p, VarExpression(x)"
    )
    assert(errors.size == 2)
    assert(errors == List(erro1, erro2))

  }

  test("Test incorrect assignment between pointer and arithmetic operation") {
    val module = parseResource("stmts/tc_PointerOperationWrong.oberon")

    val visitor = new TypeChecker()
    val errors = visitor.checkModule(module)

    val erro1 = (
      AssignmentStmt(
        "p",
        AddExpression(VarExpression("x"), VarExpression("y"))
      ),
      "Assignment between different types: p, AddExpression(VarExpression(x),VarExpression(y))"
    )

    assert(errors.size == 1)
    assert(errors == List(erro1))
  }

  test("Test assignment of NullValue to pointer") {
    val module = parseResource("stmts/tc_PointerNull.oberon")

    val visitor = new TypeChecker()
    val errors = visitor.checkModule(module)

    assert(errors.size == 0)
  }

  test("Test array subscript") {
    val visitor = new TypeChecker()
    visitor.env =visitor.env.setGlobalVariable("arr", ArrayType(1, IntegerType))


    val stmt = WriteStmt(ArraySubscript(VarExpression("arr"), IntValue(0)))

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 0)
  }

  test("Test array subscript, expression of wrong type") {
    val visitor = new TypeChecker()
    visitor.env =visitor.env.setGlobalVariable("arr", IntegerType)

    val stmt = WriteStmt(ArraySubscript(VarExpression("arr"), IntValue(0)))

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test array subscript, index of wrong type") {
    val visitor = new TypeChecker()
    visitor.env.setGlobalVariable("arr", ArrayType(1, IntegerType))

    val stmt = WriteStmt(ArraySubscript(VarExpression("arr"), BoolValue(false)))

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test array subscript, expression is ArrayValue") {
    val visitor = new TypeChecker()

    val stmt =
      WriteStmt(
        ArraySubscript(
          ArrayValue(ListBuffer(IntValue(0)), ArrayType(1, IntegerType)),
          IntValue(0)
        )
      )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.isEmpty)
  }

  test("Test array subscript, expression is empty ArrayValue") {
    val visitor = new TypeChecker()

    val stmt =
      WriteStmt(
        ArraySubscript(
          ArrayValue(ListBuffer(), ArrayType(0, IntegerType)),
          IntValue(0)
        )
      )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.isEmpty)
  }

  test("Test function call") {
    val visitor = new TypeChecker()

    visitor.env = visitor.env.declareProcedure(
      Procedure(
        name = "proc",
        args = Nil,
        returnType = None,
        constants = Nil,
        variables = Nil,
        stmt = WriteStmt(IntValue(0))
      )
    )

    val stmt = WriteStmt(FunctionCallExpression("proc", Nil))

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 0)
  }

  test("Test function call with args and return type") {
    val visitor = new TypeChecker()
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)

    visitor.env = visitor.env.declareProcedure(
      Procedure(
        name = "proc",
        args = List(
          ParameterByValue("x", IntegerType),
          ParameterByReference("y", BooleanType)
        ),
        returnType = Some(IntegerType),
        constants = Nil,
        variables = Nil,
        stmt = IfElseStmt(
          VarExpression("y"),
          ReturnStmt(AddExpression(VarExpression("x"), IntValue(1))),
          Some(ReturnStmt(AddExpression(VarExpression("x"), IntValue(-1))))
        )
      )
    )

    val stmt = AssignmentStmt(
      "x",
      FunctionCallExpression("proc", List(IntValue(5), BoolValue(true)))
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 0)
  }

  test("Test function call with one argument") {
    val visitor = new TypeChecker()
    visitor.env = visitor.env.declareProcedure(
      Procedure(
        name = "proc",
        args = List(ParameterByValue("x", IntegerType)),
        returnType = None,
        constants = Nil,
        variables = Nil,
        stmt = WriteStmt(IntValue(0))
      )
    )

    val stmt = WriteStmt(
      FunctionCallExpression("proc", List(IntValue(5)))
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 0)
  }

  test("Test function call with return type") {
    val visitor = new TypeChecker()

    visitor.env = visitor.env.setGlobalVariable("s", StringType)
    visitor.env = visitor.env.declareProcedure(
      Procedure(
        name = "proc",
        args = Nil,
        returnType = Some(StringType),
        constants = Nil,
        variables = Nil,
        stmt = ReturnStmt(StringValue("ret"))
      )
    )

    val stmt = AssignmentStmt(
      "s",
      FunctionCallExpression("proc", Nil)
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 0)
  }

  test("Test function call, wrong args") {
    val visitor = new TypeChecker()
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.declareProcedure(
      Procedure(
        name = "proc",
        args = List(
          ParameterByValue("x", IntegerType),
          ParameterByReference("y", BooleanType)
        ),
        returnType = Some(IntegerType),
        constants = Nil,
        variables = Nil,
        stmt = IfElseStmt(
          VarExpression("y"),
          ReturnStmt(AddExpression(VarExpression("x"), IntValue(1))),
          Some(ReturnStmt(AddExpression(VarExpression("x"), IntValue(-1))))
        )
      )
    )

    val stmt = AssignmentStmt(
      "x",
      FunctionCallExpression("proc", List(IntValue(5), IntValue(0)))
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test function call, less args than needed") {
    val visitor = new TypeChecker()
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.declareProcedure(
      Procedure(
        name = "proc",
        args = List(
          ParameterByValue("x", IntegerType),
          ParameterByReference("y", BooleanType)
        ),
        returnType = Some(IntegerType),
        constants = Nil,
        variables = Nil,
        stmt = IfElseStmt(
          VarExpression("y"),
          ReturnStmt(AddExpression(VarExpression("x"), IntValue(1))),
          Some(ReturnStmt(AddExpression(VarExpression("x"), IntValue(-1))))
        )
      )
    )

    val stmt = AssignmentStmt(
      "x",
      FunctionCallExpression("proc", List(IntValue(0)))
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test function call, wrong args and return type") {
    val visitor = new TypeChecker()
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env.declareProcedure(
      Procedure(
        name = "proc",
        args = List(
          ParameterByValue("x", IntegerType),
          ParameterByReference("y", BooleanType)
        ),
        returnType = Some(IntegerType),
        constants = Nil,
        variables = Nil,
        stmt = IfElseStmt(
          VarExpression("y"),
          ReturnStmt(AddExpression(VarExpression("x"), IntValue(1))),
          Some(ReturnStmt(AddExpression(VarExpression("x"), IntValue(-1))))
        )
      )
    )

    val stmt = AssignmentStmt(
      "x",
      FunctionCallExpression("proc", List(IntValue(5), VarExpression("404")))
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test EAssignment") {
    val visitor = new TypeChecker()

    visitor.env = visitor.env.addUserDefinedType(
      UserDefinedType(
        "customType",
        RecordType(List(VariableDeclaration("x1", RealType)))
      )
    )
    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable("b", PointerType(BooleanType))
    visitor.env = visitor.env.setGlobalVariable("arr", ArrayType(3, CharacterType))
    visitor.env = visitor.env.setGlobalVariable(
      "rec",
      RecordType(List(VariableDeclaration("x", StringType)))
    )
    visitor.env = visitor.env.setGlobalVariable(
      "userDefType",
      ReferenceToUserDefinedType("customType")
    )

    val stmt01 = new AssignmentStmt(VarAssignment("x"), IntValue(0))
    val stmt02 = new AssignmentStmt(PointerAssignment("b"), BoolValue(false))
    val stmt03 = new AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), IntValue(0)),
      CharValue('a')
    )
    val stmt04 = new AssignmentStmt(
      RecordAssignment(VarExpression("rec"), "x"),
      StringValue("teste")
    )
    val stmt05 = new AssignmentStmt(
      RecordAssignment(VarExpression("userDefType"), "x1"),
      RealValue(6.9)
    )

    val stmts = SequenceStmt(List(stmt01, stmt02, stmt03, stmt04))

    val typeCheckerErrors = visitor.checkStmt(stmts)

    assert(typeCheckerErrors.length == 0)
  }

  test("Test EAssignment, PointerAssignment, missing variable") {
    val visitor = new TypeChecker()

    val stmt = new AssignmentStmt(PointerAssignment("b"), BoolValue(false))

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test EAssignment, PointerAssignment, left side not PointerType") {
    val visitor = new TypeChecker()

    visitor.env.setGlobalVariable("b", IntegerType)
    val stmt = new AssignmentStmt(PointerAssignment("b"), BoolValue(false))

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test EAssignment, PointerAssignment, invalid left side Type") {
    val visitor = new TypeChecker()

    visitor.env.setGlobalVariable("b", PointerType(UndefinedType))
    val stmt = new AssignmentStmt(PointerAssignment("b"), BoolValue(false))

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test EAssignment, PointerAssignment, wrong right side Type") {
    val visitor = new TypeChecker()

    visitor.env.setGlobalVariable("b", PointerType(IntegerType))
    val stmt = new AssignmentStmt(PointerAssignment("b"), BoolValue(false))

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test EAssignment, ArrayAssignment, wrong array type") {
    val visitor = new TypeChecker()

    visitor.env.setGlobalVariable("arr", IntegerType)
    val stmt = new AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), IntValue(0)),
      CharValue('a')
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test EAssignment, ArrayAssignment, wrong index type") {
    val visitor = new TypeChecker()

    visitor.env.setGlobalVariable("arr", ArrayType(3, CharacterType))
    val stmt = new AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), BoolValue(true)),
      CharValue('a')
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test EAssignment, ArrayAssignment, missing array type") {
    val visitor = new TypeChecker()

    val stmt = new AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), IntValue(0)),
      CharValue('a')
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test EAssignment, ArrayAssignment, missing index type") {
    val visitor = new TypeChecker()

    visitor.env.setGlobalVariable("arr", ArrayType(3, CharacterType))
    val stmt = new AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), VarExpression("i")),
      CharValue('a')
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test EAssignment, ArrayAssignment, wrong array element type") {
    val visitor = new TypeChecker()

    visitor.env.setGlobalVariable("arr", ArrayType(3, IntegerType))
    val stmt = new AssignmentStmt(
      ArrayAssignment(VarExpression("arr"), IntValue(0)),
      CharValue('a')
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test EAssignment, RecordAssignment(RecordType), missing attribute") {
    val visitor = new TypeChecker()

    visitor.env.setGlobalVariable(
      "rec",
      RecordType(List(VariableDeclaration("x", StringType)))
    )
    val stmt = new AssignmentStmt(
      RecordAssignment(VarExpression("rec"), "404"),
      StringValue("teste")
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Test EAssignment, RecordAssignment(RecordType), wrong attribute type") {
    val visitor = new TypeChecker()

    visitor.env.setGlobalVariable(
      "rec",
      RecordType(List(VariableDeclaration("x", StringType)))
    )
    val stmt = new AssignmentStmt(
      RecordAssignment(VarExpression("rec"), "x"),
      IntValue(8)
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test(
    "Test EAssignment, RecordAssignment(ReferenceToUserDefinedType), missing custom type"
  ) {
    val visitor = new TypeChecker()

    visitor.env.setGlobalVariable(
      "userDefType",
      ReferenceToUserDefinedType("customType")
    )
    val stmt = new AssignmentStmt(
      RecordAssignment(VarExpression("userDefType"), "x"),
      RealValue(3.0)
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test(
    "Test EAssignment, RecordAssignment(ReferenceToUserDefinedType), missing attribute type"
  ) {
    val visitor = new TypeChecker()

    visitor.env.addUserDefinedType(
      UserDefinedType(
        "customType",
        RecordType(List(VariableDeclaration("x1", RealType)))
      )
    )
    visitor.env.setGlobalVariable(
      "userDefType",
      ReferenceToUserDefinedType("customType")
    )
    val stmt = new AssignmentStmt(
      RecordAssignment(VarExpression("userDefType"), "404"),
      RealValue(3.0)
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test(
    "Test EAssignment, RecordAssignment(ReferenceToUserDefinedType), wrong attribute type"
  ) {
    val visitor = new TypeChecker()

    visitor.env.addUserDefinedType(
      UserDefinedType(
        "customType",
        RecordType(List(VariableDeclaration("x1", RealType)))
      )
    )
    visitor.env.setGlobalVariable(
      "userDefType",
      ReferenceToUserDefinedType("customType")
    )
    val stmt = new AssignmentStmt(
      RecordAssignment(VarExpression("userDefType"), "x1"),
      IntValue(3)
    )

    val typeCheckerErrors = visitor.checkStmt(stmt)

    assert(typeCheckerErrors.length == 1)
  }

  test("Type checking foreach stmt") {
    val module = parseResource("stmts/ForEachStmt.oberon")
    assert(module.name == "ForEachStmt")

    val visitor = new TypeChecker()

    assert(visitor.checkModule(module) == List())
  }

  test("Type checking expressions with user defined types") {
    val visitor = new TypeChecker()

    val as = br.unb.cic.oberon.ir.ast.AssignmentStmt
    val arrayType: ArrayType = ArrayType(5, IntegerType)
    val udt: UserDefinedType = UserDefinedType("MediaArray", arrayType)
    val simpleAssignment: Statement = AssignmentStmt("x", IntValue(5))
    val arrayAssigment: Statement =
      as(ArrayAssignment(VarExpression("medias"), IntValue(0)), IntValue(5))


    visitor.env = visitor.env.addUserDefinedType(udt)

    visitor.env = visitor.env.setGlobalVariable("x", IntegerType)
    visitor.env = visitor.env.setGlobalVariable(
      "medias",
      ReferenceToUserDefinedType("MediaArray")
    )

    assert(visitor.checkStmt(simpleAssignment) == List())
    assert(visitor.checkStmt(arrayAssigment) == List())
  }

  test("Type checker for the new stmt") {
    val module = parseResource("Pointers/pointerNewStatement.oberon")

    assert(module.name == "PointerNewStatement")

    assert(module.stmt.isDefined)

    val visitor = new TypeChecker()

    val res = visitor.checkModule(module)

    assert(res.isEmpty)

  }

  test("Type checker for the SUMMATION module") {
    val module = parseResource("recursion/Summation.oberon")
    assert(module.name == "SUMMATION")

    assert(module.stmt.isDefined)

    val visitor = new TypeChecker()

    val res = visitor.checkModule(module)

    assert(res.isEmpty)
  }

  test("Test valid lambda expression assignment") {
    val visitor = new TypeChecker()
    val module = parseResource("lambda/lambdaExpressionsTC01.oberon")
    val res = visitor.checkModule(module)

    assert(res.isEmpty)
  }

  test("Test lambda expression assignment with wrong number of arguments.") {
    val visitor = new TypeChecker()

    val module = parseResource("lambda/lambdaExpressionsTC02.oberon")
    val res = visitor.checkModule(module)

    assert(res.size == 1)
    val msg = res(0)._2
    assert(msg.contains("Assignment between different types"))
  }

  test("Test lambda expression assignment with argument of wrong type.") {
    val visitor = new TypeChecker()

    val module = parseResource("lambda/lambdaExpressionsTC03.oberon")
    val res = visitor.checkModule(module)


    assert(res.size == 1)
    val msg = res(0)._2
    assert(msg.contains("Assignment between different types"))
  }

  test("Test lambda expression assignment with ill typed expression.") {
    val visitor = new TypeChecker()

    val module = parseResource("lambda/lambdaExpressionsTC04.oberon")
    val res = visitor.checkModule(module)
  
    assert(res.size == 1)
    val msg = res(0)._2
    assert(msg.contains("is ill typed"))
  }

  test("Test lambda expression assignment to a constant.") {
    val visitor = new TypeChecker()
    val module = parseResource("lambda/lambdaExpressionsTC05.oberon")
    assert(visitor.checkModule(module).isEmpty)

  }

  test("Test lambda expression assignment with wrong return type.") {
    val visitor = new TypeChecker()
    val module = parseResource("lambda/lambdaExpressionsTC06.oberon")
    val res = visitor.checkModule(module)


    assert(res.size == 1)
  }

  test("Test valid lambda expression application") {
    val visitor = new TypeChecker()
    val module =
      ScalaParser.parseResource("lambda/lambdaExpressionsIT01.oberon")
    val res = visitor.checkModule(module)
    assert(res.isEmpty)
  }

   test("Test assignment to lambda application statement") {
    val visitor = new TypeChecker()
    val stmt = AssignmentStmt("r", LambdaApplication(VarExpression("a"),List(IntValue(4))))
    val stmt1 = AssignmentStmt("a", LambdaExpression(List(ParameterByValue("x",IntegerType)),AddExpression(VarExpression("x"),IntValue(5))))

    visitor.env = visitor.env.setGlobalVariable("r",IntegerType)
    visitor.env = visitor.env.setGlobalVariable("a", LambdaType(List(IntegerType),IntegerType))

    assert(visitor.checkStmt(stmt).size == 0)
    assert(visitor.checkStmt(stmt1).size == 0)
  test("Test assert true statement (true)") {
    val visitor = new TypeChecker()
    val module = parseResource("stmts/AssertTrueStmt01.oberon")
    val res = visitor.checkModule(module)

    assert(res.size == 0)
  }

  test("Test assert equal statement (true)") {
    val visitor = new TypeChecker()
    val module = parseResource("stmts/AssertEqualStmt01.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)

    assert(visitor.checkModule(coreModule).size == 0)
  }

  test("Test assert equal statement (wrong)") {
    val visitor = new TypeChecker()
    val module = parseResource("stmts/AssertEqualStmt03.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)
    val res = visitor.checkModule(coreModule)

    assert(res.size == 1)
  }

  test("Test test procedure (right)") {
    val visitor = new TypeChecker()
    val module = parseResource("procedures/procedureTest01.oberon")

    assert(module.name == "procedureTest01")
    assert(module.tests.size == 1)
    assert(module.stmt.isDefined)

    val res = visitor.checkModule(module)

    assert(res.size == 0)
  }

  test("Test test procedure wrong variable assignment") {
    val visitor = new TypeChecker()
    val module = parseResource("procedures/procedureTest07.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)
    val res = visitor.checkModule(coreModule)
    assert(res.size == 0)
  }

  test("Test test procedure wrong if statement") {
    val visitor = new TypeChecker()
    val module = parseResource("procedures/procedureTest08.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)
    val res = visitor.checkModule(coreModule)
    assert(res.size == 0)
  }

  test("Test two test procedure statements") {
    val visitor = new TypeChecker()
    val module = parseResource("procedures/procedureTest09.oberon")

    val coreModule = CoreTransformer.reduceOberonModule(module)
    val res = visitor.checkModule(coreModule)
    assert(res.size == 0)
  }

}
}