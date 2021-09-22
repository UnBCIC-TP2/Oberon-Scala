package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.environment.Environment
import scala.io.Source


class StandardLibrary[T](env: Environment[T]) {
    def readf (path:String) : String = {
      val buffer = Source.fromFile(path)
      var string = ""
      for (line <- buffer.getLines()){
        string = string + line
      }
      buffer.close()
      string
    }

    val stdlib = OberonModule("STDLIB", Set.empty[String], List(), List(), List(), List(abs, odd, floor, round, power, sqrroot, ceil,readFile), None)

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

    def ceil = Procedure(
      "CEIL",                         // name
      List(FormalArg("x", RealType)), // formal arguments
      Some(RealType),                 // return type
      List(),                         // local constants
      List(),                         // local variables

      SequenceStmt(
        List(MetaStmt(() => ReturnStmt(RealValue(env.lookup("x").get.asInstanceOf[RealValue].value.ceil))))
      )
    )

      def floor = Procedure(
        "FLR",
        List(FormalArg("x", RealType)),
        Some(RealType),
        List(),
        List(),

        SequenceStmt(
          List(MetaStmt(() => ReturnStmt(RealValue(env.lookup(name = "x").get.asInstanceOf[RealValue].value.floor)))))
      )

      def round = Procedure(
        "RND",
        List(FormalArg("x", RealType)),
        Some(RealType),
        List(),
        List(),

        SequenceStmt(
          List(MetaStmt(() => ReturnStmt(RealValue(env.lookup(name = "x").get.asInstanceOf[RealValue].value.round)))))
      )
      def power = Procedure(
        "POW",
        List(FormalArg("x", RealType), FormalArg("y", RealType)),
        Some(RealType),
        List(),
        List(),

        SequenceStmt(
          List(MetaStmt(() => ReturnStmt(RealValue(scala.math.pow(env.lookup(name = "x").get.asInstanceOf[RealValue].value, env.lookup(name = "y").get.asInstanceOf[RealValue].value)))))
      ))
      def sqrroot = Procedure(
        "SQR",
        List(FormalArg("x", RealType)),
        Some(RealType),
        List(),
        List(),

        SequenceStmt(
          List(MetaStmt(() => ReturnStmt(RealValue(scala.math.sqrt(env.lookup(name = "x").get.asInstanceOf[RealValue].value)))))
        )
      )
      def readFile = Procedure(
        "READFILE",
        List(FormalArg("x",StringType)),
        Some(StringType),
        List(),
        List(),
        SequenceStmt(
          List(MetaStmt(() => ReturnStmt(StringValue(readf(env.lookup(name = "x").get.asInstanceOf[StringValue].value)))))
        )
      )
}
