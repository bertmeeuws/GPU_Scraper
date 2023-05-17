package scala.auth

import cats.effect.IO
import cats.{Applicative, Functor, Monad}
import dev.profunktor.auth._
import dev.profunktor.auth.jwt._
import pdi.jwt._

import scala.reflect.internal.util.NoSourceFile.content


trait Jwt {
  def createToken(username: String): String
  def validateToken(token: String): Boolean
  def getRole(token: String): Option[Role]
}

trait Role

case object Admin extends Role
case object User extends Role



object Jwt {
  def createToken(username: String): IO[String] = {
    val claim = JwtClaim(content =
      s"""{
         "username": "$username",
         "role": "Admin"
         }""".stripMargin, expiration = Some((System.currentTimeMillis() / 1000 + 3600).toLong))

    val secret = JwtSecretKey("dzauhduhduhduhduauhdua")
    val algorithm = JwtAlgorithm.HS256

    jwtEncode[IO](claim, secret, algorithm).map(_.value)
  }
}