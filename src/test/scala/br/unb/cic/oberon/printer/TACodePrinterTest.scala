package br.unb.cic.oberon.printer


import br.unb.cic.oberon.ir.ast.IntegerType
import br.unb.cic.oberon.ir.tac._
import org.scalatest.funsuite.AnyFunSuite


class TACodePrinterTest extends AnyFunSuite {

  test("test print for add expressions") {
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""))
    val toPrint = TACodePrinter.printExpression(ops)
  }

}
