package scala.repositories.interpreters.postgres

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

import java.util.UUID


object PostgresUsersRepositoryInterpreter {
  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", // driver classname
    "jdbc:postgresql:world", // connect URL (driver-specific)
    "postgres", // user
    "postgres" // password
  )

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

  implicit val userRepository: UserRepository[IO] = new UserRepository[IO] {
    override def find(userId: UUID): IO[Option[User]] = {
      sql"select id, name, password from person where id = $userId".query[User].option.transact(xa)
    }
    override def create(user: User): IO[Option[UUID]] = {
      sql"insert into person (name, age) values ($user.username, $user.password)".update.withUniqueGeneratedKeys[UUID]("id").transact(xa).option
    }
    override def delete(userId: UUID): IO[Option[UUID]] = {
      sql"delete from person where id = $userId".update.run.transact(xa).flatMap(_ => IO.pure(Some(userId)))
    }

    override def findByUsername(username: String): IO[Option[User]] = {
      sql"select id, name, password from person where name = $username".query[User].option.transact(xa)
    }
  }
}

