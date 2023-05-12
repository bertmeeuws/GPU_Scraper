package com.scala.repositories.algebras

import com.scala.repositories._

import java.util.UUID

trait UserRepository[F[_]] {
  def find(userId: UUID): F[Option[User]]
  def create(user: User): F[Option[UUID]]
  def delete(userId: UUID): F[Unit]

  def findByUsername(username: String): F[Option[User]]
}
