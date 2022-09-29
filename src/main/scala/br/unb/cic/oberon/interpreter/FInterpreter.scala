package br.unb.cic.oberon.interpreter

import java.io.{ByteArrayOutputStream, OutputStream, PrintStream}
import br.unb.cic.oberon.ast._
import br.unb.cic.oberon.environment.FEnvironment
import br.unb.cic.oberon.stdlib.FStandardLibrary
import br.unb.cic.oberon.util.Values
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import scala.collection.mutable.ListBuffer
import scala.io.StdIn
import scala.language.{existentials, postfixOps}

/**
 * The interpreter visitor first updates the
 * Fenvironment with the global constants, variables,
 * and procedures; after that, it visits the
 * main program statement.
 *
 * It uses an additional visitor (EvalExpressionVisitor) to
 * compute the value of the different expressions.
 *
 * We assume the program is well-typed, otherwise,
 * a runtime exception might be thrown.
 */

class FInterpreter {

  var exit = false

  var printStream: PrintStream = new PrintStream(System.out)

  // Set declarations according to their types.
  def decomposer(module: OberonModule): (FEnvironment, Option[Statement]) = {
    
    def setupStandardLibraries(env: FEnvironment): FEnvironment = {
      var newEnv = env
      val lib = new FStandardLibrary[Expression](env)
      for(p <- lib.stdlib.procedures) {
        newEnv = newEnv.declareProcedure(p)
      }
      newEnv
    }

    def setDeclarations(declaration: AnyRef, env: FEnvironment): FEnvironment = declaration match {
      case module: OberonModule => {
        // set up the global declarations
        var newEnv = env
        module.userTypes.foreach(userType => newEnv = setDeclarations(userType, newEnv))
        module.constants.foreach(c => newEnv = setDeclarations(c, newEnv))
        module.variables.foreach(v => newEnv = setDeclarations(v, newEnv))
        module.procedures.foreach(p => newEnv = setDeclarations(p, newEnv))
        setupStandardLibraries(newEnv)
      }
      
      case constant: Constant => env.setGlobalVariable(constant.name, constant.exp)
      
      case variable: VariableDeclaration => {
        env.baseType(variable.variableType) match {
          case Some(ArrayType(length, baseType)) => env.setGlobalVariable(variable.name, ArrayValue(ListBuffer.fill(length)(Undef()), ArrayType(length, baseType)))
          case _ => env.setGlobalVariable(variable.name, Undef())
        }
      }
      
      case userType: UserDefinedType => env.addUserDefinedType(userType)

      case procedure: Procedure => env.declareProcedure(procedure)
    }
    
    def getStmt(mod: OberonModule) = mod.stmt
    
    return (setDeclarations(module, new FEnvironment), getStmt(module))
    
  }
  
  
  def interpret(statement: Statement, env: FEnvironment): FEnvironment = {
    // we first check if we have encountered a return stmt.
    // if so, we should not execute any other statement
    // of a sequence of stmts. Whenever we encounter a
    // return stmt, we assign a new local variable
    // "return" as the return value.
    //
    // we also check if exit is true. if this is the case
    // we should also stop the execution of a block of
    // statements within a any kind of repeat statement.

    def interpretArrayAssignment(baseExp: Expression, indexExp: Expression, exp: Expression, environ: FEnvironment): Unit = {
      val array = evalExpression(baseExp, environ)
      val index = evalExpression(indexExp, environ)
      
      (array, index) match {
        case (ArrayValue(values, _), IntValue(v)) => values(v) = evalExpression(exp, environ)
        case _ => throw new RuntimeException
      }
    }
    
    if (exit || env.lookup(Values.ReturnKeyWord).isDefined) {
      return env
    }
    // otherwise, we pattern-match on the current stmt.
    
    statement match {
      case AssignmentStmt(designator, exp) =>
        designator match {
          case ArrayAssignment(array, index) => {
            // val newEnv = env
            interpretArrayAssignment(array, index, exp, env)
            return env
          }
          //TODO:
          case RecordAssignment(_, _) => ???
          case PointerAssignment(_) => ???
          case VarAssignment(name) => env.setVariable(name, evalExpression(exp, env))
          
        }

      case SequenceStmt(stmts) => {
        var newEnv = env
        stmts.foreach(s => newEnv = interpret(s, newEnv))
        return newEnv
      }

      case ReadRealStmt(name) => env.setVariable(name, RealValue(StdIn.readLine().toFloat))

      case ReadIntStmt(name) => env.setVariable(name, IntValue(StdIn.readLine().toInt))

      case ReadCharStmt(name) => env.setVariable(name, CharValue(StdIn.readLine().charAt(0)))

      case WriteStmt(exp) => {
        printStream.println(evalExpression(exp, env))
        env
      }

      case IfElseStmt(condition, thenStmt, elseStmt) =>
        var newEnv = env
        if (evalCondition(condition, newEnv)) newEnv = interpret(thenStmt, newEnv)
        else if (elseStmt.isDefined) newEnv = interpret(elseStmt.get, newEnv)
        return newEnv

      case WhileStmt(condition, whileStmt) => {
        var newEnv = env
        while (evalCondition(condition, newEnv) && exit == false )
          newEnv = interpret(whileStmt, newEnv)
        exit = false
        return newEnv
      }

      case ForEachStmt(v, exp, stmt) => {
        var newEnv = env
        val valArray = evalExpression(exp, newEnv)
        valArray match {
          case ArrayValue(values, _) => {
            values.foreach(value => {
                newEnv.setVariable(v, evalExpression(value, newEnv))
                newEnv = interpret(stmt, newEnv)
              // val assignment = AssignmentStmt(VarAssignment(v), value)
              // val stmts = SequenceStmt(List(assignment, stmt))
              // stmts.accept(this)
            })
            return newEnv
          }

          case _ => throw new RuntimeException("erro.... melhorar")
        }

      }
      case ExitStmt() => {
        exit = true
        return env
      }

      case ReturnStmt(exp: Expression) => {
        val newEnv = setReturnExpression(evalExpression(exp, env), env)
        return newEnv
      }

      case MetaStmt(f) => interpret(f(), env)

      case ProcedureCallStmt(name, args) => {
        val newEnv = env
        callProcedure(name, args, newEnv)
        newEnv.pop()
        return newEnv
      }
    }
  }

  private def checkIfElseIfStmt(condition: Expression, thenStmt: Statement, listOfElseIf: List[ElseIfStmt], elseStmt: Option[Statement], oldEnv: FEnvironment): FEnvironment = {
    var env = oldEnv
    var matched = false
    var i = 0

    if (evalCondition(condition, oldEnv)) env = interpret(thenStmt, env)
    else {
      while (i < listOfElseIf.size && !matched) {
        listOfElseIf(i) match {
          case ElseIfStmt(condition, stmt) => if (evalCondition(condition, env)) {
            env = interpret(stmt, env)
            matched = true
          }
        }
        i += 1
      }
      if (!matched && elseStmt.isDefined) env = interpret(elseStmt.get, env)
    }

    return env

  }

  /*
   * process the ReturnStmt(exp) statement.
   * In this case, we just create a new entry
   * in the local variables, assigning
   * "return" -> exp.
   */
  private def setReturnExpression(exp: Expression, oldEnv: FEnvironment): FEnvironment = {
    val env = oldEnv
    env.setLocalVariable(Values.ReturnKeyWord, exp)
    return env
  }

  def callProcedure(name: String, args: List[Expression], env: FEnvironment): FEnvironment = {
    val procedure = env.findProcedure(name)
    val newEnv = updateEnvironmentWithProcedureCall(procedure, args, env)
    return interpret(procedure.stmt, newEnv)
  }

  def updateEnvironmentWithProcedureCall(procedure: Procedure, args: List[Expression], oldEnv: FEnvironment): FEnvironment = {
    var env = oldEnv
    val mappedArgs = procedure.args.zip(args).map(pair => pair match {
      case (ParameterByReference(_, _), VarExpression(name2)) => (pair._1, env.pointsTo(name2))
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(_, _), exp) => (pair._1, evalExpression(exp, env))
    })

    env.push() // after that, we can "push", to indicate a procedure call.

    mappedArgs.foreach(pair => pair match {
      case (ParameterByReference(name, _), Some(location: Location)) => env = env.setParameterReference(name, location)
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(name, _), exp: Expression) => env = env.setLocalVariable(name, exp)
    })
    procedure.constants.foreach(c => env = env.setLocalVariable(c.name, c.exp))
    procedure.variables.foreach(v => env = env.setLocalVariable(v.name, Undef()))

    return env

  }

  def returnProcedure(env: FEnvironment): FEnvironment = env.pop()

  def evalCondition(expression: Expression, env: FEnvironment): Boolean = fEval(Right(expression), env) match {
    case Left(error) => throw new RuntimeException(error)
    case Right(exp) => exp.asInstanceOf[BoolValue].value
  }

  def evalExpression(expression: Expression, env: FEnvironment): Expression = fEval(Right(expression), env) match {
    case Left(error) => throw new RuntimeException(error)
    case Right(exp) => exp
  }

  /*
   * These methods used to be useful for testing purposes.
   */
  // def setGlobalVariable(name: String, exp: Expression, oldEnv: FEnvironment): FEnvironment = {
  //   val env = oldEnv
  //   env.setGlobalVariable(name, exp)
  //   return env
  // }
  // def setLocalVariable(name: String, exp: Expression, oldEnv: FEnvironment): FEnvironment = {
  //   val env = oldEnv
  //   env.setLocalVariable(name, exp)
  //   return env
  // }

  def setTestEnvironment() = {
    printStream = new PrintStream(new NullPrintStream())
  }


  def fEval(expr : Either[String, Expression], env : FEnvironment) : Either[String, Expression] = expr match {
    case Left(error) => Left(error)
    case Right(exp) => exp match{
      case Brackets(expression) => fEval(Right(expression), env)
      case IntValue(v) => Right(IntValue(v))
      case RealValue(v) => Right(RealValue(v))
      case CharValue(v) => Right(CharValue(v))
      case BoolValue(v) => Right(BoolValue(v))
      case StringValue(v) => Right(StringValue(v))
      case NullValue => Right(NullValue)
      case Undef() => Right(Undef())
      case VarExpression(name) => { 
        if(env.lookup(name).isDefined) 
          Right(env.lookup(name).get)
        else
          Left(s"Variable ${name} not defined.")
      }
      case ArraySubscript(a, i) => evalArraySubscriptExpression(ArraySubscript(a, i), env)
      case AddExpression(left, right) => arithmeticExpression(left, right, false, env , (v1: Number, v2: Number) => v1+v2)
      case SubExpression(left, right) => arithmeticExpression(left, right, false, env, (v1: Number, v2: Number) => v1-v2)
      case MultExpression(left, right) => arithmeticExpression(left, right, false, env, (v1: Number, v2: Number) => v1*v2)
      case DivExpression(left, right) => arithmeticExpression(left, right, true, env, (v1: Number, v2: Number) => v1/v2)
      case ModExpression(left, right) => modularExpression(left, right, env, (v1: Modular, v2: Modular) => v1.mod(v2))
      case EQExpression(left, right) => binExpression(left, right, env, (v1: Value, v2: Value) => BoolValue(v1 == v2))
      case NEQExpression(left, right) => binExpression(left, right, env, (v1: Value, v2: Value) => BoolValue(v1 != v2))
      case GTExpression(left, right) => binExpression(left, right, env, (v1: Value, v2: Value) => BoolValue(v1 > v2))
      case LTExpression(left, right) => binExpression(left, right, env, (v1: Value, v2: Value) => BoolValue(v1 < v2))
      case GTEExpression(left, right) => binExpression(left, right, env, (v1: Value, v2: Value) => BoolValue(v1 >= v2))
      case LTEExpression(left, right) => binExpression(left, right, env, (v1: Value, v2: Value) => BoolValue(v1 <= v2))
      case NotExpression(exp) => fEval(Right(exp), env) match {
        case Left(error) => Left(error)
        case Right(bval) => Right(BoolValue(!bval.asInstanceOf[BoolValue].value))
      }
      case AndExpression(left, right) => binExpression(left, right, env, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] && v2.value.asInstanceOf[Boolean]))
      case OrExpression(left, right) => binExpression(left, right, env, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] || v2.value.asInstanceOf[Boolean]))
      case FunctionCallExpression(name, args) => evalFunctionCall(name, args, env)

        //TODO FieldAccessExpression
        //TODO PointerAccessExpression
    }
  }

  def evalArraySubscriptExpression(arraySubscript: ArraySubscript, env : FEnvironment): Either[String, Expression] = {
      
      (fEval(Right(arraySubscript.arrayBase), env), fEval(Right(arraySubscript.index), env)) match {
        case (Left(error), _) => Left(error)
        case (_, Left(error)) => Left(error)
        case (Right(ArrayValue(values: ListBuffer[Expression], _)), Right(IntValue(v))) => Right(values(v))
        case _ => Left(s"Failed to access array at position ${arraySubscript.index}.")
      }
  }

  //maybe check for errors
  def evalFunctionCall(name: String, args: List[Expression], env : FEnvironment): Either[String, Expression] = {
      callProcedure(name, args, env)
      val returnValue = env.lookup(Values.ReturnKeyWord)
      returnProcedure(env)
      assert(returnValue.isDefined) // a function call must set a local variable with the "return" expression
      Right(returnValue.get)
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
  def arithmeticExpression(left: Expression, right: Expression, division: Boolean, env: FEnvironment, fn: (Number, Number) => Number): Either[String, Expression] = {
    val leftOperand = fEval(Right(left), env)
    val rightOperand = fEval(Right(right), env)
    (leftOperand, rightOperand) match {
      case (Right(l), Right(r)) => if(!(division && r.asInstanceOf[Number].asInstanceOf[IntValue].value == 0))
          Right(fn(l.asInstanceOf[Number], r.asInstanceOf[Number]))
        else
          Left("Division by zero.")
      case (Left(err), _) => Left(err)
      case (_, Left(err)) => Left(err)
      case (_, _) => Left("Super helpful error message.")
    }
  
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
  def modularExpression(left: Expression, right: Expression, env : FEnvironment, fn: (Modular, Modular) => Modular): Either[String, Expression] = { 
    val dividend = fEval(Right(left), env)
    val divider = fEval(Right(right), env)
    (dividend, divider) match {
      case (Right(l), Right(r)) => if(r.asInstanceOf[Number].asInstanceOf[IntValue].value != 0)
          Right(fn(l.asInstanceOf[Modular], r.asInstanceOf[Modular]))
        else
          Left("Division by zero.")
      case (Left(err), _) => Left(err)
      case (_, Left(err)) => Left(err)
      case (_, _) => Left("Super helpful error message.")
    }
    
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
  def binExpression(left: Expression, right: Expression, env : FEnvironment, fn: (Value, Value) => Expression): Either[String, Expression] = {
    val leftOperand = fEval(Right(left), env)
    val rightOperand = fEval(Right(right), env)
    (leftOperand, rightOperand) match {
      case (Right(l), Right(r)) => Right(fn(l.asInstanceOf[Value], r.asInstanceOf[Value]))
      case (Left(err), _) => Left(err)
      case (_, Left(err)) => Left(err)
      case (_, _) => Left("Super helpful error message.")
    }

  }

}

// class NullPrintStream extends PrintStream(new NullByteArrayOutputStream) {}

// class NullByteArrayOutputStream extends ByteArrayOutputStream {
//   override def writeTo(o: OutputStream) = {}
// }
