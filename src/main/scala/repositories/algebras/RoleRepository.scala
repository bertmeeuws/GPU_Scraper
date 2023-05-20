package com.scala.repositories.algebras

import com.scala.repositories._

trait RoleRepository[F[_]] {
  def create(userId: Long, roleId: Long): F[Long]
  def findRoleId(role: Role): F[Long]

  def findRoleById(roleId: Long): F[Option[Role]]
}

case class RoleAssignment(userId: Long, roleId: Long)

trait RoleAssignmentRepository[F[_]] {
  def findRoleAssignmentByUserId(userId: Long): F[List[RoleAssignment]]

  def findRoleAssignmentsByRoleId(roleId: Long): F[List[RoleAssignment]]
}
