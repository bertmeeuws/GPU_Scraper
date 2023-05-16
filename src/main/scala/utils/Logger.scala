package com.utils

import cats.effect.IO._
import cats.effect.{IO}
import cats.implicits._


object Logger {
  def log(msg: String): IO[Unit] = {
    println(msg)
    IO.delay(println(msg))
  }
}