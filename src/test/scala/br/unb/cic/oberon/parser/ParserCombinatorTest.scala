package br.unb.cic.oberon.parser

import scala.util.parsing.combinator._
import br.unb.cic.oberon.ast._
import java.nio.file.{Files, Paths}
import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.TolerantNumerics


class ParserCombinatorTestSuite extends AnyFunSuite with Oberon2ScalaParser {
    test("Testing Int Parser") {
        assert(IntValue(123) == parseAbs(parse(int, "123"))) // positive number
        assert(IntValue(-321) == parseAbs(parse(int, "-321"))) // negative number
        val thrown = intercept[Exception] {
            parseAbs(parse(int, "abc 123")) // not accepted chars in the beginning
        }
        assert(thrown.getMessage.length>0)
        assert(IntValue(123) == parseAbs(parse(int, "123 abc"))) // accepted chars in the end
    }

    test("Testing Real Parser") {
        assert(RealValue(12.3) == parseAbs(parse(real, "12.3"))) // positive number
        assert(RealValue(-32.1) == parseAbs(parse(real, "-32.1"))) // negative number
    }

    test("Testing Bool Parser") {
        assert(BoolValue(true) == parseAbs(parse(bool, "TRUE")))
        assert(BoolValue(false) == parseAbs(parse(bool, "FALSE")))
    }

    test("Testing String Parser") {
        assert(StringValue("teste") == parseAbs(parse(string, "\"teste\""))) // double quotes
    }

    test("Testing identifier parser") {
        assert("teste" == parseAbs(parse(identifier, "teste")))
    }

    test("Testing type parser") {
        assert(IntegerType == parseAbs(parse(typeParser, "INTEGER"))) 
        assert(RealType == parseAbs(parse(typeParser, "REAL")))
        assert(CharacterType == parseAbs(parse(typeParser, "CHAR")))
        assert(BooleanType == parseAbs(parse(typeParser, "BOOLEAN")))
        assert(StringType == parseAbs(parse(typeParser, "STRING")))
        assert(NullType == parseAbs(parse(typeParser, "NIL")))
        assert(ReferenceToUserDefinedType("bolo") == parseAbs(parse(typeParser, "bolo")))
    }

    test("Testing expValueParser Expression") {
        assert(IntValue(16) == parseAbs(parse(expValueParser, "16")))
        assert(RealValue(-35.2) == parseAbs(parse(expValueParser, "-35.2")))
        assert(CharValue('a') == parseAbs(parse(expValueParser, "'a'")))
        assert(StringValue("teste") == parseAbs(parse(expValueParser, "\"teste\"")))
        assert(BoolValue(true) == parseAbs(parse(expValueParser, "TRUE")))
        assert(BoolValue(false) == parseAbs(parse(expValueParser, "FALSE")))
        assert(NullValue == parseAbs(parse(expValueParser, "NIL")))
    }
}