package br.unb.cic.oberon.environment


import br.unb.cic.oberon.ast.{Expression, Procedure, Type, UserType}

import scala.collection.mutable.Map
import scala.collection.mutable.Stack

/**
 * The environment represents a memory region, which
 * could be used for interpreting oberon programs
 * or for type checking oberon programs.
 *
 * We have two kinds of memory: global and stack.
 * All global variables reside in the global are;
 * while local variables reside in the stack.
 *
 * Whenever we call a procedure, we push a memory
 * area (a hash map) into the stack. Whenever we
 * return from a procedure, we pop the stack.
 */
class Environment[T] {

  private val global = Map.empty[String, T]
  private val stack = Stack.empty[Map[String, T]]
  private val procedures = Map.empty[String, Procedure]
  // Temporary change, might be overwritten by another group - G09
  private val userTypes = Map.empty[String, UserType]

  def setGlobalVariable(name: String, value: T) : Unit = global += name -> value

  // Temporary change, might be overwritten by another group - G09
  def addUserType (name: String, value: T) : Unit = userTypes += name -> value

  def setLocalVariable(name: String, value: T) : Unit = {
    if(stack.size == 0) {
      stack.push(Map.empty[String, T])
    }
    stack.top += name -> value
  }

  def setVariable(name: String, value: T) : Unit = {
    if(!stack.isEmpty && stack.top.contains(name)) {
      setLocalVariable(name, value)
    }
    else if(global.contains(name)) {
      setGlobalVariable(name, value)
    }
    else throw new RuntimeException("Variable " + name + " is not defined")
  }

  def lookup(name: String) : Option[T] = {
    if(!stack.isEmpty && stack.top.contains(name)) Some(stack.top(name))
    else if(global.contains(name)) Some(global(name))
    else None
  }

  // Temporary change, might be overwritten by another group - G09
  def lookupType(name: String) : Option[UserType] = {
    if (userTypes.contains(name)) {
      Some(userTypes(name))
    } else None
  }
  // Temporary change, might be overwritten by another group - G09
  def lookupAttributeType(attributeName: List[String], recordTypeName: String) : Option[Type] = {
    if (userTypes.contains(recordTypeName)) {
      val recordType = userTypes(recordTypeName)
      if (recordType.attributes.contains(attributeName.head)) {
        attributeName match {
          case n1 :: n2 :: rest => {
            val recordType = userTypes(recordTypeName)
            recordType.attributes(n1) match {
              case UserType(name,_) => {
                lookupAttributeType(n2::rest, name)
              }
              // If this case is reached, there was a syntax error in the Oberon code.
              case _ => None
            }
          }
          case n1 :: List() => {
            Some(recordType.attributes(n1))
          }
        }
      } else None
    } else None

  }


  def declareProcedure(procedure: Procedure): Unit = procedures(procedure.name) = procedure

  def findProcedure(name: String): Procedure = procedures(name)

  def push(): Unit = stack.push(Map.empty[String, T])

  def pop(): Unit = stack.pop()
}
