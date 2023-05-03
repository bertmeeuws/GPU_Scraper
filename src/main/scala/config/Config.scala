package com.config

import cats.effect.{IO, Resource}
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._


object Config {
  case class ServerConfig(host: String, port: Int)

  case class DatabaseConfig(
    driver: String,
    url: String,
    user: String,
    password: String,
    threadPoolSize: Int
  )

  case class Config(server: ServerConfig, database: DatabaseConfig)

  def load(configFile: String = "application.conf"): Resource[IO, Config] = {
    Resource.eval(ConfigSource.fromConfig(ConfigFactory.load(configFile)).loadF[IO, Config]())
  }
}