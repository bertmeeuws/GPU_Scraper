package com.scala.repositories.interpreters.postgres

import com.scala.repositories.algebras.UserRepository
import com.scala.repositories._
import doobie.implicits._
import cats.effect._
import cats.effect.unsafe.implicits.global
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import doobie.postgres._
import doobie.postgres.implicits._

import java.util.UUID


class UserRepositoryInterpreters(xa: Transactor[IO]) extends UserRepository[IO] {


    override def find(userId: Long): IO[Option[User]] = {
      IO.println(s"Finding user with id: $userId") >> sql"SELECT id, username, password FROM public.user WHERE id = $userId".query[User].option.transact(xa)
    }

    override def create(user: UserWithOutId): IO[Long] = {
      IO.println(s"Creating user with data: ${user.username}") >> sql"INSERT INTO public.user (username, password) VALUES (${user.username}, ${user.password})".update.withUniqueGeneratedKeys[Long]("id").transact(xa)
    }

    override def delete(userId: Long): IO[Unit] = {
      sql"delete from user where id = $userId".update.run.transact(xa).void
    }

  override def findByUsername(username: String): IO[Option[User]] = {
    IO.println(s"Find by username: $username") >>
      sql"SELECT id, username, password FROM public.user WHERE username = ${username}".query[User].option.transact(xa)
  }
}

object UserRepositoryInterpreters {
  def apply(xa: HikariTransactor[IO]): UserRepository[IO] = new UserRepositoryInterpreters(xa)
}