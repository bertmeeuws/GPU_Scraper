package com.services

import org.jsoup._
import scala.collection.convert.ImplicitConversions.{`collection AsScalaIterable`, `iterable AsScalaIterable`, `list asScalaBuffer`}


trait Scraper[T] {
  def getAllProducts(gpuName: String, url: String): List[Product]
}

sealed trait Stores

case object Alternate extends Stores
case object Megekko extends Stores
case object Amazon extends Stores

case class Product(name: String, price: Double, url: String, brand: String, inStock: Boolean)

object Scraper {
  def apply[T](gpuName: String ,url: String)(implicit scraper: Scraper[T]): List[Product] = {
    scraper.getAllProducts(gpuName, url)
  }


  implicit val alternateScraper: Scraper[Alternate.type] = new Scraper[Alternate.type] {
    def getAllProducts(gpuName: String, url: String) = {
      val doc = Jsoup.connect(url).get()

      val list = doc.select("div.grid-container.listing a")
      val lastPage = doc.select("a.btn.btn-outline-gray.m-1").hasClass("disabled")

      list.foldLeft[List[Product]](List())((acc, element) => {
        val nameNode = element.select("div.font-weight-bold.product-name")
        val name = nameNode.textNodes().get(0).text()
        val url = element.select("a.card.align-content-center.productBox.boxCounter.text-dark").first().attr("href")

        if (name.contains(s"$gpuName ")) {
          val brand = nameNode.select("span.mr-1").text()
          val priceData = element.select("span.price").text()
          val priceParsed = priceData.substring(2).replace(".", "").replace(",", ".").toDoubleOption.getOrElse(0.toDouble)
          val isStock = element.select("div.col-auto.delivery-info.text-right span.font-weight-bold").text().contains("Op voorraad")

          val product = Product(name, priceParsed, url, brand, isStock)
          product :: acc
        } else {
          acc
        }
      })
    }
  }
}

