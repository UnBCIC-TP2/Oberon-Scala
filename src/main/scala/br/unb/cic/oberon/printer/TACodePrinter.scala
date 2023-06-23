package br.unb.cic.oberon.printer

import br.unb.cic.oberon.codegen.CodeGenerator
import br.unb.cic.oberon.ir.ast.OberonModule
import br.unb.cic.oberon.ir.tac.TAC

object TACodePrinter extends CodeGenerator[String] {

  /**
   * Generates code for an Oberon module
   *
   * @param module oberon module target of the code generator
   * @return a string of a TAC represantation to be printed.
   */
  override def generateCode(module: OberonModule): String = {
    return ""
  }

  def printExpression(tacModule : List[TAC]): String = {
    tacModule.foreach {
      case
    }
  }



}
