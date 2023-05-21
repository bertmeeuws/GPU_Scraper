package com.scala.directives

import cats.data._
import cats.effect._
import org.http4s._
import org.http4s.dsl.impl._
import com.services.{ Alternate, Amazon, Megekko, Stores }
import com.scala.stores.AlternateService
import org.http4s.circe._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.dsl.Http4sDsl

import java.net.URLEncoder

object StoresDirective {

  implicit val productEncoder: EntityEncoder[IO, List[com.services.Product]] =
    jsonEncoderOf[IO, List[com.services.Product]]

  implicit val productDecoder: EntityDecoder[IO, List[com.services.Product]] = jsonOf[IO, List[com.services.Product]]

  implicit val storesQueryParamDecoder: QueryParamDecoder[Stores] = (value: QueryParameterValue) => {
    value.value match {
      case "alternate" => Validated.valid(Alternate)
      case "megekko"   => Validated.valid(Megekko)
      case "amazon"    => Validated.valid(Amazon)
      case _           => Validated.invalidNel(ParseFailure("Invalid store", "Invalid store"))
    }
  }

  object StoreQueryParamMatcher extends QueryParamDecoderMatcher[Stores]("store")

  object GPUNameParamMatcher extends OptionalQueryParamDecoderMatcher[String]("gpuName")

  def storesRoutes: HttpRoutes[IO] = {
    val dsl = Http4sDsl[IO]
    import dsl._
    HttpRoutes.of[IO] {
      case GET -> Root / "stores" :? StoreQueryParamMatcher(store) +& GPUNameParamMatcher(gpuName) =>
        store match {
          case Alternate => {
            val gpuName = "4080"
            val query   = "Geforce GTX 4080"
            val url     = s"https://www.alternate.be/listing.xhtml?q=${URLEncoder.encode(query, "UTF-8")}"
            AlternateService.scrape(gpuName, url).flatMap { x =>
              {
                Ok(x.asJson)
              }
            }

          }
          case Megekko => Ok("Megekko")
          case Amazon  => Ok("Amazon")
          case _       => Ok("Unknown store")
        }
    }
  }
}
