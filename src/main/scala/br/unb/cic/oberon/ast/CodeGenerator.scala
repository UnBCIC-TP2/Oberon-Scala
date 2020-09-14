package ast


trait CodeGenerator {
    def read_procedure(module: OberonModule)
}


case class CCodeGenerator () extends CodeGenerator {

    def read_procedure(module:OberonModule): T {
        module
    }
}