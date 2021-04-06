package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ast._

import org.objectweb.asm._
import org.objectweb.asm.Opcodes._

import java.util.Base64

object JVMCodeGenerator extends CodeGenerator {
  override def generateCode(module: OberonModule): String = {
    val cw = new ClassWriter(0);

    cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, module.name, null, "java/lang/Object", null);
    //TODO: next steps:
    //      (a) generate fields from variables (deadline: 13/04).
    //      (b) generate methods from procedures (27/04).
    cw.visitEnd();

    Base64.getEncoder().encodeToString(cw.toByteArray);
  }


}

