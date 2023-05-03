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
import doobie.postgres._
import doobie.postgres.implicits._

import java.util.UUID


object UserRepositoryInterpreters {
  def apply[F[_]: UserRepository]: UserRepository[F] = implicitly


  def postgresUserRepoInterpreter[F[_]: Monad: Async](xa: Transactor[F]): UserRepository[F] = new UserRepository[F] {
    val y = xa.yolo

    import y._

    val create =
      sql"""
        CREATE TABLE person (
          id   UUID NOT NULL PRIMARY KEY,
          name VARCHAR NOT NULL UNIQUE,
          password  VARCHAR NOT NULL
        )
      """.update.run

    override def find(userId: UUID): F[Option[User]] = {
      sql"select id, name, password from person where id = $userId".query[User].option.transact(xa)
    }

    override def create(user: User): F[Option[UUID]] = {
      sql"insert into person (name, age) values ($user.username, $user.password)".update.withUniqueGeneratedKeys[UUID]("id").transact(xa)
    }

    override def delete(userId: UUID): F[Option[UUID]] = {
      sql"delete from person where id = $userId".update.run.transact(xa).flatMap(_ => IO.pure(Some(userId)))
    }

    override def findByUsername(username: String): F[Option[User]] = {
      sql"select id, name, password from person where name = $username".query[User].option.transact(xa)
    }
  }
}