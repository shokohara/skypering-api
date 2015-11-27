package models.json

import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.Json

case class ReportRequest(tweetId: Long)

object ReportRequest {
  implicit val format = JsonNaming.snakecase(Json.format[ReportRequest])
}
