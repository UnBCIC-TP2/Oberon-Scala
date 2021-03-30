package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ast._
import scala.annotation.tailrec
import org.typelevel.paiges._

// Testando
import org.objectweb.asm._
import org.objectweb.asm.signature._
import org.objectweb.asm.Opcodes._

abstract class JVMCodeGenerator extends CodeGenerator {}

case class PaigeBasedGenerator(lineSpaces: Int = 2) extends JVMCodeGenerator {
  override def generateCode(module: OberonModule): String = {
    val cw = new ClassWriter(0);
    cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "pkg/Comparable", null, "java/lang/Object", Array[String] { "pkg/Mesurable"});
    cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I", null, new Integer(-1)).visitEnd();
    cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I", null, new Integer(0)).visitEnd();
    cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I", null, new Integer(1)).visitEnd();
    cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "compareTo","(Ljava/lang/Object;)I", null, null).visitEnd();
    cw.visitEnd();

    var ipaddr = cw.toByteArray();

    println(ipaddr)

    return "abelha"
  }

  // def generateProcedure(procedure: Procedure, lineSpaces: Int = 2): Doc = {
  // }

  // def generateDeclarations() = {
  // }

  // def generateConstants(constants: List[Constant]): Doc = {
  // }

  // def generateStatement(
  //     statement: Statement,
  //     startSpaces: Int = 2,
  //     padSpaces: Int = 2
  // ): Doc = {
  // }

  // def generateExpression(expression: Expression): Doc = {
  // }

  // def generateBinExpression(
  //     left: Expression,
  //     right: Expression,
  //     sign: String
  // ): Doc =
  //   generateExpression(left) + Doc.space + Doc.text(
  //     sign
  //   ) + Doc.space + generateExpression(right)

  // def generateType(varType: Type): Doc = {
  // }

  // def formatLine(spaces: Int): Doc = {}
}

// class PPrintsBasedGenerator extends JVMCodeGenerator {
// }
