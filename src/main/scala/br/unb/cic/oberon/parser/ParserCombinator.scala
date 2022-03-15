package br.unb.cic.oberon.parser
import br.unb.cic.oberon.util.Resources
import scala.util.parsing.combinator._
import br.unb.cic.oberon.ast._

object Oberon2ScalaParser extends JavaTokenParsers {
    def bool: Parser[BoolValue] = "(FALSE|TRUE)".r ^^ (i => BoolValue(i=="TRUE"))
    def int: Parser[IntValue] = "[0-9]+".r ^^ { (i => IntValue(i.toInt))   }
    def real: Parser[RealValue] =  ("[0-9]+\\.[0-9]+".r | "[0-9]+".r) ^^ (i => RealValue(i.toDouble))
    def string: Parser[StringValue] = ("\"[^\"]+\"".r | "\'[^\']+\'".r)  ^^ (i =>  StringValue(i.substring(1, i.length()-1)))
}
