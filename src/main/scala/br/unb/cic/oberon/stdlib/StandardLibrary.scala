package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.environment.Environment

import scala.io.Source


class StandardLibrary[T](env: Environment[T]) {

    val stdlib = OberonModule("STDLIB", Set.empty[String], List(), List(), List(), List(abs, odd), None)

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

      def odd = Procedure(
        "ODD",
        List(FormalArg("x", IntegerType)),
        Some(BooleanType),
        List(),
        List(),

        SequenceStmt(
            List(MetaStmt(() => ReturnStmt(BoolValue((env.lookup("x").get.asInstanceOf[IntValue].value % 2) != 0))))
        )
    )

    def readFile = Procedure(
      "readFile",                             // name
      List(FormalArg("x", StringType)), // formal arguments
      Some(StringType),                 // return type
      List(),                            // local constants
      List(),                            // local variables


      MetaStmt(() => ReturnStmt(StringValue(Source.fromFile(env.lookup("x").get.asInstanceOf[StringValue].value).getLines.mkString)))
    )
}