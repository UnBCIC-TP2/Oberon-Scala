package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.environment.Environment

import scala.io.Source
import java.io.{FileOutputStream, FileWriter, PrintWriter}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.collection.mutable.Map

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

  val stdlib = OberonModule("STDLIB", Set.empty[String], List(), List(), List(),
    List(inc, dec, abs, odd, ceil, floor, round, intToFloat, power, sqrroot, ceil, readFile, writeFile, appendFile), None)

  def inc = Procedure(
    "INC",
    List(ParameterByReference("x", IntegerType)),
    Map(),
    None,
    List(),
    List(),
    MetaStmt(() => AssignmentStmt(VarAssignment("x"), AddExpression(VarExpression("x"), IntValue(1))))
  )

  def dec = Procedure(
    "DEC",
    List(ParameterByReference("x", IntegerType)),
    Map(),
    None,
    List(),
    List(),
    MetaStmt(() => AssignmentStmt(VarAssignment("x"), AddExpression(VarExpression("x"), IntValue(-1))))
  )

  def abs = Procedure(
    "ABS",                             // name
    List(ParameterByValue("x", IntegerType)), // formal arguments
    Map(),                             // referenceMap
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
    List(ParameterByValue("x", IntegerType)),
    Map(),                             // referenceMap
    Some(BooleanType),
    List(),
    List(),

    SequenceStmt(
      List(MetaStmt(() => ReturnStmt(BoolValue((env.lookup("x").get.asInstanceOf[IntValue].value % 2) != 0))))
    )
  )

  def ceil = Procedure(
    "CEIL",                         // name
    List(ParameterByValue("x", RealType)), // formal arguments
    Map(),                             // referenceMap
    Some(IntegerType),                 // return type
    List(),                         // local constants
    List(),                         // local variables

    SequenceStmt(
      List(MetaStmt(() => ReturnStmt(IntValue(env.lookup("x").get.asInstanceOf[RealValue].value.ceil.toInt))))
    )
  )

  def floor = Procedure(
    "FLOOR",
    List(ParameterByValue("x", RealType)),
    Map(),                             // referenceMap
    Some(IntegerType),
    List(),
    List(),

    SequenceStmt(
      List(MetaStmt(() => ReturnStmt(IntValue(env.lookup(name = "x").get.asInstanceOf[RealValue].value.floor.toInt)))))
  )

  def round = Procedure(
    "RND",
    List(ParameterByValue("x", RealType)),
    Map(),                             // referenceMap
    Some(IntegerType),
    List(),
    List(),

    SequenceStmt(
      List(MetaStmt(() => ReturnStmt(IntValue(env.lookup(name = "x").get.asInstanceOf[RealValue].value.round.toInt)))))
  )

  def intToFloat = Procedure(
    "FLT",
    List(ParameterByValue("x", IntegerType)),
    Map(),                             // referenceMap
    Some(RealType),
    List(),
    List(),

    SequenceStmt(
      List(MetaStmt(() => ReturnStmt(RealValue(env.lookup(name = "x").get.asInstanceOf[IntValue].value.toFloat)))))
  )
  def power = Procedure(
    "POW",
    List(ParameterByValue("x", RealType), ParameterByValue("y", RealType)),
    Map(),                             // referenceMap
    Some(RealType),
    List(),
    List(),

    SequenceStmt(
      List(MetaStmt(() => ReturnStmt(RealValue(scala.math.pow(env.lookup(name = "x").get.asInstanceOf[RealValue].value, env.lookup(name = "y").get.asInstanceOf[RealValue].value)))))
    ))
  def sqrroot = Procedure(
    "SQR",
    List(ParameterByValue("x", RealType)),
    Map(),                             // referenceMap
    Some(RealType),
    List(),
    List(),

    SequenceStmt(
      List(MetaStmt(() => ReturnStmt(RealValue(scala.math.sqrt(env.lookup(name = "x").get.asInstanceOf[RealValue].value)))))
    )
  )



  def readFile = Procedure(
    "READFILE",
    List(ParameterByValue("x",StringType)),
    Map(),                             // referenceMap
    Some(StringType),
    List(),
    List(),
    SequenceStmt(
      List(MetaStmt(() => ReturnStmt(StringValue(readf(env.lookup(name = "x").get.asInstanceOf[StringValue].value)))))
    )
  )

  //  def writeFile = Procedure(
  //    "WRITEFILE",                       // name
  //    List(FormalArg("FILENAME", StringType), FormalArg("CONTENT", StringType)), // arguments
  //    Some(StringType),                  // return the File Path
  //    List(),                            // local constants
  //    List(),                            // local variables
  //
  //    MetaStmt(() => ReturnStmt(StringValue(Files.write(Paths.get(env.lookup(name = "FILENAME").get.asInstanceOf[StringValue].value),
  //      env.lookup(name = "CONTENT").get.asInstanceOf[StringValue].value.getBytes(StandardCharsets.UTF_8)).toString)))
  //  )

  def writeF (path:String, content:String) : String={

    var file = new FileOutputStream(path)

    if (file  != null) file.close()

    Files.write(Paths.get(path),
      content.getBytes(StandardCharsets.UTF_8)).toString
  }

  def writeFile = Procedure(
    "WRITEFILE",                       // name
    List(ParameterByValue("PATH", StringType), ParameterByValue("CONTENT", StringType)), // arguments
    Map(),                             // referenceMap
    Some(StringType),                  // return the File Path
    List(),                            // local constants
    List(),                            // local variables

    MetaStmt(() => ReturnStmt(StringValue(this.writeF(env.lookup(name = "PATH").get.asInstanceOf[StringValue].value, env.lookup(name = "CONTENT").get.asInstanceOf[StringValue].value))))
  )

  /*def appendF (path:String, content:String) : String={

    var file = new FileOutputStream(path)

    if(file != null) file.close()

    File(path).appendAll(content).toString
  }*/

  def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B =
    try { f(param) } finally { param.close() }

  def appendF(path:String, content:String) =
    using (new FileWriter(path, true)){
      fileWriter => using (new PrintWriter(fileWriter)) {
        printWriter => printWriter.println(content).toString
      }
    }

  def appendFile = Procedure(
    "APPENDFILE",                       // name
    List(ParameterByValue("PATH", StringType), ParameterByValue("CONTENT", StringType)), // arguments
    Map(),                             // referenceMap
    Some(StringType),                  // return the File Path
    List(),                            // local constants
    List(),                            // local variables

    MetaStmt(() => ReturnStmt(StringValue(appendF(env.lookup(name = "PATH").get.asInstanceOf[StringValue].value, env.lookup(name = "CONTENT").get.asInstanceOf[StringValue].value))))
  )

}