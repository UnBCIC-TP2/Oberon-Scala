package br.unb.cic.oberon.external
import com.sun.jna._

trait lib extends Library {

  def abs(n: Int): Int
  def div(numerator: Int, denominator: Int): Int
  def isalnum(c: Int): Int
  def isalpha(c: Int): Int
  def isdigit(c: Int): Int
  def isascii(c: Int): Int
  def isblank(c: Int): Int 
  def islower(c: Int): Int
  def isprint(c: Int): Int 
  def ispunct(c: Int): Int 
  def isspace(c: Int): Int 
  def isupper(c: Int): Int
  def isxdigit(c: Int): Int 
  def rand(): Int
  def srand(seed: Int): Unit
}


class External extends App {

  var _lib:lib = null
  def run():lib = {
    if ( _lib == null ) {
      _lib = Native.loadLibrary("c", classOf[lib]).asInstanceOf[lib]
    }
    _lib
  }
}

