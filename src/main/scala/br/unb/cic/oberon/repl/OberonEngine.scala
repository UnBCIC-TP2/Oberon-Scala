package br.unb.cic.oberon.repl


import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.ir.common.{ArrayAssignment, BoolValue, Expression, IntValue, PointerAssignment, REPLConstant, REPLExpression, REPLStatement, REPLUserTypeDeclaration, REPLVarDeclaration, RecordAssignment, Statement, StringValue, Undef, Value, VarAssignment}
import br.unb.cic.oberon.ir.ast.{AssignmentStmt}
import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.transformations.CoreTransformer
import org.jline.console.{CmdDesc, CmdLine, ScriptEngine}
import org.jline.reader.Completer
import org.jline.reader.impl.completer.{AggregateCompleter, StringsCompleter}
import org.jline.reader.Candidate
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine
import org.jline.utils.AttributedString

import java.lang
import java.io.File
import java.nio.file.Path
import java.util
import java.util.Collections
import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.util.matching.Regex
import cats.implicits._

class OberonEngine extends ScriptEngine {
  object Format extends Enumeration {
    type Format = Value
    val JSON, OBERON, NONE = Value
  }

  val interpreter = new Interpreter
  var env = new Environment[Expression]

  override def getEngineName: String = this.getClass.getSimpleName
  override def getExtensions: java.util.List[String] =
    Collections.singletonList("oberon")

  override def getScriptCompleter: Completer = { compileCompleter }

  private def compileCompleter: Completer = {

    val vCompleter = new VariableCompleter(this)
    val kCompleter = new KeywordCompleter(this)
    val completer = new AggregateCompleter(vCompleter, kCompleter)

    return completer
  }

  override def hasVariable(name: String): Boolean =
    env.lookup(name).isDefined

  override def put(name: String, value: Object): Unit = {
    env = env.setGlobalVariable(name, objectToExpression(value))
  }

  override def get(name: String): Object = {
    val variable = env.lookup(name)
    if (variable.isDefined) expressionValue(variable.get).asInstanceOf[Object]
    else null
  }

  override def find(name: String): util.Map[String, Object] = {
    if (name == null) {
      val allVariables = env.allVariables()
      allVariables.map(v => v -> get(v)).toMap.asJava
    } else {
      val filteredVariables = internalFind(name)
      filteredVariables.map(v => v -> get(v)).toMap.asJava
    }
  }

  override def del(vars: String*): Unit = {
    if (vars == null) {
      return
    }
    vars.foreach(del)
  }

  def del(variable: String): Unit = {
    if (variable == null) {
      return
    }
    if (hasVariable(variable)) {
      env = env.delVariable(variable)
    }
  }

  /*
   * TODO: implement toJson
   */
  override def toJson(obj: Object): String = {
    println("toJson call", obj)
    "TODO: toJson"
  }

  /*
   * TODO: implement toString
   */
  override def toString(obj: Object): String = {
    // println("toString call", obj.getClass)
    obj match {
      case s: String         => s
      case i: Integer        => i.toString
      case b: lang.Boolean   => b.toString
      case v: Value          => v.value.toString
      case m: util.Map[_, _] => "{}"
      case _ =>
        if (obj == null) "null"
        else s"toString not implemented for $obj (${obj.getClass})"
    }
  }

  /*
   * TODO: implement toMap
   */
  override def toMap(obj: Object): util.Map[String, Object] = {
    println("toMap call", obj)
    null
  }

  override def getSerializationFormats: util.List[String] =
    List(Format.JSON.toString, Format.NONE.toString).asJava
  override def getDeserializationFormats: util.List[String] = List(
    Format.JSON.toString,
    Format.OBERON.toString,
    Format.NONE.toString
  ).asJava

  /*
   * TODO: implement deserialize
   */
  override def deserialize(value: String, formatStr: String): Object = {
    val out = value.asInstanceOf[Object];
    val format =
      if (formatStr != null && formatStr.nonEmpty)
        Format.withName(formatStr.toUpperCase)
      else null
    if (format == Format.NONE) {
      // do nothing
    } else if (format == Format.JSON) {
      // TODO: JSON deserialization
      return new util.HashMap[String, Object]()
    } else if (format == Format.OBERON) {
      // TODO: Oberon deserialization
    } else {
      // TODO: Undefined deserialization
    }
    out
  }

  override def persist(file: Path, obj: Object): Unit =
    persist(file, obj, getSerializationFormats().get(0));

  /*
   * TODO: implement persist
   * https://github.com/jline/jline3/blob/master/console/src/main/java/org/jline/console/ConsoleEngine.java#L125
   */
  override def persist(file: Path, obj: Object, format: String): Unit = {}

  /*
   * TODO: execute file with arguments (replace $1, $2, ... with parameters values)
   */
  override def execute(script: File, args: Array[Object]): Object = {
    val i = Source.fromFile(script)
    val content = i.getLines().mkString("\n")
    i.close()

    val pattern: Regex = "\\$(\\d+)".r
    val module = ScalaParser.parse(
      pattern.replaceAllIn(
        content,
        m =>
          expressionValue(
            objectToExpression(args(m.group(1).toInt - 1))
          ).toString
      )
    )
    val coreModule = CoreTransformer.reduceOberonModule(module)

    env =  interpreter.run(coreModule)
    null
  }

  /*
   * TODO: improve execute statement
   */
  override def execute(statement: String): Any = {
    val command = ScalaParser.parserREPL(statement)
    command match {
      case v: REPLVarDeclaration =>
        env = v.declarations.traverse(b => for {_ <- interpreter.declareVariable(b)} yield ()).runS(env).value
      case c: REPLConstant =>
        env = interpreter.declareConstant(c.constants).runS(env).value
      case u: REPLUserTypeDeclaration =>
        env = interpreter.declareUserDefinedType(u.userTypes).runS(env).value
      case s: REPLStatement =>
        s.stmt match {
          case AssignmentStmt(des, exp) =>
            des match {
              // TODO: Other types of assignment
              case ArrayAssignment(_, _)  => ???
              case RecordAssignment(_, _) => ???
              case PointerAssignment(_)   => ???
              case VarAssignment(name) =>
                put(name, exp)
            }
          case s: Statement => env = interpreter.executeStatement(CoreTransformer.reduceToCoreStatement(s)).runS(env).value
        }
      case e: REPLExpression => return expressionValue(e.exp)
    }
    null
  }

  private def expressionValue(exp: Expression): Any = {
    val (e, result) = interpreter.evalExpression(exp).run(env).value
    env = e
    result match {
      case v: Value => return v.value
      case _: Undef => return null
    }
    exp
  }

  private def objectToExpression(obj: Object): Expression = {
    obj.asInstanceOf[Any] match {
      case i: Int => IntValue(i)
      case s: String => StringValue(s)
      case b: Boolean => BoolValue(b)
      case e: Exception => StringValue(e.getMessage)
      case e: Expression => {
        val (env1, exp) = interpreter.evalExpression(e).run(env).value
        env = env1
        exp
      }
      case _ =>
        if (obj != null) println(f"Cannot convert $obj to expression")
        Undef()
    }
  }

  override def execute(closure: Object, args: Object*): Object = ???

  private def internalFind(variable: String): List[String] = env.allVariables().filter(v => v.matches(variable)).toList

  def scriptDescription(line: CmdLine): CmdDesc = {
    val out = new CmdDesc
    // TODO: Script description = out.setMainDesc()
    out
  }

  private class VariableCompleter(var oberonEngine: OberonEngine)
      extends Completer {
    val inspector = new Inspector(oberonEngine)

    def complete(
        reader: org.jline.reader.LineReader,
        commandLine: org.jline.reader.ParsedLine,
        candidates: java.util.List[org.jline.reader.Candidate]
    ): Unit = {
      assert(commandLine != null)
      assert(candidates != null)
      val wordbuffer = commandLine.word()
      val buffer = commandLine.line.substring(0, commandLine.cursor())

      var idx = -1
      var len = 1
      try {
        len = raw"[\+\-\*\=\/\(]|(:=)".r
          .findAllMatchIn(wordbuffer)
          .toList
          .last
          .toString
          .size
        idx = raw"[\+\-\*\=\/\(]|(:=)".r
          .findAllMatchIn(wordbuffer)
          .map(_.start)
          .toList
          .last
      } catch {
        case e: java.util.NoSuchElementException => {}
      }

      val pref = wordbuffer.substring(0, idx + len)
      val variables = inspector.getVariables()
      for (v <- variables) {
        candidates.add(
          new Candidate(
            AttributedString.stripAnsi(pref + v),
            v,
            null,
            null,
            null,
            null,
            false
          )
        );
      }
    }
  }

  private class Inspector(var oberonEngine: OberonEngine) {

    def getVariables(): Iterable[String] =
      (oberonEngine.find(null).asScala).keys

  }

  private class KeywordCompleter(var oberonEngine: OberonEngine)
      extends Completer {

    def complete(
        reader: org.jline.reader.LineReader,
        commandLine: org.jline.reader.ParsedLine,
        candidates: java.util.List[org.jline.reader.Candidate]
    ): Unit = {

      val keywordList: List[String] = List(
        "ARRAY",
        "BEGIN",
        "BY",
        "CASE",
        "CONST",
        "DIV",
        "DO",
        "ELSE",
        "ELSIF",
        "END",
        "EXIT",
        "FOR",
        "IF",
        "IMPORT",
        "IN",
        "IS",
        "LOOP",
        "MOD",
        "MODULE",
        "NIL",
        "OF",
        "OR",
        "POINTER",
        "PROCEDURE",
        "RECORD",
        "REPEAT",
        "RETURN",
        "THEN",
        "TO",
        "TYPE",
        "UNTIL",
        "VAR",
        "WHILE",
        "WITH"
      )
      for (v <- keywordList) {
        candidates.add(
          new Candidate(
            AttributedString.stripAnsi(v),
            v,
            null,
            null,
            null,
            null,
            false
          )
        );
      }
    }
  }

}
