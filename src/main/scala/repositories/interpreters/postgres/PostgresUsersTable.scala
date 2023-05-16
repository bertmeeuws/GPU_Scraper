package com.scala.repositories.interpreters.postgres

import com.scala.repositories.algebras.UserRepository
import com.scala.repositories._
import doobie.implicits._
import cats.effect._
import cats.effect.unsafe.implicits.global
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor

import java.util.UUID


class UserRepositoryInterpreters(xa: Transactor[IO]) extends UserRepository[IO] {


    override def find(userId: Long): IO[Option[User]] = {
      sql"SELECT id, name, password FROM user WHERE id = $userId".query[User].option.transact(xa)
    }

    override def create(user: User): IO[Option[Long]] = {
      println(user)
      println("creating user")
      sql"INSERT INTO user (username, password) VALUES ($user.username, $user.password)".update.withUniqueGeneratedKeys[Long]("id").transact(xa).map(k => {
        println(k)
        println("created user")
        Some(k)
      }).unsafeRunSync()

      sql"INSERT INTO user (username, password) VALUES (${user.username}, ${user.password})".update.withUniqueGeneratedKeys[Long]("id").transact(xa).map(k => {
        println(k)
        println("created user")
        Some(k)
      })
    }

    override def delete(userId: Long): IO[Unit] = {
      sql"delete from user where id = $userId".update.run.transact(xa).void
    }

  override def findByUsername(username: String): IO[Option[User]] =
    IO.println("Find by username") >>
      sql"SELECT id, username, password FROM user WHERE username = $username".query[User].option.transact(xa).flatTap {
        case Some(user) => IO.println(s"[findByUsername] Some($user)")
        case None => IO.println("[findByUsername] None")
      }
}

object UserRepositoryInterpreters {
  def apply(xa: HikariTransactor[IO]): UserRepository[IO] = new UserRepositoryInterpreters(xa)
}