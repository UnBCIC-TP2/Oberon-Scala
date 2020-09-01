package br.unb.cic.oberon.environment

import br.unb.cic.oberon.ast.Procedure

import scala.collection.mutable.Map
import scala.collection.mutable.Stack

/**
 * The environment represents a memory, which
 * could be used for interpreting or for type
 * checking.
 */
class Environment[T] {

  private val global = Map.empty[String, T]
  private val stack = Stack.empty[Map[String, T]]
  private val procedures = Map.empty[String, Procedure]

  def declareGlobal(name: String, value: T) : Unit = global += name -> value

  def declareLocal(name: String, value: T) : Unit = {
    if(stack.size == 0) {
      stack.push(Map.empty[String, T])
    }
    stack.top += name -> value
  }

  def lookup(name: String) : Option[T] = {
    if(!stack.isEmpty && stack.top.contains(name)) Some(stack.top(name))
    else if(global.contains(name)) Some(global(name))
    else None
  }

  def declareProcedure(procedure: Procedure): Unit = procedures(procedure.name) = procedure

  def findProcedure(name: String) : Procedure = procedures(name)

  def push(): Unit = stack.push(Map.empty[String, T])

  def pop(): Unit = stack.pop()
}
