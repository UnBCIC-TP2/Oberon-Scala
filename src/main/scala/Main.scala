import br.unb.cic.oberon.parser.ScalaParser
import br.unb.cic.oberon.tc.TypeChecker
import org.rogach.scallop._

import java.nio.file.{Files, Paths}

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val tyc = opt[String]()
  verify()
}

object Main {
  def main(args: Array[String]) {
    val conf = new Conf(args)
    if(conf.tyc.isSupplied) {
      //println("Escalobaloba " + conf.tyc())
      val path = Paths.get(conf.tyc())
      //println("chegou aqui")
      /*if(path == null) println("null")*/
      //println(path)
      val content = String.join("\n", Files.readAllLines(path))
      //println(content)

      val module = ScalaParser.parse(content)
      val visitor = new TypeChecker()

      val errors = visitor.visit(module)
      if(errors.size == 0) println("The code is correctly typed")
      else println("Type error detected")
    }

  }
}
