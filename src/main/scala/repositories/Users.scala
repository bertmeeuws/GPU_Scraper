package com.scala.repositories

import java.util.UUID


case class User(id: Long, username: String, password: String)
case class UserWithOutId(username: String, password: String)