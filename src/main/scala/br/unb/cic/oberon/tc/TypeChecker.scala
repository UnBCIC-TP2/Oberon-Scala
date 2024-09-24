package br.unb.cic.oberon.tc

import br.unb.cic.oberon.tc.StateOrErrorMonad._

import br.unb.cic.oberon.ir.ast._
import br.unb.cic.oberon.environment.Environment
import br.unb.cic.oberon.visitor.OberonVisitorAdapter

import cats.data.State
import cats.data.StateT

object TypeChecker {
  val comparableTypes = List(IntegerType, RealType)
  val numericTypes = List(IntegerType, RealType)

  def checkExpression(exp: Expression): StateOrError[Type] = {
    exp match {
      case IntValue(_)         => pure(IntegerType)
      case RealValue(_)        => pure(RealType)
      case CharValue(_)        => pure(CharacterType)
      case BoolValue(_)        => pure(BooleanType)
      case StringValue(_)      => pure(StringType)
      case Location(_)         => pure(LocationType)
      case NullValue           => pure(NullType)
      case Undef()             => assertError("Undefined type.")
      case VarExpression(name) => lookupVariable(name)
      case EQExpression(l, r) =>
        checkOperation(l, r, comparableTypes, BooleanType)
      case NEQExpression(l, r) =>
        checkOperation(l, r, comparableTypes, BooleanType)
      case GTExpression(l, r) =>
        checkOperation(l, r, comparableTypes, BooleanType)
      case LTExpression(l, r) =>
        checkOperation(l, r, comparableTypes, BooleanType)
      case GTEExpression(l, r) =>
        checkOperation(l, r, comparableTypes, BooleanType)
      case LTEExpression(l, r) =>
        checkOperation(l, r, comparableTypes, BooleanType)
      case AddExpression(l, r)  => checkOperationKeepType(l, r, numericTypes)
      case SubExpression(l, r)  => checkOperationKeepType(l, r, numericTypes)
      case MultExpression(l, r) => checkOperationKeepType(l, r, numericTypes)
      case DivExpression(l, r)  => checkOperationKeepType(l, r, numericTypes)
      case ModExpression(l, r)  => checkOperationKeepType(l, r, List(IntegerType))
      case AndExpression(l, r) =>
        checkOperation(l, r, List(BooleanType), BooleanType)
      case OrExpression(l, r) =>
        checkOperation(l, r, List(BooleanType), BooleanType)
      case NotExpression(exp) =>
        for {
          t <- checkExpression(exp)
          r <- enforceType(t, BooleanType)
        } yield r
      case Brackets(exp) => checkExpression(exp)
      case FunctionCallExpression(name: String, args: List[Expression]) => {
        for {
          p <- lookupProcedure(name)
          ts <- checkExpressions(args)
          _ <-
            if (p.args.map(fa => fa.argumentType) == ts) { pure(()) }
            else {
              assertError(s"Wrong type for argument of function ${name}.")
            }
          r <- p.returnType match {
            case None    => pure(UndefinedType)
            case Some(r) => pure(r)
          }
        } yield r
      }
      case ArrayValue(values, arrayType) =>
        for {
          ts <- checkExpressions(values.toList)
          _ <- enforceTypes(ts, arrayType.baseType)
        } yield arrayType
      case ArraySubscript(array: Expression, index: Expression) => {
        for {
          at <- checkExpression(array)
          it <- checkExpression(index)
          r <- at match {
            case ArrayType(_, bt) =>
              for {
                _ <- wrapError(
                  enforceType(it, IntegerType),
                  "ArraySubscriptError: Element from array has wrong type!"
                )
              } yield bt
            case _ =>
              assertError(
                s"ArraySubscriptError: Tried to subscript element of type ${at}. Expected ArrayType!"
              )
          }
        } yield r
      }
      case FieldAccessExpression(exp: Expression, attributeName: String) =>
        checkFieldAccess(exp, attributeName)
      case PointerAccessExpression(name: String) => lookupPointer(name)
      case LambdaExpression(args: List[FormalArg], exp: Expression) => {
        for {
          _ <- addLocalVariables(args.map(arg => (arg.name, arg.argumentType)))
          t <- checkExpression(exp)
        } yield t
      }
    }
  }

  def checkModule(module: OberonModule): StateOrError[Unit] = {
    for {
      _ <- module.constants.foldLeft[StateOrError[Unit]](pure(()))((acc, c) =>
        for {
          _ <- acc
          t <- checkExpression(c.exp)
          _ <- addGlobalVariable(c.name, t)
        } yield ()
      )
      _ <- addGlobalVariables(
        module.variables.map(v => (v.name, v.variableType))
      )
      _ <- addProcedures(module.procedures)
      _ <- addUserDefinedTypes(module.userTypes)
      _ <- module.procedures.foldLeft[StateOrError[Unit]](pure(()))((acc, p) =>
        for {
          _ <- acc
          _ <- checkProcedure(p)
        } yield ()
      )
      _ <- module.stmt match {
        case Some(stmt) => checkStmt(stmt)
        case None       => pure(()) // Should this be an error?
      }
    } yield ()
  }

  def checkStmt(stmt: Statement): StateOrError[Type] = stmt match {
    case AssignmentStmt(VarAssignment(v: String), exp: Expression) => {
      for {
        tv <- lookupVariable(v)
        te <- checkExpression(exp)
        _ <- (tv, te) match {
          case (PointerType(_), NullType) => pure(())
          case (IntegerType, BooleanType) => pure(())
          case (BooleanType, IntegerType) => pure(())
          case _                          => wrapValue(enforceType(te, tv), ())
        }
      } yield NullType
    }
    case AssignmentStmt(
          ArrayAssignment(array: Expression, index: Expression),
          exp: Expression
        ) => {
      for {
        at <- checkExpression(array)
        it <- checkExpression(index)
        et <- checkExpression(exp)
        _ <- enforceType(it, IntegerType)
        _ <- at match {
          case ArrayType(_, bt) => enforceType(et, bt)
          case _ =>
            assertError(
              s"Tried to update array value for type ${at}. Expected ArrayType!"
            )
        }
      } yield NullType
    }
    case AssignmentStmt(
          RecordAssignment(record: Expression, field: String),
          exp: Expression
        ) => {
      for {
        rt <- checkFieldAccess(record, field)
        et <- checkExpression(exp)
        _ <- enforceType(rt, et)
      } yield NullType
    }
    case AssignmentStmt(
          PointerAssignment(name: String),
          exp: Expression
        ) => {
      for {
        pt <- lookupPointer(name)
        et <- checkExpression(exp)
        _ <- enforceType(et, pt)
      } yield NullType
    }
    case SequenceStmt(stmts: List[Statement]) => {
      stmts.foldLeft[StateOrError[Type]](pure(NullType))((acc, stmt) =>
        for {
          _ <- acc
          _ <- checkStmt(stmt)
        } yield NullType
      )
    }
    case NewStmt(n: String) => lookupPointer(n)
    case ReturnStmt(exp: Expression) =>
      wrapValue(checkExpression(exp), NullType)
    case ReadLongRealStmt(v: String) =>
      wrapValue(lookupTypedVariable(v, RealType), NullType)
    case ReadRealStmt(v: String) =>
      wrapValue(lookupTypedVariable(v, RealType), NullType)
    case ReadLongIntStmt(v: String) =>
      wrapValue(lookupTypedVariable(v, IntegerType), NullType)
    case ReadIntStmt(v: String) =>
      wrapValue(lookupTypedVariable(v, IntegerType), NullType)
    case ReadShortIntStmt(v: String) =>
      wrapValue(lookupTypedVariable(v, IntegerType), NullType)
    case ReadCharStmt(v: String) =>
      wrapValue(lookupTypedVariable(v, CharacterType), NullType)
    case WriteStmt(exp: Expression) => wrapValue(checkExpression(exp), NullType)
    case ProcedureCallStmt(name: String, args: List[Expression]) => {
      for {
        p <- lookupProcedure(name)
        ts <- checkExpressions(args)
        _ <-
          if (p.args.map(fa => fa.argumentType) == ts) { pure(()) }
          else {
            assertError(s"Wrong type for argument of procedure ${name}.")
          }
      } yield NullType
    }
    case IfElseStmt(
          condition: Expression,
          thenStmt: Statement,
          elseStmt: Option[Statement]
        ) => {
      for {
        t <- checkExpression(condition)
        _ <- enforceType(t, BooleanType)
        _ <- wrapIgnoreEnvironment(checkStmt(thenStmt))
        _ <- elseStmt match {
          case Some(stmt) => wrapIgnoreEnvironment(checkStmt(stmt))
          case None       => pure(NullType)
        }
      } yield NullType
    }
    case WhileStmt(condition: Expression, stmt: Statement) => {
      for {
        t <- checkExpression(condition)
        _ <- enforceType(t, BooleanType)
        _ <- wrapIgnoreEnvironment(checkStmt(stmt))
      } yield NullType
    }
    case ForEachStmt(n: String, exp: Expression, stmt: Statement) => {
      for {
        et <- checkExpression(exp)
        vt <- lookupVariable(n)
        _ <- et match {
          case ArrayType(_, bt) => enforceType(vt, bt)
          case t =>
            assertError(
              s"ForEach statement required ArrayType, but received ${t}."
            )
        }
        _ <- wrapIgnoreEnvironment(checkStmt(stmt))
      } yield NullType
    }
    case ExitStmt() => pure(NullType)
    case _          => assertError("Statement not part of Oberon-Core")
  }

  private def checkProcedure(p: Procedure): StateOrError[Unit] = {
    for {
      env <- getEnvironment()
      _ <- updateEnvironment(env.push())
      _ <- addLocalVariables(p.args.map(arg => (arg.name, arg.argumentType)))
      _ <- p.constants.foldLeft[StateOrError[Unit]](pure(()))((acc, c) =>
        for {
          _ <- acc
          t <- checkExpression(c.exp)
          _ <- addLocalVariable(c.name, t)
        } yield ()
      )
      _ <- addLocalVariables(p.variables.map(v => (v.name, v.variableType)))
      _ <- checkStmt(p.stmt)
      _ <- updateEnvironment(env.pop())
    } yield ()
  }

  private def checkFieldAccess(
      exp: Expression,
      attributeName: String
  ): StateOrError[Type] = {
    // Not currently testable, no value has RecordType

    for {
      t <- checkExpression(exp)
      r <- t match {
        case RecordType(vars) =>
          for {
            v <- find[VariableDeclaration](
              vars,
              (v => v.name == attributeName)
            )
          } yield v.variableType
        case ReferenceToUserDefinedType(name) =>
          for {
            u <- lookupUserDefinedType(name)
            vs <- u match {
              case UserDefinedType(_, RecordType(vars)) => pure(vars)
              case _ => assertError("User defined type not with base record.")
            }
            v <- find[VariableDeclaration](
              vs,
              (v => v.name == attributeName)
            )
          } yield v.variableType
        case _ =>
          assertError(
            s"Tried to access field from type ${t}. Expected RecordType."
          )
      }
    } yield r
  }

  private def getEnvironment(): StateOrError[Environment[Type]] = {
    StateT[ErrorOr, Environment[Type], Environment[Type]](
      (env: Environment[Type]) => Right(env, env)
    )
  }

  private def updateEnvironment(
      env: Environment[Type]
  ): StateOrError[Environment[Type]] = {
    StateT[ErrorOr, Environment[Type], Environment[Type]](_ =>
      Right((env, env))
    )
  }

  private def wrapIgnoreEnvironment[A](
      wrapped: StateOrError[A]
  ): StateOrError[A] = {
    for {
      env <- getEnvironment()
      r <- wrapped
      _ <- updateEnvironment(env)
    } yield r
  }

  // TODO: Usar esses para abstrair o restante
  private def addManyToEnv[A](
      f: (A, Environment[Type]) => Environment[Type]
  )(xs: List[A]): StateOrError[Environment[Type]] = {
    for {
      env <- getEnvironment()
      nEnv <- xs.foldRight[StateOrError[Environment[Type]]](pure(env))(
        (x, acc) => {
          for {
            _ <- acc
            nEnv <- addToEnv(f)(x)
          } yield nEnv
        }
      )
    } yield nEnv
  }

  private def addToEnv[A](
      f: (A, Environment[Type]) => Environment[Type]
  )(x: A): StateOrError[Environment[Type]] = {
    for {
      env <- getEnvironment()
      nEnv <- updateEnvironment(f(x, env))
    } yield nEnv
  }

  private def addUserDefinedTypes(
      us: List[UserDefinedType]
  ): StateOrError[Environment[Type]] = {
    for {
      env <- getEnvironment()
      nEnv <- us.foldLeft[StateOrError[Environment[Type]]](pure(env))(
        (acc, u) => {
          for {
            _ <- acc
            nEnv <- addUserDefinedType(u)
          } yield nEnv
        }
      )
    } yield nEnv
  }

  private def addUserDefinedType(
      u: UserDefinedType
  ): StateOrError[Environment[Type]] = {
    for {
      env <- getEnvironment()
      nEnv <- updateEnvironment(env.addUserDefinedType(u))
    } yield nEnv
  }

  private def addProcedures(
      ps: List[Procedure]
  ): StateOrError[Environment[Type]] = {
    for {
      env <- getEnvironment()
      nEnv <- ps.foldLeft[StateOrError[Environment[Type]]](pure(env))(
        (acc, p) => {
          for {
            _ <- acc
            nEnv <- addProcedure(p)
          } yield nEnv
        }
      )
    } yield nEnv
  }

  private def addProcedure(p: Procedure): StateOrError[Environment[Type]] = {
    for {
      env <- getEnvironment()
      nEnv <- updateEnvironment(env.declareProcedure(p))
    } yield nEnv
  }

  private def addGlobalVariables(
      vars: List[(String, Type)]
  ): StateOrError[Environment[Type]] = {
    for {
      env <- getEnvironment()
      nEnv <- vars.foldLeft[StateOrError[Environment[Type]]](pure(env))(
        (acc, v) => {
          for {
            _ <- acc
            nEnv <- addGlobalVariable(v._1, v._2)
          } yield nEnv
        }
      )
    } yield nEnv
  }

  private def addGlobalVariable(
      name: String,
      varType: Type
  ): StateOrError[Environment[Type]] = {
    for {
      env <- getEnvironment()
      nEnv <- updateEnvironment(
        env.setGlobalVariable(name, varType)
      )
    } yield nEnv
  }

  private def addLocalVariables(
      vars: List[(String, Type)]
  ): StateOrError[Environment[Type]] = {
    for {
      env <- getEnvironment()
      nEnv <- vars.foldLeft[StateOrError[Environment[Type]]](pure(env))(
        (acc, v) => {
          for {
            _ <- acc
            nEnv <- addLocalVariable(v._1, v._2)
          } yield nEnv
        }
      )
    } yield nEnv
  }

  private def addLocalVariable(
      name: String,
      varType: Type
  ): StateOrError[Environment[Type]] = {
    for {
      env <- getEnvironment()
      nEnv <- updateEnvironment(
        env.setLocalVariable(name, varType)
      )
    } yield nEnv
  }

  private def wrapError[A](
      wrapped: StateOrError[A],
      error: String
  ): StateOrError[A] = {
    StateT[ErrorOr, Environment[Type], A]((env: Environment[Type]) => {
      wrapped.runA(env) match {
        case Left(_)  => Left(error)
        case Right(r) => Right((env, r))
      }
    })
  }

  private def wrapValue[A, B](wrapped: StateOrError[B], v: A): StateOrError[A] = {
    for {
      _ <- wrapped
    } yield v
  }

  private def find[A](xs: List[A], p: A => Boolean): StateOrError[A] = {
    xs.find(p) match {
      case Some(v) => pure(v)
      case None    => assertError("ElementNotFoundError")
    }
  }

  private def checkExpressions(
      exps: List[Expression]
  ): StateOrError[List[Type]] = {
    exps.foldRight[StateOrError[List[Type]]](pure(List()))((exp, acc) =>
      for {
        t <- checkExpression(exp)
        ts <- acc
      } yield (t :: ts)
    )
  }

  private def lookupUserDefinedType(
      name: String
  ): StateOrError[UserDefinedType] =
    StateT[ErrorOr, Environment[Type], UserDefinedType](
      (env: Environment[Type]) => {
        env.lookupUserDefinedType(name) match {
          case Some(u) => Right((env, u))
          case None    => Left(s"Undefined user defined type ${name}.")
        }
      }
    )

  private def lookupProcedure(name: String): StateOrError[Procedure] =
    StateT[ErrorOr, Environment[Type], Procedure]((env: Environment[Type]) => {
      env.lookupProcedure(name) match {
        case Some(p) => Right((env, p))
        case None    => Left(s"Undefined procedure ${name}.")
      }
    })

  private def lookupTypedVariable(name: String, et: Type): StateOrError[Type] =
    for {
      t <- lookupVariable(name)
      r <- enforceType(t, et)
    } yield r

  private def lookupVariable(name: String): StateOrError[Type] =
    StateT[ErrorOr, Environment[Type], Type]((env: Environment[Type]) => {
      env.lookup(name) match {
        case Some(t) => Right((env, t))
        case None    => Left(s"Undefined variable ${name}.")
      }
    })

  private def lookupPointer(name: String): StateOrError[Type] = {
    StateT[ErrorOr, Environment[Type], Type]((env: Environment[Type]) => {
      env.lookup(name) match {
        case Some(PointerType(vt)) => Right((env, vt))
        case _ => Left(s"Could not find pointer with name ${name}.")
      }
    })
  }

  private def enforceType(at: Type, et: Type): StateOrError[Type] =
    enforceAnyType(at, List(et))

  private def enforceTypes(ts: List[Type], et: Type): StateOrError[Type] =
    ts.foldRight[StateOrError[Type]](pure(et))((t, acc) => enforceType(t, et))

  private def enforceAnyType(t: Type, ts: List[Type]): StateOrError[Type] = {
    if (ts.contains(t)) {
      pure(t)
    } else {
      assertError(s"Unexpected type ${t}. Expected one of ${ts.mkString(", ")}")
    }
  }

  private def checkOperation(
      lExpr: Expression,
      rExpr: Expression,
      expTypes: List[Type],
      resType: Type
  ): StateOrError[Type] =
    wrapValue(checkOperationKeepType(lExpr, rExpr, expTypes), resType)

  private def checkOperationKeepType(
      lExpr: Expression,
      rExpr: Expression,
      expTypes: List[Type]
  ): StateOrError[Type] = {
    for {
      t1 <- checkExpression(lExpr)
      t2 <- checkExpression(rExpr)
      t <- enforceType(t1, t2)
      _ <- enforceAnyType(t, expTypes)
    } yield t
  }
}
