package models.json.response

import com.github.tototoshi.play.json.JsonNaming
import models.{DateTimeFormat, Prefecture, Sex}
import play.api.libs.json.Json

case class User(
    id: Long,
    skypeId: String,
    name: String,
    sex: Option[Sex],
    prefecture: Option[Prefecture],
    bio: Option[String],
    image: Option[String]
  ) extends models.traits.User

object User extends DateTimeFormat {
  implicit val format = JsonNaming.snakecase(Json.format[User])

  implicit def u2u(user: models.User) =
    models.json.response.User(user.id.get, user.skypeId, user.name, user.sex, user.prefecture, user.bio, user.image)
}
