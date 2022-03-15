package br.unb.cic.oberon.parser

import br.unb.cic.oberon.ast._
import java.nio.file.{Files, Paths}
import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.TolerantNumerics


class ParserCombinatorTestSuite extends AnyFunSuite{

    test("Testing Int Parser") {
        val assign = IntValue(123)
        val a = ParserInt.parse(ParserInt.int, "123") match {

            case ParserInt.Success(matched,_) => matched 
            case ParserInt.Failure(msg,_)  => fail("FAILURE: " + msg)
            case ParserInt.Error(msg,_) => fail("ERROR: " + msg)
        }

        assert(assign ==a ) 
    }

    test("Testing Real Parser") {
        val assign = RealValue(1.02)
        val a = ParserReal.parse(ParserReal.real, "1.02") match {
            case ParserReal.Success(matched,_) => matched 
            case ParserReal.Failure(msg,_)  => fail("FAILURE: " + msg)
            case ParserReal.Error(msg,_) => fail("ERROR: " + msg)
        }

        assert(assign == a)
    }

    test("Testing Bool Parser") {

        val assign1 = BoolValue(true)
        val assign2 = BoolValue(false)

        val a = ParserBool.parse(ParserBool.bool, "TRUE") match {
            case ParserBool.Success(matched,_) => matched 
            case ParserBool.Failure(msg,_)  => fail("FAILURE: " + msg)
            case ParserBool.Error(msg,_) => fail("ERROR: " + msg)
        }


        val b = ParserBool.parse(ParserBool.bool, "FALSE") match {
            case ParserBool.Success(matched,_) => matched 
            case ParserBool.Failure(msg,_)  => fail("FAILURE: " + msg)
            case ParserBool.Error(msg,_) => fail("ERROR: " + msg)
        }

        assert(assign1 == a)
        assert(assign2 == b)
    }

    test("Testing String Parser") {

        val assign = StringValue("teste")

        val a = ParserString.parse(ParserString.string, "\"teste\"") match {
            case ParserString.Success(matched,_) => matched 
            case ParserString.Failure(msg,_)  => fail("FAILURE: " + msg)
            case ParserString.Error(msg,_) => fail("ERROR: " + msg)
        }
        
        val b = ParserString.parse(ParserString.string, "\'teste\'") match {
            case ParserString.Success(matched,_) => matched 
            case ParserString.Failure(msg,_)  => fail("FAILURE: " + msg)
            case ParserString.Error(msg,_) => fail("ERROR: " + msg)
        }

        assert (a == assign)
        assert (b == assign)
    }
}