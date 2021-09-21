package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.environment.Environment
import scala.io.Source

class StandardLibrary[T](env: Environment[T]) {
    def readF (path:String) : String={
      var string = ""
      var bufferFile = Source.fromFile(path)
      for (line <- bufferFile.getLines()){
        string = string.concat(line)
      }
      bufferFile.close
      string
    }
    val stdlib = OberonModule("STDLIB", Set.empty[String], List(), List(), List(), List(abs, odd, readFile), None)

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
        "READFILE",
        List(FormalArg("x",StringType)),
        Some(StringType),
        List(),
        List(),

        SequenceStmt(
          List(MetaStmt(()=>ReturnStmt(StringValue(this.readF(env.lookup("x").get.asInstanceOf[StringValue].value)))))
        )
      )
}