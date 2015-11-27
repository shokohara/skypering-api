package models.slick

import models.ColumnTypes._
import models.PortableJodaSupport._
import models.{Prefecture, Sex, Tweet, User}
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.{DB, Session}

object Tweets {
  val table = TableQuery[Tweets]

  val TAKE = 500

  def list(userId: Long): Seq[Tweet] = DB.withTransaction { implicit session =>
    Tweets.table.filter(_.user_id === userId).filter(_.deleted_at.?.isEmpty).run
  }

  def list: Seq[(Tweet, User)] = DB.withTransaction { implicit session =>
    val q: Query[(Tweets, Users), (Tweet, User), Seq] = for {
      t <- Tweets.table
      u <- Users.table
      if t.user_id === u.id
      if t.deleted_at.?.isEmpty
    } yield (t, u)
    filteredList(q)
  }

  def list(user: User): Seq[(Tweet, User)] = DB.withTransaction { implicit session =>
    val q: Query[(Tweets, Users), (Tweet, User), Seq] = for {
      t <- Tweets.table
      u <- Users.table
      if u.id === user.id.get
      if t.user_id === u.id
      if t.deleted_at.?.isEmpty
    } yield (t, u)
    filteredList(q)
  }

  def insert(tweet: Tweet): (Tweet, User) = DB.withTransaction { implicit session =>
    val id = (Tweets.table returning Tweets.table.map(_.id)) += tweet
    val q = for {
      t <- Tweets.table
      u <- Users.table
      if t.user_id === u.id
      if t.id === id
    } yield (t, u)
    q.first
  }

  def insert(userId: Long, text: String, now: DateTime): (Tweet, User) = insert(Tweet(None, userId, text, now, now, None))

  def findLastPost(userId: Long) = DB.withTransaction { implicit session: Session =>
    val q: Query[(Tweets, Users), (Tweet, User), Seq] = for {
      t <- Tweets.table
      u <- Users.table
      if t.user_id === u.id
    } yield (t, u)
    q.sortBy(_._1.created_at desc).firstOption
  }

  def filteredList(
      sexes: List[Sex],
      prefectures: List[Prefecture]): List[(Tweet, User)] = DB.withTransaction { implicit session: Session =>
    val q: Query[(Tweets, Users), (Tweet, User), Seq] = for {
      t <- Tweets.table
      u <- Users.table
      if t.user_id === u.id
      if t.deleted_at.?.isEmpty
      if u.sex inSetBind sexes
      if u.prefecture inSetBind prefectures
    } yield (t, u)
    filteredList(q)
  }

  def filteredList(sexes: List[Sex]): List[(Tweet, User)] = DB.withTransaction { implicit session: Session =>
    val q: Query[(Tweets, Users), (Tweet, User), Seq] = for {
      t <- Tweets.table
      u <- Users.table
      if t.user_id === u.id
      if t.deleted_at.?.isEmpty
      if u.sex inSetBind sexes
    } yield (t, u)
    filteredList(q)
  }

  def filteredList(q: Query[(Tweets, Users), (Tweet, User), Seq])(implicit session: Session): List[(Tweet, User)] =
    q.sortBy(_._1.created_at desc).take(TAKE).list

  def delete(user_id: Long, tweet_id: Long) = DB.withTransaction { implicit session: Session =>
    Tweets.table.filter(_.user_id === user_id).filter(_.id === tweet_id).map(_.deleted_at).update(DateTime.now)
  }

  def updateTweet(tweet: Tweet): Long = updateTweet(tweet.id.get, tweet.text, tweet.deletedAt)

  def updateTweet(id: Long, text: String, deleted: Option[DateTime]): Long = DB.withTransaction { implicit session: Session =>
    Tweets.table.filter(_.id === id).map(t => (t.text, t.deleted_at.?)).update(text, deleted)
  }
}

class Tweets(tag: Tag) extends Table[Tweet](tag, "tweets") {
  def id = column[Long]("id", O PrimaryKey, O AutoInc)

  def user_id = column[Long]("user_id")

  def text = column[String]("text")

  def created_at = column[DateTime]("created_at")

  def updated_at = column[DateTime]("updated_at")

  def deleted_at = column[DateTime]("deleted_at", O Nullable)

  def * = (id.?, user_id, text, created_at, updated_at, deleted_at.?) <>((Tweet.apply _).tupled, Tweet.unapply)

  def su_user_id = foreignKey("su_tweets_user_id", user_id, Users.table)(_.id)
}
