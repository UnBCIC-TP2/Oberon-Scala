package br.unb.cic.oberon.printer

import br.unb.cic.oberon.codegen.TACodeGenerator
import br.unb.cic.oberon.codegen.LabelGenerator
import br.unb.cic.oberon.ir.ast.{Constant => ASTConstant, _}
import br.unb.cic.oberon.ir.tac._
import org.scalatest.funsuite.AnyFunSuite

class TACodePrinterOberonCodeTest extends AnyFunSuite {

  private val bl = "\n"

  val t0 = new Temporary(BooleanType, 0, true)
  val t1 = new Temporary(BooleanType, 1, true)
  val t2 = new Temporary(BooleanType, 2, true)
  val t3 = new Temporary(BooleanType, 3, true)
  val t4 = new Temporary(BooleanType, 4, true)

  test("Print 'Add' expression between two constants") {
    // Given
    val expr = AddExpression(IntValue(1), IntValue(2))
    val (t, operationList) = TACodeGenerator.generateExpression(expr, List())

    // Expected
    val expectedOutput = bl + "t0 = 1 + 2"

    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(operationList)
    assertResult(expectedOutput)(tacDocumentToPrint)
  }

  test("Print 'Sub' expression between two constants") {
    // Given
    val expr = SubExpression(IntValue(1), IntValue(2))
    val (t, operationList) = TACodeGenerator.generateExpression(expr, List())

    // Expected
    val expectedOutput = bl + "t0 = 1 - 2"

    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(operationList)
    assertResult(expectedOutput)(tacDocumentToPrint)
  }

  test("Print 'Mult' expression between two constants") {
    // Given
    val expr = MultExpression(IntValue(1), IntValue(2))
    val (t, operationList) = TACodeGenerator.generateExpression(expr, List())

    // Expected
    val expectedOutput = bl + "t0 = 1 * 2"
    
    // Then  
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(operationList)
    assert(expectedOutput == tacDocumentToPrint)

  }

    test("Print 'Div' expression between two constants") {
    // Given
    val expr = DivExpression(IntValue(1), IntValue(2))
    val (t, operationList) = TACodeGenerator.generateExpression(expr, List())

    // Expected
    val expectedOutput = bl + "t0 = 1 / 2"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(operationList)
    assert(expectedOutput == tacDocumentToPrint)

  }

    test("Print 'And' expression between two booleans") {
    // Given
    val expr = AndExpression(BoolValue(true), BoolValue(false))
    val (t, operationList) = TACodeGenerator.generateExpression(expr, List())

    // Expected
    val expectedOutput = bl + "t0 = true && false"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(operationList)
    assert(expectedOutput == tacDocumentToPrint)

  }

    test("Print 'Or' expression between two booleans") {
    // Given
    val expr = OrExpression(BoolValue(true), BoolValue(false))
    val (t, operationList) = TACodeGenerator.generateExpression(expr, List())

    // Expected
    val expectedOutput = bl + "t0 = true || false"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(operationList)
    assert(expectedOutput == tacDocumentToPrint)

  }

    test("Print 'Rem' expression between two constants") {
    // Given
    val expr = ModExpression(IntValue(1), IntValue(2))
    val (t, operationList) = TACodeGenerator.generateExpression(expr, List())

    // Expected
    val expectedOutput = bl + "t0 = 1 % 2"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(operationList)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'EQJump' to label") {
    // Given
    val l1 = LabelGenerator.generateLabel
    val ops = List(EqJump(Constant("2", IntegerType), Constant("1", IntegerType), l1, ""))
    
    // Expected
    val expectedOutput = bl + s"if 2 == 1 jump $l1"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'NEQJump' to label") {
    // Given
    val l1 = LabelGenerator.generateLabel
    val ops = List(NeqJump(Constant("2", IntegerType), Constant("1", IntegerType), l1, ""))
    
    // Expected
    val expectedOutput = bl + s"if 2 != 1 jump $l1"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'GTJump' to label") {
    // Given
    val l1 = LabelGenerator.generateLabel
    val ops = List(GTJump(Constant("2", IntegerType), Constant("1", IntegerType), l1, ""))
    
    // Expected
    val expectedOutput = bl + s"if 2 > 1 jump $l1"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'GTEJump' to label") {
    // Given
    val l1 = LabelGenerator.generateLabel
    val ops = List(GTEJump(Constant("2", IntegerType), Constant("1", IntegerType), l1, ""))
    
    // Expected
    val expectedOutput = bl + s"if 2 >= 1 jump $l1"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'LTJump' to label") {
    // Given
    val l1 = LabelGenerator.generateLabel
    val ops = List(LTJump(Constant("2", IntegerType), Constant("1", IntegerType), l1, ""))
    
    // Expected
    val expectedOutput = bl + s"if 2 < 1 jump $l1"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'LTEJump' to label") {
    // Given
    val l1 = LabelGenerator.generateLabel
    val ops = List(LTEJump(Constant("2", IntegerType), Constant("1", IntegerType), l1, ""))
    
    // Expected
    val expectedOutput = bl + s"if 2 <=> 1 jump $l1"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'JumpTrue' to label") {
    // Given
    val l1 = LabelGenerator.generateLabel
    val ops = List(JumpTrue(Constant("false", BooleanType), l1, ""))
    
    // Expected
    val expectedOutput = bl + s"if false == true jump $l1"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'JumpFalse' to label") {
    // Given
    val l1 = LabelGenerator.generateLabel
    val ops = List(JumpFalse(Constant("false", BooleanType), l1, ""))
    
    // Expected
    val expectedOutput = bl + s"if false == false jump $l1"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'Jump' to label") {
    // Given
    val l1 = LabelGenerator.generateLabel
    val ops = List(Jump(l1, ""))
    
    // Expected
    val expectedOutput = bl + s"jump $l1"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'Not' for boolean") {
    // Given
    val expr = NotExpression(BoolValue(true))
    val (t, operationList) = TACodeGenerator.generateExpression(expr, List())
    
    // Expected
    val expectedOutput = bl + "t0 = NOT true"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(operationList)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'EQExpression' between two constants") {
    // Given
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      SubOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      SLTUOp(t0, Constant("1", IntegerType), t1, "")
    )
    
    // Expected
    val expectedOutput = bl + "t0 = 1 - 2" + bl + "t1 = SLTU t0 1"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(expectedOutput == tacDocumentToPrint)

    }

    test("Print 'NEQExpression' between two constants") {
    // Given
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      SubOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      SLTUOp(t0, Constant("1", IntegerType), t1, "")
    )
    
    // Expected
    val expectedOutput = bl + "t0 = 1 - 2" + bl + "t1 = SLTU t0 1"
    
    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assert(expectedOutput == tacDocumentToPrint)

    }

  test("Print 'LT' expression with positive operands") {
    // Given
    val expr = LTExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    // Expected
    val expectedOutput = bl + "t0 = SLT 1 2"

    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(list)
    assertResult(expectedOutput)(tacDocumentToPrint)
  }

  test("Print 'GT' expression with positive operands") {
    // Given
    val expr = GTExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    // Expected
    val expectedOutput = bl + "t0 = SLT 2 1"

    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(list)
    assertResult(expectedOutput)(tacDocumentToPrint)
  }

  test("Print 'ArrayAssignment' statement") {
    // Given
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("lista", ArrayType(4, IntegerType)))
    TACodeGenerator.load_vars(list_var)

    val stmt = AssignmentStmt(
      ArrayAssignment(VarExpression("lista"), IntValue(1)),
      IntValue(2)
    )
    val list = TACodeGenerator.generateStatement(stmt, List())

    // Expected
    val expectedOutput = bl + "lista[1] = 2"

    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(list)
    assertResult(expectedOutput)(tacDocumentToPrint)
  }

  test("Print 'ArraySubscript' expression") {
    // Given
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("lista", ArrayType(4, IntegerType)))
    TACodeGenerator.load_vars(list_var)

    val expr = ArraySubscript(VarExpression("lista"), IntValue(1))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    // Expected
    val expectedOutput = bl + "t0 = lista[1]"

    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(list)
    assertResult(expectedOutput)(tacDocumentToPrint)
  }

  test("Print 'Loop' with array operations") {
    // Given
    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0)
    val t1 = new Temporary(IntegerType, 1)
    val t2 = new Temporary(IntegerType, 2)
    val t3 = new Temporary(IntegerType, 3)
    val l1 = LabelGenerator.generateLabel
    val l2 = LabelGenerator.generateLabel
    var ops = List(
        Jump(l1, ""),
        NOp(l2),
        ArrayGet(Name("lista", ArrayType(4, IntegerType)), Constant("1", IntegerType), t0, ""),
        ArrayGet(Name("lista", ArrayType(4, IntegerType)), Constant("2", IntegerType), t1, ""),
        AddOp(t0, t1, t2, ""),
        ArraySet(t2, Constant("4", IntegerType), Name("lista", ArrayType(4, IntegerType)), ""),
        NOp(l1),
        ArrayGet(Name("lista", ArrayType(4, IntegerType)), Constant("1", IntegerType), t3, ""),
        LTEJump(t3, Constant("10", IntegerType), l2, "")
    )

    val firstOp = s"jump $l1"
    val secondOp = s"$l2"
    val thirdOp = "t0 = lista[1]"
    val fourthOp = "t1 = lista[2]"
    val fifthOp = "t2 = t0 + t1"
    val sixthOp = "lista[1] = t2"
    val seventhOp = s"$l1"
    val eighthOp = "t3 = lista[1]"
    val ninthOp = s"if t3 <=> 10 jump $l2"
    
    // Expected
    val expectedOutput = bl + firstOp + bl + secondOp + bl + thirdOp + bl + fourthOp + bl + fifthOp +
                         bl + sixthOp + bl + seventhOp + bl + eighthOp + bl + ninthOp

    // Then
    val tacDocumentToPrint = TACodePrinter.getTacDocumentStringFormatted(ops)
    assertResult(expectedOutput)(tacDocumentToPrint)
  }

}
