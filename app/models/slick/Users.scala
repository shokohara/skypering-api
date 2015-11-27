package models.slick

import models.ColumnTypes._
import models.PortableJodaSupport._
import models.{Prefecture, Role, Sex, User}
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.{DB, Session}

class Users(tag: Tag) extends Table[User](tag, "users") {

  def id = column[Long]("id", O PrimaryKey, O AutoInc)

  def role = column[Role]("role")

  def password = column[String]("password")

  def skype_id = column[String]("skype_id")

  def name = column[String]("name")

  def sex = column[Sex]("sex", O Nullable)

  def prefecture = column[Prefecture]("prefecture", O Nullable)

  def bio = column[String]("bio", O Nullable)

  def image = column[String]("image", O Nullable)

  def source = column[String]("source", O Nullable)

  def created_at = column[DateTime]("created_at")

  def updated_at = column[DateTime]("updated_at")

  def deleted_at = column[DateTime]("deleted_at", O Nullable)

  def * = (id.?, role, password, skype_id, name, sex.?, prefecture.?, bio.?, image.?, source.?, created_at, updated_at, deleted_at.?) <>((User.apply _).tupled, User.unapply)
}

object Users {

  val table = TableQuery[Users]

  def autoInc(user: User): User = DB.withTransaction { implicit session =>
    val id: Long = (Users.table returning Users.table.map(_.id)) += user
    Users.table.filter(_.id === id).first
  }

  def updateUser(user: User): Long = updateUser(user.id.get, user.password, user.skypeId, user.name, user.sex, user.prefecture, user.bio, user.image, user.deletedAt)

  def updateUser(id: Long, password: String, skype_id: String, name: String, sex: Option[Sex], prefecture: Option[Prefecture] = None, bio: Option[String] = None, image: Option[String] = None, deleted_at: Option[DateTime] = None): Long = DB.withTransaction { implicit session: Session =>
    Users.table.filter(_.id === id)
      .map(u => (u.password, u.skype_id, u.name, u.sex.?, u.prefecture.?, u.bio.?, u.image.?, u.deleted_at.?))
      .update(password, skype_id, name, sex, prefecture, bio, image, deleted_at)
  }

  def findById(id: Long): Option[User] = DB.withTransaction { implicit session =>
    Users.table.filter(_.id === id).firstOption
  }
}
