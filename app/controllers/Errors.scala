package controllers

import com.github.tototoshi.play.json.JsonNaming
import play.api.libs.json.Json

case class Errors(errors: List[String])

object Errors {
  implicit val format = JsonNaming.snakecase(Json.format[Errors])
}
