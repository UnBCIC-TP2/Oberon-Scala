package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ast._

import org.objectweb.asm._
import org.objectweb.asm.Opcodes._

import java.util.Base64

object JVMCodeGenerator extends CodeGenerator {
  override def generateCode(module: OberonModule): String = {
    val cw = new ClassWriter(0);

    cw.visit(V1_5, ACC_PUBLIC, module.name, null, "java/lang/Object", null);
    //TODO: next steps:
    //      (a) generate fields from variables (deadline: 13/04).
    //      (b) generate methods from procedures (27/04).
    module.variables.filter(_.variableType == IntegerType).map {
      case (intVar) => cw.visitField(ACC_PUBLIC, intVar.name, "I", null, new Integer(0)).visitEnd();
    }

    module.variables.filter(_.variableType == BooleanType).map {
      case (boolVar) => cw.visitField(ACC_PUBLIC, boolVar.name, "Z", null, false).visitEnd();
    }

    cw.visitEnd();

    Base64.getEncoder().encodeToString(cw.toByteArray);
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

