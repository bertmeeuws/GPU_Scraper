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

object RoleRepository {
  def apply(xa: Transactor[IO]): RoleRepository[IO] = new RoleRepositoryInterpreter(xa)
}

// Role assignments interpreter
class RoleAssignmentsInterpreter(xa: Transactor[IO]) extends RoleAssignmentRepository[IO] {

  override def findRoleAssignmentByUserId(userId: Long): IO[List[RoleAssignment]] = {
    for {
      _ <- IO.println(s"Finding role by user id: $userId")
      result <- sql"SELECT user_id, role_id FROM public.role_assignments WHERE user_id=$userId".query[RoleAssignment].stream.transact(xa).compile.toList
      _ <- IO.println(s"Result: $result")
    }
    yield result
  }

  override def findRoleAssignmentsByRoleId(roleId: Long): IO[List[RoleAssignment]] = {
    for {
      _ <- IO.println(s"Finding role by role id: $roleId")
      result <- sql"SELECT user_id, role_id FROM public.role_assignments WHERE role_id=$roleId".query[RoleAssignment].stream.transact(xa).compile.toList
      _ <- IO.println(s"Result: $result")
    }
    yield result
  }
}

object RoleAssignmentsInterpreter {
  def apply(xa: Transactor[IO]): RoleAssignmentRepository[IO] = new RoleAssignmentsInterpreter(xa)
}