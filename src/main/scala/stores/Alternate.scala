package com.scala.stores

import cats.effect.IO
import com.services.{ Product, Scraper }
import org.jsoup.Jsoup
import org.jsoup.select.Elements

import scala.annotation.tailrec
import scala.collection.convert.ImplicitConversions.`list asScalaBuffer`

object AlternateService {
  import com.services._

  def scrape(gpuName: String, url: String): IO[List[Product]] =
    for {
      _ <- IO.println(s"Scraping $url")
      products <- IO {
        fetchAllProducts(gpuName, url, isLastPage = false, List.empty[Product], page = 1)
      }
      _ <- IO.println(s"Found ${products.length} products")
    } yield {
      products
    }

  @tailrec def fetchAllProducts(gpuName: String,
                                url: String,
                                isLastPage: Boolean,
                                products: List[Product],
                                page: Int): List[Product] = {

    val doc  = Jsoup.connect(s"$url&page=${page}").get()
    val list = doc.select("div.grid-container.listing a")

    val isCurrentPageLast = doc.select("div.mt-2 > .text-center").text() match {
      case (s"$_-$max van $total resultaten") => if (max == total) true else false
      case _                                  => true
    }

    if (isLastPage) {
      val newProducts = alternateParser(list, gpuName)
      newProducts ::: products
    } else {
      val newProducts = alternateParser(list, gpuName)
      val combined    = newProducts ++ products

      fetchAllProducts(gpuName, url, isCurrentPageLast, combined, page + 1)
    }

  }

  def alternateParser(html: Elements, gpuName: String): List[Product] =
    html.foldLeft[List[Product]](List())((acc, element) => {
      val nameNode = element.select("div.font-weight-bold.product-name")
      println(nameNode)
      val name = nameNode.textNodes().get(0).text()

      val url = element.select("a.card.align-content-center.productBox.boxCounter.text-font").first().attr("href")

      if (name.contains(s"$gpuName ")) {
        val brand       = nameNode.select("span.mr-1").text()
        val priceData   = element.select("span.price").text()
        val priceParsed = priceData.substring(2).replace(".", "").replace(",", ".").toDoubleOption
        val isStock =
          element.select("div.col-auto.delivery-info.text-right span.font-weight-bold").text().contains("Op voorraad")

        val product = Product(name, priceParsed, url, brand, isStock, Alternate)
        product :: acc
      } else {
        acc
      }
    })

}
