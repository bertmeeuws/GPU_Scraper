package com.scala.stores

import sttp.client3.circe.asJson
import sttp.client3.{ basicRequest, multipart, HttpClientSyncBackend, UriContext }

import io.circe.generic.auto._

import scala.domain.entities.{ MegekkoArticle, RootInterface }

object MegekkoService {
  def scrape(gpuName: String, url: String): List[Product] = {
    val request =
      basicRequest.post(uri"https://www.megekko.nl/pages/zoeken/v4/v4.php").response(asJson[RootInterface])

    val requestWithFormdata = request.multipartBody(Seq(multipart("zoek", "GTX 4080")))

    val backend  = HttpClientSyncBackend()
    val response = requestWithFormdata.send(backend)

    val rawProducts = response.body.getOrElse(RootInterface(Array[MegekkoArticle]())).zoek

    import com.services._

    rawProducts.foldLeft(List[Product]())((acc, x) => {
      val product = Product(x.prodname, x.price.toDoubleOption, x.link, "FILL IN", true, Megekko)

      acc :+ product
    })
  }
}
