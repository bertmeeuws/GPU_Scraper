package com.scala.services

import cats.effect.IO
import cats.implicits._
import com.scala.repositories.Role
import com.scala.repositories.algebras.{ RoleAssignmentRepository, RoleRepository }

class RoleService(roleRepository: RoleRepository[IO], roleAssignmentRepository: RoleAssignmentRepository[IO]) {

  def getRolesForUser(userId: Long): IO[List[Role]] =
    for {
      _               <- IO.println(s"Getting roles for user $userId")
      roleAssignments <- roleAssignmentRepository.findRoleAssignmentByUserId(userId)
      _               <- IO { println(s"Role assignments for user $userId: $roleAssignments") }
      roles <- roleAssignments
        .traverse(roleAssignment => roleRepository.findRoleById(roleAssignment.roleId))
        .map(_.flatten)
      _ <- IO { println(s"Roles for user $userId: $roles") }
    } yield roles

}
