package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{Constant => ASTConstant, _}
import br.unb.cic.oberon.ir.tac._
import org.scalatest.funsuite.AnyFunSuite

class TACodeTest extends AnyFunSuite {

  test("Generate add between constants") {

    TACodeGenerator.reset
    val expr = AddExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // 1 + 2

    Temporary.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""))
    // t0 = 1 + 2

    assert(list == ops)
  }


  test("Generate add between add and constant") {
    
    TACodeGenerator.reset
    val expr = AddExpression(AddExpression(IntValue(1), IntValue(2)), IntValue(3))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      AddOp(t0, Constant("3", IntegerType), t1, "")
    )

    assert(list == ops)
  }

  test("Generate add between adds") {

    TACodeGenerator.reset
    val expr = AddExpression(AddExpression(IntValue(1), IntValue(2)), AddExpression(IntValue(3), IntValue(4)))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val t2 = new Temporary(IntegerType, 2, true)
    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      AddOp(Constant("3", IntegerType), Constant("4", IntegerType), t1, ""),
      AddOp(t0, t1, t2, "")
    )

    assert(list == ops)
  }

  test("Testing complex operation 2") {

    TACodeGenerator.reset
    val expr = SubExpression(DivExpression(MultExpression(IntValue(2), IntValue(2)), IntValue(3)), IntValue(6))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // ((2 * 2) / 3) - 6

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val t2 = new Temporary(IntegerType, 2, true)
    val ops = List(
      MulOp(Constant("2", IntegerType), Constant("2", IntegerType), t0, ""),
      DivOp(t0, Constant("3", IntegerType), t1, ""),
      SubOp(t1, Constant("6", IntegerType), t2, "")
    )
    // t0 = 2 * 2
    // t1 = t0 / 3
    // t2 = t1 - 6

    assert(list == ops)
  }

  test("Testing complex operation 3") {
    
    TACodeGenerator.reset
    val expr = OrExpression(AndExpression(BoolValue(true), BoolValue(false)), OrExpression(BoolValue(true), AndExpression(BoolValue(true), NotExpression(BoolValue(true)))))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (True and False) or (True or (True and False))

    TACodeGenerator.reset
    val t0 = new Temporary(BooleanType, 0, true)
    val t1 = new Temporary(BooleanType, 1, true)
    val t2 = new Temporary(BooleanType, 2, true)
    val t3 = new Temporary(BooleanType, 3, true)
    val t4 = new Temporary(BooleanType, 4, true)
    val ops = List(
      AndOp(Constant("true", BooleanType), Constant("false", BooleanType), t0, ""),
      NotOp(Constant("true", BooleanType), t1, ""),
      AndOp(Constant("true", BooleanType), t1, t2, ""),
      OrOp(Constant("true", BooleanType), t2, t3, ""),
      OrOp(t0, t3, t4, "")
    )
    // t0 = true and false
    // t1 = not(true)
    // t2 = true and t1
    // t3 = true or t2
    // t4 = t0 or t3

    assert(list == ops)
  }

  test ("Testing NotExpression") {

    TACodeGenerator.reset
    val expr = NotExpression(BoolValue(true))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // not(true)

    TACodeGenerator.reset
    val t0 = new Temporary(BooleanType, 0, true)
    val ops = List(
      NotOp(Constant("true", BooleanType), t0, "")
    )    
    // t0 = not true

    assert(list == ops)

  }

/*
    ==
    !=
    >
    <
    >=
    <=
*/

  test("Testing EQ") {
    
    TACodeGenerator.reset
    val expr = EQExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 == 2)

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      SubOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      SLTUOp(t0, Constant("1", IntegerType), t1, "")
    )    
    // t0 = 1 - 2
    // t1 = sltuop t0 1

    assert(list == ops)
  }

  test("Testing NEQ") {
    
    TACodeGenerator.reset
    val expr = NEQExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 != 2)

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      SubOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      SLTUOp(Constant("0", IntegerType), t0, t1, "")
    )    
    // t0 = 1 - 2
    // t1 = sltuop t0 1

    assert(list == ops)
  }

  test("Testing LT") {
    
    TACodeGenerator.reset
    val expr = LTExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 < 2)

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      SLTOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, "")
    )    
    // t0 = slt 1 2

    assert(list == ops)
  }

  test("Testing GT") {
    
    TACodeGenerator.reset
    val expr = GTExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 > 2)

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      SLTOp(Constant("2", IntegerType), Constant("1", IntegerType), t0, "")
    )    
    // t0 = 1 > 2

    assert(list == ops)
  }


  test("Testing GTE") {
    
    TACodeGenerator.reset
    val expr = GTEExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 >= 2)

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      SLTOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      NotOp(t0, t1, "")
    )    
    // t0 = slt 1 2
    // t1 = not t0

    assert(list == ops)
  }

  test("Testing LTE") {
    
    TACodeGenerator.reset
    val expr = LTEExpression(IntValue(1), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (1 <= 2)

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      SLTOp(Constant("2", IntegerType), Constant("1", IntegerType), t0, ""),
      NotOp(t0, t1, "")
    )    
    // t0 = slt 2 1
    // t1 = not t0

    assert(list == ops)
  }

  test("Testing VarExpression"){
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)

    val expr = VarExpression("var")
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    val ops = Name("var", IntegerType)
    // var
    assert(t == ops)
  }

  test("Testing ArraySubscript"){
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("lista", ArrayType(4, IntegerType)))
    TACodeGenerator.load_vars(list_var)

    
    val expr = ArraySubscript(VarExpression("lista"), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      ListGet(Name("lista", ArrayType(4, IntegerType)), Constant("2", IntegerType), t0, "")
    )    
    // lista[2]
    assert(list == ops)
  }
  test("Testing PointerAccessExpression"){
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("pointer", PointerType(IntegerType)))
    TACodeGenerator.load_vars(list_var)

    val expr = PointerAccessExpression("pointer")
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      GetValue(Name("pointer", LocationType), t0, "")
    )    
    // t0 = *pointer;

    assert(list == ops)
  }


  // Assignment Statements
  test("Testing Var Assignment"){
    TACodeGenerator.reset

    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val stmt = AssignmentStmt(VarAssignment("var"), AddExpression(IntValue(1), IntValue(2)))
    val list = TACodeGenerator.generateStatement(stmt, List())

    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      CopyOp(t0, Name("var", IntegerType), "")
    )    
    // var = 1+2
    assert(list == ops)
  }

  test("Testing Array Assignment"){
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("lista", ArrayType(4, IntegerType)))
    TACodeGenerator.load_vars(list_var)

    val stmt = AssignmentStmt(ArrayAssignment(VarExpression("lista"), IntValue(1)),
              AddExpression(IntValue(2), IntValue(3))
            )
    val list = TACodeGenerator.generateStatement(stmt, List())

    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      AddOp(Constant("2", IntegerType), Constant("3", IntegerType), t0, ""),
      ListSet(t0, Constant("1", IntegerType), Name("lista", ArrayType(4,IntegerType)), "")
    )    
    // lista[1] = 2+3
    assert(list == ops)
  }

  test("Testing Pointer Assignment"){
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("pointer", PointerType(IntegerType)))
    TACodeGenerator.load_vars(list_var)

    val stmt = AssignmentStmt(PointerAssignment("pointer"),
              AddExpression(IntValue(2), IntValue(3))
            )
    val list = TACodeGenerator.generateStatement(stmt, List())

    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      AddOp(Constant("2", IntegerType), Constant("3", IntegerType), t0, ""),
      SetPointer(t0, Name("pointer", LocationType), "")
    )    
    // *pointer = 2+3
    assert(list == ops)
  }

  test("Testing IfElse-EQExpression"){
    TACodeGenerator.reset

    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val condition = EQExpression(IntValue(0), IntValue(0))
    val thenStmt = AssignmentStmt(VarAssignment("var"), AddExpression(IntValue(1), IntValue(2)))
    val elseStmt = None
    val ifElseStmt = IfElseStmt(condition, thenStmt, elseStmt)
    val list = TACodeGenerator.generateStatement(ifElseStmt, List())
    

    TACodeGenerator.reset

    val t0 = new Temporary(IntegerType, 0, true)
    val l1 = LabelGenerator.generateLabel
    val ops = List(
      NeqJump(Constant("0", IntegerType), Constant("0", IntegerType), l1, ""),
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      CopyOp(t0, Name("var", IntegerType), ""),
      NOp(l1)
      )
    // if(1==1){var = 1 + 2}
    assert(list == ops)
  }

  test("Testing IfElse-BooleanExpression"){
    TACodeGenerator.reset

    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val condition = OrExpression(BoolValue(true), BoolValue(false))
    val thenStmt = AssignmentStmt(VarAssignment("var"), AddExpression(IntValue(1), IntValue(2)))
    val elseStmt = Some(AssignmentStmt(VarAssignment("var"), SubExpression(IntValue(3), IntValue(2))))
    val ifElseStmt = IfElseStmt(condition, thenStmt, elseStmt)
    val list = TACodeGenerator.generateStatement(ifElseStmt, List())
    

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val t2 = new Temporary(IntegerType, 2, true)
    val l1 = LabelGenerator.generateLabel
    val l2 = LabelGenerator.generateLabel
    val ops = List(
      OrOp(Constant("true", BooleanType), Constant("false", BooleanType), t0, ""),
      JumpFalse(t0, l1, ""),
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t1, ""),
      CopyOp(t1, Name("var", IntegerType), ""),
      Jump(l2, ""),
      NOp(l1),
      SubOp(Constant("3", IntegerType), Constant("2", IntegerType), t2, ""),
      CopyOp(t2, Name("var", IntegerType), ""),
      NOp(l2)
    )
    // if(1|0){var = 1 + 2} else {var = 3 - 2}
    assert(list == ops)
  }

  test("Testing IfElse-NotExpression"){
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val condition = NotExpression(BoolValue(false))
    val thenStmt = AssignmentStmt(VarAssignment("var"), AddExpression(IntValue(1), IntValue(2)))
    val elseStmt = None
    
    val ifElseStmt = IfElseStmt(condition, thenStmt, elseStmt)
    val list = TACodeGenerator.generateStatement(ifElseStmt, List())
    TACodeGenerator.reset

    val t0 = new Temporary(IntegerType, 0, true)
    val l1 = LabelGenerator.generateLabel
    val ops = List(
      JumpTrue(Constant("false", BooleanType), l1, ""),
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      CopyOp(t0, Name("var", IntegerType), ""),
      NOp(l1),
      )
    // if(!(false)){var = 1 + 2}

    assert(list == ops)
  }

  test("Testing WhileStmt-LTExpression"){
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    
    val condition = LTExpression(VarExpression("var"), IntValue(5))
    val thenStmt = AssignmentStmt(VarAssignment("var"), AddExpression(VarExpression("var"), IntValue(1)))
    val whileStmt = WhileStmt(condition, thenStmt)
    val list = TACodeGenerator.generateStatement(whileStmt, List())

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val l1 = LabelGenerator.generateLabel
    val l2 = LabelGenerator.generateLabel
    val ops = List(
      Jump(l1, ""),
      NOp(l2),
      AddOp(Name("var", IntegerType), Constant("1", IntegerType), t0, ""),
      CopyOp(t0, Name("var", IntegerType), ""),  
      NOp(l1),
      LTEJump(Name("var", IntegerType), Constant("5", IntegerType), l2, ""),  
    )
    // while(var < 5){var = var + 1}
    assert(list == ops)
  }
}
