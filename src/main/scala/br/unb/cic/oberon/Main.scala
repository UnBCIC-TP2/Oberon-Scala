package br.unb.cic.oberon

import br.unb.cic.oberon.codegen.{CodeGenerator, JVMCodeGenerator, PaigesBasedGenerator}
import br.unb.cic.oberon.interpreter._
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.tc.TypeChecker
import br.unb.cic.oberon.repl.REPL
import org.rogach.scallop._
import org.rogach.scallop.exceptions

import java.nio.file.{Files, Paths, Path}
import java.util.Base64

object Main extends App {

  object conf extends ScallopConf(args) {
    version("Oberon 0.1.1-SNAPSHOT")
    banner("Compiler and interpreter for Oberon")

    object tc extends Subcommand("typeChecker") {
      val inputPath = opt[Path](name="in", descr = "Path of the input file", argName = "path", required=true)
      validatePathExists(inputPath)
    }

    object interpreter extends Subcommand("interpreter") {
      val inputPath = opt[Path](name="in", descr = "Path of the input file", argName = "path", required=true)
      validatePathExists(inputPath)
    }

    object compile extends Subcommand("compile") {
      val inputPath = opt[Path](name="in", descr = "Path of the input file", argName = "path", required=true)
      val outputPath = opt[Path](name = "out", descr = "Path of the output file", argName = "path", required=true)
      val backend = choice(name="backend", choices=Seq("llvm", "c", "jvm"), default=Some("c"), descr="Which backend to compile to", argName="backend")

      validatePathExists(inputPath)
    }

    object repl extends Subcommand("repl") {}

    addSubcommand(tc)
    addSubcommand(interpreter)
    addSubcommand(compile)
    addSubcommand(repl)
    requireSubcommand()

    verify()
  }

  conf.subcommand match {
    case Some(conf.tc) => typeCheck()
    case Some(conf.interpreter) => interpret()
    case Some(conf.compile) => compile()
    case Some(conf.repl) => REPL.runREPL()
  }

  private def compile() {
    val content = Files.readString(conf.compile.inputPath.get.get)
    val module = ScalaParser.parse(content)

    conf.compile.backend.get.get match {
      case "c" => {
        val generatedCode = (new PaigesBasedGenerator).generateCode(module)
        Files.writeString(conf.compile.outputPath.get.get, generatedCode)
      }
      case "jvm" => {
        val generatedCode = JVMCodeGenerator.generateCode(module)
        Files.write(conf.compile.outputPath.get.get, Base64.getDecoder.decode(generatedCode))
      }
      case "llvm" => {
        Files.writeString(conf.compile.outputPath.get.get, "LLVM :)")
      }
    }
  }

  private def interpret() = {
    val content = Files.readString(conf.interpreter.inputPath.get.get)
    val module = ScalaParser.parse(content)

    val interpreter = new Interpreter()
    val result = interpreter.runInterpreter(module)
  }

  private def typeCheck() = {
    val content = Files.readString(conf.tc.inputPath.get.get)
    val module = ScalaParser.parse(content)

    val visitor = new TypeChecker()
    val errors = visitor.visit(module)
    if (errors.isEmpty) {
      println("The code is correctly typed")
    } else {
      println("Type errors detected:")
      errors.filter(v => (v._2 != "None")).foreach(v => println(v))
    }
  }
}
