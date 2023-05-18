package scala.database

import cats.effect._

import com.config.Config._

import org.flywaydb.core.Flyway
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

object Database {
  def transactor(config: DatabaseConfig, executionContext: ExecutionContext): Resource[IO, HikariTransactor[IO]] =
    for {
      xa <- HikariTransactor.newHikariTransactor[IO](
        config.driver,
        config.url,
        config.user,
        config.password,
        executionContext
      )
    } yield xa

  def initialize(transactor: HikariTransactor[IO]): IO[Unit] =
    transactor.configure { dataSource =>
      IO {
        val flyWay = Flyway.configure().baselineOnMigrate(true).dataSource(dataSource).load()
        flyWay.repair()
        flyWay.migrate()
        ()
      }
    }
}
