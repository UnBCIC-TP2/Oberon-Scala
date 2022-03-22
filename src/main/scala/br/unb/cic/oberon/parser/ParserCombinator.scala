package br.unb.cic.oberon.parser
import br.unb.cic.oberon.util.Resources
import scala.util.parsing.combinator._
import br.unb.cic.oberon.ast._

trait BasicParsers extends JavaTokenParsers {
    // unity parsers
    def digit: Parser[String] = "[0-9]".r ^^ (i => i)
    def number: Parser[String] = "[0-9]+".r ^^ (i => i)
    def alpha: String = "[A-z]"
    /*def number: Parser[String] = digit ~ opt(rep(digit)) ^^ {
        case r ~ None => r
        case r ~ Some(rs) => r + rs.mkString
    }*/ // number parser using combinators

    // TypeValues
    def int: Parser[IntValue] = opt("-")~number ^^ {
        case None ~ a => IntValue(a.toInt)
        case Some("-") ~ a => IntValue(-(a.toInt))
    }
    def real: Parser[RealValue] =  opt("-") ~ number ~ opt("." ~ number) ^^ {
        case None ~ a ~ None => RealValue(a.toDouble)
        case None ~ a ~ Some("." ~ b) => RealValue((a + "." + b).toDouble)
        case Some("-") ~ a ~ None => RealValue(- a.toDouble)
        case Some("-") ~ a ~ Some("." ~ b) => RealValue(-(a + "." + b).toDouble)
    }
    def bool: Parser[BoolValue] = "(FALSE|TRUE)".r ^^ (i => BoolValue(i=="TRUE"))
    def string: Parser[StringValue] = ("\"[^\"]+\"".r | "\'[^\']+\'".r)  ^^ (i =>  StringValue(i.substring(1, i.length()-1)))
    def identifier: Parser[String] = (alpha +"(" + alpha + "|" + digit + "|_)*").r ^^ (i => i)
    //
    def typeParser: Parser[Type] = (
        "INTEGER" ^^ (i => IntegerType)
    |   "REAL" ^^ (i => RealType)
    |   "CHAR" ^^ (i => CharacterType)
    |   "BOOLEAN" ^^ (i => BooleanType)
    |   "STRING" ^^ (i => StringType)
    |   "NIL" ^^ (i => NullType)
    |   identifier ^^ (i =>  ReferenceToUserDefinedType(i))
    )
}

trait Oberon2ScalaParser extends BasicParsers{
    //parser principal


    def parseAbs[T](result: ParseResult[T]): T = {
        return result match {
            case Success(matched,_) => matched
            case Failure(msg,_)  => throw new Exception(msg)
            case Error(msg,_) => throw new Exception(msg)
        }
    }
}
