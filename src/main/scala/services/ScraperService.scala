package com.services

import org.jsoup._

import scala.domain.entities._
import sttp.client3._
import io.circe.generic.auto._
import sttp.client3.circe._

import scala.annotation.tailrec

trait Scraper[T] {
  def getAllProducts(gpuName: String, url: String): List[Product]
}

sealed trait Stores

case object Alternate extends Stores {
  override def toString() = "alternate"
}
case object Megekko extends Stores {
  override def toString() = "megekko"

}
case object Amazon extends Stores {
  override def toString() = "amazon"

}

case class Product(name: String, price: Option[Double], url: String, brand: String, inStock: Boolean, store: Stores)

object Scraper {
  def apply[T <: Stores](gpuName: String, url: String)(implicit scraper: Scraper[T]): List[Product] =
    scraper.getAllProducts(gpuName, url)

  implicit val megekkoScraper: Scraper[Megekko.type] = new Scraper[Megekko.type] {
    override def getAllProducts(gpuName: String, url: String): List[Product] = {
      val request =
        basicRequest.post(uri"https://www.megekko.nl/pages/zoeken/v4/v4.php").response(asJson[RootInterface])

      val requestWithFormdata = request.multipartBody(Seq(multipart("zoek", "GTX 4080")))

      val backend  = HttpClientSyncBackend()
      val response = requestWithFormdata.send(backend)

      val rawProducts = response.body.getOrElse(RootInterface(Array[MegekkoArticle]())).zoek

      rawProducts.foldLeft(List[Product]())((acc, x) => {
        val product = Product(x.prodname, x.price.toDoubleOption, x.link, "FILL IN", true, Megekko)

        acc :+ product
      })
    }
  }

  implicit val amazonScraper: Scraper[Amazon.type] = new Scraper[Amazon.type] {
    override def getAllProducts(gpuName: String, url: String): List[Product] = List()
  }
}
