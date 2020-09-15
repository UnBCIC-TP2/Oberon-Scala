package br.unb.cic.oberon.codegen
import br.unb.cic.oberon.ast.OberonModule

abstract class CCodeGenerator extends CodeGenerator {

}


class PaigesBasedGenerator extends CCodeGenerator {
  override def generateCode(module: OberonModule): String = ???
}

class PPrintBasedGenerator extends CCodeGenerator {
  override def generateCode(module: OberonModule): String = ???
}
