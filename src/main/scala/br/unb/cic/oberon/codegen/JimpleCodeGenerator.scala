package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ir.ast.OberonModule
import br.unb.cic.oberon.ir.jimple._

object JimpleCodeGenerator extends CodeGenerator[ClassOrInterfaceDeclaration] {
    val javaObject = TObject("java.lang.Object")

    override def generateCode(module: OberonModule): ClassOrInterfaceDeclaration = {
        ClassDecl(
            modifiers = List(PublicModifer),
            classType = TObject(module.name),
            superClass = javaObject,
            interfaces = List.empty[Type],
            fields = List.empty[Field],
            methods = List.empty[Method]
        )
    }
}
