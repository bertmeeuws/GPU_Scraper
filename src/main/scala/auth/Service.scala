package scala.auth

import scala.auth.Auth.redis


object Service {

  case class User(username: String, password: String)

  def login(username: String, password: String): Option[User] = {
    redis.get(username.toLowerCase) match {
      case Some(x) if x == password => Some(User(username, password))
      case _ => None
    }
  }
}