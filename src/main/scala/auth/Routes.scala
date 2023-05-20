package scala.auth

import org.http4s.HttpRoutes
import cats.effect._
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._
import com.scala.services._
import com.utils.Logger
import scala.api.http.Server.{ Repositories, Resources }

object Auth {
  val redis = Map(
    "bert" -> "abc123"
  )

  import scala.auth.Service._

  case class JWT(jwt: String)

  def authRoutes(resources: Resources, repositories: Repositories): HttpRoutes[IO] = {
    implicit val decoder: EntityDecoder[IO, User] = jsonOf[IO, User]

    implicit val jwtEncoder: EntityDecoder[IO, JWT] = jsonOf[IO, JWT]

    val dsl = Http4sDsl[IO]
    import dsl._
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "auth" / "login" => {
        val authService = new AuthService(repositories.userRepository)
        val roleService = new RoleService(repositories.roleRepository, repositories.roleAssignmentRepository)

        req.as[User].flatMap { user =>
          authService.login(user.username, user.password).flatMap {
            case Some(userId) => {
              roleService.getRolesForUser(userId.toLong).flatMap { roles =>
                {
                  if (roles.isEmpty) BadRequest("Invalid username or password".asJson)
                  else {
                    Jwt.createToken(user.username, roles).flatMap { token =>
                      Ok(JWT(token).asJson)
                    }
                  }
                }
              }
            }
            case None => BadRequest("Invalid username or password".asJson)
          }
        }
      }

      case req @ POST -> Root / "auth" / "register" => {
        val userService = UserService(repositories.userRepository)
        val roleService = new RoleService(repositories.roleRepository, repositories.roleAssignmentRepository)

        req.as[User].flatMap { user =>
          {
            userService.create(user.username, user.password).flatMap {
              case Right(x) => Ok(JWT(x).asJson)
              case Left(x)  => BadRequest(x.asJson)
            }
          }
        }
      }
    }
  }
}
