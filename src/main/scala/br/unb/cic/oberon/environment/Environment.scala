package br.unb.cic.oberon.environment

import br.unb.cic.oberon.ir.ast.{Location, Procedure, ReferenceToUserDefinedType, Type, UserDefinedType}

import scala.collection.mutable.{Map, Stack}

/**
 * The environment represents a memory region, which
 * could be used for interpreting oberon programs
 * or for type checking oberon programs.
 *
 * We have two kinds of memory: global and stack.
 * All global variables reside in the global area;
 * while local variables reside in the stack.
 *
 * Whenever we call a procedure, we push a memory
 * area (a hash map) into the stack. Whenever we
 * return from a procedure, we pop the stack.
 */
class Environment[T] {

  private var top_loc = 0
  private val locations = Map.empty[Location, T]
  private val global = Map.empty[String, Location]
  private val stack = Stack.empty[Map[String, Location]]
  private val procedures = Map.empty[String, Procedure]
  private val userDefinedTypes = Map.empty[String, UserDefinedType]

  def setGlobalVariable(name: String, value: T): Unit = {
    top_loc += 1
    global += name -> Location(top_loc)
    locations += Location(top_loc) -> value
  }

  def addUserDefinedType(userType: UserDefinedType) : Unit = {
    userDefinedTypes += userDefinedTypeName(userType) -> userType
  }

  def setParameterReference(name: String, loc: Location): Unit = {
    stack.top += name -> loc
  }

  def setLocalVariable(name: String, value: T) : Unit = {
    top_loc += 1
    if(stack.isEmpty) {
      stack.push(Map.empty[String, Location])
    }
    stack.top += name -> Location(top_loc)
    locations += Location(top_loc) -> value
  }

  def setVariable(name: String, value: T) : Unit = {
    if(stack.nonEmpty && stack.top.contains(name)) {
      locations(stack.top(name)) = value
    }
    else if(global.contains(name)) {
      locations(global(name)) = value
    }
    else throw new RuntimeException("Variable " + name + " is not defined")
  }

  def pointsTo(name: String): Option[Location] = {
    if(stack.nonEmpty && stack.top.contains(name)) Some(stack.top(name))
    else if(global.contains(name)) Some(global(name))
    else None
  }

  def lookup(name: String) : Option[T] = {
    if(stack.nonEmpty && stack.top.contains(name)) Some(locations(stack.top(name)))
    else if(global.contains(name)) Some(locations(global(name)))
    else None
  }

  def baseType(aType: Type) : Option[Type] = aType match {
    case ReferenceToUserDefinedType(name) => lookupUserDefinedType(name).flatMap(udt => baseType(udt.baseType))
    case _ => Some(aType)
  }

  def lookupUserDefinedType(name: String) : Option[UserDefinedType] = userDefinedTypes.get(name)

  def declareProcedure(procedure: Procedure): Unit = procedures(procedure.name) = procedure

  def findProcedure(name: String): Procedure = procedures(name)

  def push(): Unit = stack.push(Map.empty[String, Location])

  def pop(): Unit = stack.pop()

  def userDefinedTypeName(userDefinedType: UserDefinedType) : String =
    userDefinedType.name

  def allVariables(): collection.Set[String] = global.keySet

  def delVariable(name: String): Unit = {
    if(stack.nonEmpty && stack.top.contains(name)) stack.remove(stack.indexOf(name))
    else if(global.contains(name)) global.remove(name)
  }
}
