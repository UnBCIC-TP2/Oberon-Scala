import br.unb.cic.oberon.codegen.PaigesBasedGenerator
import br.unb.cic.oberon.interpreter.Interpreter
import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.tc.TypeChecker
import org.rogach.scallop._

import java.nio.file.{Files, Paths}

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {

  version("Oberon")
  banner("""Interpretador Oberon""".stripMargin)
  footer("\nFor all other tricks, consult the documentation!")

  val tyc = opt[String](name = "typeChecker", short = 't', descr = "Check if the oberon code is correctly typed", argName = "Oberon program path")
  val interpreter = opt[String](name = "interpreter", short = 'i', descr = "Interprets the Oberon program", argName = "Oberon program path" )
  val compile = opt[List[String]](name = "compile", short = 'c', descr = "Compile the Oberon program", argName = "Oberon program path")

  verify()

}

object Main {
  def main(args: Array[String]) {
    val conf = new Conf(args)

    if(conf.tyc.isSupplied) {

      val path = Paths.get(conf.tyc())
      if(Files.exists(path)) {
        val content = String.join("\n", Files.readAllLines(path))

        val module = ScalaParser.parse(content)
        val visitor = new TypeChecker()

        val errors = visitor.visit(module)
        if (errors.isEmpty) println("The code is correctly typed")
        else println("Type error detected")
      }
      else {
        println("This file does not exist")
      }

    }

    if(conf.interpreter.isSupplied) {

      val path = Paths.get(conf.interpreter())

      if(Files.exists(path)) {
        val interpreter = new Interpreter()

        val content = String.join("\n", Files.readAllLines(path))
        val module = ScalaParser.parse(content)

        val result = module.accept(interpreter)
      }
      else {
        println("This file does not exist")
      }

      //println(result)
      //println(interpreter.env.lookup("x"))
      //println(interpreter.env.lookup("factorial"))

    }

    if(conf.compile.isSupplied) {

      val oberonPath = Paths.get(conf.compile()(0))

      if(Files.exists(oberonPath)) {
        val oberonContent = String.join("\n", Files.readAllLines(oberonPath))
        val module = ScalaParser.parse(oberonContent)

        val codeGen = PaigesBasedGenerator()
        val generatedCCode = codeGen.generateCode(module)

        //val cFilePath = conf.compile()(1) + "/" + module.name + ".c"

        //println("cFilePath = " + cFilePath)
        var cPath = Paths.get("")
        if (conf.compile().length == 1) {
          cPath = Paths.get("compiled.c")  // If path not provided, default path applied
        } else cPath = Paths.get(conf.compile()(1))
  
        val writer = Files.newBufferedWriter(cPath)

        writer.write(generatedCCode)
        writer.flush()
        writer.close()
      }
      else {
        println("This file does not exist")
      }
      //Files.createFile(cPath)

    }

  }
}
