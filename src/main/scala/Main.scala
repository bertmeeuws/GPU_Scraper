package scala

import cats.effect._
import java.net.URLEncoder
import cats.implicits._
import scala.api.http.Server



object Init extends IOApp {
  val run = IO.println("Hello, World!")


  val gpuName = "4080"
  val query = "Geforce GTX 4080"

  val url = s"https://www.alternate.be/listing.xhtml?q=${URLEncoder.encode(query, "UTF-8")}"


  //val products = Semigroup[List[Product]].combine(Scraper[Alternate.type](gpuName, url), Scraper[Megekko.type ](gpuName, url))


  override def run(args: List[String]): IO[ExitCode] = {
    Server.create()
  }
}