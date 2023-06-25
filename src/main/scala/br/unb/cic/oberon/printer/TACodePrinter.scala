package br.unb.cic.oberon.printer

import br.unb.cic.oberon.ir.tac.{AddOp, Address, AndOp, Constant, DivOp, MulOp, Name, NotOp, OrOp, SubOp, TAC, Temporary}

object TACodePrinter {

  def printInstructionSequence(instructions : List[TAC]) : Unit = {
    instructions.foreach(instruction => printInstruction(instruction))
  }
  /**
   *
   *
   */
  private def printInstruction(instruction : TAC): Unit  = {
    instruction match {
      case AddOp(s1, s2, dest, label) => println(s"${handleAddress(dest)} = ${handleAddress(s1)} + ${handleAddress(s2)}")
      case SubOp(s1, s2, dest, label) => println(s"${handleAddress(dest)} = ${handleAddress(s1)} - ${handleAddress(s2)}")
      case MulOp(s1, s2, dest, label) => println(s"${handleAddress(dest)} = ${handleAddress(s1)} * ${handleAddress(s2)}")
      case DivOp(s1, s2, dest, label) => println(s"${handleAddress(dest)} = ${handleAddress(s1)} / ${handleAddress(s2)}")
      case AndOp(s1, s2, dest, label) => println(s"${handleAddress(dest)} = ${handleAddress(s1)} && ${handleAddress(s2)}")
      case OrOp(s1, s2, dest, label) => println(s"${handleAddress(dest)} = ${handleAddress(s1)} || ${handleAddress(s2)}")
      case NotOp(s1, dest, label) => println(s"${handleAddress(dest)} = not(${handleAddress(s1)})")
    }
  }
  /**
   *
   *
   */
  private def handleAddress(address : Address): String = {
    address match {
      case Temporary(t, number, p) => s"t$number"
      case Constant(value, t) => s"$value"
      case Name(id, t) => s"$id"
    }
  }

}
