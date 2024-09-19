package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.IntegerType
import br.unb.cic.oberon.ir.ast.Statement
import br.unb.cic.oberon.ir.tac._
import org.typelevel.paiges.Doc
import org.typelevel.paiges.Doc._

object RiscvCodeGenerator extends CodeGenerator[List[TAC], String] {
    val indentSize: Int = 4
    val twoLines: Doc = line * 2

    override def generateCode(module: List[TAC]): String = {
        val mainHeader =
            text(".data") / text(".text") + twoLines

        val textContent = text(module.map(generateOps).mkString)

        return (mainHeader + textContent).render(60)
    
    }

    def addrToReg(addr: Address): String = {
        addr match {
            case Temporary(t, num, manual) => {
                s"x$num"
            }
        }
    }

    def generateBinExp(addrLeft: Address, addrRight: Address, addrDest: Temporary, instr: String): String = {
        (addrLeft, addrRight) match {
            case (Constant(valueLeft, _), Constant(valueRight, _)) => 
                s"li x5,${valueLeft}\n" +
                s"li x6,${valueRight}\n" +
                s"${instr} ${addrToReg(addrDest)},x5,x6\n"
            case (Constant(valueLeft, _), Temporary(_, num, _)) => 
                s"li x5,${valueLeft}\n" +
                s"${instr} ${addrToReg(addrDest)},x5,x$num\n"
            case (Temporary(_, num, _), Constant(valueRight, _)) =>
                s"li x5,${valueRight}\n" +
                s"${instr} ${addrToReg(addrDest)},x$num,x5\n"
            case (Temporary(_, numLeft, _), Temporary(_, numRight, _)) => 
                s"${instr} ${addrToReg(addrDest)},x$numLeft,x${numRight}\n"
            case _ => throw new Exception("invalid binary expression")
        }
        
    }

    def generateOps(op: TAC): String = {
        op match {
            case AddOp(s1, s2, dest:Temporary, label) =>  generateBinExp(s1, s2, dest, "add")
            case SubOp(s1, s2, dest:Temporary, label) =>  generateBinExp(s1, s2, dest, "sub")
            case MulOp(s1, s2, dest:Temporary, label) =>  generateBinExp(s1, s2, dest, "mul")
            case DivOp(s1, s2, dest:Temporary, label) =>  generateBinExp(s1, s2, dest, "div")
            case RemOp(s1, s2, dest:Temporary, label) =>  generateBinExp(s1, s2, dest, "rem")
            case AndOp(s1, s2, dest:Temporary, label) =>  generateBinExp(s1, s2, dest, "and")
            case OrOp(s1, s2, dest:Temporary, label) =>   generateBinExp(s1, s2, dest, "or")
            case SLTOp(s1, s2, dest:Temporary, label) =>  generateBinExp(s1, s2, dest, "slt")
            case SLTUOp(s1, s2, dest:Temporary, label) => generateBinExp(s1, s2, dest, "sltu")
            case NOp(label) => s"$label:\n"
            case Jump(destLabel, label) => s"j $destLabel\n"
            case JumpFalse(s1:Temporary, destLabel, label) => s"beq x${s1.num},x0,$destLabel\n"
            case JumpTrue(s1:Temporary, destLabel, label) => s"bne x${s1.num},x0,$destLabel\n"
            case LTEJump(s1:Temporary, s2:Temporary, destLabel, label) => s"ble x${s1.num},x${s2.num},$destLabel\n"
            case LTJump(s1:Temporary, s2:Temporary, destLabel, label) => s"blt x${s1.num},x${s2.num},$destLabel\n"
            case GTEJump(s1:Temporary, s2:Temporary, destLabel, label) => s"bge x${s1.num},x${s2.num},$destLabel\n"
            case GTJump(s1:Temporary, s2:Temporary, destLabel, label) => s"bgt x${s1.num},x${s2.num},$destLabel\n"
            case EqJump(s1:Temporary, s2:Temporary, destLabel, label) => s"beq x${s1.num},x${s2.num},$destLabel\n"
            case NeqJump(s1:Temporary, s2:Temporary, destLabel, label) => s"bne x${s1.num},x${s2.num},$destLabel\n"
            case NegOp(s1:Temporary, dest:Temporary, label) => s"neg x${dest.num},x${s1.num}\n"
            case NotOp(s1:Temporary, dest:Temporary, label) => s"not x${dest.num},x${s1.num}\n"
            case MoveOp(s1:Temporary, dest:Temporary, label) => s"mv x${dest.num},x${s1.num}\n"
            case _ => throw new Exception("invalid operation")

        }
    }
}
