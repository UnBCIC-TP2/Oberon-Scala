package br.unb.cic.oberon.repl

import org.jline.builtins.Completers.{OptDesc, OptionCompleter}
import org.jline.console.{
  ArgDesc,
  CmdDesc,
  CommandInput,
  CommandMethods,
  CommandRegistry,
  Printer
}
import org.jline.console.impl.AbstractCommandRegistry
import org.jline.reader.Completer
import org.jline.reader.impl.completer.{
  ArgumentCompleter,
  NullCompleter,
  StringsCompleter
}
import org.jline.utils.AttributedString

import java.util
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._
import scala.jdk.FunctionConverters._

class REPLCommand(
    commands: Option[Array[Commands.REPL]],
    engine: OberonEngine,
    printer: Printer
) extends AbstractCommandRegistry
    with CommandRegistry {
  val cmds: Array[Commands.REPL] = commands.getOrElse(Commands.REPL.values())
  val commandName = new util.HashMap[Commands.REPL, String]
  val commandExecute = new util.HashMap[Commands.REPL, CommandMethods]
  val commandDescs = new util.HashMap[Commands.REPL, CmdDesc]
  val commandInfos = new util.HashMap[Commands.REPL, util.List[String]]

  for (cmd <- cmds) {
    commandName.put(cmd, cmd.name().toLowerCase())
  }

  commandExecute.put(
    Commands.REPL.INSPECT,
    new CommandMethods(
      (i => inspect(i)).asJava,
      (c => inspectCompleter(c)).asJava
    )
  )
  registerCommands(commandName, commandExecute)
  commandDescs.put(Commands.REPL.INSPECT, inspectCmdDesc())

  def this(engine: OberonEngine, printer: Printer) {
    this(None, engine, printer)
  }

  override def commandInfo(command: String): util.List[String] =
    commandInfos.get(registeredCommand(command).asInstanceOf[Commands.REPL])

  override def commandDescription(args: util.List[String]): CmdDesc = {
    val command = if (args != null && !args.isEmpty) args.get(0) else ""
    commandDescs.get(registeredCommand(command).asInstanceOf[Commands.REPL])
  }

  private def helpDesc(command: Commands.REPL): CmdDesc = doHelpDesc(
    command.toString.toLowerCase,
    commandInfos.get(command),
    commandDescs.get(command)
  )

  private def inspect(input: CommandInput): Object = {
    if (input.xargs.isEmpty) return null
    if (input.args.length > 2)
      throw new IllegalArgumentException(
        "Wrong number of command parameters: " + input.args().length
      )

    val idx = optionIdx(input.args())
    val option = if (idx < 0) "--info" else input.args()(idx)
    if (option == "-?" || option == "--help") {
      printer.println(helpDesc(Commands.REPL.INSPECT))
    }

    var id = 0
    if (idx >= 0) {
      id = if (idx == 0) 1 else 0
    }

    if (input.args.length < id + 1)
      throw new IllegalArgumentException(
        "Wrong number of command parameters: " + input.args.length
      )

    try {
      val obj = input.xargs()(id)
      // TODO: inspect object
    } catch {
      case e: Exception => saveException(e)
    }
    null
  }

  private def inspectCmdDesc(): CmdDesc = {
    val optDescs = new util.HashMap[String, util.List[AttributedString]]
    optDescs.put("-? --help", doDescription("Displays command help"))
    optDescs.put("-i --info", doDescription("Object class info"))
    // optDescs.put ("-m --methods", doDescription ("List object methods") )
    // optDescs.put ("-n --metaMethods", doDescription ("List object metaMethods") )

    val argDescs = new util.ArrayList[ArgDesc]()
    val out = new CmdDesc(argDescs, optDescs)
    val mainDesc = new mutable.ListBuffer[AttributedString]
    val info = new mutable.ListBuffer[String]

    info += "Display object info on terminal"
    commandInfos.put(Commands.REPL.INSPECT, info.toList.asJava)
    mainDesc += new AttributedString("inspect [OPTION] OBJECT")
    out.setMainDesc(mainDesc.toList.asJava)
    out.setHighlighted(false)
    out
  }

  private def doDescription(
      description: String
  ): util.List[AttributedString] = {
    List(new AttributedString(description)).asJava
  }

  private def optionIdx(args: Array[String]): Int =
    args.indexWhere(a => a.startsWith("-"))

  private def variables(): List[String] = {
    engine.find(null).keySet().asScala.map("$" + _).toList
  }

  private def compileOptDescs(command: String): List[OptDesc] = {
    val cmd = Commands.REPL.valueOf(command.toUpperCase)
    val out = new ListBuffer[OptDesc]
    commandDescs
      .get(cmd)
      .getOptsDesc
      .entrySet
      .forEach(entry => {
        val option = entry.getKey.split("\\s+")
        val desc = entry.getValue.get(0).toString
        if (option.length == 2) {
          out += new OptDesc(option(0), option(1), desc)
        } else if (option(0).charAt(1) == '-') {
          out += new OptDesc(null, option(0), desc)
        } else {
          out += new OptDesc(option(0), null, desc)
        }
      })
    out.toList
  }

  private def inspectCompleter(command: String): util.List[Completer] = {
    val variablesFunc = (() => variables().asJavaCollection).asJavaSupplier
    val compileOptFunc =
      ((c: String) => compileOptDescs(c).asJavaCollection).asJava
    List(
      new ArgumentCompleter(
        NullCompleter.INSTANCE,
        new OptionCompleter(
          List(
            new StringsCompleter(variablesFunc),
            NullCompleter.INSTANCE
          ).asJava,
          compileOptFunc,
          1
        )
      ).asInstanceOf[Completer]
    ).asJava
  }

}
