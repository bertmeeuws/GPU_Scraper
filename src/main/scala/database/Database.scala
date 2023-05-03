package scala.database

import cats.effect._
import com.config.Config._
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext
import doobie.implicits._
import org.flywaydb.core.Flyway


object Database {
  def transactor(config: DatabaseConfig, executionContext: ExecutionContext): Resource[IO, Transactor[IO]] = {
  Transactor.fromDriverManager(
    config.driver,
    config.url,
    config.user,
    config.password,
  )

    def initialize(transactor: Transactor[IO]): IO[Unit] = {

      transactor.configure { dataSource => IO {
        val flyWay = Flyway.configure().dataSource(dataSource).load()
        flyWay.migrate()
        ()
      }}
    }
  }
}