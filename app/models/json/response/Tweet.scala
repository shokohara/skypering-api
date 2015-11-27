package models.json.response

import com.github.tototoshi.play.json.JsonNaming
import models.DateTimeFormat
import org.joda.time.DateTime
import play.api.libs.json._

case class Tweet(id: Long, user: User, text: String, createdAt: DateTime, updatedAt: DateTime)

object Tweet extends DateTimeFormat {
  implicit val format = JsonNaming.snakecase(Json.format[Tweet])

  implicit def t2t(tweet: models.Tweet, user: models.User) =
    models.json.response.Tweet(tweet.id.get, user, tweet.text, tweet.createdAt, tweet.updatedAt)
}
