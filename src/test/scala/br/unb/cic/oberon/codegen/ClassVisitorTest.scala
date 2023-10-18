package br.unb.cic.oberon.codegen

import jdk.internal.org.objectweb.asm.{ClassVisitor, FieldVisitor}
import jdk.internal.org.objectweb.asm.Opcodes._;

class ClassVisitorTest(version: Integer) extends ClassVisitor(version) {
  var numberOfTotalFields: Integer = 0;
  var numberOfIntegerVariables: Integer = 0;
  var numberOfBooleanVariables: Integer = 0;
  var numberOfIntegerConstants: Integer = 0;
  var numberOfBooleanConstants: Integer = 0;

  val VAR: Int = ACC_PUBLIC;
  val CONST: Int = ACC_PUBLIC + ACC_FINAL;

  override def visitField(
      access: Int,
      name: String,
      desc: String,
      signature: String,
      value: Any
  ): FieldVisitor = {
    numberOfTotalFields += 1;

    access match {
      case VAR => {
        desc match {
          case "I" => {
            numberOfIntegerVariables += 1;
          }
          case "Z" => {
            numberOfBooleanVariables += 1;
          }
        }
      }
      case CONST => {
        desc match {
          case "I" => {
            numberOfIntegerConstants += 1;
          }
          case "Z" => {
            numberOfBooleanConstants += 1;
          }
        }
      }
    }

    super.visitField(access, name, desc, signature, value)
  }
}
