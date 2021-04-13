package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.interpreter._

import br.unb.cic.oberon.ast._

import org.objectweb.asm._
import org.objectweb.asm.Opcodes._

import java.util.Base64

//TODO: next steps:
//      (a) generate fields from variables (deadline: 13/04).
//      (b) generate methods from procedures (27/04).

object JVMCodeGenerator extends CodeGenerator {
  val cw = new ClassWriter(0);
  override def generateCode(module: OberonModule): String = {


    cw.visit(V1_5, ACC_PUBLIC, module.name, null, "java/lang/Object", null);

    generateConstants(module.constants)
    generateDeclarations(module.variables)

    cw.visitEnd();

    Base64.getEncoder().encodeToString(cw.toByteArray);
    
  }

  def generateDeclarations(variables: List[VariableDeclaration]): Unit = {
    variables.foreach((v : VariableDeclaration) =>
      v.variableType match {
        case IntegerType =>  cw.visitField(ACC_PUBLIC, v.name, "I", null, new Integer(0)).visitEnd()
        case BooleanType => cw.visitField(ACC_PUBLIC, v.name, "Z", null, false).visitEnd()
      }
    )
  }

  def generateConstants(constants: List[Constant]): Unit = {
    val interpreter = new Interpreter()

    val visitor = new EvalExpressionVisitor(interpreter)

    constants.map {
      case (constant) => 
        val v = constant.exp.accept(visitor)
        v.isInstanceOf[IntValue] match {
          case true => {
            val value = v.asInstanceOf[IntValue].value
            cw.visitField(ACC_PUBLIC + ACC_FINAL, constant.name, "I", null, new Integer(value)).visitEnd();
          }
          case false => {
            val value = v.asInstanceOf[BoolValue].value
            cw.visitField(ACC_PUBLIC + ACC_FINAL, constant.name, "Z", null, value).visitEnd();
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

