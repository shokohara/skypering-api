package models.json

import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.Json

case class RemoveRequest(tweetId: Long)

object RemoveRequest {
  implicit val format = JsonNaming.snakecase(Json.format[RemoveRequest])
}
