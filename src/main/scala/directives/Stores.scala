package com.scala.directives

import cats.data._
import cats.effect._
import org.http4s._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import com.services.{Alternate, Amazon, Megekko, Stores}

object StoresDirective {
  implicit val storesQueryParamDecoder: QueryParamDecoder[Stores] = (value: QueryParameterValue) => {
    value.value match {
      case "alternate" => Validated.valid(Alternate)
      case "megekko" => Validated.valid(Megekko)
      case "amazon" => Validated.valid(Amazon)
      case _ => Validated.invalidNel(ParseFailure("Invalid store", "Invalid store"))
    }
  }

  object StoreQueryParamMatcher extends OptionalValidatingQueryParamDecoderMatcher[Stores]("store")

  object GPUNameParamMatcher extends QueryParamDecoderMatcher[String]("gpuName")

  def storesRoutes: HttpRoutes[IO] = {
    val dsl = Http4sDsl[IO]
    import dsl._
    HttpRoutes.of[IO] {
      case GET -> Root / "stores" :? StoreQueryParamMatcher(store) +& GPUNameParamMatcher(gpuName) =>
        store match {
          case Some(x) => Ok(x.toString)
          case None => BadRequest()
        }
      case GET -> Root / "stores" / UUIDVar(storeId) / "information" => ???
      case GET -> Root / "stores" / "testing" => Ok("Testing")
    }
  }
}