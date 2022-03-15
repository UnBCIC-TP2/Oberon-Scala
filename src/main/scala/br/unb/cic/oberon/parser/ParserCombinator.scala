package br.unb.cic.oberon.parser
import br.unb.cic.oberon.util.Resources
import scala.util.parsing.combinator._
import br.unb.cic.oberon.ast._

object Oberon2ScalaParser extends JavaTokenParsers {
    def bool: Parser[BoolValue] = "(FALSE|TRUE)".r ^^ (i => BoolValue(i=="TRUE"))
    def int: Parser[IntValue] = ("-?" + number).r ^^ (i => IntValue(i.toInt))
    def real: Parser[RealValue] =  (("-?" + number + "\\." + number).r | ("-?"+ number).r) ^^ (i => RealValue(i.toDouble))
    def string: Parser[StringValue] = ("\"[^\"]+\"".r | "\'[^\']+\'".r)  ^^ (i =>  StringValue(i.substring(1, i.length()-1)))
    def digit: String = "[0-9]"
    def number: String = digit + "+"
    def alpha: String = "[A-z]"
    def identifier: Parser[String] = (alpha +"(" + alpha + "|" + digit + "|_)*").r ^^ (i => i)
}
