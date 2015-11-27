package models

import play.api.db.slick.Config.driver.simple._

object ColumnTypes {

  implicit val roleColumnType = MappedColumnType.base[Role, Int](
    role => role.DB_VALUE,
    int => Role.toRole(int)
  )

  def toSex(int: Int) = int match {
    case x if x == Male.DB_VALUE => Male
    case x if x == Female.DB_VALUE => Female
  }

  implicit val sexColumnType = MappedColumnType.base[Sex, Int](
    sex => sex.DB_VALUE,
    int => toSex(int)
  )

  def toPrefecture(string: String) = Prefecture.toPrefecture(string)

  implicit val prefectureColumnType = MappedColumnType.base[Prefecture, String](
    prefecture => prefecture.DB_VALUE,
    string => toPrefecture(string)
  )
}
