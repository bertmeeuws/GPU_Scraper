package com.services

import org.jsoup._

import scala.collection.convert.ImplicitConversions.{`collection AsScalaIterable`, `iterable AsScalaIterable`, `list asScalaBuffer`}
import scala.domain.entities._
import sttp.client3._
import io.circe.generic.auto._
import sttp.client3.circe._
import cats._
import cats.effect.IO
import cats.effect.unsafe.implicits.global

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.services.parser.Parser

trait Scraper[T] {
  def getAllProducts(gpuName: String, url: String): List[Product]
}

sealed trait Stores

case object Alternate extends Stores
case object Megekko extends Stores
case object Amazon extends Stores

case class Product(name: String, price: Option[Double], url: String, brand: String, inStock: Boolean, store: Stores)

object Scraper {
  def apply[T <: Stores](gpuName: String ,url: String)(implicit scraper: Scraper[T]):List[Product] = {
    scraper.getAllProducts(gpuName, url)
  }

  implicit val megekkoScraper: Scraper[Megekko.type ] = new Scraper[Megekko.type] {
    override def getAllProducts(gpuName: String, url: String): List[Product] = {
      val request = basicRequest.post(
        uri"https://www.megekko.nl/pages/zoeken/v4/v4.php").response(asJson[RootInterface])

      val requestWithFormdata = request.multipartBody(Seq(multipart("zoek", "GTX 4080")))

      val backend = HttpClientSyncBackend()
      val response = requestWithFormdata.send(backend)

      val rawProducts = response.body.getOrElse(RootInterface(Array[MegekkoArticle]())).zoek

      rawProducts.foldLeft(List[Product]())((acc, x) => {
        val product = Product(x.prodname, x.price.toDoubleOption, x.link, "FILL IN", true, Megekko)

        acc :+ product
      })
    }
  }

  implicit val amazonScraper: Scraper[Amazon.type ] = new Scraper[Amazon.type] {
    override def getAllProducts(gpuName: String, url: String): List[Product] = List()
  }


  implicit val alternateScraper: Scraper[Alternate.type] = new Scraper[Alternate.type] {
    override def getAllProducts(gpuName: String, url: String) = {
      getProducts(gpuName, url, false, List(), 1)
    }
  }

  @tailrec def getProducts(gpuName: String, url: String, isLastPage: Boolean, products: List[Product], page: Int): List[Product] = {
    val doc = Jsoup.connect(s"$url&page=${page}").get()
    val list = doc.select("div.grid-container.listing a")

    val isCurrentPageLast = doc.select("div.mt-2 > .text-center").text() match {
      case (s"$_-$max van $total resultaten") => if (max == total) true else false
      case _ => true
    }

    if (isLastPage) {
      val newProducts = Parser[Alternate.type](list, gpuName)

      newProducts ::: products
    } else {
      val newProducts = Parser[Alternate.type](list, gpuName)
      val combined = newProducts ++ products

      getProducts(gpuName, url, isCurrentPageLast, combined, page + 1)
    }

  }
}

