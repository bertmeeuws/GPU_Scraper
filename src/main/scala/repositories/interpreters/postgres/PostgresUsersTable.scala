package com.scala.repositories.interpreters.postgres

import cats.Id
import com.scala.repositories.algebras.UserRepository
import com.scala.repositories._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import doobie.hikari.HikariTransactor
import doobie.postgres._
import doobie.postgres.implicits._

import java.util.UUID


class UserRepositoryInterpreters(xa: HikariTransactor[IO]) extends UserRepository[IO] {


    override def find(userId: UUID): IO[Option[User]] = {
      sql"select id, name, password from person where id = $userId".query[User].option.transact(xa)
    }

    override def create(user: User): IO[Option[UUID]] = {
      sql"insert into person (name, age) values ($user.username, $user.password)".update.withUniqueGeneratedKeys[UUID]("id").transact(xa).option
    }

    override def delete(userId: UUID): IO[Unit] = {
      sql"delete from person where id = $userId".update.run.transact(xa).void
    }

    override def findByUsername(username: String): IO[Option[User]] = {
      sql"select id, name, password from person where name = $username".query[User].option.transact(xa)
    }

}

object UserRepositoryInterpreters {
  def apply(xa: HikariTransactor[IO]): UserRepository[IO] = new UserRepositoryInterpreters(xa)
}