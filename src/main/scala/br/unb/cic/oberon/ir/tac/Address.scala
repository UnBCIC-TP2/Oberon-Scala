package br.unb.cic.oberon.ir.tac

import br.unb.cic.oberon.ir.common.{Type}

class Address(t: Type){}

case class Name(id: String, t:Type) extends Address(t){}

case class Constant(value: String, t: Type) extends Address(t){}

case class Temporary(t: Type, num: Int = 0, manual: Boolean = false) extends Address(t){
  //num e manual somente para testes
  import Temporary._
  var number = num
  if (!manual) {
    number = Temporary.counter
    Temporary.counter += 1
  }


  def canEqual(a: Any): Boolean = a.isInstanceOf[Temporary]

  override def equals(that: Any): Boolean = {
    that match {
      case that: Temporary =>
        that.canEqual(this) &&
          this.number == that.number

      case _ => false
    }
  }
}

object Temporary {
  var counter = 0

  def reset(): Unit = {counter = 0}//somente para testes
}