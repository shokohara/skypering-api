package models

import org.joda.time.DateTime

case class Report(id: Option[Long], user_id: Long, tweet_id: Long, created: DateTime)
