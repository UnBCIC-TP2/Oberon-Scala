package br.unb.cic.oberon.repl

import org.jline.builtins.ConfigurationPath
import org.jline.builtins.Nano.SyntaxHighlighter
import org.jline.console.{CmdLine, CommandInput, CommandMethods, CommandRegistry, ConsoleEngine}
import org.jline.console.impl.{Builtins, ConsoleEngineImpl, DefaultPrinter, JlineCommandRegistry, SystemHighlighter, SystemRegistryImpl}
import org.jline.keymap.KeyMap
import org.jline.reader.{EndOfFileException, LineReader, LineReaderBuilder, Parser, Reference, UserInterruptException}
import org.jline.terminal.Terminal.Signal
import org.jline.terminal.{Size, Terminal, TerminalBuilder}
import org.jline.utils.InfoCmp.Capability
import org.jline.utils.OSUtils
import org.jline.widget.{TailTipWidgets, Widgets}
import org.jline.widget.TailTipWidgets.TipType

import java.nio.file.{Path, Paths}
import java.io.{File, FileWriter}
import java.util
import scala.jdk.CollectionConverters._
import scala.jdk.FunctionConverters._

/**
 * Our REPL module singleton class (i.e., an
 * Scala object).
 */

object REPL {
  val DEFAULT_NANORC_FILE = "jnanorc"
  val VAR_NANORC = "NANORC"

  /**
   * Executes the REPL Oberon interpreter
   */

  def runREPL(): Unit = {
    val workDir = () => Paths.get(System.getProperty("user.dir"))

    /*
     * Parser & Terminal
     */
    val parser = new REPLParser
    val terminal = TerminalBuilder.builder.build
    if (terminal.getWidth == 0 || terminal.getHeight == 0) {
      terminal.setSize(new Size(120, 40)) // hard coded terminal size when redirecting
    }
    val executeThread = Thread.currentThread
    terminal.handle(Signal.INT, _ => executeThread.interrupt())

    /*
     * Create jnanorc config file
     */

    //val file = new File(REPL.getClass.getProtectionDomain.getCodeSource.getLocation.toURI.getPath)
    //val root = file.getCanonicalPath.replace("classes", "").replaceAll("\\\\", "/")
    println("A")
    val root = workDir().toFile.getCanonicalPath
    val jnanorcFile = Paths.get(root, DEFAULT_NANORC_FILE).toFile
    if (!jnanorcFile.exists) {
      val fw = new FileWriter(jnanorcFile)
      try {
        fw.write("theme " + root + "nanorc/dark.nanorctheme\n")
        fw.write("include " + root + "nanorc/*.nanorc\n")
      } finally if (fw != null) fw.close()
    }
    println("B")

    /*
     * REPLEngine and command registries
     */
    println("C")
    val scriptEngine = new OberonEngine
    val configPath = new ConfigurationPath(Paths.get(root), Paths.get(root))
    val printer = new DefaultPrinter(scriptEngine, configPath)
    val consoleEngine = new ConsoleEngineImpl(scriptEngine, printer, workDir.asJava, configPath)
    val builtins = new Builtins(workDir.asJava, configPath, (fun: String) => new ConsoleEngine.WidgetCreator(consoleEngine, fun))
    println("D")

    println("E")
    val myCommands = new MyCommands(workDir)
    val systemRegistry = new REPLSystemRegistry(parser, terminal, workDir, configPath)
    systemRegistry.register("oberon", new REPLCommand(scriptEngine, printer))
    systemRegistry.setCommandRegistries(consoleEngine, builtins, myCommands)
    systemRegistry.addCompleter(scriptEngine.getScriptCompleter)
    systemRegistry.setScriptDescription(scriptEngine.scriptDescription)
    println("F")

    /*
     * Command line highlighter
     */
    println("G")
    val jnanorc = configPath.getConfig(DEFAULT_NANORC_FILE)
    println("GA")
    val commandHighlighter = SyntaxHighlighter.build(jnanorc, "COMMAND")
    println("GC")
    val argsHighlighter = SyntaxHighlighter.build(jnanorc, "ARGS")
    println("GD")
    val groovyHighlighter = SyntaxHighlighter.build(jnanorc, "Oberon")
    println("GE")
    val highlighter = new SystemHighlighter(commandHighlighter, argsHighlighter, groovyHighlighter)
    println("GF")
    // if (!OSUtils.IS_WINDOWS) highlighter.setSpecificHighlighter("!", SyntaxHighlighter.build(jnanorc, "SH-REPL"))
    highlighter.addFileHighlight("nano", "less", "slurp")
    // highlighter.addFileHighlight("oberon", "classloader", List("-a", "--add").asJavaCollection)
    // highlighter.addExternalHighlighterRefresh(printer.refresh)
    // highlighter.addExternalHighlighterRefresh(scriptEngine.refresh)
    println("H")

    /*
     * Line reader
     */
    println("I")
    val reader = LineReaderBuilder.builder
      .terminal(terminal)
      .completer(systemRegistry.completer)
      .parser(parser)
      .highlighter(highlighter)
      .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
      .variable(LineReader.INDENTATION, 2)
      .variable(LineReader.LIST_MAX, 100)
      .variable(LineReader.HISTORY_FILE, Paths.get(root, "history"))
      .option(LineReader.Option.INSERT_BRACKET, true)
      .option(LineReader.Option.EMPTY_WORD_OPTIONS, false)
      .option(LineReader.Option.USE_FORWARD_SLASH, true) // use forward slash in directory separator
      .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true).build
    if (OSUtils.IS_WINDOWS) reader.setVariable(LineReader.BLINK_MATCHING_PAREN, 0) // if enabled cursor remains in begin parenthesis (gitbash)
    println("J")

    // complete command registries
    consoleEngine.setLineReader(reader)
    builtins.setLineReader(reader)
    myCommands.setLineReader(reader)
    println("K")

    // widgets and console initialization
    val cmdDesc = (v: CmdLine) => systemRegistry.commandDescription(v)
    println("KA")
    new TailTipWidgets(reader, cmdDesc.asJava, 5, TipType.COMPLETER)
    println("KB")
    val keyMap = reader.getKeyMaps.get("main")
    println("KC")
    keyMap.bind(new Reference(Widgets.TAILTIP_TOGGLE), KeyMap.alt("s"))
    println("KD")
    systemRegistry.initialize(Paths.get(root, "init.jline").toFile)
    println("L")

    /*
     * REPL loop
     */
    println(terminal.getName + ": " + terminal.getType)
    var keepRunning = true
    while (keepRunning) {
      try {
        systemRegistry.cleanUp() // delete temporary variables and reset output streams

        var line: String = reader.readLine("oberon-repl> ")
        line = if (parser.getCommand(line).startsWith("!")) line.replaceFirst("!", "! ") else line

        val result: Any = systemRegistry.execute(line)
        consoleEngine.println(result)
      } catch {
        case u: UserInterruptException => // ignore
        case e: EndOfFileException =>
          val pl: String = e.getPartialLine
          if (pl != null) { // execute last line from redirected file (required for Windows)
            try consoleEngine.println(systemRegistry.execute(pl))
            catch {
              case e2: Exception =>
                systemRegistry.trace(e2)
            }
          }
          keepRunning = false
        case e@(_: Exception | _: Error) =>
          println("ERROR")
          systemRegistry.trace(e) // print exception and save it to console variable
      }
    }

  }

  // TODO: Test REPLSystemRegistry
  private class REPLSystemRegistry(parser: Parser, terminal: Terminal, workDir: () => Path, configPath: ConfigurationPath) extends SystemRegistryImpl(parser, terminal, workDir.asJavaSupplier, configPath) {
    override def isCommandOrScript(command: String): Boolean = command.startsWith("!") || super.isCommandOrScript(command)
  }

  /*
   * TODO: Implements shell commands
   */
  private class MyCommands(val workDir: () => Path) extends JlineCommandRegistry() with CommandRegistry {
    var reader: Option[LineReader] = None
    val commandExecute = new util.HashMap[String, CommandMethods]()
    commandExecute.put("clear", new CommandMethods((i => clear(i)).asJava, (c => defaultCompleter(c)).asJava))
    // commandExecute.put("!", new CommandMethods(this::clear, this::defaultCompleter))
    registerCommands(commandExecute)

    def setLineReader(lineReader: LineReader): Unit = {
      reader = Option(lineReader)
    }

    private def terminal(): Terminal = if (reader.isDefined) reader.get.getTerminal else null

    private def clear(input: CommandInput): Unit = {
      val usage = Array(
        "clear -  clear terminal",
        "Usage: clear",
        "  -? --help                       Displays command help"
      )
      try {
        parseOptions(usage, input.args().asInstanceOf[Array[AnyRef]])
        terminal().puts(Capability.clear_screen)
        terminal().flush()
      } catch {
        case e: Exception => saveException(e)
      }
    }
  }
}
