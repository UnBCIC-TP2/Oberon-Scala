package br.unb.cic.oberon.printer

import br.unb.cic.oberon.codegen.TACodeGenerator
import br.unb.cic.oberon.ir.ast.{
  AddExpression,
  BooleanType,
  GTExpression,
  IntValue,
  LTExpression
}
import br.unb.cic.oberon.ir.tac.Temporary
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

class TACodePrinterOberonCodeTest extends AnyFunSuite with BeforeAndAfterEach {

  private val bl = "\n"

  val t0 = new Temporary(BooleanType, 0, true)
  val t1 = new Temporary(BooleanType, 1, true)
  val t2 = new Temporary(BooleanType, 2, true)
  val t3 = new Temporary(BooleanType, 3, true)
  val t4 = new Temporary(BooleanType, 4, true)

  test("Print add between two constants") {

    val expr = AddExpression(IntValue(1), IntValue(2))
    val (t, operationList) = TACodeGenerator.generateExpression(expr, List())

    val expectedOutput = bl + "t0 = 1 + 2"
    val tacDocumentToPrint =
      TACodePrinter.getTacDocumentStringFormatted(operationList)

    assert(expectedOutput == tacDocumentToPrint)

  }

  test("Testing LT") {

    // (1 < 2)
    val expr = LTExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    val expectedOutput = bl + "t0 = SLT 1 2"
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(list)

    assert(expectedOutput == tacDocumentToPrint)

  }

  test("Testing GT") {

    // (1 > 2)
    val expr = GTExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    val expectedOutput = bl + "t0 = SLT 2 1"
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(list)

    assert(tacDocumentToPrint == expectedOutput)

  }

}
