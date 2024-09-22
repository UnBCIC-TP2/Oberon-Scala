package br.unb.cic.oberon.interpreter

import java.io.{ByteArrayOutputStream, OutputStream, PrintStream}
import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.ir.common._
import br.unb.cic.oberon.environment.{Environment, MetaStmt}
import br.unb.cic.oberon.stdlib.StandardLibrary
import br.unb.cic.oberon.util.Values
import br.unb.cic.oberon.visitor.OberonVisitorAdapter
import scala.collection.mutable.ListBuffer
import scala.io.StdIn
import scala.language.{existentials, postfixOps}
import cats.data.State
import State._
import cats.implicits._

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
class Interpreter {
  type IResult[A] = State[Environment[Expression], A]

  type T = Unit

  var exit = false

  var printStream: PrintStream = new PrintStream(System.out)

  def run(module: OberonModule): Environment[Expression] =
    runInterpreter(module).runS(new Environment[Expression]()).value

  def setupStandardLibraries(): IResult[Unit] = for {
    _ <- get[Environment[Expression]]
    lib = new StandardLibrary[Expression]()
    _ <- lib.stdlib.procedures.traverse(declareProcedure)
  } yield ()

def runInterpreter(module: OberonModule): IResult[Unit] = for {
    // set up the global declarations
    _ <- module.userTypes.traverse(declareUserDefinedType)
    _ <- module.constants.traverse(declareConstant)
    _ <- module.variables.traverse(declareVariable)
    _ <- module.procedures.traverse(declareProcedure)

    // execute the statement if it is defined.
    // remember, module.stmt is an Option[Statement].
    _ <- if (module.stmt.isDefined) for {
      _ <- setupStandardLibraries()
      _ <- executeStatement(module.stmt.get)
    } yield ()
    else State[Environment[Expression], Unit] {env => (env, ())}
  } yield ()

  def declareConstant(constant: Constant): IResult[Unit] = for {
    _ <- modify[Environment[Expression]](_.setGlobalVariable(constant.name, constant.exp))
  } yield ()

  def declareVariable(variable: VariableDeclaration): IResult[Unit] = for {
    environment <- get
    _ <- environment.baseType(variable.variableType) match {
      case Some(ArrayType(length, baseType)) => for {_ <- modify[Environment[Expression]](_.setGlobalVariable(variable.name, ArrayValue(ListBuffer.fill(length)(Undef()), ArrayType(length, baseType))))} yield ()
      case _ => for {_ <- modify[Environment[Expression]](_.setGlobalVariable(variable.name, Undef()))} yield ()
    }
  } yield ()

  def declareUserDefinedType(userType: UserDefinedType): IResult[Unit] = for {
    _ <- modify[Environment[Expression]](_.addUserDefinedType(userType))
  } yield ()

  def declareProcedure(procedure: Procedure[Statement]): IResult[Unit] = for {
    _ <- modify[Environment[Expression]](_.declareProcedure(procedure))
  } yield ()



  def executeStatement(stmt: Statement): IResult[Unit] = for {
    // we first check if we have encountered a return stmt.
    // if so, we should not execute any other statement
    // of a sequence of stmts. Whenever we encounter a
    // return stmt, we assign a new local variable
    // "return" as the return value.
    //
    // we also check if exit is true. if this is the case
    // we should also stop the execution of a block of
    // statements within a any kind of repeat statement.

    envt <- get

    _ <- if (exit || envt.lookup(Values.ReturnKeyWord).isDefined) State[Environment[Expression], Unit] {env => (env, ())} else
    // otherwise, we pattern-match on the current stmt.
    stmt match {
      case AssignmentStmt(designator, exp) =>
        designator match {
          case VarAssignment(name) => for {
            expr <- evalExpression(exp)
            _ <- modify[Environment[Expression]](_.setVariable(name, expr))
          } yield ()
          case ArrayAssignment(array, index) => for {_ <- arrayAssignment(array, index, exp)} yield ()
          case RecordAssignment(_, _) => ???
          case PointerAssignment(_) => ???
        }

      case SequenceStmt(stmts) =>
        // as stmts is a List[Statement], we can use traverse_ to execute all statements in order, saving the resulting env in a State monad
        stmts.traverse_(executeStatement)

      case ReadRealStmt(name) => for {
        _ <- modify[Environment[Expression]](_.setVariable(name, RealValue(StdIn.readLine().toFloat)))
      } yield ()

      case ReadIntStmt(name) => for {
        _ <- modify[Environment[Expression]](_.setVariable(name, IntValue(StdIn.readLine().toInt)))
      } yield ()

      case ReadCharStmt(name) => for {
        _ <- modify[Environment[Expression]](_.setVariable(name, CharValue(StdIn.readLine().charAt(0))))
      } yield ()

      case WriteStmt(exp) => for {
        expr <- evalExpression(exp)
      } yield printStream.println(expr)

      case IfElseStmt(condition, thenStmt, elseStmt) => for {
        cond <- evalCondition(condition)
        _ <- if (cond) executeStatement(thenStmt) else if (elseStmt.isDefined) executeStatement(elseStmt.get) else State[Environment[Expression], Unit] {env => (env, ())}
      } yield ()
      
      case WhileStmt(condition, whileStmt) => for {
        cond <- evalCondition(condition)
        _ <- if (cond && !exit) for {
            _ <- executeStatement(whileStmt)
            _ <- executeStatement(stmt)
            } yield () else for {_ <- get[Environment[Expression]]} yield exit = true
      } yield exit = false

      case ForEachStmt(v, exp, stmt) => for {
        valArray <- evalExpression(exp)
        _ <- valArray match {
          case ArrayValue(values, _) => for {
            _ <- modify[Environment[Expression]](_.push())
            _ <- values.toList.traverse(value => for {
              expr <- evalExpression(value)
              _ <- modify[Environment[Expression]](_.setLocalVariable(v, expr))
              _ <- executeStatement(stmt)
            } yield ())
            _ <- modify[Environment[Expression]](_.pop())
          } yield ()
          case _ => throw new RuntimeException("erro.... melhorar")
        }
      } yield ()

      case ExitStmt() =>
        exit = true
        State[Environment[Expression], Unit] {env => (env, ())}

      case ReturnStmt(exp: Expression) => for {
        expr <- evalExpression(exp)
        _ <- setReturnExpression(expr)
      } yield ()

      case MetaStmt(f) => for {
        _ <- executeStatement(f(envt))
      } yield ()

      case ProcedureCallStmt(name, args) => for {
        _ <- callProcedure(name, args)
      } yield ()

      case AssertTrueStmt(exp: Expression) => for {
        assert <- evalCondition(exp)
      } yield if (!assert) throw new Exception("Exception thrown from assert") else ()
    }
  } yield ()


  /*
   * process the ReturnStmt(exp) statement.
   * In this case, we just create a new entry
   * in the local variables, assigning
   * "return" -> exp.
   */
  private def setReturnExpression(exp: Expression): IResult[Unit] = for {
    _ <- modify[Environment[Expression]](_.setLocalVariable(Values.ReturnKeyWord, exp))
  } yield ()

  def callProcedure(name: String, args: List[Expression]): IResult[Unit] = for {
    env <- get[Environment[Expression]]
    procedure = env.findProcedure(name)
    _ <- updateEnvironmentWithProcedureCall(procedure, args)
    _ <- executeStatement(procedure.stmt)
    _ <- modify[Environment[Expression]](_.pop())
  } yield ()


  def updateEnvironmentWithProcedureCall(procedure: Procedure[Statement], args: List[Expression]): IResult[Unit] = for {
      mappedArgs <- procedure.args.zip(args).traverse(pair => pair match {
      case (ParameterByReference(_, _), VarExpression(name2)) => for {env <- get[Environment[Expression]] ; loc: Expression = env.pointsTo(name2).get} yield (pair._1, loc)
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(_, _), exp) => for {expr <- evalExpression(exp)} yield (pair._1, expr)
    })
    _ <- modify[Environment[Expression]](_.push()) // after that, we can "push", to indicate a procedure call.
    _ <- mappedArgs.traverse(pair => pair match {
      case (ParameterByReference(name, _), exp: Location) => for {_ <- modify[Environment[Expression]](_.setParameterReference(name, exp))} yield ()
      case (ParameterByReference(_, _), _) => throw new RuntimeException
      case (ParameterByValue(name, _), exp: Expression) => for {_ <- modify[Environment[Expression]](_.setLocalVariable(name, exp))} yield ()
	  case _ => throw new RuntimeException
    })
    _ <- procedure.constants.traverse(declareConstant)
    _ <- procedure.variables.traverse(declareVariable)
  } yield ()


  def evalCondition(expression: Expression): IResult[Boolean] = for {
    condition <- evalExpression(expression)
  } yield condition.asInstanceOf[BoolValue].value

  def arrayAssignment(baseExp: Expression, indexExp: Expression, exp: Expression): IResult[Unit] = for {
    array <- evalExpression(baseExp)
    index <- evalExpression(indexExp)
    expr <- evalExpression(exp)
  } yield (array, index) match {
      case (ArrayValue(values, _), IntValue(v)) => values(v) = expr
      case _ => throw new RuntimeException
  }

  def updateEnvironmentWithTest(test: Test): IResult[Unit] = for {
    _ <- modify[Environment[Expression]](_.push())

    _ <- test.constants.traverse(c => for {_ <- modify[Environment[Expression]](_.setLocalVariable(c.name, c.exp))} yield ())
    _ <- test.variables.traverse(v => for {_ <- modify[Environment[Expression]](_.setLocalVariable(v.name, Undef()))} yield ())
  } yield ()


  
  /*
   * This method is mostly useful for testing purposes.
   * That is, here we are considering testability a
   * design concern.
   */
  def setGlobalVariable(name: String, exp: Expression): IResult[Unit] = for {
    _ <- modify[Environment[Expression]](_.setGlobalVariable(name, exp))
  } yield ()

  /*
   * the same here.
   */
  def setLocalVariable(name: String, exp: Expression): IResult[Unit] = for {
    _ <- modify[Environment[Expression]](_.setLocalVariable(name, exp))
  } yield ()

  def setTestEnvironment() = {
    printStream = new PrintStream(new NullPrintStream())
  }

  def evalExpression(exp: Expression): IResult[Expression] = exp match {
    case IntValue(v) => pure(IntValue(v))
    case RealValue(v) => pure(RealValue(v))
    case CharValue(v) => pure(CharValue(v))
    case BoolValue(v) => pure(BoolValue(v))
    case StringValue(v) => pure(StringValue(v))
    case NullValue => pure(NullValue)
    case Undef() => pure(Undef())
    case VarExpression(name) => evalVarExpression(name)
    //TODO eval array
    //case ArrayValue(v, t) =>
    case ArraySubscript(a, i) => evalArraySubscript(ArraySubscript(a, i))
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
    case NotExpression(exp) => for { expression <- evalExpression(exp) } yield BoolValue(!expression.asInstanceOf[Value].value.asInstanceOf[Boolean])
    case AndExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] && v2.value.asInstanceOf[Boolean]))
    case OrExpression(left, right) => binExpression(left, right, (v1: Value, v2: Value) => BoolValue(v1.value.asInstanceOf[Boolean] || v2.value.asInstanceOf[Boolean]))
    case FunctionCallExpression(name, args) => evalFunctionCall(name, args)
    // TODO FieldAccessExpression
    // TODO PointerAccessExpression
  }

  def evalVarExpression(name: String): IResult[Expression] = for {
    env <- get[Environment[Expression]]
    variable = env.lookup(name)
  } yield 
    if (variable.isEmpty) throw new NoSuchElementException(f"Variable $name is not defined")
    else variable.get

  def evalArraySubscript(arraySubscript: ArraySubscript): IResult[Expression] = for {
    array <- evalExpression(arraySubscript.arrayBase)
    idx <- evalExpression(arraySubscript.index)
    // nao sei se funciona direito colocar o match na frente do yield
  } yield (array, idx) match {
      case (ArrayValue(values: ListBuffer[Expression], _), IntValue(v)) => values(v)
      case _ => throw new RuntimeException
    }

  def evalFunctionCall(name: String, args: List[Expression]): IResult[Expression] =  for {
    env <- get[Environment[Expression]]
    procedure = env.findProcedure(name)
    _ <- updateEnvironmentWithProcedureCall(procedure, args)
    _ <- executeStatement(procedure.stmt)
    env <- get[Environment[Expression]]
    returnValue = env.lookup(Values.ReturnKeyWord)
    _ <- modify[Environment[Expression]](_.pop())
  } yield returnValue.get

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
  def arithmeticExpression(left: Expression, right: Expression, fn: (Number, Number) => Number): IResult[Expression] = for {
      vl <- evalExpression(left)
      vr <- evalExpression(right)
  } yield fn(vl.asInstanceOf[Number], vr.asInstanceOf[Number])


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
  def modularExpression(left: Expression, right: Expression, fn: (Modular, Modular) => Modular): IResult[Expression] = for {
    vl <- evalExpression(left)
    vr <- evalExpression(right)
  } yield fn(vl.asInstanceOf[Modular], vr.asInstanceOf[Modular])


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
  def binExpression(left: Expression, right: Expression, fn: (Value, Value) => Expression): IResult[Expression] = for {
    v1 <- evalExpression(left)
    v2 <- evalExpression(right)
  } yield fn(v1.asInstanceOf[Value], v2.asInstanceOf[Value])
}

class NullPrintStream extends PrintStream(new NullByteArrayOutputStream) {}

class NullByteArrayOutputStream extends ByteArrayOutputStream {
  override def writeTo(o: OutputStream): Unit = ()
}
