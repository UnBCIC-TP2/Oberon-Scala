package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ast.OberonModule

/**
 * The base interface for code generation.
 * We can generate code for different target languages (e.g.: C, C++, LLVM, JIMPLE) and
 * using different technologies (String Interpolation, Pretty Printer Library, and so on).
 */
trait CodeGenerator {
  /**
   * Generates code for an Oberon module
   *
   * @param module oberon module target of the code generator
   *
   * @return the resulting code for a given target language.
   */
  def generateCode(module: OberonModule): String
}
