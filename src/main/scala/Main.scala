

import cats._
import cats.effect._
import org.jsoup._
import com.services.{Alternate, Megekko, Product, Scraper}
import sttp.client3.circe._

import java.net.URLEncoder
import scala.collection.convert.ImplicitConversions.{`collection AsScalaIterable`, `iterable AsScalaIterable`, `list asScalaBuffer`}
import sttp.client3._
import io.circe.generic.auto._
import org.http4s.server.Router

import scala.api.http.Server.{allRoutesComplete, directorRoutes, storesRoutes}
import scala.concurrent.ExecutionContext
import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import com.comcast.ip4s.IpLiteralSyntax
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers._
import org.http4s.implicits._
import org.http4s.server._



object Init extends IOApp {
  import scala.domain.entities._
  val run = IO.println("Hello, World!")


  val gpuName = "4080"
  val query = "Geforce GTX 4080"

  val url = s"https://www.alternate.be/listing.xhtml?q=${URLEncoder.encode(query, "UTF-8")}"


  val products = Semigroup[List[Product]].combine(Scraper[Alternate.type](gpuName, url), Scraper[Megekko.type ](gpuName, url))


  override def run(args: List[String]): IO[ExitCode] = {
    /*
        val apis = Router(
          "/api" -> storesRoutes[IO],
          "/api" -> directorRoutes[IO]
        ).orNotFound
    */

    EmberServerBuilder.default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(allRoutesComplete[IO])
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)


  }
}