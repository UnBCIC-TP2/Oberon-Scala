package br.unb.cic.oberon.codegen

import br.unb.cic.oberon.ast.OberonModule

object JimpleCodeGenerator extends CodeGenerator {
    val javaObject = TObject("java.lang.Object")

    override def generateCode(module: OberonModule): String = {
        val classDeclaration = ClassDecl(
            modifiers = List(PublicModifer),
            classType = TObject(module.name),
            superClass = javaObject,
            interfaces = List.empty[Type],
            fields = List.empty[Field],
            methods = List.empty[Method]
        )

        ???
    }
}
