package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.util.Values

object StandardLibrary {

    val stdlib = OberonModule("STDLIB", Set.empty[String], List(), List(), List(), List(abs), None)

    def abs = Procedure(
        "ABS",                             // name
        List(FormalArg("x", IntegerType)), // formal arguments
        Some(IntegerType),                 // return type
        List(),                            // local constants
        List(),                            // local variables

        //
        // this is the procedure body
        // if(x < 0) return -1 * x
        // else      return x
        //
        IfElseStmt(LTExpression(VarExpression("x"), IntValue(0)),
            ReturnStmt(MultExpression(IntValue(-1), VarExpression("x"))),
            Some(ReturnStmt(VarExpression("x"))))
    )

//    def odd = Procedure(
//        "ODD",
//        List(FormalArg("x", IntegerType)),
//        Some(BooleanType),
//        List(),
//        List(VariableDeclaration("temp", IntegerType)),
//
//        SequenceStmt(
//            List(AssignmentStmt("temp", VarExpression("x")),
//                ReturnStmt
//        )
//    )
}