package br.unb.cic.oberon.parser

import java.nio.file.{Files, Paths}

import org.scalatest.funsuite.AnyFunSuite
import br.unb.cic.oberon.ast._

class ModuleLoaderTestSuite extends AnyFunSuite {

  test("Testing if the ModuleLoader imports a file") {
    val loader = ResourceModuleLoader.load("imports/B.oberon")

    var file = """MODULE A;

VAR
    x : INTEGER;

BEGIN
    x := 1
END

END A."""

    assert(loader.modules.get("A") == file)
  }

}