package br.unb.cic.oberon.parser

import br.unb.cic.oberon.AbstractTestSuite

import java.nio.file.{Files, Paths}
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ir.ast._

class ModuleLoaderTestSuite extends AbstractTestSuite {

  def makeModule(
      name: String = "",
      submodules: Set[String] = Set(),
      userTypes: List[UserDefinedType] = List(),
      constants: List[Constant] = List(),
      variables: List[VariableDeclaration] = List(),
      procedures: List[Procedure] = List(),
      tests: List[Test] = List(),
      stmt: Option[Statement] = None
  ): OberonModule = {
    OberonModule(
      name,
      submodules,
      userTypes,
      constants,
      variables,
      procedures,
      tests,
      stmt
    )
  }

  test("ModuleLoader loads a file with no imports") {
    val module = ResourceModuleLoader.loadAndMerge("imports/A.oberon")

    val expected = makeModule(
      name = "A",
      variables = List(VariableDeclaration("x", IntegerType)),
      stmt = Some(SequenceStmt(List(AssignmentStmt("x", IntValue(1)))))
    )
    assert(module == expected)
  }

  test("ModuleLoader loads a file with a simple import") {
    val module = ResourceModuleLoader.loadAndMerge("imports/B.oberon")

    val expected = makeModule(
      name = "B",
      variables = List(VariableDeclaration("A::x", IntegerType)),
      stmt = Some(
        SequenceStmt(
          List(
            WriteStmt(VarExpression("A::x"))
          )
        )
      )
    )
    assert(module == expected)
  }

  ignore("ModuleLoader accepts aliases in files") {
    val module = ResourceModuleLoader.loadAndMerge("imports/F.oberon")

    val expected = makeModule(
      name = "F",
      variables = List(VariableDeclaration("alias::x", IntegerType)),
      stmt = Some(
        SequenceStmt(
          List(
            WriteStmt(VarExpression("alias::x"))
          )
        )
      )
    )
    assert(module == expected)
  }

  ignore("ModuleLoader accepts two files that import from a common third file") {
    val module = ResourceModuleLoader.loadAndMerge("imports/M.oberon")

    val expected = makeModule(
      name = "M",
      variables = List(VariableDeclaration("B::A::x", IntegerType), VariableDeclaration("L::A::x", IntegerType)),
      stmt = Some(
        SequenceStmt(
          List(
            WriteStmt(VarExpression("B::A::x")),
            WriteStmt(VarExpression("L::A::x"))
          )
        )
      )
    )
    assert(module == expected)
  }

  test("ModuleLoader imports variables with the same name from different files") {
    val module = ResourceModuleLoader.loadAndMerge("imports/D.oberon")

    val expected = makeModule(
      name = "D",
      variables = List(VariableDeclaration("A::x", IntegerType), VariableDeclaration("C::x", IntegerType)),
      stmt = Some(
        SequenceStmt(
          List(
            WriteStmt(VarExpression("A::x")),
            WriteStmt(VarExpression("C::x"))
          )
        )
      )
    )
    assert(module == expected)
  }

  test("ModuleLoader with 'interpreter_factorial01.oberon'") {
    val module = ResourceModuleLoader.loadAndMerge("procedures/interpreter_factorial01.oberon")

    val expected_stmt = Some(
      SequenceStmt(
          List(
              AssignmentStmt("y",IntValue(1)),
              ForStmt(AssignmentStmt(
                  "x",IntValue(5)),
                  GTExpression(VarExpression("x"),IntValue(1)),
                  SequenceStmt(
                      List(
                          AssignmentStmt("y",MultExpression(VarExpression("y"),VarExpression("x"))),
                          AssignmentStmt("x",SubExpression(VarExpression("x"),IntValue(1)))
                      )
                  )
              ),
              WriteStmt(VarExpression("y"))
          )
      )
  )

    val expected = makeModule(
      name = "SimpleModule",
      variables = List(VariableDeclaration("x", IntegerType), VariableDeclaration("y", IntegerType)),
      stmt = expected_stmt
    )
    assert(module == expected)
  }

  ignore("ModuleLoader deals with transitive imports") {
    val module = ResourceModuleLoader.loadAndMerge("imports/I.oberon")

    val expected = makeModule(
      name = "I",
      variables = List(VariableDeclaration("B::A::x", IntegerType)),
      stmt = Some(
        SequenceStmt(
          List(
            WriteStmt(VarExpression("B::A::x"))
          )
        )
      )
    )
    assert(module == expected)
  }

  test("ModuleLoader throws error with cyclic imports") {
    val thrown = intercept[Exception] {
      val module = ResourceModuleLoader.loadAndMerge("imports/I.oberon")
    }
    assert(thrown.getMessage.length > 0)
  }

  test("ModuleLoader throws error when IMPORT is declared empty") {
    val thrown = intercept[Exception] {
      val module = ResourceModuleLoader.loadAndMerge("imports/E.oberon")
    }
    assert(thrown.getMessage.length > 0)
  }
}
