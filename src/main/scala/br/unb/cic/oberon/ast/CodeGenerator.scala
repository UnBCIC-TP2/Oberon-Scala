package br.unb.cic.oberon.ast


trait CodeGenerator {
    def readProcedure(module: OberonModule)
}


case class CCodeGenerator () extends CodeGenerator {

    def readProcedure(module: OberonModule) = {
        for (procedure <- module.procedures) {
            var args = "";
            var returnType = "";
            procedure.returnType match {
                case Some(IntegerType) => returnType = "int"
                case Some(BooleanType) => returnType = "bool"
                case None => returnType = "void"
                case _ => returnType = "undefined"
            }
            for((arg, index) <- procedure.args.view.zipWithIndex) {
                var argumentType = "";
                arg.argumentType match {
                    case IntegerType => argumentType = "int"
                    case BooleanType => argumentType = "bool"
                    case _ => argumentType = "undefined"
                }
                args = args + s"${argumentType} ${arg.name}"
                if (index + 1 < procedure.args.length)
                    args = args + ", "
            }
            println(s"${returnType} ${procedure.name} (${args})")
        }
    }
}

