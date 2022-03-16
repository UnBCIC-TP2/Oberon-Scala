package br.unb.cic.oberon.parser

import scala.util.parsing.combinator._
import br.unb.cic.oberon.ast._
import java.nio.file.{Files, Paths}
import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.TolerantNumerics




class ParserCombinatorTestSuite extends AnyFunSuite{
    
    test("Testing Int Parser") {
        val len = 4
        var input = new Array[String](len)
        var output = new Array[Any](len)

        //positive number
        input(0) = "123"
        output(0) = IntValue(123)
        //negative number
        input(1) = "-321"
        output(1) = IntValue(-321)
        //string before
        input(2) = "abc 123"
        output(2) = "FAILURE: string matching regex '-?[0-9]+' expected but 'a' found"
        //string after
        input(3) = "123 abc"
        output(3) = IntValue(123)

        for( i <- 0 to len-1) 
            assert(output(i) == Oberon2ScalaParser.parseAbs(Oberon2ScalaParser.parse(Oberon2ScalaParser.int, input(i))))
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
            assert(output(i) == Oberon2ScalaParser.parseAbs(Oberon2ScalaParser.parse(Oberon2ScalaParser.real, input(i))))

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
            assert(output(i) == Oberon2ScalaParser.parseAbs(Oberon2ScalaParser.parse(Oberon2ScalaParser.bool, input(i))))
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
            assert(output(i) == Oberon2ScalaParser.parseAbs(Oberon2ScalaParser.parse(Oberon2ScalaParser.string, input(i))))

    }

    test("Testing identifier parser") {
        val len = 1
        var input = new Array[String](len)
        var output = new Array[Any](len)

        //Testing Simple id
        input(0) = "teste"
        output(0) = "teste"

        for( i <- 0 to len-1) 
            assert(output(i) == Oberon2ScalaParser.parseAbs(Oberon2ScalaParser.parse(Oberon2ScalaParser.identifier, input(i))))


    }
}