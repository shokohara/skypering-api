package controllers

import models.{Prefecture, Sex}
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import play.api.mvc.QueryStringBindable
import play.api.mvc.QueryStringBindable._

import scala.util.Try

object QueryBindable {

  implicit def bindableSex = new QueryStringBindable[Sex] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Sex]] = {
      params(key).headOption.map { sexString =>
        try {
          Right(Sex.toSex(sexString))
        } catch {
          case e: MatchError => Left(e.getMessage())
        }
      }
    }

    def unbind(key: String, value: Sex) = s"sex=${value.toString}"
  }

  implicit def bindablePrefecture = new QueryStringBindable[Prefecture] {
    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Prefecture]] = {
      params(key).headOption.map { sexString =>
        try {
          Right(Prefecture.toPrefecture(sexString))
        } catch {
          case e: MatchError => Left(e.getMessage())
        }
      }
    }

    def unbind(key: String, value: Prefecture) = s"prefecture=${value.toString}"
  }
}
