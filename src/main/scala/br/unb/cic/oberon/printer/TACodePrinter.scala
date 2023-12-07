package br.unb.cic.oberon.printer

import br.unb.cic.oberon.ir.tac.{AddOp, Address, AndOp, Constant, MoveOp, DivOp, EqJump, GTEJump, GTJump, Jump, JumpFalse, JumpTrue, LTEJump, LTJump, MulOp, Name, NeqJump, NotOp, OrOp, RemOp, SLTOp, SubOp, TAC, Temporary}
import org.typelevel.paiges.Doc
import org.typelevel.paiges.Doc.{ line, text }

/**
 * This class has the responsibility to print TAC Abstraction in Scala
 * We wanted an string visualization of the abstraction
 * We can also get a Doc representation using an external library (paiges)
 * @author Marcelo M. Amorim
 * @since 28/06/2023
 */
object TACodePrinter {

  private val jumpLine : String = "\n"
  private val tab : String = "  "
  private val ifStatement : String = "if"
  private val jumpStatement : String = "jump"

  /**
   *
   * @param instructions:
   * @return
   */
  private def buildDocument(instructions: List[TAC]): Doc = {
    val initializeDoc = Doc.empty
    instructions.foldLeft(initializeDoc)(generateCode)
  }

  /**
   *
   * @param tac: document
   * @param instruction: instruction to deal with
   * @return
   */
  private def generateCode(tac: Doc, instruction: TAC): Doc = {

    instruction match {
      case AddOp(s1, s2, dest, label) => tac / text(handleArithmeticOps(dest, s1, s2, "+", label))
      case SubOp(s1, s2, dest, label) => tac / text(handleArithmeticOps(dest, s1, s2, "-", label))
      case MulOp(s1, s2, dest, label) => tac / text(handleArithmeticOps(dest, s1, s2, "*", label))
      case DivOp(s1, s2, dest, label) => tac / text(handleArithmeticOps(dest, s1, s2, "/", label))
      case AndOp(s1, s2, dest, label) => tac / text(handleArithmeticOps(dest, s1, s2, "&&", label))
      case OrOp(s1, s2, dest, label) => tac / text(handleArithmeticOps(dest, s1, s2, "||", label))
      case RemOp(s1, s2, dest, label) => tac / text(handleArithmeticOps(dest, s1, s2, "%", label))
      case EqJump(s1, s2, dest, label) => tac / text(handleConditionalOps(s1,s2,"==",dest, label))
      case NeqJump(s1, s2, dest, label) => tac / text(handleConditionalOps(s1,s2,"!=",dest, label))
      case GTJump(s1, s2, dest, label) => tac / text(handleConditionalOps(s1,s2,">",dest, label))
      case GTEJump(s1, s2, dest, label) => tac / text(handleConditionalOps(s1,s2,">=",dest, label))
      case LTJump(s1, s2, dest, label) => tac / text(handleConditionalOps(s1,s2,"<",dest, label))
      case LTEJump(s1, s2, dest, label) => tac / text(handleConditionalOps(s1,s2,"<=>",dest, label))
      case JumpTrue(s1, dest, label) => tac / text(s"if ${handleAddress(s1)} == true $jumpStatement $dest")
      case JumpFalse(s1, dest, label) => tac / text(s"if ${handleAddress(s1)} == false $jumpStatement $dest")
      case Jump(dest, label) => tac / text(s"$jumpStatement $dest")
      case NotOp(s1, dest, label) => tac / text(s"${handleAddress(dest)} = NOT ${handleAddress(s1)}")
      case MoveOp(s1, dest, label) => tac / text(s"${handleAddress(dest)} = ${handleAddress(s1)}")
      case SLTOp(s1, s2, dest, label) => tac / text(s"${handleAddress(dest)} = SLT ${handleAddress(s1)} ${handleAddress(s2)}")
      case _ => text("Not implemented in printer")
    }

  }

  /**
   *
   * @param destiny of operation
   * @param s1 storage 1
   * @param s2 storage 2
   * @param operation of instruction
   * @return
   */
  private def handleArithmeticOps(destiny : Address, s1 : Address, s2: Address, operation : String, label : String): String = {
    label match {
      case "" => s"${handleAddress(destiny)} = ${handleAddress(s1)} $operation ${handleAddress(s2)}"
      case _ => s"$label:" + jumpLine + tab + s"${handleAddress(destiny)} = ${handleAddress(s1)} $operation ${handleAddress(s2)}"
    }
  }

  /**
   *
   * @param s1 : storage 1
   * @param s2 : storage 2
   * @param operation : op
   * @param destLabel : destiny
   * @param label : label
   * @return
   */
  private def handleConditionalOps(s1: Address, s2: Address, operation: String, destLabel: String, label: String): String = {
    label match {
      case "" => ifStatement + s"${handleAddress(s1)} $operation ${handleAddress(s2)} jump $destLabel"
      case _ => s"$label:" + jumpLine + tab + ifStatement + s"${handleAddress(s1)} $operation ${handleAddress(s2)} jump $destLabel"
    }
  }

  /**
   * @param address address to be handled
   */
  private def handleAddress(address : Address): String = {
    address match {
      case Temporary(t, number, p) => s"t$number"
      case Constant(value, t) => s"$value"
      case Name(id, t) => s"$id"
    }
  }

  /**
   * This method print all items in instructions list
   * @param instructions : reference to instructions list
   */
  def getTacDocumentStringFormatted(instructions: List[TAC]): String = {
    val tacToPrint: Doc = getTacDocument(instructions)
    tacToPrint.render(60)
  }

  private def getTacDocument(instructions: List[TAC]): Doc = {
    buildDocument(instructions)
  }

}
