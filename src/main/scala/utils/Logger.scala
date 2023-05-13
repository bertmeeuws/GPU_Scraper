package com.utils

import cats.effect.IO._
import cats.effect.{IO, Sync}
import cats._
import cats.implicits._
import cats.syntax.all._


object Logger {
  def log[F[_]](msg: String)(implicit S: Sync[F]): F[Unit] = {
    println(msg)
    S.delay(println(msg))
  }
}