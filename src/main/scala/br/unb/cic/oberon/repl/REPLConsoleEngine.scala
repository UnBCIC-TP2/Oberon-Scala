package br.unb.cic.oberon.repl

import org.jline.builtins.Completers.OptionCompleter
import org.jline.builtins.Options.HelpException
import org.jline.builtins.{ConfigurationPath, Styles}
import org.jline.console.ConsoleEngine.ExecutionResult
import org.jline.console.{
  CmdDesc,
  CommandInput,
  CommandMethods,
  CommandRegistry,
  ConsoleEngine,
  Printer,
  SystemRegistry
}
import org.jline.console.impl.{JlineCommandRegistry, SystemRegistryImpl}
import org.jline.reader.Parser.ParseContext
import org.jline.reader.impl.completer.{
  ArgumentCompleter,
  NullCompleter,
  StringsCompleter
}
import org.jline.reader.{
  Completer,
  EOFError,
  EndOfFileException,
  LineReader,
  Parser,
  SyntaxError
}
import org.jline.terminal.Terminal
import org.jline.utils.AttributedStringBuilder

import java.io.{BufferedReader, File, FileReader}
import java.{lang, util}
import java.nio.file.{
  FileSystems,
  Files,
  InvalidPathException,
  NoSuchFileException,
  Path,
  PathMatcher,
  Paths
}
import java.util.regex.Pattern
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.jdk.FunctionConverters._
import scala.util.control.Breaks.break

class REPLConsoleEngine(
    commands: Option[Array[Commands.Console]],
    engine: OberonEngine,
    printer: Printer,
    workDir: () => Path,
    configPath: ConfigurationPath
) extends JlineCommandRegistry()
    with ConsoleEngine {
  private val VAR_PATH = "PATH"
  private val VAR_CONSOLE_OPTIONS = "CONSOLE_OPTIONS"
  private val OPTION_VERBOSE = "-v"
  private val OPTION_HELP = Array("-?", "--help")
  private val END_HELP = "END_HELP"
  private val HELP_MAX_SIZE = 30

  def this(
      scriptEngine: OberonEngine,
      printer: Printer,
      workDir: () => Path,
      configPath: ConfigurationPath
  ) {
    this(None, scriptEngine, printer, workDir, configPath)
  }
  private var reader: Option[LineReader] = None
  private var systemRegistry: Option[SystemRegistry] = None
  private var _executing = false
  private var scriptExtension = "jline"
  private val pipes = Map[String, List[String]]()
  private var exception: Option[Exception] = None;

  val cmds: Array[Commands.Console] =
    commands.getOrElse(Commands.Console.values())
  val commandName = new util.HashMap[Commands.Console, String]
  val commandExecute = new util.HashMap[Commands.Console, CommandMethods]
  for (cmd <- cmds) {
    commandName.put(cmd, cmd.name().toLowerCase())
  }

  commandExecute.put(
    Commands.Console.DEL,
    new CommandMethods((i => del(i)).asJava, (c => variableCompleter(c)).asJava)
  )
  commandExecute.put(
    Commands.Console.SHOW,
    new CommandMethods(
      (i => show(i)).asJava,
      (c => variableCompleter(c)).asJava
    )
  )
  commandExecute.put(
    Commands.Console.PRNT,
    new CommandMethods((i => prnt(i)).asJava, (c => prntCompleter(c)).asJava)
  )
  /*
  commandExecute.put(Commands.Console.SLURP, new CommandMethods((i => slurpcmd(i)).asJava, (c => slurpCompleter(c)).asJava))
  commandExecute.put(Commands.Console.DOC, new CommandMethods((i => doc(i)).asJava, (c => docCompleter(c)).asJava))
  commandExecute.put(Commands.Console.PIPE, new CommandMethods((i => pipe(i)).asJava, (c => defaultCompleter(c)).asJava))
   */
  registerCommands(commandName, commandExecute)

  override def setLineReader(lineReader: LineReader): Unit = reader = Some(
    lineReader
  )
  override def setSystemRegistry(sr: SystemRegistry): Unit = systemRegistry =
    Some(sr)
  override def setScriptExtension(extension: String): Unit = scriptExtension =
    extension

  private def parser: Parser = reader.get.getParser
  private def terminal: Terminal = systemRegistry.get.terminal()
  override def isExecuting: Boolean = _executing

  /*
   * TODO: Study and maybe implement aliases
   */
  override def hasAlias(name: String): Boolean = false
  override def getAlias(name: String): String = name

  override def getPipes: util.Map[String, util.List[String]] =
    pipes.map(kv => (kv._1, kv._2.asJava)).asJava
  override def getNamedPipes: util.List[String] =
    pipes.keySet.filter(p => p.matches("[a-zA-Z0-9]+")).toList.asJava

  override def scriptCompleters(): util.List[Completer] = {
    List[Completer](
      new ArgumentCompleter(
        new StringsCompleter(() => scriptNames),
        new OptionCompleter(
          NullCompleter.INSTANCE,
          (s: String) => commandOptions(s),
          1
        )
      )
    ).asJava
  }

  private def scriptExtensions: List[String] =
    engine.getExtensions.asScala.toList.concat(List(scriptExtension))
  private def scriptNames: util.Set[String] = scripts().keySet()
  override def scripts(): util.Map[String, lang.Boolean] = {
    try {
      val scripts = ListBuffer[Path]()
      if (engine.hasVariable(VAR_PATH)) {
        val dirs =
          engine.get(VAR_PATH).asInstanceOf[List[String]].flatMap { f =>
            val file =
              if (f.startsWith("~"))
                f.replace("~", System.getProperty("user.home"))
              else f
            val dir = new File(file)
            if (dir.exists() && dir.isDirectory) Some(file) else None
          }
        for (d <- dirs) {
          scriptExtensions.foreach(se => {
            val regex = d + "/*." + se
            val pathMatcher =
              FileSystems.getDefault.getPathMatcher("glob:" + regex)
            Files
              .find(
                Paths.get(new File(regex).getParent),
                Integer.MAX_VALUE,
                (path, _) => pathMatcher.matches(path)
              )
              .forEach(p => scripts.addOne(p))
          })
        }
      }
      val scriptsMap = new util.HashMap[String, lang.Boolean]()
      for (path <- scripts) {
        val name = path.toFile.getName
        val idx = name.lastIndexOf(".")
        scriptsMap.put(
          name.substring(0, idx),
          name.substring(idx + 1).equals(scriptExtension)
        )
      }
      return scriptsMap
    } catch {
      case n: NoSuchFileException =>
        error("Failed reading PATH. No file found: " + n.getMessage)
      case i: InvalidPathException => {
        error("Failed reading PATH. Invalid path:")
        error(i.toString)
      }
      case e: Exception => {
        error("Failed reading PATH:")
        trace(e)
        engine.put("exception", e)
      }
    }
    new util.HashMap[String, lang.Boolean]()
  }

  override def expandParameters(args: Array[String]): Array[Object] = {
    val regexPath = "(.*)\\$\\{(.*?)}(/.*)"
    args.map(arg => {
      if (arg.matches(regexPath)) {
        val matcher = Pattern.compile(regexPath).matcher(arg)
        matcher.group(1) + engine
          .get(matcher.group(2)) + matcher.group(3).asInstanceOf[Object]
      } else if (arg.startsWith("${")) {
        engine.execute(expandName(arg))
      } else if (arg.startsWith("$")) {
        engine.get(expandName(arg))
      } else {
        engine.deserialize(arg)
      }
    })
  }

  private def expandToList(args: Array[String]): String = expandToList(
    args.toList.asJava
  )
  override def expandToList(params: util.List[String]): String = {
    /*
     * TODO: List expansion
     */
    ""
  }

  private def expandName(name: String): String = {
    val regexVar = "[a-zA-Z_]+[a-zA-Z0-9_-]*"
    if (name.matches("^\\$" + regexVar)) {
      name.substring(1)
    } else if (name.matches("^\\$\\{" + regexVar + "}.*")) {
      val matcher =
        Pattern.compile("^\\$\\{(" + regexVar + ")}(.*)").matcher(name)
      matcher.group(1) + matcher.group(2)
    } else {
      name
    }
  }

  private def isNumber(str: String): Boolean = str.matches("-?\\d+(\\.\\d+)?")
  private def isCommandLine(line: String): Boolean = {
    var command = parser.getCommand(line)
    if (command != null && command.startsWith(":")) {
      command = command.substring(1)
      if (systemRegistry.get.hasCommand(command)) {
        return true
      } else {
        val sf = new ScriptFile(command, "", Array[String]())
        if (sf.isScript) {
          return true
        }
      }
    }
    false
  }

  private def quote(variable: String): String = {
    if (
      (variable.startsWith("\"") && variable.endsWith("\"")) || (variable
        .startsWith("'") && variable.endsWith("'"))
    ) {
      variable
    } else if (variable.contains("\\\"")) {
      "'" + variable + "'"
    } else {
      "\"" + variable + "\""
    }
  }

  override def expandCommandLine(line: String): String = {
    if (isCommandLine(line)) {
      val sb = new mutable.StringBuilder
      val ws = parser.parse(line, 0, ParseContext.COMPLETE).words()
      val idx = ws.get(0).lastIndexOf(":")
      if (idx > 0) {
        sb.append(ws.get(0).substring(0, idx))
      }

      val argv = ws.asScala.map(arg => {
        if (arg.startsWith(s"${}")) {
          val argvMatcher = Pattern.compile("\\$\\{(.*)}").matcher(arg)
          if (argvMatcher.find()) {
            return arg.replace(arg, argvMatcher.group(1))
          }
        } else if (arg.startsWith("$")) {
          return arg.substring(1)
        } else {
          return quote(arg)
        }
      })

      val cmd = ws.get(0).substring(idx + 1)
      sb.append(classOf[SystemRegistry].getCanonicalName)
        .append(".get().invoke('")
        .append(cmd)
        .append("'")
      for (arg <- argv) {
        sb.append(", ")
        sb.append(arg)
      }
      sb.append(")")
      sb.toString
    } else {
      line
    }
  }

  @throws[Exception]
  override def execute(
      script: File,
      cmdLine: String,
      args: Array[String]
  ): Object = {
    val file = new ScriptFile(script, cmdLine, args)
    file.execute
    file.getResult
  }

  @throws[Exception]
  override def execute(
      cmd: String,
      line: String,
      args: Array[String]
  ): Object = {
    if (!line.trim.startsWith("#")) {
      var file: Option[ScriptFile] = None
      if (parser.validCommandName(cmd)) {
        file = Some(new ScriptFile(cmd, line, args))
      } else {
        val f = new File(line.split("\\s+")(0))
        if (f.exists()) {
          file = Some(new ScriptFile(f, line, args))
        }
      }

      if (file.isDefined && file.get.execute) {
        return file.get.getResult
      } else {
        val trimmedLine = line.trim()
        if (engine.hasVariable(trimmedLine)) {
          return engine.get(trimmedLine)
        } else if (parser.getVariable(trimmedLine) == null) {
          val out = engine.execute(trimmedLine)
          engine.put("it", out.asInstanceOf[Object])
          return out.asInstanceOf[Object]
        } else {
          engine.execute(trimmedLine)
        }
      }
    }
    null
  }

  override def purge(): Unit = engine.del("_*")

  override def putVariable(name: String, value: Object): Unit =
    engine.put(name, value)
  override def getVariable(name: String): Object = {
    if (!hasVariable(name))
      throw new IllegalArgumentException(
        "Variable " + name + " does not exists!"
      )
    engine.get(name)
  }
  override def hasVariable(name: String): Boolean = engine.hasVariable(name)

  /*
   * TODO: Implement widget support
   */
  override def executeWidget(function: Object): Boolean = {
    false
  }

  // Console options
  private def consoleOptions(): Map[String, Object] = {
    if (engine.hasVariable(VAR_CONSOLE_OPTIONS))
      getVariable(VAR_CONSOLE_OPTIONS).asInstanceOf[Map[String, Object]]
    else Map[String, Object]()
  }

  override def consoleOption[T](option: String, defval: T): T = {
    if (consoleOption(option)) {
      consoleOptions().getOrElse(option, defval).asInstanceOf[T]
    }
    defval
  }

  def setConsoleOption(name: String, value: Object): Unit = {}

  private def consoleOption(option: String): Boolean = {
    try {
      consoleOptions().contains(option)
    } catch {
      case e: Exception =>
        trace(new Exception("Bad CONSOLE_OPTION value: " + e.getMessage))
    }
    false
  }

  // Post process
  override def postProcess(
      line: String,
      result: Object,
      output: String
  ): ExecutionResult = {
    val _output =
      if (
        output != null && output.trim.nonEmpty && !consoleOption(
          "no-splittedOutput"
        )
      ) output.split("\\r?\\n")
      else output
    val consoleVar = parser.getVariable(line)
    if (consoleVar != null && result != null) {
      engine.put("output", _output)
    }

    if (systemRegistry.get.hasCommand(parser.getCommand(line))) {
      postProcess(
        line,
        if (consoleVar != null && result == null) _output else result
      )
    } else {
      val _result = if (result == null) _output else result
      val status = saveResult(consoleVar, _result)
      new ExecutionResult(
        status,
        if (consoleVar != null && !consoleVar.startsWith("_")) null else _result
      )
    }
  }

  private def postProcess(line: String, result: Object): ExecutionResult = {
    var status = 0
    var out = result match {
      case str: String if str.trim.isEmpty => null
      case _                               => result
    }
    val consoleVar = parser.getVariable(line)
    if (consoleVar != null) {
      status = saveResult(consoleVar, result)
      out = null
    } else if (!parser.getCommand(line).equals("show")) {
      status = if (result != null) saveResult("it", result) else 1
    }
    new ExecutionResult(status, out)
  }

  override def postProcess(result: Object): ExecutionResult =
    new ExecutionResult(saveResult(null, result), result)

  private def saveResult(variable: String, result: Object): Int = {
    /* TODO: Implement saveResult
    try {
      engine.put("_executionResult", result)
      if (variable != null) {
        if (variable.contains(".") || variable.contains("[")) {
          engine.execute(variable + " := _executionResult")
        } else {
          engine.put(variable, result)
        }
      }
      engine.put("_executionOut", Int.box(0))
      engine.execute("IF (_executionResult) THEN _executionOut := 0 ELSE _executionOut := 1 END")
      return engine.get("_executionOut").asInstanceOf[Int]
    } catch {
       case e: Exception =>
         trace(e)
         return 1
    }
     */
    1
  }

  @throws[Exception]
  override def invoke(
      session: CommandRegistry.CommandSession,
      command: String,
      args: AnyRef*
  ): Any = {
    var out: Any = null
    exception = None
    if (hasCommand(command)) {
      out = getCommandMethods(command)
        .execute()
        .apply(new CommandInput(command, Array.from(args), session))
    } else {
      val _args = ListBuffer[String]()
      for (arg <- args) {
        if (!arg.isInstanceOf[String]) throw new IllegalArgumentException()
        _args += arg.toString
      }
      val sf = new ScriptFile(command, "", _args.toArray)
      if (sf.execute) {
        out = sf.getResult
      }
    }
    if (exception.isDefined) {
      throw exception.get
    }
    out
  }

  override def trace(obj: Object): Unit = {
    var toPrint = obj
    val className = if (obj != null) obj.getClass.getSimpleName else ""
    val level = consoleOption("trace", 0)
    val options = new util.HashMap[String, Object]()
    if (level < 2) {
      options.put("exception", "message")
    }
    if (level == 0 && !obj.isInstanceOf[Throwable]) {
      toPrint = null
    } else if (level == 1 && className == "CommandData") {
      toPrint = obj.asInstanceOf[SystemRegistryImpl.CommandData].rawLine()
    } else if (level > 1 && className == "CommandData") {
      toPrint = obj.toString
    }
    // if (obj.isInstanceOf[Exception]) obj.asInstanceOf[Exception].printStackTrace()
    printer.println(options, toPrint)
  }

  private def error(message: String): Unit = {
    val asb = new AttributedStringBuilder()
    asb.styled(Styles.prntStyle().resolve(".em"), message)
    asb.print(terminal)
  }

  override def println(obj: Object): Unit = {
    val options = Map[String, Object](
      Printer.VALUE_STYLE -> "GRON",
      Printer.BORDER -> "|"
    )
    printer.println(options.asJava, obj)
  }

  override def persist(file: Path, obj: Object): Unit =
    engine.persist(file, obj)

  /*
   * Slurp is a kind of JSON parsing, I don't think it's useful for this project
   */
  override def slurp(file: Path): Object = {
    new util.HashMap[String, util.List[String]]()
  }

  // Commands
  private def show(input: CommandInput): Object = {
    val usage = Array[String](
      "show -  list console variables",
      "Usage: show [VARIABLE]",
      "  -? --help                       Displays command help"
    )
    try {
      parseOptions(usage, input.args().asInstanceOf[Array[Object]])
      val options = mutable.Map[String, Object]()
      options.put(Printer.MAX_DEPTH, Int.box(0))
      printer.println(
        options.asJava,
        engine.find(if (input.args().nonEmpty) input.args()(0) else null)
      )
    } catch {
      case e: Exception => exception = Some(e)
    }
    null
  }

  private def del(input: CommandInput): Object = {
    val usage = Array[String](
      "del -  delete console variables, methods, classes and imports",
      "Usage: del [var1] ...",
      "  -? --help                       Displays command help"
    )
    try {
      parseOptions(usage, input.args().asInstanceOf[Array[Object]])
      input.args().foreach(arg => engine.del(arg))
    } catch {
      case e: Exception => exception = Some(e)
    }
    null
  }

  private def prnt(input: CommandInput): Object = {
    val result = printer.prntCommand(input)
    if (result != null) {
      exception = Some(result)
    }
    null
  }

  // Completers
  private def variableCompleter(command: String): util.List[Completer] = {
    List[Completer](
      new StringsCompleter(() => engine.find().keySet())
    ).asJava
  }

  private def prntCompleter(command: String): util.List[Completer] = {
    List[Completer](
      /* TODO: Implement VariableReferenceCompleter
      new ArgumentCompleter(
        NullCompleter.INSTANCE,
        new OptionCompleter(
          List[Completer](
            new VariableReferenceCompleter(engine),
            NullCompleter.INSTANCE
          ).asJava,
          (s: String) => commandOptions(s),
          1
        )
      )
       */
    ).asJava
  }

  private class ScriptFile(
      val script: File,
      val cmdLine: String,
      var args: Array[String],
      private var verbose: Boolean
  ) {
    private var result: Object = null
    private var extension = ""

    def this(command: String, cmdLine: String, args: Array[String]) {
      // if (!parser.validCommandName(command)) throw new IllegalArgumentException("Invalid command name!")
      this(ScriptFile.getScriptCommandFile(command), cmdLine, args, false)
      try {
        setScriptExtension(command)
        doArgs(args)
      } catch {
        case e: Exception => // ignore
      }
    }

    def this(script: File, cmdLine: String, args: Array[String]) {
      // if (!script.exists()) throw new IllegalArgumentException("Script file not found!")
      this(script, cmdLine, args, false)
      setScriptExtension(script.getName)
      doArgs(args)
    }

    def setScriptExtension(command: String): Unit = {
      extension =
        if (command.contains("."))
          command.substring(command.lastIndexOf(".") + 1)
        else ""
      if (!isEngineScript && !isConsoleScript) {
        throw new IllegalArgumentException("Command not found: " + command)
      }
    }

    private def doArgs(args: Array[String]): Unit = {
      val _args = ListBuffer[String]()
      if (isConsoleScript) {
        _args += script.getAbsolutePath
      }

      for (a <- args) {
        if (isConsoleScript) {
          if (!a.equals(OPTION_VERBOSE)) {
            _args += a
          } else {
            verbose = true
          }
        } else {
          _args += a
        }
      }

      this.args = _args.toArray
    }

    def isEngineScript: Boolean = engine.getExtensions.contains(extension)
    def isConsoleScript: Boolean = scriptExtension == extension
    def isScript: Boolean = isEngineScript || isConsoleScript

    @throws[Exception]
    def execute: Boolean = {
      if (!isScript) return false
      result = null
      if (
        util.Arrays.asList(args).contains(OPTION_HELP(0)) || util.Arrays
          .asList(args)
          .contains(OPTION_HELP(1))
      ) {
        val bufferedSource = Source.fromFile(script)
        var size = 0
        val usage = new StringBuilder
        var helpEnd = false
        var headComment = false
        for (l <- bufferedSource.getLines) {
          size += 1
          var line = l.replaceAll("\\s+$", "")
          if (size > HELP_MAX_SIZE || line.endsWith(END_HELP)) {
            helpEnd = line.endsWith(END_HELP)
            break
          }

          if (headComment || size < 3) {
            val ltr = line.trim
            if (ltr.startsWith("*") || ltr.startsWith("#")) {
              headComment = true
              line = if (ltr.length > 1) ltr.substring(2) else ""
            } else if (ltr.startsWith("/*") || ltr.startsWith("//")) {
              headComment = true
              line = if (ltr.length > 2) ltr.substring(3) else ""
            }
          }

          usage.append(line).append("\n")
        }

        if (usage.nonEmpty) {
          usage.append("\n")
          if (!helpEnd) {
            usage.insert(0, "\n")
          }
          bufferedSource.close
          throw new HelpException(usage.toString())
        } else {
          internalExecute()
        }

        bufferedSource.close
      } else {
        internalExecute()
      }
      true
    }

    private def internalExecute(): Unit = {
      if (isEngineScript) {
        result = engine.execute(script, expandParameters(args))
      } else if (isConsoleScript) {
        _executing = true

        var done = true
        val bufferedSource = Source.fromFile(script)

        var line = ""
        for (l <- bufferedSource.getLines) {
          if (l.trim.isEmpty || l.trim.startsWith("#")) {
            done = true
          } else {
            try {
              line += l
              parser.parse(line, line.length + 1, ParseContext.ACCEPT_LINE)
              done = true
              for ((arg, i) <- args.zipWithIndex) {
                line = line.replaceAll(
                  "\\s\\$" + i + "\\b",
                  (" " + expandParameterName(arg) + " ")
                )
                line = line.replaceAll(
                  "\\$\\{" + i + "(|:-.*)}",
                  expandParameterName(arg)
                )
              }
              line = line.replaceAll("\\$\\{@}", expandToList(args))
              line = line.replaceAll("\\$@", expandToList(args))
              line = line.replaceAll("\\s\\$\\d\\b", "")
              line = line.replaceAll("\\$\\{\\d+}", "")
              val matcher = Pattern.compile("\\$\\{\\d+:-(.*?)}").matcher(line)
              if (matcher.find()) {
                line = matcher.replaceAll(expandParameterName(matcher.group(1)))
              }

              if (verbose) {
                val asb = new AttributedStringBuilder()
                asb.styled(Styles.prntStyle().resolve(".vs"), line)
                asb.toAttributedString.println(terminal)
                terminal.flush()
              }

              println(systemRegistry.get.execute(line))
              line = "";
            } catch {
              case e: EOFError =>
                done = false
                line += "\n"
              case s: SyntaxError => throw s
              case f: EndOfFileException =>
                done = true
                result = engine.get("it")
                postProcess(cmdLine, result)
                break
              case e: Exception =>
                System.out.println("execute: abort on exception")
                bufferedSource.close
                _executing = false
                throw new IllegalArgumentException(line + "\n" + e.getMessage)
            }
          }
        }
        bufferedSource.close
        _executing = false
        if (!done) {
          throw new IllegalArgumentException("Incompleted command: \n" + line)
        }
      }
    }

    def getResult: Object = result

    private def expandParameterName(parameter: String): String = {
      if (parameter.startsWith("$")) {
        expandName(parameter)
      } else if (isNumber(parameter)) {
        parameter
      } else {
        quote(parameter)
      }
    }

    override def toString: String = {
      val sb = new mutable.StringBuilder
      sb.append("[")
      try sb.append("script:").append(script.getCanonicalPath)
      catch { case e: Exception => sb.append(e.getMessage) }
      sb.append(", ")
      sb.append("extension:").append(extension)
      sb.append(", ")
      sb.append("cmdLine:").append(cmdLine)
      sb.append(", ")
      sb.append("args:").append(args.toList)
      sb.append(", ")
      sb.append("verbose:").append(verbose)
      sb.append(", ")
      sb.append("result:").append(result)
      sb.append("]")
      sb.toString
    }

  }

  object ScriptFile {
    def getScriptCommandFile(command: String): File = {
      val file = new File(command)
      if (!file.exists() && engine.hasVariable(VAR_PATH)) {
        val path = engine.get(VAR_PATH).asInstanceOf[List[String]]
        for (p <- path) {
          for (e <- scriptExtensions) {
            val fileName = command + "." + e
            val filePath = Paths.get(p, fileName)
            if (filePath.toFile.exists()) {
              return filePath.toFile
            }
          }
        }
      }
      file
    }
  }
}
