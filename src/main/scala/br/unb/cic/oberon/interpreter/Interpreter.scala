package br.unb.cic.oberon.interpreter

import java.io.{ByteArrayOutputStream, OutputStream, PrintStream}
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.stdlib.StandardLibrary
import br.unb.cic.oberon.util.Values
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import scala.collection.mutable.ListBuffer
import scala.io.StdIn
import scala.language.{existentials, postfixOps}

/**
 * The interpreter visitor first updates the
 * environment with the global constants, variables,
 * and procedures; after that, it visits the
 * main program statement.
 *
 * It uses an additional visitor (EvalExpressionVisitor) to
 * compute the value of the different expressions.
 *
 * We assume the program is well-typed, otherwise,
 * a runtime exception might be thrown.
 */
class Interpreter extends OberonVisitorAdapter {


  type T = Unit

  var exit = false
  val env = new Environment[Expression]()

  var printStream: PrintStream = new PrintStream(System.out)

  def setupStandardLibraries(): Unit = {
    val lib = new StandardLibrary[Expression](env)
    for(p <- lib.stdlib.procedures) {
      env.declareProcedure(p)
    }
  }

  override def visit(module: OberonModule): Unit = {
    // set up the global declarations
    module.userTypes.foreach(userType => userType.accept(this))
    module.constants.foreach(c => c.accept(this))
    module.variables.foreach(v => v.accept(this))
    module.procedures.foreach(p => p.accept(this))


    // execute the statement if it is defined.
    // remember, module.stmt is an Option[Statement].
    if (module.stmt.isDefined) {
      setupStandardLibraries()
      module.stmt.get.accept(this)
    }
  }

  override def visit(constant: Constant): Unit = {
    env.setGlobalVariable(constant.name, constant.exp)
  }

  override def visit(variable: VariableDeclaration): Unit = {
    env.setGlobalVariable(variable.name,getUndefVariableValue(variable.variableType))
  }

  override def visit(userType: UserDefinedType): Unit = {
    env.addUserDefinedType(userType)
  }

  override def visit(procedure: Procedure): Unit = {
    env.declareProcedure(procedure)
  }

  private def getUndefVariableValue(variableType: Type) : Expression = {
    variableType match {
      case ReferenceToUserDefinedType(_) =>
        val Some(baseType) = env.baseType(variableType)
        getUndefVariableValue(baseType)
      case ArrayType(length, baseType) =>
        SimpleArrayValue(ListBuffer.fill(length)(getUndefVariableValue(baseType)))
      case RecordType(variables) =>
        val recordValue = RecordValue(ListBuffer())
        variables.foreach(v => recordValue.value.append(FieldValue(v.name, getUndefVariableValue(v.variableType))))
        recordValue
      case PointerType(variableType) =>
        getUndefVariableValue(variableType)
      case _ =>
        Undef()
    }
  }
  
  def visitArrayAssignment(baseExp: Expression, indexExp: Expression, exp: Expression): Unit = {
    val array = evalExpression(baseExp)
    val index = evalExpression(indexExp)

    (array, index) match {
      case (SimpleArrayValue(value), IntValue(v)) => value(v) = evalExpression(exp)
      case _ => throw new RuntimeException
    }
  }

  override def visit(stmt: Statement): Unit = {
    // we first check if we have encountered a return stmt.
    // if so, we should not execute any other statement
    // of a sequence of stmts. Whenever we encounter a
    // return stmt, we assign a new local variable
    // "return" as the return value.
    //
    // we also check if exit is true. if this is the case
    // we should also stop the execution of a block of
    // statements within a any kind of repeat statement.

    if (exit || env.lookup(Values.ReturnKeyWord).isDefined) {
      return
    }
    // otherwise, we pattern-match on the current stmt.
    stmt match {
      case AssignmentStmt(designator, exp: SimpleArrayValue) =>
        val newValue = evalExpression(exp)
        designator match {
          case VarAssignment(varName) =>
            env.setVariable(varName, newValue)
          case PointerAssignment(pointerName) =>
            env.setVariable(pointerName, newValue)
          case ArrayAssignment(array, index) =>
            val intIndex = evalExpression(index).asInstanceOf[IntValue]
            val (varName, currentVarExpValue, targetIndexStack) = getArrayAssignmentInputs(array, ListBuffer(intIndex.value))
            val newFullValue = getNewSimpleArrayValueFromArrayAssignment(currentVarExpValue.asInstanceOf[SimpleArrayValue], newValue, targetIndexStack)
            env.setVariable(varName, newFullValue)
          case RecordAssignment(record, field) =>
            val (varName, currentVarExpValue, targetFieldStack) = getRecordAssignmentInputs(record, ListBuffer(field))
            val newFullValue = getNewSimpleArrayValueFromRecordAssignment(currentVarExpValue.asInstanceOf[RecordValue], newValue, targetFieldStack)
            env.setVariable(varName, newFullValue)
        }
      
      
      
      case AssignmentStmt(designator, exp) =>
        designator match {
          case ArrayAssignment(array, index) => visitArrayAssignment(array, index, exp)
          //TODO:
          case RecordAssignment(_, _) => ???
          case PointerAssignment(_) => ???
          case VarAssignment(name) => env.setVariable(name, evalExpression(exp))
        }

      case SequenceStmt(stmts) =>
        stmts.foreach(s => s.accept(this))

      case ReadRealStmt(name) =>
        env.setVariable(name, RealValue(StdIn.readLine().toFloat))

      case ReadIntStmt(name) =>
        env.setVariable(name, IntValue(StdIn.readLine().toInt))

      case ReadCharStmt(name) =>
        env.setVariable(name, CharValue(StdIn.readLine().charAt(0)))

      case WriteStmt(exp) =>
        printStream.println(evalExpression(exp))

      case IfElseStmt(condition, thenStmt, elseStmt) =>
        if (evalCondition(condition)) thenStmt.accept(this)
        else if (elseStmt.isDefined) elseStmt.get.accept(this)

      case WhileStmt(condition, whileStmt) =>
        while (evalCondition(condition) && exit == false )
          whileStmt.accept(this)
        exit = false

      case ForEachStmt(v, exp, stmt) =>
         val valArray = evalExpression(exp)
         valArray match {
           case SimpleArrayValue(values) => {
             values.foreach(value => {
                 env.setVariable(v, evalExpression(value))
                 stmt.accept(this)
//               val assignment = AssignmentStmt(VarAssignment(v), value)
//               val stmts = SequenceStmt(List(assignment, stmt))
//               stmts.accept(this)
             })
           }

           case _ => throw new RuntimeException("erro.... melhorar")
         }
      case ExitStmt() =>
        exit = true

      case ReturnStmt(exp: Expression) =>
        setReturnExpression(evalExpression(exp))

      case MetaStmt(f) => f().accept(this)

      case ProcedureCallStmt(name, args) =>
        callProcedure(name, args)
        env.pop()
    }
  }

private def getArrayAssignmentInputs(expression: Expression, targetIndexStack: ListBuffer[Int]): (String, Expression, ListBuffer[Int]) = {
    expression match {
      case ArraySubscript(arrayBase, i) =>
        val intIndex = evalExpression(i).asInstanceOf[IntValue].value
        getArrayAssignmentInputs(arrayBase, targetIndexStack.append(intIndex))
      case VarExpression(name) =>
        val currentVarExpValue = env.lookup(name)
        currentVarExpValue match {
          case Some(value) => (name, value, targetIndexStack)
          case _ => (name, Undef(), targetIndexStack)
        }
    }
  }

  private def getNewSimpleArrayValueFromArrayAssignment(currentVarExpValue: SimpleArrayValue, newValue: Expression, targetIndexStack: ListBuffer[Int]) : Expression = {
    if (targetIndexStack.length > 1) {
      val index = targetIndexStack.last
      targetIndexStack.dropRightInPlace(1)
      currentVarExpValue.value(index) = getNewSimpleArrayValueFromArrayAssignment(currentVarExpValue.value(index).asInstanceOf[SimpleArrayValue], newValue, targetIndexStack)
      currentVarExpValue
    }
    else {
      val index = targetIndexStack.last
      currentVarExpValue.value(index) = newValue
      currentVarExpValue
    }
  }

  private def getRecordAssignmentInputs(expression: Expression, targetFieldStack: ListBuffer[String]) : (String, Expression, ListBuffer[String]) = {
    expression match {
      case FieldAccessExpression(exp, name) => getRecordAssignmentInputs(exp, targetFieldStack.append(name))
      case VarExpression(name) =>
        val currentVarExpValue = env.lookup(name)
        currentVarExpValue match {
          case Some(value) => (name, value, targetFieldStack)
          case _ => (name, Undef(), targetFieldStack)
        }
    }
  }

  private def getNewSimpleArrayValueFromRecordAssignment(currentVarExpValue: RecordValue, newValue: Expression, targetFieldStack: ListBuffer[String]) : Expression = {
    val field = targetFieldStack.last
    val newVarExpValue = RecordValue(ListBuffer())

    if (targetFieldStack.length > 1) {
      targetFieldStack.dropRightInPlace(1)
      for (f <- currentVarExpValue.value) {
        if (f.name == field) {
          newVarExpValue.value.append(FieldValue(f.name, getNewSimpleArrayValueFromRecordAssignment(f.value.asInstanceOf[RecordValue], newValue, targetFieldStack)))
        }
        else newVarExpValue.value.append(f)
      }
    }
    else {
      for (f <- currentVarExpValue.value) {
        if (f.name == field) {
          newVarExpValue.value.append(FieldValue(f.name, newValue))
        }
        else newVarExpValue.value.append(f)
      }
    }
    newVarExpValue
  }

  private def checkIfElseIfStmt(condition: Expression, thenStmt: Statement, listOfElseIf: List[ElseIfStmt], elseStmt: Option[Statement]): Unit = {
    var matched = false
    var i = 0

    if (evalCondition(condition)) thenStmt.accept(this)
    else {
      while (i < listOfElseIf.size && !matched) {
        listOfElseIf(i) match {
          case ElseIfStmt(condition, stmt) => if (evalCondition(condition)) {
            stmt.accept(this)
            matched = true
          }
        }
        i += 1
      }
      if (!matched && elseStmt.isDefined) elseStmt.get.accept(this)
    }
  }

  /*
   * process the ReturnStmt(exp) statement.
   * In this case, we just create a new entry
   * in the local variables, assigning
   * "return" -> exp.
   */
  private def setReturnExpression(exp: Expression): Unit =
    env.setLocalVariable(Values.ReturnKeyWord, exp)

  def callProcedure(name: String, args: List[Expression]): Unit = {
    val procedure = env.findProcedure(name)
    updateEnvironmentWithProcedureCall(procedure, args)
    procedure.stmt.accept(this)
  }

  def updateEnvironmentWithProcedureCall(procedure: Procedure, args: List[Expression]): Unit = {
    val mappedArgs = procedure.args.zip(args).map(pair => pair match {
      case (ParameterByReference(_, _), VarExpression(name2)) => (pair._1, env.pointsTo(name2))
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(_, _), exp) => (pair._1, evalExpression(exp))
    })

    env.push() // after that, we can "push", to indicate a procedure call.

    mappedArgs.foreach(pair => pair match {
      case (ParameterByReference(name, _), Some(location: Location)) => env.setParameterReference(name, location)
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(name, _), exp: Expression) => env.setLocalVariable(name, exp)
    })
    procedure.constants.foreach(c => env.setLocalVariable(c.name, c.exp))
    procedure.variables.foreach(v => env.setLocalVariable(v.name, Undef()))
  }

  def returnProcedure() = {
    env.pop()
  }

  def evalCondition(expression: Expression): Boolean = {
    val evalVisitor = new EvalExpressionVisitor(this)
    expression.accept(evalVisitor).asInstanceOf[BoolValue].value
  }

  def evalExpression(expression: Expression): Expression = {
    val evalVisitor = new EvalExpressionVisitor(this)
    expression.accept(evalVisitor)
  }

  /*
   * This method is mostly useful for testing purposes.
   * That is, here we are considering testability a
   * design concern.
   */
  def setGlobalVariable(name: String, exp: Expression): Unit = {
    env.setGlobalVariable(name, exp)
  }

  /*
   * the same here.
   */
  def setLocalVariable(name: String, exp: Expression): Unit = {
    env.setLocalVariable(name, exp)
  }

  def setTestEnvironment() = {
    printStream = new PrintStream(new NullPrintStream())
  }
}

class EvalExpressionVisitor(val interpreter: Interpreter) extends OberonVisitorAdapter {
  type T = Expression


  override def visit(exp: Expression): Expression = exp match {
    case Brackets(expression) => expression.accept(this)
    case IntValue(v) => IntValue(v)
    case RealValue(v) => RealValue(v)
    case CharValue(v) => CharValue(v)
    case BoolValue(v) => BoolValue(v)
    case StringValue(v) => StringValue(v)
    case NullValue => NullValue
    case Undef() => Undef()
    case VarExpression(name) => interpreter.env.lookup(name).get
    case PointerAccessExpression(name) => interpreter.env.lookup(name).get
    case FieldAccessExpression(exp, name) => visitFieldAccessExpression(exp, ListBuffer(name))
    case SimpleArrayValue(value) =>
      val simpleArrayValue = SimpleArrayValue(ListBuffer())
      value.foreach(v => simpleArrayValue.value.append(v.accept(this)))
      simpleArrayValue
    case ArraySubscript(exp,i) =>
      val intIndex = i.accept(this).asInstanceOf[IntValue].value
      visitArraySubscriptExpression(exp, ListBuffer(intIndex))
    case AddExpression(left, right) => arithmeticExpression(left, right, (v1: Number, v2: Number) => v1+v2)
    case SubExpression(left, right) => arithmeticExpression(left, right, (v1: Number, v2: Number) => v1-v2)
    case MultExpression(left, right) => arithmeticExpression(left, right, (v1: Number, v2: Number) => v1*v2)
    case DivExpression(left, right) => arithmeticExpression(left, right, (v1: Number, v2: Number) => v1/v2)
    case ModExpression(left, right) => modularExpression(left, right, (v1: Modular, v2: Modular) => v1.mod(v2))
    case EQExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 == v2))
    case NEQExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 != v2))
    case GTExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 > v2))
    case LTExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 < v2))
    case GTEExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 >= v2))
    case LTEExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1 <= v2))
    case NotExpression(exp) => BoolValue(!exp.accept(this).asInstanceOf[Value].value.asInstanceOf[Boolean])
    case AndExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] && v2.value.asInstanceOf[Boolean]))
    case OrExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] || v2.value.asInstanceOf[Boolean]))
    case FunctionCallExpression(name, args) => {
      val exp = visitFunctionCall(name, args)
      exp
    }
  }

  def visitFieldAccessExpression(expression: Expression, fieldStack: ListBuffer[String]): Expression = {
    expression match {
      case FieldAccessExpression(exp, name) =>
        val field = fieldStack.last
        val expValue = visitFieldAccessExpression(exp, fieldStack.append(name))
        var newExpValue = expValue
        for (f <- expValue.asInstanceOf[RecordValue].value if f.name == field) newExpValue = f.value
        newExpValue
      case VarExpression(_) =>
        val expValue = expression.accept(this)
        var newExpValue = expValue
        while (!fieldStack.isEmpty) {
          val field = fieldStack.last
          fieldStack.dropRightInPlace(1)
          for (f <- expValue.asInstanceOf[RecordValue].value if f.name == field) newExpValue = f.value
        }
        newExpValue
    }
  }

  def visitArraySubscriptExpression(expression: Expression, indexStack: ListBuffer[Int]): Expression = {
    expression match {
      case ArraySubscript(arrayBase, i) =>
        val intIndex = i.accept(this).asInstanceOf[IntValue].value
        visitArraySubscriptExpression(arrayBase, indexStack.append(intIndex))
      case VarExpression(_) =>
        var expValue = expression.accept(this)
        while (!indexStack.isEmpty) {
          val index = indexStack.last
          indexStack.dropRightInPlace(1)
          expValue = expValue.asInstanceOf[SimpleArrayValue].value(index)
        }
        expValue
      case FieldAccessExpression(_,_) =>
        var fieldValue = expression.accept(this)
        var expValue = fieldValue
        while (!indexStack.isEmpty) {
          expValue = fieldValue.asInstanceOf[SimpleArrayValue].value(indexStack.last)
          fieldValue = expValue
          indexStack.dropRightInPlace(1)
        }
        expValue
    }
  }
  
  def visitFunctionCall(name: String, args: List[Expression]): Expression = {
    interpreter.callProcedure(name, args)
    val returnValue = interpreter.env.lookup(Values.ReturnKeyWord)
    interpreter.returnProcedure()
    assert(returnValue.isDefined) // a function call must set a local variable with the "return" expression
    returnValue.get
  }

  /**
   * Eval an arithmetic expression on Numbers
   *
   * @param left  the left expression
   * @param right the right expression
   * @param op    a function representing the operator
   * @return the application of the operator after
   *         evaluating left and right to reduce them to
   *         numbers.
   */
  def arithmeticExpression(left: Expression, right: Expression, fn: (Number, Number) => Number): Expression = {
    val vl = left.accept(this).asInstanceOf[Number]
    val vr = right.accept(this).asInstanceOf[Number]

    fn(vl, vr)
  }

  /**
   * Eval an modular expression on Numbers
   *
   * @param left  the left expression
   * @param right the right expression
   * @param op    a function representing the operator
   * @return the application of the operator after
   *         evaluating left and right to reduce them to
   *         numbers.
   */
  def modularExpression(left: Expression, right: Expression, fn: (Modular, Modular) => Modular): Expression = {
    val vl = left.accept(this).asInstanceOf[Modular]
    val vr = right.accept(this).asInstanceOf[Modular]

    fn(vl, vr)
  }

  /**
   * Eval a binary expression on values.
   *
   * @param left  the left expression
   * @param right the right expression
   * @param fn    a function that constructs an expression. Here we
   *              are using a high-order function. We assign to
   *              the "result" visitor attribute the value we compute
   *              after applying this function.
   */
  def binExpression(left: Expression, right: Expression, fn: (Value, Value) => Expression): Expression = {
    val v1 = left.accept(this).asInstanceOf[Value]
    val v2 = right.accept(this).asInstanceOf[Value]

    fn(v1, v2)
  }
}

class NullPrintStream extends PrintStream(new NullByteArrayOutputStream) {}

class NullByteArrayOutputStream extends ByteArrayOutputStream {
  override def writeTo(o: OutputStream) {}
}
