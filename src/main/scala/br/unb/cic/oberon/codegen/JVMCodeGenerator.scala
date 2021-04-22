package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.interpreter._

import br.unb.cic.oberon.ast._

import org.objectweb.asm._
import org.objectweb.asm.util._
import java.io.PrintWriter
import org.objectweb.asm.Opcodes._

import java.util.Base64

//TODO: next steps:
//      (b) generate methods from procedures (27/04).

object JVMCodeGenerator extends CodeGenerator {
  val cw = new ClassWriter(0);
  val printWriter = new PrintWriter("file.txt");
  val cv = new TraceClassVisitor(cw, printWriter);

  override def generateCode(module: OberonModule): String = {

    cv.visit(V1_5, ACC_PUBLIC, module.name, null, "java/lang/Object", null);

    generateConstants(module.constants)
    generateDeclarations(module.variables)

    cv.visitEnd();

    Base64.getEncoder().encodeToString(cw.toByteArray);
    
  }

  def generateDeclarations(variables: List[VariableDeclaration]): Unit = {
    variables.foreach((v : VariableDeclaration) =>
      v.variableType match {
        case IntegerType =>  cv.visitField(ACC_PUBLIC, v.name, "I", null, Integer.valueOf(0)).visitEnd()
        case BooleanType => cv.visitField(ACC_PUBLIC, v.name, "Z", null, false).visitEnd()
      }
    )
  }

  def generateConstants(constants: List[Constant]): Unit = {
    val interpreter = new Interpreter()

    val visitor = new EvalExpressionVisitor(interpreter)

    constants.map {
      case (constant) => 
        val v = constant.exp.accept(visitor)

        v match {
          case IntValue (value) => {
            cv.visitField(ACC_PUBLIC + ACC_FINAL, constant.name, "I", null, value).visitEnd();
          }
          case BoolValue (value) => {
            cv.visitField(ACC_PUBLIC + ACC_FINAL, constant.name, "Z", null, value).visitEnd();
          }
        }
    }
  }

  // def generateDeclarations(
  //     variables: List[VariableDeclaration],
  //     lineSpaces: Int = 2
  // ): Doc = {

    

  //   val boolVariables = variables.filter(_.variableType == BooleanType).map {
  //     case (boolVar) => Doc.text(boolVar.name)
  //   }
  //   val boolDeclaration =
  //     if (boolVariables.nonEmpty)
  //       formatLine(lineSpaces) + Doc.text("bool ") + Doc.intercalate(
  //         Doc.comma + Doc.space,
  //         boolVariables
  //       ) + Doc.char(';') + Doc.line
  //     else Doc.empty

  //   intDeclaration + boolDeclaration
  // }
}

