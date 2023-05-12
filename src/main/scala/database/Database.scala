package scala.database

import cats.effect._
import com.config.Config._
import doobie.util.transactor.Transactor

import doobie.implicits._
import org.flywaydb.core.Flyway
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext


object Database {
  def transactor[F[_]](config: DatabaseConfig, executionContext: ExecutionContext)(implicit as: Async[F]): Resource[F, HikariTransactor[F]] = {
    for {
      xa <- HikariTransactor.newHikariTransactor[F](
        config.driver,
        config.url,
        config.user,
        config.password,
        executionContext
      )
    } yield xa
  }

  def initialize[F[_]](transactor: HikariTransactor[F])(implicit as: Async[F]): F[Unit] = {
    transactor.configure { dataSource =>
      Async[F].pure {
        val flyWay = Flyway.configure().dataSource(dataSource).load()
        flyWay.migrate()
        ()
      }
    }
  }
}