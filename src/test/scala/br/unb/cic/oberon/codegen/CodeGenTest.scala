package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ast.OberonModule
import org.scalatest.funsuite.AnyFunSuite

import scala.runtime.Nothing$


/**
 * The base class that can represents any oberon module.
 *
 */
class ModuleTest(val filePath: String, val moduleName: String) {

  var module: OberonModule = _

}

/**
 * The base interface for testing code generation.
 *
 *
 */
trait CodeGenTest extends AnyFunSuite{
  /**
   * Asserts all given oberon module components to the current test file.
   *
   * @param codeTest CodeTest object that represents module tests
   *
   */
  def assertWithJVMCodeGenerated(codeTest: ModuleTest): Unit

  /**
   * Run all tests necessary to conclude CodeGenTest verification.
   *
   * @param codeTest CodeTest object that represents module tests
   *
   */
  def testGenerator(testName: String, codeTest: ModuleTest): Unit
}
