package scala.services.parser

import org.jsoup.select.Elements
import com.services.{Alternate, Product}
import scala.collection.convert.ImplicitConversions.{`collection AsScalaIterable`, `iterable AsScalaIterable`, `list asScalaBuffer`}



trait Parser[T] {
  def parse(html: Elements, gpuName: String): List[Product]
}


// This module is responsible for the HTML parsing
object Parser {
  def apply[T](html: Elements, gpuName: String)(implicit parser: Parser[T]): List[Product] = {
    parser.parse(html, gpuName)
  }

  implicit val alternateParser: Parser[Alternate.type ] = new Parser[Alternate.type ] {
    override def parse(html: Elements, gpuName: String): List[Product] = {
      html.foldLeft[List[Product]](List())((acc, element) => {
        val nameNode = element.select("div.font-weight-bold.product-name")
        val name = nameNode.textNodes().get(0).text()
        val url = element.select("a.card.align-content-center.productBox.boxCounter.text-dark").first().attr("href")

        if (name.contains(s"$gpuName ")) {
          val brand = nameNode.select("span.mr-1").text()
          val priceData = element.select("span.price").text()
          val priceParsed = priceData.substring(2).replace(".", "").replace(",", ".").toDoubleOption
          val isStock = element.select("div.col-auto.delivery-info.text-right span.font-weight-bold").text().contains("Op voorraad")

          val product = Product(name, priceParsed, url, brand, isStock, Alternate)
          product :: acc
        } else {
          acc
        }
      })
    }
  }
}