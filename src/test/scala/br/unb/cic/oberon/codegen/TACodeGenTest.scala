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
    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, "")
    )
    // t0 = 1 + 2

    assert(list == ops)
  }

  test("Generate add between add and constant") {

    TACodeGenerator.reset
    val expr =
      AddExpression(AddExpression(IntValue(1), IntValue(2)), IntValue(3))
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
    val expr = AddExpression(
      AddExpression(IntValue(1), IntValue(2)),
      AddExpression(IntValue(3), IntValue(4))
    )
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
    val expr = SubExpression(
      DivExpression(MultExpression(IntValue(2), IntValue(2)), IntValue(3)),
      IntValue(6)
    )
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
    val expr = OrExpression(
      AndExpression(BoolValue(true), BoolValue(false)),
      OrExpression(
        BoolValue(true),
        AndExpression(BoolValue(true), NotExpression(BoolValue(true)))
      )
    )
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (True and False) or (True or (True and False))

    TACodeGenerator.reset
    val t0 = new Temporary(BooleanType, 0, true)
    val t1 = new Temporary(BooleanType, 1, true)
    val t2 = new Temporary(BooleanType, 2, true)
    val t3 = new Temporary(BooleanType, 3, true)
    val t4 = new Temporary(BooleanType, 4, true)
    val ops = List(
      AndOp(
        Constant("true", BooleanType),
        Constant("false", BooleanType),
        t0,
        ""
      ),
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

  test("Testing NotExpression") {

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

  test("Testing VarExpression") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)

    val expr = VarExpression("var")
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    val ops = Name("var", IntegerType)
    // var
    assert(t == ops)
  }

  test("Testing ArraySubscript") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("lista", ArrayType(4, IntegerType)))
    TACodeGenerator.load_vars(list_var)

    val expr = ArraySubscript(VarExpression("lista"), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())

    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(

      ArrayGet(Name("lista", ArrayType(4, IntegerType)), Constant("2", IntegerType), t0, "")
    )    
    assert(list == ops)
  }
  test("Testing PointerAccessExpression") {
    TACodeGenerator.reset
    val list_var =
      List(VariableDeclaration("pointer", PointerType(IntegerType)))
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
  test("Testing Var Assignment") {
    TACodeGenerator.reset

    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val stmt = AssignmentStmt(
      VarAssignment("var"),
      AddExpression(IntValue(1), IntValue(2))
    )
    val list = TACodeGenerator.generateStatement(stmt, List())

    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      CopyOp(t0, Name("var", IntegerType), "")
    )    
    // var = 1+2
    assert(list == ops)
  }

  test("Testing Array Assignment") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("lista", ArrayType(4, IntegerType)))
    TACodeGenerator.load_vars(list_var)

    val stmt = AssignmentStmt(
      ArrayAssignment(VarExpression("lista"), IntValue(1)),
      AddExpression(IntValue(2), IntValue(3))
    )
    val list = TACodeGenerator.generateStatement(stmt, List())

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0)
    val ops = List(
      AddOp(Constant("2", IntegerType), Constant("3", IntegerType), t0, ""),
      ArraySet(t0, Constant("4", IntegerType), Name("lista", ArrayType(4,IntegerType)), "")
    )    
    // lista[1] = 2+3
    assert(list == ops)
  }

  test("Testing Pointer Assignment") {
    TACodeGenerator.reset
    val list_var =
      List(VariableDeclaration("pointer", PointerType(IntegerType)))
    TACodeGenerator.load_vars(list_var)

    val stmt = AssignmentStmt(
      PointerAssignment("pointer"),
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

  test("Testing IfElse-EQExpression") {
    TACodeGenerator.reset

    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val condition = EQExpression(IntValue(0), IntValue(0))
    val thenStmt = AssignmentStmt(
      VarAssignment("var"),
      AddExpression(IntValue(1), IntValue(2))
    )
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

  test("Testing IfElse-BooleanExpression") {
    TACodeGenerator.reset

    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val condition = OrExpression(BoolValue(true), BoolValue(false))
    val thenStmt = AssignmentStmt(
      VarAssignment("var"),
      AddExpression(IntValue(1), IntValue(2))
    )
    val elseStmt = Some(
      AssignmentStmt(
        VarAssignment("var"),
        SubExpression(IntValue(3), IntValue(2))
      )
    )
    val ifElseStmt = IfElseStmt(condition, thenStmt, elseStmt)
    val list = TACodeGenerator.generateStatement(ifElseStmt, List())

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val t2 = new Temporary(IntegerType, 2, true)
    val l1 = LabelGenerator.generateLabel
    val l2 = LabelGenerator.generateLabel
    val ops = List(
      OrOp(
        Constant("true", BooleanType),
        Constant("false", BooleanType),
        t0,
        ""
      ),
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

  test("Testing IfElse-NotExpression") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val condition = NotExpression(BoolValue(false))
    val thenStmt = AssignmentStmt(
      VarAssignment("var"),
      AddExpression(IntValue(1), IntValue(2))
    )
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

  test("Testing WhileStmt-LTExpression") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)

    val condition = LTExpression(VarExpression("var"), IntValue(5))
    val thenStmt = AssignmentStmt(
      VarAssignment("var"),
      AddExpression(VarExpression("var"), IntValue(1))
    )
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
      LTEJump(Name("var", IntegerType), Constant("5", IntegerType), l2, "")
    )
    // while(var < 5){var = var + 1}
    assert(list == ops)
  }


  test("Testing procedure sum(var1,var2)") {
    TACodeGenerator.reset()
    Temporary.reset()
    val list_var = List(VariableDeclaration("var1", IntegerType), VariableDeclaration("var2", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val procedureName = "sum"
    val procedureCallStmt = ProcedureCallStmt(procedureName, List(VarExpression("var1"), VarExpression("var2")))
    val list = TACodeGenerator.generateStatement(procedureCallStmt, List())

    Temporary.reset()

    val t0 = new Temporary(IntegerType, 0, manual = true)
    val t1 = new Temporary(IntegerType, 1, manual = true)
    val ops = List(
      MoveOp(Name("var1", IntegerType), t0, ""),
      MoveOp(Name("var2", IntegerType), t1, ""),
      Param(t0, ""),
      Param(t1, ""),
      Call("sum", 2, "")
    )
    assert(list == ops)
  }
  test("testing procedure sub(1+2,1+1)") {

    val procedureName = "sub"
    val procedureCallStmt = ProcedureCallStmt(procedureName, List(AddExpression(IntValue(1), IntValue(2)),
      AddExpression(IntValue(1), IntValue(1))))
    val list = TACodeGenerator.generateStatement(procedureCallStmt, List())
    Temporary.reset()
    val t0 = new Temporary(IntegerType, 0, true)
    val t1 = new Temporary(IntegerType, 1, true)
    val ops = List(
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      AddOp(Constant("1", IntegerType), Constant("1", IntegerType), t1, ""),
      Param(t0, ""),
      Param(t1, ""),
      Call("sub", 2, "")
    )

    assert(ops == list)
  }

  test("testing RecordAssignment") {
    TACodeGenerator.reset()
    val userTypeName = "date"
    val typeVariables = List(VariableDeclaration("day", IntegerType), VariableDeclaration("month", IntegerType))
    val list_userTypes = List(UserDefinedType(userTypeName, RecordType(typeVariables)))
    val list_var = List(VariableDeclaration("d1", ReferenceToUserDefinedType("date")))

    TACodeGenerator.load_userTypes_and_vars(userTypes = list_userTypes, vars = list_var)

    val recordAssignment = AssignmentStmt(RecordAssignment(VarExpression("d1"), "day"), IntValue(5))

    val list = TACodeGenerator.generateStatement(recordAssignment, List())

    val ops = List(RecordSet(Constant("5", IntegerType), Constant("4", IntegerType),
      Name("d1", RecordType(List(VariableDeclaration("day", IntegerType), VariableDeclaration("month", IntegerType)))), ""))

    assert(list == ops)
  }

  test("testing RecordAssignment 2") {
    TACodeGenerator.reset()
    val userTypeName = "date"
    val typeVariables = List(VariableDeclaration("day", IntegerType), VariableDeclaration("month", IntegerType))
    val list_userTypes = List(UserDefinedType(userTypeName, RecordType(typeVariables)))
    val list_var = List(VariableDeclaration("d1", ReferenceToUserDefinedType("date")))

    TACodeGenerator.load_userTypes_and_vars(userTypes = list_userTypes, vars = list_var)

    val recordAssignment = AssignmentStmt(RecordAssignment(VarExpression("d1"), "day"), AddExpression(IntValue(3), IntValue(5)))

    val list = TACodeGenerator.generateStatement(recordAssignment, List())
    Temporary.reset()
    val t0 = new Temporary(IntegerType, 0, true)

    val ops = List(AddOp(Constant("3", IntegerType), Constant("5", IntegerType), t0, ""),
      RecordSet(t0, Constant("4", IntegerType),
        Name("d1", RecordType(List(VariableDeclaration("day", IntegerType), VariableDeclaration("month", IntegerType)))), ""))

    assert(list == ops)
  }

  test("testing RecordAssignment 3") {
    TACodeGenerator.reset()
    val userTypeName = "date"
    val typeVariables = List(VariableDeclaration("day", IntegerType), VariableDeclaration("month", IntegerType))
    val list_userTypes = List(UserDefinedType(userTypeName, RecordType(typeVariables)))
    val list_var = List(VariableDeclaration("d1", ReferenceToUserDefinedType("date")))

    TACodeGenerator.load_userTypes_and_vars(userTypes = list_userTypes, vars = list_var)

    val recordAssignment = AssignmentStmt(RecordAssignment(VarExpression("d1"), "month"), IntValue(5))

    val list = TACodeGenerator.generateStatement(recordAssignment, List())

    val ops = List(RecordSet(Constant("5", IntegerType), Constant("8", IntegerType),
      Name("d1", RecordType(List(VariableDeclaration("day", IntegerType), VariableDeclaration("month", IntegerType)))), ""))

    assert(list == ops)
  }

  test("testing RecordAssignment offset com array") {
    TACodeGenerator.reset()
    val userTypeName = "date"
    val typeVariables = List(VariableDeclaration("day", IntegerType), VariableDeclaration("lista", ArrayType(4, IntegerType)), VariableDeclaration("month", IntegerType))
    val list_userTypes = List(UserDefinedType(userTypeName, RecordType(typeVariables)))
    val list_var = List(VariableDeclaration("d1", ReferenceToUserDefinedType("date")))

    TACodeGenerator.load_userTypes_and_vars(userTypes = list_userTypes, vars = list_var)

    val recordAssignment = AssignmentStmt(RecordAssignment(VarExpression("d1"), "month"), IntValue(5))

    val list = TACodeGenerator.generateStatement(recordAssignment, List())

    val ops = List(RecordSet(Constant("5", IntegerType), Constant("24", IntegerType),
      Name("d1", RecordType(List(VariableDeclaration("day", IntegerType), VariableDeclaration("lista", ArrayType(4, IntegerType)), VariableDeclaration("month", IntegerType)))), ""))

    assert(list == ops)
  }

  test("testing Record Usage") {
    TACodeGenerator.reset()
    val userTypeName = "date"
    val typeVariables = List(VariableDeclaration("day", IntegerType), VariableDeclaration("lista", ArrayType(4, IntegerType)), VariableDeclaration("month", IntegerType))
    val list_userTypes = List(UserDefinedType(userTypeName, RecordType(typeVariables)))
    val list_var = List(VariableDeclaration("d1", ReferenceToUserDefinedType("date")))

    TACodeGenerator.load_userTypes_and_vars(userTypes = list_userTypes, vars = list_var)

    val recordUsage = FieldAccessExpression(VarExpression("d1"), "day")

    val (t, list) = TACodeGenerator.generateExpression(recordUsage, List())
    Temporary.reset()
    val t0 = new Temporary(IntegerType, 0, true)

    val ops = List(RecordGet(Name("d1", RecordType(List(VariableDeclaration("day", IntegerType), VariableDeclaration("lista", ArrayType(4, IntegerType)), VariableDeclaration("month", IntegerType)))),
      Constant("4", IntegerType), t0, ""))

    assert(list == ops)
  }

  test("testing New statement") {
    TACodeGenerator.reset()
    val list_var = List(VariableDeclaration("p", PointerType(IntegerType)))

    TACodeGenerator.load_vars(list_var)

    val newstmt = NewStmt("p")

    val list = TACodeGenerator.generateStatement(newstmt, List())
    Temporary.reset()

    val ops = List(New(Name("p", PointerType(IntegerType)), ""))

    assert(list == ops)
  }

  test("Testing OrExpression") {
    TACodeGenerator.reset
    val expr = OrExpression(BoolValue(true), BoolValue(false))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (true or false)

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(OrOp(Constant("true", BooleanType), Constant("false", BooleanType), t0, "")
    )

    assert(list == ops)
  }

  test("Testing AndExpression") {
    TACodeGenerator.reset
    val expr = AndExpression(BoolValue(true), BoolValue(false))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // (true and false)

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(AndOp(Constant("true", BooleanType), Constant("false", BooleanType), t0, "")
    )

    assert(list == ops)
  }

  test("Testing MultExpression") {
    TACodeGenerator.reset
    val expr = MultExpression(IntValue(3), IntValue(3))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // 3 * 3

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(MulOp(Constant("3", IntegerType), Constant("3", IntegerType), t0, ""))

    assert(list == ops)
  }

  test("Testing DivExpression") {
    TACodeGenerator.reset
    val expr = DivExpression(IntValue(2), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // 2 / 2

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(DivOp(Constant("2", IntegerType), Constant("2", IntegerType), t0, ""))

    assert(list == ops)
  }

  test("Testing SubExpression") {
    TACodeGenerator.reset
    val expr = SubExpression(IntValue(1), IntValue(1))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // 1 - 1

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, true)
    val ops = List(SubOp(Constant("1", IntegerType), Constant("1", IntegerType), t0, ""))

    assert(list == ops)
  }

 test("Testing ModExpression") {
    TACodeGenerator.reset
    val expr = ModExpression(IntValue(4), IntValue(2))
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    // 4 % 2

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0, false)
    val ops = List(RemOp(Constant("4", IntegerType), Constant("2", IntegerType), t0, ""))

    assert(list == ops)
  }

  test("testing Exit statement") {
    TACodeGenerator.reset()
    val exitstmt = ExitStmt()
    val list = TACodeGenerator.generateStatement(exitstmt, List())

    val ops = List(Exit(""))

    assert(list == ops)
  }

  test("testing Return statement") {
    TACodeGenerator.reset()
    val returnstmt = ReturnStmt(IntValue(5))
    val list = TACodeGenerator.generateStatement(returnstmt, List())
    // return 5

    val ops = List(Return(Constant("5", IntegerType), ""))

    assert(list == ops)
  }

  test("testing Write statement") {
    TACodeGenerator.reset()
    val writestmt = WriteStmt(IntValue(1))
    val list = TACodeGenerator.generateStatement(writestmt, List())

    val ops = List(Write(Constant("1", IntegerType), ""))

    assert(list == ops)
  }

  //test("Testing WhileStmt with Array Assignment") {
  //  TACodeGenerator.reset
  //  val list_var = List(VariableDeclaration("var", IntegerType))
  //  TACodeGenerator.load_vars(list_var)

  //  val condition = LTExpression(VarExpression("var"), IntValue(10))
  //  val thenStmt = AssignmentStmt(
  //    ArrayAssignment(VarExpression("lista"), IntValue(1)),
  //    AddExpression(IntValue(1), IntValue(2))
  //  )
  //  val whileStmt = WhileStmt(condition, thenStmt)
  //  val list = TACodeGenerator.generateStatement(whileStmt, List())

  //  val ops = List()
  //  assert(list == ops)
  //}

  test("Testing Array Assignment With Expressions (index) 1") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("lista", ArrayType(4, IntegerType)))
    TACodeGenerator.load_vars(list_var)

    val stmt = AssignmentStmt(
      ArrayAssignment(VarExpression("lista"), MultExpression(IntValue(2), IntValue(2))),
      DivExpression(MultExpression(IntValue(3), IntValue(4)), IntValue(7))
    )
    val list = TACodeGenerator.generateStatement(stmt, List())

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0)
    val t1 = new Temporary(IntegerType, 1)
    val expr = TACodeGenerator.generateExpression(MultExpression(IntValue(4), MultExpression(IntValue(2), IntValue(2))), List())
    val offset = Constant(expr.toString, IntegerType)

    val ops = List(
        MulOp(Constant("3", IntegerType), Constant("4", IntegerType), t0, ""),
        DivOp(t0, Constant("7", IntegerType), t1, ""),
        ArraySet(t1, offset, Name("lista", ArrayType(4, IntegerType)), ""))

    // lista[2*2] = (3*4)/7
    assert(list == ops)

  }

  test("Testing Array Assignment With Expressions (index) 2") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("lista", ArrayType(4, IntegerType)))
    TACodeGenerator.load_vars(list_var)

    val stmt = AssignmentStmt(
      ArrayAssignment(VarExpression("lista"), SubExpression(MultExpression(IntValue(6), IntValue(8)), IntValue(3))),
      IntValue(1)
    )
    val list = TACodeGenerator.generateStatement(stmt, List())

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0)
    val t1 = new Temporary(IntegerType, 1)
    val expr = TACodeGenerator.generateExpression(MultExpression(IntValue(4), SubExpression(MultExpression(IntValue(6), IntValue(8)), IntValue(3))), List())
    val offset = Constant(expr.toString, IntegerType)

    val ops = List(
        ArraySet(Constant("1", IntegerType), offset, Name("lista", ArrayType(4, IntegerType)), "")
    )

    // lista[3-(6*8)] = 1
    assert(list == ops)

  }

  test("Testing Array Assignment With Expressions (index) 3") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("pointer", PointerType(IntegerType)), VariableDeclaration("lista", ArrayType(4, IntegerType)))
    TACodeGenerator.load_vars(list_var)

    val stmt = AssignmentStmt(
        ArrayAssignment(VarExpression("lista"), DivExpression(IntValue(6), IntValue(2))),
        AddExpression(PointerAccessExpression("pointer"), IntValue(3))
    )
    val list = TACodeGenerator.generateStatement(stmt, List())


    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0)
    val t1 = new Temporary(IntegerType, 1)
    val expr = TACodeGenerator.generateExpression(MultExpression(IntValue(4), DivExpression(IntValue(6), IntValue(2))), List())
    val offset = Constant(expr.toString, IntegerType)

    val ops = List(
        GetValue(Name("pointer", LocationType), t0, ""),
        AddOp(t0, Constant("3", IntegerType), t1, ""),
        ArraySet(t1, offset, Name("lista", ArrayType(4, IntegerType)), "")
    )
    
    // Lista[6/2] = *pointer + 3
    assert(list == ops)

  }

  test("Testing Array Assignment With Variables 1") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var1", IntegerType), 
        VariableDeclaration("var2", IntegerType),
        VariableDeclaration("lista", ArrayType(4, IntegerType))
    )
    TACodeGenerator.load_vars(list_var)

    val stmt = AssignmentStmt(
        ArrayAssignment(VarExpression("lista"), VarExpression("var1")),
        AddExpression(IntValue(1), VarExpression("var2"))
    )
    val list = TACodeGenerator.generateStatement(stmt, List())
    val expr = TACodeGenerator.generateExpression(MultExpression(IntValue(4), VarExpression("var1")), List())
    val offset = Constant(expr.toString, IntegerType)

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0)

    val ops = List(
        AddOp(Constant("1", IntegerType), Name("var2", IntegerType), t0, ""),
        ArraySet(t0, offset, Name("lista", ArrayType(4, IntegerType)), "")
    )
    
    // Lista[var1] = 1 + var2
    assert(list == ops)

  }

  test("Testing Array Assignment With Variables 2") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var1", BooleanType), 
        VariableDeclaration("var2", BooleanType),
        VariableDeclaration("lista", ArrayType(4, IntegerType))
    )
    TACodeGenerator.load_vars(list_var)

    val stmt = AssignmentStmt(
        ArrayAssignment(VarExpression("lista"), IntValue(0)),
        AndExpression(VarExpression("var1"), VarExpression("var2"))
    )
    val list = TACodeGenerator.generateStatement(stmt, List())

    TACodeGenerator.reset
    val t0 = new Temporary(BooleanType, 0)
    val ops = List(
        AndOp(Name("var1", BooleanType), Name("var2", BooleanType), t0, ""),
        ArraySet(t0, Constant("0", IntegerType), Name("lista", ArrayType(4, IntegerType)), "")
    )
    
    // Lista[0] = var1 and var2
    assert(list == ops)

  }

  test("Testing Array Operations With Variables 1") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var", IntegerType), VariableDeclaration("lista", ArrayType(4, IntegerType)))
    TACodeGenerator.load_vars(list_var)
    val stmt = AssignmentStmt(
      VarAssignment("var"),
      AddExpression(ArraySubscript(VarExpression("lista"), IntValue(1)), ArraySubscript(VarExpression("lista"), IntValue(2)))
    )
    var list = TACodeGenerator.generateStatement(stmt, List())
    TACodeGenerator.reset

    val t0 = new Temporary(IntegerType, 0, false)
    val t1 = new Temporary(IntegerType, 1, false)
    val t2 = new Temporary(IntegerType, 2, false)
    var ops = List(
        ArrayGet(Name("lista", ArrayType(4, IntegerType)), Constant("1", IntegerType), t0, ""),
        ArrayGet(Name("lista", ArrayType(4, IntegerType)), Constant("2", IntegerType), t1, ""),
        AddOp(t0, t1, t2, ""),
        CopyOp(t2, Name("var", IntegerType), "")
    )    

    // var = lista[1] + lista[2]
    assert(list == ops)

  }

  test("Testing Array Operations With Variables 2") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var", IntegerType), VariableDeclaration("lista", ArrayType(4, IntegerType)))
    TACodeGenerator.load_vars(list_var)
    val expr = SubExpression(
        VarExpression("var"),
        AddExpression(ArraySubscript(VarExpression("lista"), VarExpression("var")), ArraySubscript(VarExpression("lista"), IntValue(2)))
    )
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    println(list)
    TACodeGenerator.reset

    val t0 = new Temporary(IntegerType, 0, false)
    val t1 = new Temporary(IntegerType, 1, false)
    val t2 = new Temporary(IntegerType, 2, false)
    val t3 = new Temporary(IntegerType, 3, false)
    var ops = List(
        ArrayGet(Name("lista", ArrayType(4, IntegerType)), Name("var", IntegerType), t0, ""),
        ArrayGet(Name("lista", ArrayType(4, IntegerType)), Constant("2", IntegerType), t1, ""),
        AddOp(t0, t1, t2, ""),
        SubOp(Name("var", IntegerType), t2, t3, "")
    )    

    // (lista[var] + lista[2]) - var
    assert(list == ops)

  }

  test("Testing Array Operations With Variables 3") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var", IntegerType), 
                        VariableDeclaration("lista1", ArrayType(4, IntegerType)),
                        VariableDeclaration("lista2", ArrayType(4, IntegerType))
    )
    TACodeGenerator.load_vars(list_var)

    val expr = DivExpression(
        ArraySubscript(VarExpression("lista1"), VarExpression("var")),
        ArraySubscript(VarExpression("lista2"), ArraySubscript(VarExpression("lista1"), VarExpression("var")))
    )
    val (t, list) = TACodeGenerator.generateExpression(expr, List())
    println(list)
    TACodeGenerator.reset

    val t0 = new Temporary(IntegerType, 0, false)
    val t1 = new Temporary(IntegerType, 1, false)
    val t2 = new Temporary(IntegerType, 2, false)
    val t3 = new Temporary(IntegerType, 3, false)
    var ops = List(
        ArrayGet(Name("lista1", ArrayType(4, IntegerType)), Name("var", IntegerType), t0, ""),
        ArrayGet(Name("lista1", ArrayType(4, IntegerType)), Name("var", IntegerType), t1, ""),
        ArrayGet(Name("lista2", ArrayType(4, IntegerType)), t1, t2, ""),
        DivOp(t0, t2, t3, "")
    )    

    // lista1[var] / lista2[lista1[var]])
    assert(list == ops)

  }

  test("Testing Loops With Array Operations 1") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("var", IntegerType),
                        VariableDeclaration("lista", ArrayType(4, IntegerType))
    )
    TACodeGenerator.load_vars(list_var)

    val condition = LTExpression(ArraySubscript(VarExpression("lista"), IntValue(1)), IntValue(10))
    val thenStmt = AssignmentStmt(
      ArrayAssignment(VarExpression("lista"), IntValue(1)),
      AddExpression(ArraySubscript(VarExpression("lista"), IntValue(1)), ArraySubscript(VarExpression("lista"), IntValue(2)))
    )
    val whileStmt = WhileStmt(condition, thenStmt)
    var list = TACodeGenerator.generateStatement(whileStmt, List())

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

    // while (lista[1] < 10) {lista[1] = lista[1] + lista[2]}
    assert(list == ops)

  }

  test("Testing Loops With Array Operations 2") {
    TACodeGenerator.reset
    val list_var = List(VariableDeclaration("lista1", ArrayType(4, IntegerType)),
                        VariableDeclaration("lista2", ArrayType(4, IntegerType))
    )
    TACodeGenerator.load_vars(list_var)

    val condition = GTExpression(ArraySubscript(VarExpression("lista1"), IntValue(1)),
                                 ArraySubscript(VarExpression("lista2"), IntValue(10))
    )
    val thenStmt = AssignmentStmt(
      ArrayAssignment(VarExpression("lista1"), IntValue(1)),
      SubExpression(ArraySubscript(VarExpression("lista1"), IntValue(1)), ArraySubscript(VarExpression("lista2"), IntValue(3)))
    )
    val whileStmt = WhileStmt(condition, thenStmt)
    var list = TACodeGenerator.generateStatement(whileStmt, List())
    println(list)

    TACodeGenerator.reset
    val t0 = new Temporary(IntegerType, 0)
    val t1 = new Temporary(IntegerType, 1)
    val t2 = new Temporary(IntegerType, 2)
    val t3 = new Temporary(IntegerType, 3)
    val t4 = new Temporary(IntegerType, 3)
    val l1 = LabelGenerator.generateLabel
    val l2 = LabelGenerator.generateLabel
    var ops = List(
        Jump(l1, ""),
        NOp(l2),
        ArrayGet(Name("lista1", ArrayType(4, IntegerType)), Constant("1", IntegerType), t0, ""),
        ArrayGet(Name("lista2", ArrayType(4, IntegerType)), Constant("3", IntegerType), t1, ""),
        SubOp(t0, t1, t2, ""),
        ArraySet(t2, Constant("4", IntegerType), Name("lista1", ArrayType(4, IntegerType)), ""),
        NOp(l1),
        ArrayGet(Name("lista1", ArrayType(4, IntegerType)), Constant("1", IntegerType), t3, ""),
        ArrayGet(Name("lista2", ArrayType(4, IntegerType)), Constant("10", IntegerType), t4, ""),
        GTEJump(t3, t4, l2, "")
    )

    // while (lista1[1] > lista2[10]) {lista1[1] = lista1[1] - lista2[3]}
    assert(list == ops)

  }

  test("Testing IfElse-NEQExpression") {
    TACodeGenerator.reset

    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val condition = NEQExpression(IntValue(0), IntValue(0))
    val thenStmt = AssignmentStmt(
      VarAssignment("var"),
      AddExpression(IntValue(1), IntValue(2))
    )
    val elseStmt = None
    val ifElseStmt = IfElseStmt(condition, thenStmt, elseStmt)
    val list = TACodeGenerator.generateStatement(ifElseStmt, List())

    TACodeGenerator.reset

    val t0 = new Temporary(IntegerType, 0, true)
    val l1 = LabelGenerator.generateLabel
    val ops = List(
      EqJump(Constant("0", IntegerType), Constant("0", IntegerType), l1, ""),
      AddOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      CopyOp(t0, Name("var", IntegerType), ""),
      NOp(l1)
    )
    // if(1!=1){var = 1 + 2}
    assert(list == ops)
  }

  test("Testing IfElse-GTExpression") {
    TACodeGenerator.reset

    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val condition = GTExpression(IntValue(2), IntValue(1))
    val thenStmt = AssignmentStmt(
      VarAssignment("var"),
      MultExpression(IntValue(1), IntValue(2))
    )
    val elseStmt = None
    val ifElseStmt = IfElseStmt(condition, thenStmt, elseStmt)
    val list = TACodeGenerator.generateStatement(ifElseStmt, List())

    TACodeGenerator.reset

    val t0 = new Temporary(IntegerType, 0, true)
    val l1 = LabelGenerator.generateLabel
    val ops = List(
      LTEJump(Constant("2", IntegerType), Constant("1", IntegerType), l1, ""),
      MulOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      CopyOp(t0, Name("var", IntegerType), ""),
      NOp(l1)
    )
    // if(2>1){var = 1 * 2}
    assert(list == ops)
  }

  test("Testing IfElse-LTExpression") {
    TACodeGenerator.reset

    val list_var = List(VariableDeclaration("var", IntegerType))
    TACodeGenerator.load_vars(list_var)
    val condition = LTExpression(IntValue(2), IntValue(1))
    val thenStmt = AssignmentStmt(
      VarAssignment("var"),
      MultExpression(IntValue(1), IntValue(2))
    )
    val elseStmt = None
    val ifElseStmt = IfElseStmt(condition, thenStmt, elseStmt)
    val list = TACodeGenerator.generateStatement(ifElseStmt, List())

    TACodeGenerator.reset

    val t0 = new Temporary(IntegerType, 0, true)
    val l1 = LabelGenerator.generateLabel
    val ops = List(
      GTEJump(Constant("2", IntegerType), Constant("1", IntegerType), l1, ""),
      MulOp(Constant("1", IntegerType), Constant("2", IntegerType), t0, ""),
      CopyOp(t0, Name("var", IntegerType), ""),
      NOp(l1)
    )
    // if(2<1){var = 1 * 2}
    assert(list == ops)
  }

}
