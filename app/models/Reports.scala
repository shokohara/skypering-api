package models

import PortableJodaSupport._
import models.slick.{Tweets, Users}
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.{DB, Session}

import scala.slick.driver.H2Driver.simple._

object Reports {
  val table = TableQuery[Reports]

  def insert(userId: Long, tweetId: Long) = DB.withTransaction { implicit session: Session =>
    val report: Report = models.Report(None, userId, tweetId, DateTime.now)
    table.insert(report)
  }
}

class Reports(tag: Tag) extends Table[Report](tag, "reports") {
  def id = column[Long]("id", O PrimaryKey, O AutoInc)

  def user_id = column[Long]("user_id")

  def tweet_id = column[Long]("tweet_id")

  def created = column[DateTime]("created")

  def * = (id.?, user_id, tweet_id, created) <>((Report.apply _).tupled, Report.unapply)

  def su_user_id = foreignKey("su_reports_user_id", user_id, Users.table)(_.id)

  def su_tweet_id = foreignKey("su_reports_tweet_id", tweet_id, Tweets.table)(_.id)
}
