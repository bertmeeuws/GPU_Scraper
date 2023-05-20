package com.scala.services

import cats.effect.IO
import com.scala.repositories.User
import com.scala.repositories.algebras.UserRepository

class AuthService(userRepository: UserRepository[IO]) {
  def login(username: String, password: String): IO[Option[User]] =
    IO.println(s"Logging in user: $username") >> userRepository.findByUsername(username).flatMap {
      case Some(user) => {
        IO.println(s"User: $user")
        if (user.password == password) IO.pure(Some(user))
        else IO.pure(None)
      }
      case None => IO.pure(None)
    }
}

object AuthService {
  def apply(userRepository: UserRepository[IO]): AuthService = new AuthService(userRepository)
}
