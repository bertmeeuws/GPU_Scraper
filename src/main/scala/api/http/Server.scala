package scala.api.http

import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.headers._
import org.http4s.implicits._
import org.http4s.server._
import com.services.{Alternate, Amazon, Megekko, Stores}

object Server {
  implicit val storesQueryParamDecoder: QueryParamDecoder[Stores] = new QueryParamDecoder[Stores] {
    override def decode(value: QueryParameterValue): ValidatedNel[ParseFailure, Stores] = {
      value.value match {
        case "alternate" => Validated.valid(Alternate)
        case "megekko" => Validated.valid(Megekko)
        case "amazon" => Validated.valid(Amazon)
        case _ => Validated.invalidNel(ParseFailure("Invalid store", "Invalid store"))
      }
    }
  }
  object StoreQueryParamMatcher extends QueryParamDecoderMatcher[Stores]("store")
  object GPUNameParamMatcher extends QueryParamDecoderMatcher[String]("gpuName")

    def storesRoutes[F[_]: Monad]: HttpRoutes[F] = {
        val dsl = Http4sDsl[F]
        import dsl._
        HttpRoutes.of[F] {
          case GET -> Root / "stores" :? StoreQueryParamMatcher(store) +& GPUNameParamMatcher(gpuName) =>
            store match {
              case Alternate => Ok("Alternate")
              case Megekko => Ok("Megekko")
              case Amazon => Ok("Amazon")
              case _ => BadRequest()
            }
          case GET -> Root / "stores" / UUIDVar(storeId) / "information" => ???
        }
    }

  def directorRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "directors" / UUIDVar(directorId) => ???
    }
  }

  def allRoutes[F[_]: Monad]: HttpRoutes[F] = {
    storesRoutes[F] <+> directorRoutes[F]
  }

  def allRoutesComplete[F[_]: Monad]: HttpApp[F] = {
    allRoutes[F].orNotFound
  }

