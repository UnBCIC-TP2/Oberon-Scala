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
        assert(charsInTargetNotInCode.isEmpty && charsInCodeNotInTarget.isEmpty,
            s"\nCharacters not found in generated code:\n${charsInTargetNotInCode}\n") 
    }

    test("Testing if simple04.jimple is equal to generated JIMPLE") {
        val (jimpleTarget, jimpleCode) = getTargetAndPrintedJimple(
            "simple/simple04.oberon",
            "src/test/resources/jimpleCodes/simple04.jimple"
        )
        compareTargetAndPrintedJimple(jimpleTarget, jimpleCode)
    }
}