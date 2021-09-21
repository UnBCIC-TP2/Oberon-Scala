package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.environment.Environment
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets
import com.sun.jdi.IntegerValue


class StandardLibrary[T](env: Environment[T]) {


    val stdlib = OberonModule("STDLIB", Set.empty[String], List(), List(), List(), List(abs, odd, floor, round, power, sqrroot, ceil), None)

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
        List(MetaStmt(() => ReturnStmt(RealValue((env.lookup("x").get.asInstanceOf[RealValue].value.ceil)))))
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
          List(MetaStmt(() => ReturnStmt(RealValue(scala.math.sqrt(env.lookup(name = "x").get.asInstanceOf[RealValue].value))))))
  )

  def writeFile = Procedure(
    "writeFile",                       // name
    List(FormalArg("FILENAME", StringType), FormalArg("CONTENT", StringType)), // arguments
    Some(StringType),                  // return the File Path
    List(),                            // local constants
    List(),                            // local variables

    MetaStmt(() => ReturnStmt(StringValue(Files.write(Paths.get(env.lookup(name = "FILENAME").get.asInstanceOf[StringValue].value),
      env.lookup(name = "CONTENT").get.asInstanceOf[StringValue].value.getBytes(StandardCharsets.UTF_8)).toString)))
  )
}
