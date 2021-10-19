package br.unb.cic.oberon.codegen

import jdk.internal.org.objectweb.asm.{ClassVisitor, FieldVisitor, MethodVisitor}
import jdk.internal.org.objectweb.asm.Opcodes._

import scala.collection.mutable.ListBuffer

class ClassVisitorTest(version: Int) extends ClassVisitor(version) {
  var constants = new ListBuffer[String]()
  var variables = new ListBuffer[String]()
  var procedures = new ListBuffer[String]()

  def registerByOpcodes(access: Int, name: String): Unit = {
    val VAR: Int = ACC_PUBLIC
    val CONST: Int = ACC_PUBLIC + ACC_FINAL
    val PROC: Int = ACC_PUBLIC + ACC_ABSTRACT

    access match {
      case CONST => constants += name
      case VAR => variables += name
      case PROC => procedures += name
      case _ =>
    }
  }

  override def visitField(access: Int, name: String, desc: String, signature: String, value: Any): FieldVisitor = {
    registerByOpcodes(access, name)
    super.visitField(access, name, desc, signature, value)
  }

  override def visitMethod(access: Int, name: String, desc: String, signature: String,
                           exceptions: Array[String]): MethodVisitor = {
    registerByOpcodes(access, name)
    super.visitMethod(access, name, desc, signature, exceptions)
  }
}