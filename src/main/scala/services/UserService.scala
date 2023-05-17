package com.scala.services

import cats.implicits.toFlatMapOps
import cats.{Applicative, Monad}
import com.scala.repositories._
import cats._
import cats.effect.IO
import implicits._
import com.scala.repositories.algebras.UserRepository

import java.util.UUID
import scala.auth.Jwt

sealed trait UserError
case class UserNotFound(userId: Long) extends UserError
case class UserAlreadyExists(username: String) extends UserError


class UserService(usersRepository: UserRepository[IO]) {
  def get(userId: Long): IO[Option[User]] = {
    for {
      user <- usersRepository.find(userId)
      result <- user match {
        case Some(user) => IO { Some(user) }
        case None => IO { None }
      }
    } yield result

  }

  def create(username: String, password: String): IO[Either[UserError,String]] = {
    for {
      user <- usersRepository.findByUsername(username)
      result <- user match {
        case Some(user) => IO { Left(UserAlreadyExists(user.username)) }
        case None => {
          val user = UserWithOutId(username, password)

          for {
            userId <- usersRepository.create(user)
            createdUser <- usersRepository.find(userId)
            token <- Jwt.createToken(createdUser.get.username)
          } yield Right(token)
        }
      }
    } yield result
  }


  def delete(userId: Long): IO[Either[UserError, Unit]] = {
    for {
      user <- usersRepository.find(userId)
      result <- user match {
        case Some(foundUser) => {
          for {
            _ <- usersRepository.delete(foundUser.id)
          } yield Right(())
        }
        case None => IO { Left(UserNotFound(userId)) }
      }
    } yield result
  }
}

object UserService {
  def apply(usersRepository: UserRepository[IO]) = new UserService(usersRepository)
}