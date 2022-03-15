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
        val positiveReal = RealValue(1.02)
        val negativeReal = RealValue(-3.14)

        val a = Oberon2ScalaParser.parse(Oberon2ScalaParser.real, "1.02") match {
            case Oberon2ScalaParser.Success(matched,_) => matched 
            case Oberon2ScalaParser.Failure(msg,_)  => fail("FAILURE: " + msg)
            case Oberon2ScalaParser.Error(msg,_) => fail("ERROR: " + msg)
        }
        val b = Oberon2ScalaParser.parse(Oberon2ScalaParser.real, "-3.14") match {
            case Oberon2ScalaParser.Success(matched,_) => matched 
            case Oberon2ScalaParser.Failure(msg,_)  => fail("FAILURE: " + msg)
            case Oberon2ScalaParser.Error(msg,_) => fail("ERROR: " + msg)
        }

        assert(positiveReal == a)
        assert(negativeReal == b)
    }

    test("Testing Bool Parser") {

        val assign1 = BoolValue(true)
        val assign2 = BoolValue(false)

        val a = Oberon2ScalaParser.parse(Oberon2ScalaParser.bool, "TRUE") match {
            case Oberon2ScalaParser.Success(matched,_) => matched 
            case Oberon2ScalaParser.Failure(msg,_)  => fail("FAILURE: " + msg)
            case Oberon2ScalaParser.Error(msg,_) => fail("ERROR: " + msg)
        }


        val b = Oberon2ScalaParser.parse(Oberon2ScalaParser.bool, "FALSE") match {
            case Oberon2ScalaParser.Success(matched,_) => matched 
            case Oberon2ScalaParser.Failure(msg,_)  => fail("FAILURE: " + msg)
            case Oberon2ScalaParser.Error(msg,_) => fail("ERROR: " + msg)
        }

        assert(assign1 == a)
        assert(assign2 == b)
    }

    test("Testing String Parser") {

        val assign = StringValue("teste")

        val a = Oberon2ScalaParser.parse(Oberon2ScalaParser.string, "\"teste\"") match {
            case Oberon2ScalaParser.Success(matched,_) => matched 
            case Oberon2ScalaParser.Failure(msg,_)  => fail("FAILURE: " + msg)
            case Oberon2ScalaParser.Error(msg,_) => fail("ERROR: " + msg)
        }
        
        val b = Oberon2ScalaParser.parse(Oberon2ScalaParser.string, "\'teste\'") match {
            case Oberon2ScalaParser.Success(matched,_) => matched 
            case Oberon2ScalaParser.Failure(msg,_)  => fail("FAILURE: " + msg)
            case Oberon2ScalaParser.Error(msg,_) => fail("ERROR: " + msg)
        }

        assert (a == assign)
        assert (b == assign)
    }

    test("Testing identifier parser") {
        val assign = "teste"

        val a = Oberon2ScalaParser.parse(Oberon2ScalaParser.identifier, "teste") match {
            case Oberon2ScalaParser.Success(matched,_) => matched 
            case Oberon2ScalaParser.Failure(msg,_)  => fail("FAILURE: " + msg)
            case Oberon2ScalaParser.Error(msg,_) => fail("ERROR: " + msg)
        }

        assert (a == assign)
    }
}