package com.scala.repositories.algebras

import com.scala.repositories._

trait RoleRepository[F[_]] {
  def create(userId: Long, roleId: Long): F[Long]
  def getRoleId(role: Role): F[Long]
}

case class RoleAssignment(userId: Long, roleId: Long)

trait RoleAssignmentRepository[F[_]] {
  def findRoleAssignmentByUserId(userId: Long): F[List[RoleAssignment]]

  def findRoleAssignmentsByRoleId(roleId: String): F[List[RoleAssignment]]
}