package scala.auth

import org.http4s.HttpRoutes
import cats._
import cats.effect._
import cats.effect.kernel.Resource.Pure
import cats.implicits._
import org.http4s.circe._
import org.http4s._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.Header.Raw
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.headers._
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.headers.`Content-Type`
import org.http4s.MediaType


import scala.util.Try


object Auth {
  val redis = Map(
    "bert" -> "abc123"
  )

  import scala.auth.Service._

  implicit val decoder: EntityDecoder[IO, User] = jsonOf[IO, User]


  def authRoutes[F[_]: Concurrent]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    HttpRoutes.of[F] {
      case req @ POST -> Root / "auth" / "login" => for {
        user <- req.as[User]
        _ <- login(user.username, user.password) match {
          case Some(x) => Ok(x.asJson)
          case None => {
            println("testing")
            NotFound()
          }
        }
        jwt <- Jwt.createToken[F](user.username) flatMap {
          case Some(x) => Ok(s"""{"jwt": "${x}"}""").map(_.putHeaders(`Content-Type`(MediaType.application.json)))
          case None => InternalServerError()
        }
      } yield jwt
      case POST -> Root / "auth" / "register" => Ok("Register")
    }
  }
}