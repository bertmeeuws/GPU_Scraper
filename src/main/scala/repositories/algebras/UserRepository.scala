package com.scala.repositories.algebras

import com.scala.repositories._

import java.util.UUID

trait UserRepository[F[_]] {
  def find(userId: Long): F[Option[User]]
  def create(user: UserWithOutId): F[Long]
  def delete(userId: Long): F[Unit]

  def findByUsername(username: String): F[Option[User]]
}
