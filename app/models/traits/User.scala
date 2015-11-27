package models.traits

import models.{Prefecture, Sex}

abstract trait User {
  val skypeId: String
  val name: String
  val sex: Option[Sex]
  val prefecture: Option[Prefecture]
  val bio: Option[String]
  val image: Option[String]
}
