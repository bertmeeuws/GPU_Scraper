package scala.auth

import cats.effect.IO
import dev.profunktor.auth.jwt._
import pdi.jwt._
import com.scala.repositories._

trait Jwt {
  def createToken(username: String): String
  def validateToken(token: String): Boolean
  def getRole(token: String): Option[Role]
}

object Jwt {
  def createToken(username: String, roles: List[Role]): IO[String] = {
    val start = """[""""
    val end   = """"]"""

    val json = if (roles.isEmpty) "[]" else List("Admin", "User", "Mod").mkString(start, """","""", end)

    val claim = JwtClaim(
      content = s"""{
         "username": "$username",
         "role": ${json}
         }""".stripMargin,
      expiration = Some((System.currentTimeMillis() / 1000 + 3600).toLong)
    )

    val secret    = JwtSecretKey("dzauhduhduhduhduauhdua")
    val algorithm = JwtAlgorithm.HS256

    jwtEncode[IO](claim, secret, algorithm).map(_.value)
  }
}
