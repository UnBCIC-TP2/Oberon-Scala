package br.unb.cic.oberon.environment

import br.unb.cic.oberon.ast.{Location, Procedure, ReferenceToUserDefinedType, Type, UserDefinedType}

import scala.collection.Map

import br.unb.cic.oberon.ast.Expression


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
class FEnvironment(top_loc : Int = 0, 
                  locations : Map[Location, Expression] = Map.empty[Location, Expression],
                  global : Map[String, Location] = Map.empty[String, Location],
                  stack : List[Map[String, Location]] = List.empty[Map[String, Location]],
                  procedures : Map[String, Procedure] = Map.empty[String, Procedure],
                  userDefinedTypes : Map[String, UserDefinedType] = Map.empty[String, UserDefinedType]
                  ) {


  def update(tl : Int = this.top_loc, 
          locs : Map[Location, Expression] = this.locations,
          glbl : Map[String, Location] = this.global,
          stk : List[Map[String, Location]] = this.stack,
          prcdrs : Map[String, Procedure] = this.procedures,
          udt : Map[String, UserDefinedType] = this.userDefinedTypes) = 
          new FEnvironment(tl, locs, glbl, stk, prcdrs, udt)

  def setGlobalVariable(name: String, value: Expression): FEnvironment = {
    this.update(tl = this.top_loc + 1,
                        glbl = this.global + (name -> Location(top_loc + 1)), 
                        locs = this.locations + (Location(top_loc + 1) -> value))
  }

  def addUserDefinedType(userType: UserDefinedType) : FEnvironment = this.update(udt = this.userDefinedTypes + (userDefinedTypeName(userType) -> userType))

  def setParameterReference(name: String, loc: Location): FEnvironment = this.update(stk = this.stack:+Map(name -> loc))

  def setLocalVariable(name: String, value: Expression) : FEnvironment = {
    
    if(this.stack.isEmpty){
      val newStack = this.stack :+ Map(name -> Location(top_loc + 1))
      this.update(tl = this.top_loc + 1,
                  stk = newStack,
                  locs = this.locations + (Location(top_loc + 1) -> value))
    }
    else{
      val newStack = this.stack.init:+this.stack.last + (name -> Location(top_loc + 1))
      this.update(tl = this.top_loc + 1,
                  stk = newStack,
                  locs = this.locations + (Location(top_loc + 1) -> value))
    }
  }

  def setVariable(name: String, value: Expression) : FEnvironment = {
    if(this.stack.nonEmpty && this.stack.last.contains(name)) {
      this.update(locs = (this.locations - (this.stack.last.apply(name)) + (this.stack.last.apply(name) -> value)))
    }
    else if(this.global.contains(name)) {
      this.update(locs = this.locations - (global(name)) + (global(name) -> value))
    }
    else throw new RuntimeException("Variable " + name + " is not defined")
  }

  def pointsTo(name: String): Option[Location] = {
    if(stack.nonEmpty && stack.last.contains(name)) Some(stack.last(name))
    else if(global.contains(name)) Some(global(name))
    else None
  }

  def lookup(name: String) : Option[Expression] = {
    if(stack.nonEmpty && stack.last.contains(name)) Some(locations(stack.last(name)))
    else if(global.contains(name)) Some(locations(global(name)))
    else None
  }

  def baseType(aType: Type) : Option[Type] = aType match {
    case ReferenceToUserDefinedType(name) => lookupUserDefinedType(name).flatMap(udt => baseType(udt.baseType))
    case _ => Some(aType)
  }

  def lookupUserDefinedType(name: String) : Option[UserDefinedType] = userDefinedTypes.get(name)

  def userDefinedTypeName(userDefinedType: UserDefinedType) : String =
    userDefinedType.name

  def findProcedure(name: String): Procedure = procedures(name)
  
  def push(): FEnvironment = this.update(stk = this.stack:+(Map.empty[String, Location]))
  
  def pop(): FEnvironment = this.update(stk = this.stack.dropRight(1))
  
  def declareProcedure(procedure: Procedure): FEnvironment = this.update(prcdrs = this.procedures + (procedure.name -> procedure))
}