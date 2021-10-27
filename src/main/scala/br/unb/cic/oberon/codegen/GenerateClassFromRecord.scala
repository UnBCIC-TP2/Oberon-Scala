package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.interpreter._
import br.unb.cic.oberon.ast._
import org.objectweb.asm._
import org.objectweb.asm.Opcodes._

import java.io.PrintStream
import java.util.Base64

import br.unb.cic.oberon.parser.ScalaParser

import java.io.FileOutputStream
import java.nio.file.{Files, Paths}

import java.io.File

object GenerateClassFromRecord{
  def generateCode(record: RecordType): (String, Array[Byte]) = {
      val cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

      cw.visit(V1_5, ACC_PUBLIC, record.name, null, "java/lang/Object", null);
      record.variables.foreach((v : VariableDeclaration) =>
        v.variableType match {

          case IntegerType =>  cw.visitField(ACC_PUBLIC, v.name, "I", null, null).visitEnd()
          case RealType =>  cw.visitField(ACC_PUBLIC, v.name, "D", null, null).visitEnd()
          case BooleanType => cw.visitField(ACC_PUBLIC, v.name, "Z", null, null).visitEnd()
          case CharacterType => cw.visitField(ACC_PUBLIC, v.name, "C", null, null).visitEnd()
          case StringType => cw.visitField(ACC_PUBLIC, v.name, "Ljava/lang/String;", null, null).visitEnd()
          // checar linha abaixo e ver como resolver o tipo array ccom o grupo array
          case ReferenceToUserDefinedType(_) => cw.visitField(ACC_PUBLIC, v.name, "L" + v.name + ";", null, null).visitEnd()
          case UndefinedType => cw.visitField(ACC_PUBLIC, v.name, "Ljava/lang/Object;", null, null).visitEnd()
        }
      )
      cw.visitEnd()

      (record.name, cw.toByteArray)
    }
}

