package com.scala.services

import cats.implicits.toFlatMapOps
import cats.{Applicative, Monad}
import com.scala.repositories._
import cats._
import cats.effect.IO
import implicits._
import com.scala.repositories.algebras.UserRepository

import java.util.UUID

sealed trait UserError
case class UserNotFound(userId: Long) extends UserError
case class UserAlreadyExists(username: String) extends UserError


class UserService(usersRepository: UserRepository[IO]) {
  def get(userId: Long): IO[Option[User]] = {
    usersRepository.find(userId)
  }

  def create(username: String, password: String): IO[Either[UserError,Long]] = {
    usersRepository.findByUsername(username).flatMap {
      case None => usersRepository.create(User(2, username, password)).flatMap { k =>
        {
        k match {
        case Some(userId) => {
          println(userId)
          IO { userId.asRight }
        }
        case None => IO(UserAlreadyExists(username).asLeft)
      }
      }}
      case Some(existingUser) => IO(UserAlreadyExists(existingUser.username).asLeft)
    }
  }

  def delete(userId: UUID): IO[Either[String ,UUID]] = ???
}

object UserService {
  def apply(usersRepository: UserRepository[IO]) = new UserService(usersRepository)
}