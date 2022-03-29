package br.unb.cic.oberon.parser

import scala.util.parsing.combinator._
import br.unb.cic.oberon.ast._
import java.nio.file.{Files, Paths}
import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.TolerantNumerics


class ParserCombinatorTestSuite extends AnyFunSuite with Oberon2ScalaParser {
    
    test("Testing Int Parser") {
        //toDo comentar
        assert(IntValue(123) == parseAbs(parse(int, "123")))
        assert(IntValue(-321) == parseAbs(parse(int, "-321")))
        val thrown = intercept[Exception] {
            parseAbs(parse(int, "abc 123"))
        }
        assert(thrown.getMessage.length>0) //não verifica o tipo de erro
        assert(IntValue(123) == parseAbs(parse(int, "123 abc")))
    }

    test("Testing Real Parser") {
        val len = 2
        var input = new Array[String](len)
        var output = new Array[Any](len)

        //positive number
        input(0) = "12.3"
        output(0) = RealValue(12.3)
        //negative number
        input(1) = "-32.1"
        output(1) = RealValue(-32.1)

        for( i <- 0 to len-1) 
            assert(output(i) == parseAbs(parse(real, input(i))))

    }

    test("Testing Bool Parser") {

        val len = 2
        var input = new Array[String](len)
        var output = new Array[Any](len)

        //True
        input(0) = "TRUE"
        output(0) = BoolValue(true)
        //False
        input(1) = "FALSE"
        output(1) = BoolValue(false)

        for( i <- 0 to len-1) 
            assert(output(i) == parseAbs(parse(bool, input(i))))
    }

    test("Testing String Parser") {

        val len = 2
        var input = new Array[String](len)
        var output = new Array[Any](len)

        //Double quotes
        input(0) = "\"teste\""
        output(0) = StringValue("teste")
        //Single quotes
        input(1) = "\'teste\'"
        output(1) = StringValue("teste")

        for( i <- 0 to len-1) 
            assert(output(i) == parseAbs(parse(string, input(i))))

    }

    test("Testing identifier parser") {
        val len = 1
        var input = new Array[String](len)
        var output = new Array[Any](len)

        //Testing Simple id
        input(0) = "teste"
        output(0) = "teste"

        for( i <- 0 to len-1) 
            assert(output(i) == parseAbs(parse(identifier, input(i))))
    }

    test("Testing type parser"){
        val len = 7
        var input = new Array[String](len)
        var output = new Array[Any](len)

        //Testing 
        input(0) = "INTEGER"
        output(0) = IntegerType
        //
        input(1) = "REAL"
        output(1) = RealType

        input(2) = "CHAR"
        output(2) = CharacterType
        //
        input(3) = "BOOLEAN"
        output(3) = BooleanType
        //
        input(4) = "STRING"
        output(4) = StringType
        //
        input(5) = "NIL"
        output(5) = NullType
        //
        input(6) = "bolo"
        output(6) = ReferenceToUserDefinedType("bolo")
        //

        for( i <- 0 to len-1) 
            assert(output(i) == parseAbs(parse(typeParser, input(i))))
    }
}