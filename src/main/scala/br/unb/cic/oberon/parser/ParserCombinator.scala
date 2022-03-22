package br.unb.cic.oberon.parser
import br.unb.cic.oberon.util.Resources
import scala.util.parsing.combinator._
import br.unb.cic.oberon.ast._

trait Oberon2ScalaParser extends JavaTokenParsers {
    def bool: Parser[BoolValue] = "(FALSE|TRUE)".r ^^ (i => BoolValue(i=="TRUE"))
    def int: Parser[IntValue] = ("-?" + number).r ^^ (i => IntValue(i.toInt))
    def real: Parser[RealValue] =  (("-?" + number + "\\." + number).r | ("-?"+ number).r) ^^ (i => RealValue(i.toDouble))
    def string: Parser[StringValue] = ("\"[^\"]+\"".r | "\'[^\']+\'".r)  ^^ (i =>  StringValue(i.substring(1, i.length()-1)))
    def digit: String = "[0-9]"
    def number: String = digit + "+"
    def alpha: String = "[A-z]"
    def identifier: Parser[String] = (alpha +"(" + alpha + "|" + digit + "|_)*").r ^^ (i => i)

    def typeParser: Parser[Type] = (
        "INTEGER" ^^ (i => IntegerType)
    |   "REAL" ^^ (i => RealType)
    |   "CHAR" ^^ (i => CharacterType)
    |   "BOOLEAN" ^^ (i => BooleanType)
    |   "STRING" ^^ (i => StringType)
    |   "NIL" ^^ (i => NullType)
    |   identifier ^^ (i =>  ReferenceToUserDefinedType(i))
    )

    def parseAbs[T](result: ParseResult[T]): T = {
        return result match {
            case Success(matched,_) => matched
            case Failure(msg,_)  => throw new Exception(msg)
            case Error(msg,_) => throw new Exception(msg)
        }
    }
}
