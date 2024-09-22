package br.unb.cic.oberon.parser

import br.unb.cic.oberon.AbstractTestSuite

import java.nio.file.{Files, Paths}
import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.ir.common._

class ModuleLoaderTestSuite extends AbstractTestSuite {

  def makeModule(
      name: String = "",
      submodules: Set[String] = Set(),
      userTypes: List[UserDefinedType] = List(),
      constants: List[Constant] = List(),
      variables: List[VariableDeclaration] = List(),
      procedures: List[Procedure[Statement]] = List(),
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

  ignore("Testing if the ModuleLoader imports a file") {
    val module = ResourceModuleLoader.loadAndMerge("imports/A.oberon")

    val expected = makeModule(
      name = "A",
      variables = List(VariableDeclaration("A::x", IntegerType)),
      stmt = Some(SequenceStmt(List(AssignmentStmt("A::x", IntValue(1)))))
    )
    assert(module == expected)
  }

  ignore("Testing if the ModuleLoader loads an import recursively") {
    val module = ResourceModuleLoader.loadAndMerge("imports/B.oberon")

    val expected = makeModule(
      name = "B",
      variables = List(VariableDeclaration("A::x", IntegerType)),
      stmt = Some(
        SequenceStmt(
          List(
            AssignmentStmt("A::x", IntValue(1)),
            WriteStmt(VarExpression("A::x"))
          )
        )
      )
    )
    assert(module == expected)
  }

}
