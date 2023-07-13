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
class Environment[T](private val top_loc:Int = 0,
                     private val locations: Map[Location, T] = Map.empty[Location, T],
                     private val global: Map[String, Location] = Map.empty[String, Location],
                     private val stack: Stack[Map[String, Location]] = Stack.empty[Map[String, Location]],
                     private val procedures: Map[String, Procedure] = Map.empty[String, Procedure],
                     private val userDefinedTypes: Map[String, UserDefinedType] = Map.empty[String, UserDefinedType]) {



  def setGlobalVariable(name: String, value: T): Environment[T] = {
    //top_loc += 1
    //global += name -> Location(top_loc)
    //val loc = locations + (Location(top_loc), value)
    return new Environment[T](top_loc = this.top_loc+1,
      locations = this.locations+(Location(top_loc)-> value),
      global = this.global+(name -> Location(top_loc)) ,
     procedures = this.procedures,
      userDefinedTypes = this.userDefinedTypes,
      stack = this.stack
    )

  }

  def addUserDefinedType(userType: UserDefinedType) : Environment[T] = {
    //userDefinedTypes += userDefinedTypeName(userType) -> userType
    new Environment[T](top_loc = this.top_loc,
    locations = this.locations,
      global = this.global,
      procedures = this.procedures,
      userDefinedTypes = this.userDefinedTypes+(this.userDefinedTypeName(userType)-> userType),
      stack = this.stack)
  }


  def setParameterReference(name: String, loc: Location): Environment[T] = {
    //stack.top += name -> loc
    val copyStack = stack.clone()
    copyStack.top += name->loc

    new Environment[T](top_loc = this.top_loc,
      locations = this.locations,
      global = this.global,
      procedures = this.procedures,
      userDefinedTypes = this.userDefinedTypes,
      stack = copyStack)
  }

  def setLocalVariable(name: String, value: T) : Environment[T] = {
//    top_loc += 1
//    if(stack.isEmpty) {
//      stack.push(Map.empty[String, Location])
//    }
//  stack.top += name -> Location(top_loc)
//   locations += Location(top_loc) -> value
    val copyStack = stack.clone()
    if(copyStack.isEmpty) {
      copyStack.push(Map.empty[String, Location])
    }
    copyStack.top.addOne(name -> Location(top_loc))
    new Environment[T](top_loc = this.top_loc+1,
        locations = this.locations.addOne(Location(top_loc) -> value),
        global = this.global,
        procedures = this.procedures,
        userDefinedTypes = this.userDefinedTypes,
        stack = copyStack)
  }

  def setVariable(name: String, value: T) : Environment[T] = {
    if(stack.nonEmpty && stack.top.contains(name)) {
      val newlocations = locations.clone()
      newlocations(stack.top(name)) = value
      new Environment[T](top_loc = this.top_loc,
        locations = newlocations,
        global = this.global,
        procedures = this.procedures,
        userDefinedTypes = this.userDefinedTypes,
        stack = this.stack)
    }
    else if(global.contains(name)) {
      val newlocations = locations.clone()
      newlocations(global(name)) = value
      new Environment[T](top_loc = this.top_loc,
        locations = newlocations,
        global = this.global,
        procedures = this.procedures,
        userDefinedTypes = this.userDefinedTypes,
        stack = this.stack)
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

  def declareProcedure(procedure: Procedure): Environment[T] = {
    //Unit = procedures(procedure.name) = procedure
    val copyprocedures = procedures.clone() + (procedure.name -> procedure)
    //copyprocedures(procedure.name) = procedure

      new Environment[T](top_loc = this.top_loc,
        locations = this.locations,
        global = this.global,
        procedures = copyprocedures,
        userDefinedTypes = this.userDefinedTypes,
        stack = this.stack)
  }


  def findProcedure(name: String): Procedure = procedures(name)

  def push(): Environment[T] ={
    var copystack = stack.clone()
    copystack.push (Map.empty[String, Location] )
    new Environment[T](top_loc = this.top_loc,
      locations = this.locations,
      global = this.global,
      procedures = this.procedures,
      userDefinedTypes = this.userDefinedTypes,
      stack = copystack)
  }
  def pop(): Environment[T] = {
    var copystack = stack.clone()
    copystack.pop()
    new Environment[T](top_loc = this.top_loc,
      locations = this.locations,
      global = this.global,
      procedures = this.procedures,
      userDefinedTypes = this.userDefinedTypes,
      stack = copystack)
  }

  def userDefinedTypeName(userDefinedType: UserDefinedType) : String =
    userDefinedType.name

  def allVariables(): collection.Set[String] = global.keySet

  def delVariable(name: String): Environment[T] = {
    if(stack.nonEmpty && stack.top.contains(name)) {
      var copystack = stack.clone()
      copystack.remove(stack.indexOf(name))
      new Environment[T](top_loc = this.top_loc,
        locations = this.locations,
        global = this.global,
        procedures = this.procedures,
        userDefinedTypes = this.userDefinedTypes,
        stack = copystack)

    }
    else if(global.contains(name)) {
      var copyglobal = global.clone()
      copyglobal.remove(name)
      new Environment[T](top_loc = this.top_loc,
        locations = this.locations,
        global = copyglobal,
        procedures = this.procedures,
        userDefinedTypes = this.userDefinedTypes,
        stack = this.stack)
    }
      else throw new RuntimeException("Variable " + name + " is not defined")
  }
//  new Environment[T](top_loc = this.top_loc,
//    locations = this.locations,
//    global = this.global,
//    procedures = this.procedures,
//    userDefinedTypes = this.userDefinedTypes,
  //    stack = this.stack)
}
