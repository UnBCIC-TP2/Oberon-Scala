package br.unb.cic.oberon.tc

import cats.data.EitherT
import cats.data.StateT
import cats.data.State
import cats.MonadError

import javax.naming.NameNotFoundException
import javax.naming.InvalidNameException
import java.util.concurrent.Future

import br.unb.cic.oberon.ir.ast.Type
import br.unb.cic.oberon.environment.Environment

package object StateOrErrorMonad {
  type ErrorOr[A] = Either[String, A]

  type StateOrError[A] = StateT[ErrorOr, Environment[Type], A]

  def pure[A](element: A): StateOrError[A] = StateT.pure(element)

  def assertError[A](error: String): StateOrError[A] = {
    StateT[ErrorOr, Environment[Type], A](_ => Left(error))
  }

  def parseErrorOr[A](value: ErrorOr[A]): StateOrError[A] = {
    value match {
      case Left(err) => assertError(err)
      case Right(v) => pure(v)
    }
  }
}
