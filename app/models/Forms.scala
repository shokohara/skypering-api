package models

import play.api.data.Forms._
import play.api.data._
import play.api.data.format.{Formats, Formatter}
import play.api.data.validation.Constraints._
import play.api.data.validation._

case class ProfileForm(name: String, skypeId: String, sex: Sex, prefecture: Prefecture, bio: Option[String])

case class SignInForm(id: Long, password: String)

case class FilterForm(notKnown: Boolean, male: Boolean, female: Boolean)

object Forms {
  val TEXT_MIN = 1
  val TEXT_MAX = 1000
  val BIO_MIN = 1
  val BIO_MAX = 255
  val MIN_MONTHES = 1
  val MAX_MONTHES = 12
  val SKYPE_ID_MIN = 6
  val SKYPE_ID_MAX = 32
  val SKYPE_ID_PATTERN = s"[a-zA-Z0-9.,-_]{$SKYPE_ID_MIN,$SKYPE_ID_MAX}".r

  def parsing[T](parse: String => T, errMsg: String, errArgs: Seq[Any])
                (key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
    Formats.stringFormat.bind(key, data).right.flatMap { s =>
      scala.util.control.Exception.allCatch[T]
        .either(parse(s))
        .left.map(e => Seq(FormError(key, errMsg, errArgs)))
    }
  }

  implicit def sexFormat: Formatter[Sex] = new Formatter[Sex] {

    override val format = Some(("format.sex", Nil))

    def bind(key: String, data: Map[String, String]) = parsing(Sex.toSex, "error.sex", Nil)(key, data)

    def unbind(key: String, value: Sex) = Map(key -> value.toString)
  }

  implicit def prefectureFormat: Formatter[Prefecture] = new Formatter[Prefecture] {

    override val format = Some(("format.prefecture", Nil))

    def bind(key: String, data: Map[String, String]) = parsing(Prefecture.toPrefecture, "error.prefecture", Nil)(key, data)

    def unbind(key: String, value: Prefecture) = Map(key -> value.toString)
  }

  lazy val sexCheckConstraint: Constraint[Sex] = Constraint("constraints.sex") { plainText =>
    val errors = plainText match {
      case Male | Female => Nil
      case _ => Seq(ValidationError("Password is all letters"))
    }
    if (errors.isEmpty) Valid else Invalid(errors)
  }

  lazy val sexCheck: Mapping[Sex] = of[Sex].verifying(sexCheckConstraint)

  lazy val prefectureCheckConstraint: Constraint[Prefecture] = Constraint("constraints.prefecture") { plainText =>
    val errors = plainText match {
      case Hokkaido | Aomori | Iwate | Miyagi | Akita | Yamagata | Fukushima | Ibaraki | Tochigi | Gunma | Saitama | Chiba | Tokyo | Kanagawa | Niigata | Toyama | Ishikawa | Fukui | Yamanashi | Nagano | Gifu | Shizuoka | Aichi | Mie | Shiga | Kyoto | Osaka | Hyogo | Nara | Wakayama | Tottori | Shimane | Okayama | Hiroshima | Yamaguchi | Tokushima | Kagawa | Ehime | Kochi | Fukuoka | Saga | Nagasaki | Kumamoto | Oita | Miyazaki | Kagoshima | Okinawa => Nil
      case _ => Seq(ValidationError("Password is all letters"))
    }
    if (errors.isEmpty) Valid else Invalid(errors)
  }

  lazy val prefectureCheck: Mapping[Prefecture] = of[Prefecture].verifying(prefectureCheckConstraint)

  lazy val postForm = Form(single("text" -> nonEmptyText(TEXT_MIN, TEXT_MAX)))

  lazy val searchForm = Form(single("text" -> optional(text())))

  lazy val signInForm = Form(mapping(
    "id" -> longNumber,
    "password" -> nonEmptyText
  )(SignInForm.apply)(SignInForm.unapply))

  lazy val userCreateForm = Form(mapping(
    "name" -> nonEmptyText,
    "skype_id" -> nonEmptyText.verifying(pattern(SKYPE_ID_PATTERN)),
    "sex" -> sexCheck,
    "prefecture" -> prefectureCheck,
    "bio" -> optional(text(BIO_MIN, BIO_MAX))
  )(ProfileForm.apply)(ProfileForm.unapply))

  lazy val filterForm = Form(mapping(
    "not_known" -> boolean,
    "male" -> boolean,
    "female" -> boolean
  )(FilterForm.apply)(FilterForm.unapply))

  lazy val removePostForm = Form(single("id" -> longNumber))
}
