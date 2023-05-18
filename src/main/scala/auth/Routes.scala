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
import com.scala.services._
import com.scala.repositories._
import com.scala.repositories.algebras.UserRepository

import scala.util.Try
import com.scala.repositories.interpreters.postgres.UserRepositoryInterpreters._
import com.utils.Logger
import doobie.Transactor

import scala.api.http.Server.Resources



object Auth {
  val redis = Map(
    "bert" -> "abc123"
  )

  import scala.auth.Service._

  case class JWT(jwt: String)

  def authRoutes(resources: Resources, userRepository: UserRepository[IO]): HttpRoutes[IO] = {
    implicit val decoder: EntityDecoder[IO, User] = jsonOf[IO, User]

    implicit val jwtEncoder: EntityDecoder[IO, JWT] = jsonOf[IO, JWT]

    val dsl = Http4sDsl[IO]
    import dsl._
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "auth" / "login" => {
        val authService = new AuthService(userRepository)

        for {
        user <- req.as[User]
        userId <- authService.login(user.username, user.password)
        response <- userId match {
          case Some(x) => Jwt.createToken(x).flatMap(token => Ok(JWT(token).asJson))
          case None => BadRequest("Invalid username or password".asJson)
        }
      } yield response
      }

    case req @ POST -> Root / "auth" / "register" => {
      val userService = UserService(userRepository)

      for {
        _ <- Logger.log(s"""Starting to create user""")
        user <- req.as[User]
        _ <- Logger.log(s"""User: ${user.username}""")
        result <- userService.create(user.username, user.password)
        response <- result match {
          case Right(x) => Ok(JWT(x).asJson)
          case Left(x) => BadRequest(x.asJson)
        }
      } yield response


    }

    }
  }
}