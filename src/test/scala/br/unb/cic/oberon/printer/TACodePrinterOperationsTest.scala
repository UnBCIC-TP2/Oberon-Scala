package br.unb.cic.oberon.printer


import br.unb.cic.oberon.ir.ast.{BooleanType, IntegerType}
import br.unb.cic.oberon.ir.tac._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite


/**
 * Tests for TACodePrinter
 * This tests should see with the printer is correctly handling TAC abstraction
 */
class TACodePrinterOperationsTest extends AnyFunSuite with BeforeAndAfterEach {

  /**
   * Doc document always begin with a line break
   * bl = break line
   */
  private val bl = "\n"

  /**
   * Temporary addresses instanced for test purpose
   */
  val t0 = new Temporary(BooleanType, 0, true)
  val t1 = new Temporary(BooleanType, 1, true)
  val t2 = new Temporary(BooleanType, 2, true)
  val t3 = new Temporary(BooleanType, 3, true)
  val t4 = new Temporary(BooleanType, 4, true)

  override def beforeEach() = {}

  test("test print for add expressions") {

    val ops = List(AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""))
    val expectedOutput = bl + "t0 = 1 + 2"

    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(tacDocumentToPrint.equals(expectedOutput))
  }

  test("test print for more than one add expressions in sequence") {

    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, "L1"),
      AddOp(Constant("3", IntegerType), Constant("4", IntegerType), t1, ""),
      AddOp(t0, t1, t2, "")
    )

    val expectedOutput = bl + s"L1:$bl  t0 = 1 + 2" + bl + "t1 = 3 + 4" + bl + "t2 = t0 + t1"
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)

    assert(tacDocumentToPrint.equals(expectedOutput))

  }

  test("testing different operations in sequence (MulOp, DivOp, SubOp)") {

    val ops = List(
      MulOp(Constant("2", IntegerType), Constant("2", IntegerType), t0, ""),
      DivOp(t0, Constant("3", IntegerType), t1, ""),
      SubOp(t1, Constant("6", IntegerType), t2, "")
    )

    val firstOp = "t0 = 2 * 2"
    val secondOp = "t1 = t0 / 3"
    val thirdOp = "t2 = t1 - 6"

    val expectedOutput = bl + firstOp + bl + secondOp + bl + thirdOp
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)

    assert(tacDocumentToPrint.equals(expectedOutput))

  }

  test("testing AndOp, NotOp and OrOp with prettier printer") {

    val ops = List(
      AndOp(Constant("true", BooleanType), Constant("false", BooleanType), t0, ""),
      NotOp(Constant("true", BooleanType), t1, ""),
      AndOp(Constant("true", BooleanType), t1, t2, ""),
      OrOp(Constant("true", BooleanType), t2, t3, ""),
      OrOp(t0, t3, t4, "")
    )

    val firstOp = "t0 = true && false"
    val secondOp = "t1 = NOT true"
    val thirdOp = "t2 = true && t1"
    val fourthOp = "t3 = true || t2"
    val fifthOp = "t4 = t0 || t3"

    val expectedOutput = bl + firstOp + bl + secondOp + bl + thirdOp + bl + fourthOp + bl + fifthOp
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(tacDocumentToPrint.equals(expectedOutput))

  }

  test("testing copy op with prettier printer") {

    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      MoveOp(t0, Name("var", IntegerType), ""),
    )

    val firstOp = "t0 = 1 + 2"
    val secondOp = "var = t0"

    val expectedOutput = bl + firstOp + bl + secondOp
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(tacDocumentToPrint.equals(expectedOutput))

  }

}
