package br.unb.cic.oberon.stdlib

import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.util.Values

object Functions {
    def abs(x: Int): Int = Math.abs(x)
    def odd(x: Int): Boolean = x%2 == 1
    def lsl(x: Int, n:Int): Int = x << n
    def asr(x: Int, n:Int): Int = x >> n
    def ror(x: Int, n: Int): Int = {
        var y = x
        y >> n
        var z = x
        z << (32-n)
        return y | z
    } 
}

object StandardLibrary {

    def abs = Procedure(
        "ABS", // name
        List(FormalArg("x", IntegerType)), // arguments
        Some(IntegerType), // return type
        List(),
        List(),

        ScalaStmt(env => {
            val value = env.lookup("x") match {
                case Some(IntValue(x)) => IntValue(Functions.abs(x))
                case _ => ???
            }
            env.setLocalVariable(Values.ReturnKeyWord, value)
        })
    )

    def odd = Procedure(
        "ODD", // name
        List(FormalArg("x", IntegerType)), // arguments
        Some(BooleanType), // return type
        List(),
        List(),

        ScalaStmt(env => {
            val value = env.lookup("x") match {
                case Some(IntValue(x)) => BoolValue(Functions.odd(x))
                case _ => ???
            }
            env.setLocalVariable(Values.ReturnKeyWord, value)
        })
    )

    /*
    def len = Procedure(
        "LEN", // name
        List(FormalArg("v", ArrayType)), // arguments <-------------------------- "v" e do tipo USerDefinedType
        Some(IntegerType), // return type
        List(),
        List(),

        ScalaStmt(env => {
            val value = (env.lookup("v")) match {
                case Some(ArrayValue(v)) => IntValue(v.length)
                case _ => ???
            }
            env.setLocalVariable(Values.ReturnKeyWord, value)
        })
    )
    */

    def lsl = Procedure(
        "LSL", // name
        List(FormalArg("x", IntegerType), FormalArg("n", IntegerType)), // arguments
        Some(IntegerType), // return type
        List(),
        List(),

        ScalaStmt(env => {
            val value = (env.lookup("x"), env.lookup("n")) match {
                case (Some(IntValue(x)),Some(IntValue(n))) => IntValue(Functions.lsl(x,n))
                case _ => ???
            }
            env.setLocalVariable(Values.ReturnKeyWord, value)
        })
    )

    def asr = Procedure(
        "ASR", // name
        List(FormalArg("x", IntegerType), FormalArg("n", IntegerType)), // arguments
        Some(IntegerType), // return type
        List(),
        List(),

        ScalaStmt(env => {
            val value = (env.lookup("x"), env.lookup("n")) match {
                case (Some(IntValue(x)),Some(IntValue(n))) => IntValue(Functions.asr(x,n))
                case _ => ???
            }
            env.setLocalVariable(Values.ReturnKeyWord, value)
        })
    )

    def ror = Procedure(
        "ROR", // name
        List(FormalArg("x", IntegerType), FormalArg("n", IntegerType)), // arguments
        Some(IntegerType), // return type
        List(),
        List(),

        ScalaStmt(env => {
            val value = (env.lookup("x"), env.lookup("n")) match {
                case (Some(IntValue(x)),Some(IntValue(n))) => IntValue(Functions.ror(x,n))
                case _ => ???
            }
            env.setLocalVariable(Values.ReturnKeyWord, value)
        })
    )



    val procedures = Map(
        abs.name -> abs,
        odd.name -> odd
    )
}