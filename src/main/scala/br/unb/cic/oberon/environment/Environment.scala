package br.unb.cic.oberon.environment

import br.unb.cic.oberon.ast.{ArrayType, Expression, Procedure, RecordType, Type, UserDefinedType}

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
  private val arrayTypes = Map.empty[String, UserDefinedType]
  private val recordTypes = Map.empty[String, Map[String, Type]]

  def setGlobalVariable(name: String, value: T) : Unit = global += name -> value

  def addUserType (userType: UserDefinedType) : Unit = {
    userType match {
      case RecordType(name, attributes) => {
        val attributeMap = Map.empty[String, Type]
        attributes.foreach(attribute => {
          attributeMap += attribute.name -> attribute.variableType
        })
        recordTypes += name -> attributeMap
      }
      case ArrayType(name, _, _) => arrayTypes += name -> userType
    }
  }

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

  def lookupRecordType(name: String) : Option[Map[String, Type]] = {
    recordTypes.get(name)
  }

  def declareProcedure(procedure: Procedure): Unit = procedures(procedure.name) = procedure

  def findProcedure(name: String): Procedure = procedures(name)

  def push(): Unit = stack.push(Map.empty[String, T])

  def pop(): Unit = stack.pop()

  def userDefinedTypeName(userDefinedType: UserDefinedType) : String =
     userDefinedType match {
         case ArrayType(name, _, _) => name
         case RecordType(name, _) => name
     }
}
