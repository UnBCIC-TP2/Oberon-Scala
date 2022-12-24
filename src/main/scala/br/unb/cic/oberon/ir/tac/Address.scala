package br.unb.cic.oberon.ir.tac

import br.unb.cic.oberon.ir.ast.{Type}

class Address(t: Type){}

class Name(id: String, t:Type) extends Address(t){}

class Constant(value: String, t: Type) extends Address(t){}

class Temporary(t: Type) extends Address(t){
  import Temporary._
  val number = Temporary.counter
  Temporary.counter += 1
}

object Temporary {var counter = 0}
