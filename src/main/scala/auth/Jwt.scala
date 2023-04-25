package scala.auth

import cats.{Applicative, Functor}
import dev.profunktor.auth._
import dev.profunktor.auth.jwt._
import pdi.jwt._

import scala.reflect.internal.util.NoSourceFile.content


trait Jwt {
  def createToken(username: String): String
  def validateToken(token: String): Option[String]
  def getRole(token: String): Option[Role]
}

trait Role

case object Admin extends Role
case object User extends Role



object Jwt {
  def createToken[F[_]:  Applicative: Functor](username: String): Option[String] = {
    val claim = JwtClaim(content =
      s"""{"
         |username": "$username",
         |"role": "Admin",
         |}""".stripMargin, expiration = Some((System.currentTimeMillis() / 1000 + 3600).toLong))

    val secret = JwtSecretKey("dzauhduhduhduhduauhdua")
    val algorithm = JwtAlgorithm.HS256

    val jwt: F[JwtToken] = jwtEncode(claim, secret, algorithm)

    println(jwt)
    Some(jwt.toString)
  }
}