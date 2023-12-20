package br.unb.cic.oberon.printer

import br.unb.cic.oberon.codegen.JimpleCodeGenerator
import br.unb.cic.oberon.printer.JimpleCodePrinter

import br.unb.cic.oberon.ir.jimple._
import br.unb.cic.oberon.parser.Oberon2ScalaParser
import org.scalatest.funsuite.AnyFunSuite

import scala.io.Source

import java.nio.file.{Files, Paths}

class JimpleCodePrinterTest extends AnyFunSuite with Oberon2ScalaParser {
    def getTargetAndPrintedJimple(oberonModulePath: String, jimpleFilePath: String): (String, String) = {
        // Get Oberon class declaration
        val oberonFilePath = Paths.get(
            getClass.getClassLoader.getResource(oberonModulePath).toURI
        )
        assert(oberonFilePath != null)
        val content = String.join("\n", Files.readAllLines(oberonFilePath))
        val module = parseAbs(parse(oberonParser, content))
        // Get Jimple class declaration and target
        val jimpleTarget: String = Source.fromFile(jimpleFilePath).mkString
        // Generate Jimple Pretty Printed code
        val jimpleCode: String = JimpleCodePrinter.generateDoc(module).toString
        (jimpleTarget, jimpleCode)
    }

    def compareTargetAndPrintedJimple(jimpleTarget: String, jimpleCode: String) = {
        // Get uncommon characters between target and generated code
        val charsInCodeNotInTarget = jimpleCode diff jimpleTarget
        val charsInTargetNotInCode = jimpleTarget diff jimpleCode
        assert((charsInTargetNotInCode.length == 0 && charsInCodeNotInTarget.length == 0) ||
            (stringIsWhiteSpace(charsInTargetNotInCode) && stringIsWhiteSpace(charsInCodeNotInTarget)))
    }

    def assertGeneratedJimpleMatchesReference(oberonModulePath: String, referenceJimpleFilePath: String) {
        val (jimpleTarget, jimpleCode) = getTargetAndPrintedJimple(
            oberonModulePath,
            referenceJimpleFilePath,
        )
        compareTargetAndPrintedJimple(jimpleTarget, jimpleCode)       
    }

    def stringIsWhiteSpace(str: String): Boolean = {
        str.forall(_.isWhitespace)
    }

    test("Testing if generated JIMPLE from simple01.oberon matches simple01.jimple") {
        assertGeneratedJimpleMatchesReference(
            "simple/simple01.oberon",
            "src/test/resources/jimpleCodes/simple01.jimple"
        )
    }

    test("Testing if generated JIMPLE from simple02.oberon matches simple02.jimple") {
        assertGeneratedJimpleMatchesReference(
            "simple/simple02.oberon",
            "src/test/resources/jimpleCodes/simple02.jimple"
        )
    }

    test("Testing if generated JIMPLE from simple03.oberon matches simple03.jimple") {
        assertGeneratedJimpleMatchesReference(
            "simple/simple03.oberon",
            "src/test/resources/jimpleCodes/simple03.jimple"
        )
    }

    test("Testing if generated JIMPLE from simple04.oberon matches simple04.jimple") {
        assertGeneratedJimpleMatchesReference(
            "simple/simple04.oberon",
            "src/test/resources/jimpleCodes/simple04.jimple"
        )
    }

    ignore("Testing if generated JIMPLE from simple08.oberon matches simple08.jimple") {
        assertGeneratedJimpleMatchesReference(
            "simple/simple08.oberon",
            "src/test/resources/jimpleCodes/simple08.jimple"
        )
    }

    ignore("Testing if generated JIMPLE from simple10.oberon matches simple10.jimple") {
        assertGeneratedJimpleMatchesReference(
            "simple/simple10.oberon",
            "src/test/resources/jimpleCodes/simple10.jimple"
        )
    }

    ignore("Testing if generated JIMPLE from simple11.oberon matches simple11.jimple") {
        assertGeneratedJimpleMatchesReference(
            "simple/simple11.oberon",
            "src/test/resources/jimpleCodes/simple11.jimple"
        )
    }
}