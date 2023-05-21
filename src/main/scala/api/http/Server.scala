package scala.api.http

import cats._
import cats.data._
import cats.effect._
import cats.effect.implicits.effectResourceOps
import cats.implicits._
import com.comcast.ip4s.IpLiteralSyntax
import com.config.Config.{ load, Config }
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.implicits._
import com.services.{ Alternate, Amazon, Megekko, Stores }

import scala.auth.Auth.authRoutes
import com.scala.repositories.interpreters.postgres.UserRepositoryInterpreters._
import doobie.hikari.HikariTransactor
import org.http4s.ember.server.EmberServerBuilder
import pureconfig.ConfigSource.resources
import com.config.Config
import com.scala.directives.StoresDirective.storesRoutes
import com.scala.repositories.algebras.{ RoleAssignmentRepository, RoleRepository, UserRepository }
import com.scala.repositories.interpreters.postgres._
import doobie.util.ExecutionContexts
import doobie._
import doobie.implicits._
import doobie.hikari._
import org.http4s.server.Server
import com.utils.Logger

import scala.database.Database

object Server {
  def allRoutes(resources: Resources, repositories: Repositories): HttpApp[IO] =
    (storesRoutes <+> authRoutes(resources, repositories)).orNotFound

  def create(configFile: String = "application.conf"): IO[ExitCode] =
    resources(configFile).use(initializeResources)

  private def resources(configFile: String): Resource[IO, Resources] =
    for {
      config     <- load(configFile)
      ec         <- ExecutionContexts.fixedThreadPool[IO](config.database.threadPoolSize)
      transactor <- Database.transactor(config.database, ec)
    } yield Resources(transactor, config)

  object HTTPServer {
    def createEmberServer(configFile: String = "application.conf",
                          resources: Resources,
                          repositories: Repositories): Resource[IO, Server] =
      EmberServerBuilder
        .default[IO]
        .withHost(host"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(allRoutes(resources, repositories))
        .build
  }

  def initializeResources(resources: Resources): IO[ExitCode] =
    (for {
      _ <- IO.println("Starting server").toResource
      _ <- Database.initialize(resources.transactor).toResource
      _ <- IO.println("Database initialized").toResource
      userRepository           = new UserRepositoryInterpreters(resources.transactor)
      roleRepostiory           = new RoleRepositoryInterpreter(resources.transactor)
      roleAssignmentRepository = new RoleAssignmentsInterpreter(resources.transactor)
      repositories             = Repositories(userRepository, roleRepostiory, roleAssignmentRepository)
      _      <- IO.println("User repository initialized").toResource
      server <- HTTPServer.createEmberServer("application.conf", resources, repositories)
    } yield {
      server
    }).use(_ => IO.never).as(ExitCode.Success)

  case class Resources(transactor: HikariTransactor[IO], config: Config)
  case class Repositories(userRepository: UserRepository[IO],
                          roleRepository: RoleRepository[IO],
                          roleAssignmentRepository: RoleAssignmentRepository[IO])
}
