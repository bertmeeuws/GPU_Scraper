package com.scala.services

import cats.effect.IO
import com.scala.repositories.algebras.UserRepository


class AuthService(userRepository: UserRepository[IO]) {
  def login(username: String, password: String): IO[Option[String]] = {
        for {
          user <- userRepository.findByUsername(username)
          _ <- IO.println(s"User: $user")
        } yield {
          user match {
            case Some(x) => {
              if (x.password == password) {
                Some(x.username)
              } else {
                None
              }
            }
            case None => None
          }
        }
  }
}

object AuthService {
  def apply(userRepository: UserRepository[IO]): AuthService = new AuthService(userRepository)
}