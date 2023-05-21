package scala

import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.syntax.all._
import cats.implicits._

import java.net.URLEncoder
import cats.implicits._

import scala.api.http.Server

object Init extends IOApp {
  //val run = IO.println("Hello, World!")

  //val products = Semigroup[List[Product]].combine(Scraper[Alternate.type](gpuName, url), Scraper[Megekko.type ](gpuName, url))

  override def run(args: List[String]): IO[ExitCode] =
    Server.create("application.conf")
}
