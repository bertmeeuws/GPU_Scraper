package com.scala.repositories

sealed trait Role
case object AdminRole extends Role {
  override def toString: String = "admin"
}
case object UserRole extends Role {
  override def toString: String = "user"
}

object RoleUtils {
  def getRole(role: String): Role =
    role.toLowerCase match {
      case "admin" => AdminRole
      case "user"  => UserRole
    }
}
