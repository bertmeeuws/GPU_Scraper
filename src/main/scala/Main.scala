

import cats.effect._
import org.jsoup._
import com.services.{Alternate, Product, Scraper}

import java.net.URLEncoder
import scala.collection.convert.ImplicitConversions.{`collection AsScalaIterable`, `iterable AsScalaIterable`, `list asScalaBuffer`}


object Init extends IOApp.Simple {
  val run = IO.println("Hello, World!")

  val gpuName = "4080"
  val query = "Geforce GTX 4080"

  val url = s"https://www.alternate.be/listing.xhtml?q=${URLEncoder.encode(query, "UTF-8")}"

  val products = Scraper[Alternate.type](gpuName, url)

  println(products)


}