package com.scala.repositories

sealed trait Role
case object AdminRole extends Role
case object UserRole extends Role



object RoleUtils {
  def getRole(role: String): Role = {
    role match {
      case "admin" => AdminRole
      case "user" => UserRole
    }
  }
}