package com.scala.services

import cats.effect.IO
import cats.implicits._
import com.scala.repositories.Role
import com.scala.repositories.algebras.{ RoleAssignmentRepository, RoleRepository }

class RoleService(roleRepository: RoleRepository[IO], roleAssignmentRepository: RoleAssignmentRepository[IO]) {

  def getRolesForUser(userId: Long): IO[List[Role]] =
    for {
      roleAssignments <- roleAssignmentRepository.findRoleAssignmentByUserId(userId)
      roles           <- roleAssignments.traverse(roleAssignment => roleRepository.findRoleById(roleAssignment.roleId))
    } yield roles

}
