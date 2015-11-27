package models

import org.joda.time.DateTime

case class Tweet(
    id: Option[Long],
    userId: Long,
    text: String,
    createdAt: DateTime,
    updatedAt: DateTime,
    deletedAt: Option[DateTime])
