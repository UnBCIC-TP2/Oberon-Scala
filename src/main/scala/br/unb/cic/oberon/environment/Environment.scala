package br.unb.cic.oberon.environment

import br.unb.cic.oberon.ir.ast.{Expression, Location, Procedure, ReferenceToUserDefinedType, Statement, Type, Test, UserDefinedType}
import org.jline.builtins.Completers.CompletionEnvironment

import scala.collection.mutable.{Map, Stack}

case class MetaStmt(f: Environment[Expression] => Statement) extends Statement

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
                     private val tests: Map[String, Test] = Map.empty[String, Test],
                     private val userDefinedTypes: Map[String, UserDefinedType] = Map.empty[String, UserDefinedType]) {



  def setGlobalVariable(name: String, value: T): Environment[T] = {
    return new Environment[T](top_loc = this.top_loc+1,
      locations = this.locations+(Location(top_loc)-> value),
      global = this.global+(name -> Location(top_loc)) ,
      procedures = this.procedures,
      tests = this.tests,
      userDefinedTypes = this.userDefinedTypes,
      stack = this.stack
    )

  }

  def addUserDefinedType(userType: UserDefinedType) : Environment[T] = {
    new Environment[T](top_loc = this.top_loc,
      locations = this.locations,
      global = this.global,
      procedures = this.procedures,
      tests = this.tests,
      userDefinedTypes = this.userDefinedTypes+(this.userDefinedTypeName(userType)-> userType),
      stack = this.stack)
  }


  def setParameterReference(name: String, loc: Location): Environment[T] = {
    val copyStack = stack.clone()
    copyStack.top += name->loc

    new Environment[T](top_loc = this.top_loc,
      locations = this.locations,
      global = this.global,
      procedures = this.procedures,
      tests = this.tests,
      userDefinedTypes = this.userDefinedTypes,
      stack = copyStack)
  }

  def setLocalVariable(name: String, value: T) : Environment[T] = {
    val copyStack = stack.clone()
    if(copyStack.isEmpty) {
      copyStack.push(Map.empty[String, Location])
    }
    copyStack.top.addOne(name -> Location(top_loc))
    new Environment[T](top_loc = this.top_loc+1,
      locations = this.locations.addOne(Location(top_loc) -> value),
      global = this.global,
      procedures = this.procedures,
      tests = this.tests,
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
        tests = this.tests,
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
        tests = this.tests,
        userDefinedTypes = this.userDefinedTypes,
        stack = this.stack)
    }
    else throw new RuntimeException("Variable " + name + " is not defined")
  }

  def pointsTo(name: String): Option[Location] = {
    if (stack.nonEmpty && stack.top.contains(name)) Some(stack.top(name))
    else if (global.contains(name)) Some(global(name))
    else None
  }

  def lookup(name: String): Option[T] = {
    if (stack.nonEmpty && stack.top.contains(name))
      Some(locations(stack.top(name)))
    else if (global.contains(name)) Some(locations(global(name)))
    else None
  }

  def baseType(aType: Type): Option[Type] = aType match {
    case ReferenceToUserDefinedType(name) =>
      lookupUserDefinedType(name).flatMap(udt => baseType(udt.baseType))
    case _ => Some(aType)
  }

  def lookupUserDefinedType(name: String): Option[UserDefinedType] =
    userDefinedTypes.get(name)

  def declareProcedure(procedure: Procedure): Environment[T] = {
    val copyprocedures = procedures.clone() + (procedure.name -> procedure)

    new Environment[T](top_loc = this.top_loc,
      locations = this.locations,
      global = this.global,
      procedures = copyprocedures,
      tests = this.tests,
      userDefinedTypes = this.userDefinedTypes,
      stack = this.stack)
  }

  def push(): Environment[T] ={
    var copystack = stack.clone()
    copystack.push (Map.empty[String, Location] )
    new Environment[T](top_loc = this.top_loc,
      locations = this.locations,
      global = this.global,
      procedures = this.procedures,
      tests = this.tests,
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
      tests = this.tests,
      userDefinedTypes = this.userDefinedTypes,
      stack = copystack)
  }

  def userDefinedTypeName(userDefinedType: UserDefinedType): String =
    userDefinedType.name

  def declareTest(test: Test): Environment[T] = {
    val copytests = tests.clone() + (test.name -> test)

    new Environment[T](top_loc = this.top_loc,
      locations = this.locations,
      global = this.global,
      procedures = this.procedures,
      tests = copytests,
      userDefinedTypes = this.userDefinedTypes,
      stack = this.stack)
  }


  def findProcedure(name: String): Procedure = procedures(name)

  def findTest(name: String): Test = tests(name)

  def allVariables(): collection.Set[String] = global.keySet

  def delVariable(name: String): Environment[T] = {
    if(stack.nonEmpty && stack.top.contains(name)) {
      var copystack = stack.clone()
      copystack.remove(stack.indexOf(name))
      new Environment[T](top_loc = this.top_loc,
        locations = this.locations,
        global = this.global,
        procedures = this.procedures,
        tests = this.tests,
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
        tests = this.tests,
        userDefinedTypes = this.userDefinedTypes,
        stack = this.stack)
    }
    else throw new RuntimeException("Variable " + name + " is not defined")
  }

  def dumpAttributes(): Unit = {
    println("top_loc: " + top_loc)
    println("locations:")
    locations.foreach { case (location, value) =>
      println(s"  $location: $value")
    }

    println("global:")
    global.foreach { case (name, loc) =>
      println(s"  $name: $loc")
    }

    println("stack:")
    stack.zipWithIndex.foreach { case (frame, index) =>
      println(s"  Frame $index:")
      frame.foreach { case (name, loc) =>
        println(s"    $name: $loc")
      }
    }

    println("procedures:")
    procedures.foreach { case (name, procedure) =>
      println(s"  $name:")
      //procedure.variables.foreach { case (variableName, loc) =>
      //  println(s"    $variableName: $loc")
      //}
    }

    println("tests:")
    tests.foreach { case (name, test) =>
      println(s"  $name: $test")
    }

    println("userDefinedTypes:")
    userDefinedTypes.foreach { case (typeName, userType) =>
      println(s"  $typeName: $userType")
    }
  }


}
