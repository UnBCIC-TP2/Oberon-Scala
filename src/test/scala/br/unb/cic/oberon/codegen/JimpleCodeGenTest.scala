package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.jimple._
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.transformations.CoreVisitor
import br.unb.cic.oberon.util.Resources
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.{Files, Paths}

class JimpleCodeGenTest extends AnyFunSuite {


  private def testGenerator(oberonFile: String) = {

    val module = ScalaParser.parseResource(oberonFile)
    val coreModule =
      if (module.stmt.isDefined) {
        val coreVisitor = new CoreVisitor()
        coreVisitor.transformModule(module)
      }
      else {
        module
      }

    val generatedJimpleCode = PaigesJimpleGenerator().generateCode(coreModule)
    //val JimpleFile: String = s"JimpleDeclarationTest/$oberonFile".replace(".oberon", ".scala")
    //val JimpleCode = Resources.getContent(JimpleFile)
    //assert(generatedJimpleCode.toString == JimpleCode)      // toString para comparar String com String
    println(generatedJimpleCode)

  }


  test("generate class declaration from simple01.oberon") {
    testGenerator("simple/simple01.oberon")
  }


  test("generate class declaration from simple02.oberon") {
    testGenerator("simple/simple02.oberon")
  }


  test("generate class declaration from simple03.oberon") {
    testGenerator("simple/simple03.oberon")
  }


  test("generate class declaration from simple04.oberon") {
    testGenerator("simple/simple04.oberon")
  }


  test("generate class declaration from simple05.oberon") {
    testGenerator("simple/simple05.oberon")
  }



  test("generate class declaration from simple08.oberon") {
    testGenerator("simple/simple08.oberon")
  }




  test("generate class declaration from simple10.oberon") {
    testGenerator("simple/simple10.oberon")
  }


  test("generate class declaration from simple11.oberon") {
    testGenerator("simple/simple11.oberon")
  }

}