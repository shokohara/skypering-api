package models

import org.joda.time.DateTime

case class User(
    id: Option[Long],
    role: Role,
    password: String,
    skypeId: String,
    name: String,
    sex: Option[Sex],
    prefecture: Option[Prefecture],
    bio: Option[String],
    image: Option[String],
    source: Option[String],
    createdAt: DateTime,
    updatedAt: DateTime,
    deletedAt: Option[DateTime]
    ) extends traits.User
