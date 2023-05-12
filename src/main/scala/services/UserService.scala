package com.scala.services

import cats.implicits.toFlatMapOps
import cats.{Applicative, Monad}
import com.scala.repositories._
import cats._
import implicits._
import com.scala.repositories.algebras.UserRepository

import java.util.UUID

sealed trait UserError
case class UserNotFound(userId: UUID) extends UserError
case class UserAlreadyExists(username: String) extends UserError


class UserService[F[_]: Monad](usersRepository: UserRepository[F]) {
  def get(userId: UUID): F[Option[User]] = {
    usersRepository.find(userId)
  }

  def create(username: String, password: String): F[Either[UserError,UUID]] = {
    // Do all the validating here
    usersRepository.findByUsername(username).flatMap {
      case None => usersRepository.create(User(UUID.randomUUID(), username, password)).flatMap { k => k match {
        case Some(userId) => Monad[F].pure(userId.asRight)
        case None => Monad[F].pure(UserAlreadyExists(username).asLeft)
      }
      }
      case Some(existingUser) => Monad[F].pure(UserAlreadyExists(existingUser.username).asLeft)
    }
  }

  def delete(userId: UUID): F[Either[String ,UUID]] = ???
}

object UserService {
  def apply[F[_]: Monad](usersRepository: UserRepository[F]): UserService[F] = new UserService[F](usersRepository)
}