package com.scala.repositories.interpreters.postgres

import cats.effect.IO
import com.scala.repositories.{Role, User}
import com.scala.repositories.algebras.{RoleAssignment, RoleAssignmentRepository, RoleRepository}
import doobie.Transactor
import doobie.implicits._


// Role interpreter
class RoleRepositoryInterpreter(xa: Transactor[IO]) extends RoleRepository[IO] {
  override def create(userId: Long, roleId: Long): IO[Long] = ???

  override def getRoleId(role: Role): IO[Long] = ???
}