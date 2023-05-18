package scala.api.http

import cats._
import cats.data._
import cats.effect._
import cats.effect.implicits.effectResourceOps
import cats.implicits._
import com.comcast.ip4s.IpLiteralSyntax
import com.config.Config.{Config, load}
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.implicits._
import com.services.{Alternate, Amazon, Megekko, Stores}

import scala.auth.Auth.authRoutes
import com.scala.repositories.interpreters.postgres.UserRepositoryInterpreters._
import doobie.hikari.HikariTransactor
import org.http4s.ember.server.EmberServerBuilder
import pureconfig.ConfigSource.resources
import com.config.Config
import com.scala.repositories.algebras.{RoleAssignmentRepository, RoleRepository, UserRepository}
import com.scala.repositories.interpreters.postgres.UserRepositoryInterpreters
import doobie.util.ExecutionContexts
import doobie._
import doobie.implicits._
import doobie.hikari._
import org.http4s.server.Server
import com.utils.Logger

import scala.database.Database

object Server {
  implicit val storesQueryParamDecoder: QueryParamDecoder[Stores] = (value: QueryParameterValue) => {
    value.value match {
      case "alternate" => Validated.valid(Alternate)
      case "megekko" => Validated.valid(Megekko)
      case "amazon" => Validated.valid(Amazon)
      case _ => Validated.invalidNel(ParseFailure("Invalid store", "Invalid store"))
    }
  }

  object StoreQueryParamMatcher extends OptionalValidatingQueryParamDecoderMatcher[Stores]("store")

  object GPUNameParamMatcher extends QueryParamDecoderMatcher[String]("gpuName")

  def storesRoutes: HttpRoutes[IO] = {
    val dsl = Http4sDsl[IO]
    import dsl._
    HttpRoutes.of[IO] {
      case GET -> Root / "stores" :? StoreQueryParamMatcher(store) +& GPUNameParamMatcher(gpuName) =>
        store match {
          case Some(x) => Ok(x.toString)
          case None => BadRequest()
        }
      case GET -> Root / "stores" / UUIDVar(storeId) / "information" => ???
      case GET -> Root / "stores" / "testing" => Ok("Testing")
    }
  }

  def allRoutes(resources: Resources, userRepository: UserRepository[IO]): HttpRoutes[IO] = {
    storesRoutes <+> authRoutes(resources, userRepository)
  }

  def allRoutesComplete(resources: Resources, userRepository: UserRepository[IO]): HttpApp[IO] = {
    allRoutes(resources, userRepository).orNotFound
  }

  def create(configFile: String = "application.conf"): IO[ExitCode] = {
    resources(configFile).use(createe)
  }

  private def resources(configFile: String): Resource[IO, Resources] = {
    for {
      config <- load(configFile)
      ec <- ExecutionContexts.fixedThreadPool[IO](config.database.threadPoolSize)
      transactor <- Database.transactor(config.database, ec)
    } yield Resources(transactor, config)
  }

  object HTTPServer {
    def createEmberServer(configFile: String = "application.conf", resources: Resources, userRepository: UserRepository[IO]): Resource[IO, Server] = {
      EmberServerBuilder.default[IO]
        .withHost(host"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(
          allRoutesComplete(resources, userRepository))
        .build
    }
  }

  def createe(resources: Resources): IO[ExitCode] = {
    (for {
      _ <- IO.println("Starting server").toResource
      _ <- Database.initialize(resources.transactor).toResource
      _ <- IO.println("Database initialized").toResource
      userRepository = new UserRepositoryInterpreters(resources.transactor)
      _ <- IO.println("User repository initialized").toResource
      server <- HTTPServer.createEmberServer("application.conf", resources, userRepository)
    } yield {
      server
    }).use(_ => IO.never).as(ExitCode.Success)
  }

  case class Resources(transactor: HikariTransactor[IO], config: Config, userRepository: UserRepository[IO])
  case class Repositories(userRepository: UserRepository[IO], roleRepository: RoleRepository[IO], roleAssignmentRepository: RoleAssignmentRepository[IO])
}