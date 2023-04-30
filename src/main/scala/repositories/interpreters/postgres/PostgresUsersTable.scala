package scala.repositories.interpreters.postgres

import cats.Id
import com.scala.repositories.algebras.UserRepository
import com.scala.repositories._

import java.util.UUID


object PostgresUsersRepositoryInterpreter {

  implicit val userRepository: UserRepository[Id] = new UserRepository[Id] {
    override def find(userId: UUID): Id[Option[User]] = ???
    override def create(user: User): Id[Option[UUID]] = ???
    override def delete(userId: UUID): Id[Option[UUID]] = ???

    override def findByUsername(username: String): Id[Option[User]] = ???
  }
}