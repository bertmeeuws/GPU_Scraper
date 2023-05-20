package com.scala.repositories.interpreters.postgres

import cats.effect.IO
import com.scala.repositories.{ Role, RoleUtils, User }
import com.scala.repositories.algebras.{ RoleAssignment, RoleAssignmentRepository, RoleRepository }
import doobie.Transactor
import doobie.implicits.toSqlInterpolator
import doobie.implicits._

// Role interpreter

case class RoleItem(id: Long, name: String, description: String, createdAt: String, updatedAt: String)
class RoleRepositoryInterpreter(xa: Transactor[IO]) extends RoleRepository[IO] {
  override def create(userId: Long, roleId: Long): IO[Long] = ???

  override def findRoleId(role: Role): IO[Long] = ???

  override def findRoleById(roleId: Long): IO[Option[Role]] =
    sql"SELECT * FROM public.role WHERE id = $roleId".query[RoleItem].option.transact(xa) map {
      case Some(roleItem) => Some(RoleUtils.getRole(roleItem.name))
      case None           => None
    }
}
