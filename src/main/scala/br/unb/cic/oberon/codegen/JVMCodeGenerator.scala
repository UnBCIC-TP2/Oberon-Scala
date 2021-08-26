package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.interpreter._

import br.unb.cic.oberon.ast._

import org.objectweb.asm._
import org.objectweb.asm.Opcodes._

import java.util.Base64

object JVMCodeGenerator extends CodeGenerator {
  override def generateCode(module: OberonModule): String = {
    val cw = new ClassWriter(0);

    cw.visit(V1_5, ACC_PUBLIC, module.name, null, "java/lang/Object", null);

    generateConstants(module.constants, cw)
    generateDeclarations(module.variables, cw)

    cw.visitEnd();

    Base64.getEncoder().encodeToString(cw.toByteArray);
    
  }

  def generateDeclarations(variables: List[VariableDeclaration], cw: ClassWriter): Unit = {
    variables.foreach((v : VariableDeclaration) =>
      v.variableType match {
        case IntegerType =>  cw.visitField(ACC_PUBLIC, v.name, "I", null, Integer.valueOf(0)).visitEnd()
        case BooleanType => cw.visitField(ACC_PUBLIC, v.name, "Z", null, false).visitEnd()
      }
    )
  }

  def generateConstants(constants: List[Constant], cw: ClassWriter): Unit = {
    val interpreter = new Interpreter()

    val visitor = new EvalExpressionVisitor(interpreter)

    constants.map {
      case (constant) => 
        val v = constant.exp.accept(visitor)

        v match {
          case IntValue (value) => {
            cw.visitField(ACC_PUBLIC + ACC_FINAL, constant.name, "I", null, value).visitEnd();
          }
          case BoolValue (value) => {
            cw.visitField(ACC_PUBLIC + ACC_FINAL, constant.name, "Z", null, value).visitEnd();
          }
        }
    }
  }

}

