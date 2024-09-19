package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.{Constant => ASTConstant, _}
import br.unb.cic.oberon.ir.tac._
import br.unb.cic.oberon.util.Resources
import org.scalatest.funsuite.AnyFunSuite


class RiscvCodeGenTest extends AnyFunSuite {
    test("add + rem + slt") {
      TACodeGenerator.reset
      val t0 = new Temporary(IntegerType, 8, true)
      val t1 = new Temporary(IntegerType, 9, true)
      val t2 = new Temporary(IntegerType, 10, true)
      val ops = List(
        AddOp(Constant("9", IntegerType), Constant("4", IntegerType), t0, ""),
        RemOp(t0, Constant("3", IntegerType), t1, ""),
        SLTOp(t1, t0, t2, "")
      )

      val generatedCode = RiscvCodeGenerator.generateCode(ops)
      val baseCode = Resources.getContent(s"riscvCode/add_rem_slt.asm")

      assert(generatedCode == baseCode)
    }

    test("sub + div + mul") {
      TACodeGenerator.reset
      val t0 = new Temporary(IntegerType, 8, true)
      val t1 = new Temporary(IntegerType, 9, true)
      val t2 = new Temporary(IntegerType, 10, true)
      val ops = List(
        MulOp(Constant("2", IntegerType), Constant("2", IntegerType), t0, ""),
        DivOp(t0, Constant("3", IntegerType), t1, ""),
        SubOp(t1, Constant("6", IntegerType), t2, "")
      )
      val generatedCode = RiscvCodeGenerator.generateCode(ops)
      val baseCode = Resources.getContent(s"riscvCode/sub_div_mul.asm")  

      assert(generatedCode == baseCode)
    }
    
    test("jump operation") {
      TACodeGenerator.reset
      val t0 = new Temporary(IntegerType, 8, true)
      val t1 = new Temporary(IntegerType, 9, true)
      val l1 = LabelGenerator.generateLabel.replace(":","")
      val ops = List(
        AddOp(Constant("1", IntegerType), Constant("6", IntegerType), t0, ""),
        Jump(l1, ""),
        AddOp(Constant("4", IntegerType), Constant("5", IntegerType), t0, ""),
        NOp(l1)
      )

      val generatedCode = RiscvCodeGenerator.generateCode(ops)
      val baseCode = Resources.getContent(s"riscvCode/jump.asm")  
      assert(generatedCode == baseCode)
    }

    test("and + or + slt") {
      TACodeGenerator.reset
      val t0 = new Temporary(IntegerType, 8, true)
      val t1 = new Temporary(IntegerType, 9, true)
      val t2 = new Temporary(IntegerType, 10, true)
      val ops = List(
        AndOp(Constant("5", IntegerType), Constant("-7", IntegerType), t0, ""),
        OrOp(Constant("5", IntegerType), Constant("-7", IntegerType), t1, ""),
        SLTUOp(t0, t1, t2, ""),
      )

      val generatedCode = RiscvCodeGenerator.generateCode(ops)
      val baseCode = Resources.getContent(s"riscvCode/and_or_sltu.asm")  
      assert(generatedCode == baseCode)
    }

    test("jumptrue + jumpfalse") {
      TACodeGenerator.reset
      val t0 = new Temporary(IntegerType, 8, true)
      val t1 = new Temporary(IntegerType, 9, true)
      val t2 = new Temporary(IntegerType, 10, true)
      val zero = new Temporary(IntegerType, 0, true)
      val l0 = LabelGenerator.generateLabel.replace(":","")
      val l1 = LabelGenerator.generateLabel.replace(":","")

      val ops = List(
        AddOp(Constant("2", IntegerType), zero, t0, ""),
        JumpFalse(t0, l0, ""),
        AddOp(Constant("6", IntegerType), zero, t1, ""),
        JumpTrue(t0, l1, ""),
        NOp(l0),
        AndOp(t0, t1, t2, ""),
        NOp(l1)
      )

      val generatedCode = RiscvCodeGenerator.generateCode(ops)
      val baseCode = Resources.getContent(s"riscvCode/jumptrue_jumpfalse.asm")  
      assert(generatedCode == baseCode)
    }

    test("lte + lt + gte jumps") {
      TACodeGenerator.reset
      val t0 = new Temporary(IntegerType, 8, true)
      val t1 = new Temporary(IntegerType, 9, true)
      val t2 = new Temporary(IntegerType, 10, true)
      val zero = new Temporary(IntegerType, 0, true)
      val l0 = LabelGenerator.generateLabel.replace(":","")
      val l1 = LabelGenerator.generateLabel.replace(":","")
      val l2 = LabelGenerator.generateLabel.replace(":","")

      val ops = List(
        AddOp(Constant("1", IntegerType), zero, t0, ""),
        LTEJump(t0, zero, l0, ""),
        LTJump(t0, zero, l1, ""),
        GTEJump(t0, zero, l2, ""),
        NOp(l0),
        NOp(l1),  
        NOp(l2),
      )

      val generatedCode = RiscvCodeGenerator.generateCode(ops)
      val baseCode = Resources.getContent(s"riscvCode/lte_lt_gte_jumps.asm")  
      assert(generatedCode == baseCode)
    }

    test("gt + eq + neq jumps") {
      TACodeGenerator.reset
      val t0 = new Temporary(IntegerType, 8, true)
      val t1 = new Temporary(IntegerType, 9, true)
      val t2 = new Temporary(IntegerType, 10, true)
      val zero = new Temporary(IntegerType, 0, true)
      val l0 = LabelGenerator.generateLabel.replace(":","")
      val l1 = LabelGenerator.generateLabel.replace(":","")
      val l2 = LabelGenerator.generateLabel.replace(":","")

      val ops = List(
        AddOp(Constant("1", IntegerType), zero, t0, ""),
        GTJump(t0, zero, l0, ""),
        EqJump(t0, zero, l1, ""),
        NeqJump(t0, zero, l2, ""),
        NOp(l0),
        NOp(l1),  
        NOp(l2),
      )

      val generatedCode = RiscvCodeGenerator.generateCode(ops)
      val baseCode = Resources.getContent(s"riscvCode/gt_eq_neq_jumps.asm")  
      assert(generatedCode == baseCode)
    }
    
    test("not + neg + move") {
      TACodeGenerator.reset
      val t0 = new Temporary(IntegerType, 8, true)
      val t1 = new Temporary(IntegerType, 9, true)
      val t2 = new Temporary(IntegerType, 10, true)
      val zero = new Temporary(IntegerType, 0, true)

      val ops = List(
        AddOp(zero, zero, t0, ""),
        NotOp(t0, t1, ""),
        NegOp(t1, t2, ""),
        MoveOp(t2, t0, "")
      )

      val generatedCode = RiscvCodeGenerator.generateCode(ops)
      val baseCode = Resources.getContent(s"riscvCode/not_neg_mv.asm")  
      assert(generatedCode == baseCode)
    }
    
    
}