package br.unb.cic.oberon.parser

import br.unb.cic.oberon.ast._
import java.nio.file.{Files, Paths}
import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.TolerantNumerics


class ParserCombinatorTestSuite extends AnyFunSuite{

    test("Testing Int Parser") {
        val assign = IntValue(123)
        val a = Oberon2ScalaParser.parse(Oberon2ScalaParser.int, "123") match {

            case Oberon2ScalaParser.Success(matched,_) => matched 
            case Oberon2ScalaParser.Failure(msg,_)  => fail("FAILURE: " + msg)
            case Oberon2ScalaParser.Error(msg,_) => fail("ERROR: " + msg)
        }

        assert(assign ==a ) 
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