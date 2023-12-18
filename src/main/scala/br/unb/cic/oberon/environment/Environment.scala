package br.unb.cic.oberon.environment

import br.unb.cic.oberon.ir.ast.{ArrayType, ArrayValue, BaseLocation, Expression, Location, NullLocation, NullType, NullValue, Procedure, RecordType, ReferenceToUserDefinedType, Statement, Type, Undef, UserDefinedType, VariableDeclaration}
import org.jline.builtins.Completers.CompletionEnvironment

import scala.collection.mutable.{ListBuffer, Map, Stack}

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
                     private val userDefinedTypes: Map[String, UserDefinedType] = Map.empty[String, UserDefinedType]) {


  def setGlobalVariable(name: String, value: T): Environment[T] = {
    return new Environment[T](top_loc = this.top_loc+1,
      locations = this.locations+(BaseLocation(top_loc)-> value),
      global = this.global+(name -> BaseLocation(top_loc)) ,
      procedures = this.procedures,
      userDefinedTypes = this.userDefinedTypes,
      stack = this.stack
    )
  }

  def addUserDefinedType(userType: UserDefinedType) : Environment[T] = {
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
    copyStack.top.addOne(name -> BaseLocation(top_loc))
    new Environment[T](top_loc = this.top_loc+1,
        locations = this.locations.addOne(BaseLocation(top_loc) -> value),
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

  def userDefinedTypeName(userDefinedType: UserDefinedType): String =
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

  def declareGlobalPointer(pointerName: String, variableType: Type): Environment[T] = {

    var newGlobal = global.clone();

    variableType match {
      case ReferenceToUserDefinedType(name) => {

        var userDefinedType = this.userDefinedTypes(name);

        userDefinedType.baseType match {
          case RecordType(fields) => {
            fields.foreach {
              case VariableDeclaration(name, variableType) => {
                newGlobal += (getNameForRecordField(pointerName, name) -> NullLocation)
              }
              case _ => {
                throw new RuntimeException("Unknown field type");
              }
            };
          } case _ => {
            newGlobal(pointerName) = NullLocation;
          }
        }
      } case _ => {
       newGlobal(pointerName) = NullLocation;
      }
    }

    return new Environment[T](top_loc = this.top_loc,
      locations = this.locations,
      global = newGlobal,
      procedures = this.procedures,
      userDefinedTypes = this.userDefinedTypes,
      stack = this.stack
    )

  }

  def createLocationForGlobalPointer(name: String, value: T): Environment[T] = {

    if(!global.contains(name)) {
      throw new RuntimeException("Variable " + name + " is not defined")
    }

    val newGlobal = global.clone()
    newGlobal(name) = BaseLocation(this.top_loc)

    val newLocations = locations.clone()
    newLocations(BaseLocation(this.top_loc)) = value

    return new Environment[T](top_loc = this.top_loc + 1,
      locations = newLocations,
      global = newGlobal,
      procedures = this.procedures,
      userDefinedTypes = this.userDefinedTypes,
      stack = this.stack
    )

  }

  def setGlobalPointer(name: String, value: Location): Environment[T] = {
    if(!global.contains(name)) {
      throw new RuntimeException("Variable " + name + " is not defined")
    }

    val newGlobal = global.clone()
    newGlobal(name) = value

    new Environment[T](
      top_loc = this.top_loc,
      locations = this.locations,
      global = newGlobal,
      procedures = this.procedures,
      userDefinedTypes = this.userDefinedTypes,
      stack = this.stack
    )

  }

  private def getNameForRecordField(recordName: String, field: String): String = {
    return "$" + recordName + "." + field;
  }

}

