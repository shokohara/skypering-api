package controllers

import jp.t2v.lab.play2.auth._
import models.ColumnTypes._
import models.PortableJodaSupport._
import models.slick.{Tweets, Users}
import models.{Prefecture, Sex, Tweet, _}
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.i18n.Lang
import play.api.libs.json.Json
import play.api.mvc._
import play.api.Play
import com.github.nscala_time.time.Imports._

object TweetsController extends Controller with LoginLogout with AuthConfigImpl with AuthElement {

  implicit val lang = Lang("ja")
  val MAX_COUNT = 200

  def create = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    models.Forms.postForm.bindFromRequest.fold(
      hasErrors => {
        BadRequest(hasErrors.errorsAsJson)
      },
      success => {
        val interval = Play.configuration.getInt("application.posts.interval").getOrElse(Int.MaxValue)
        val tweetOption = DB.withTransaction { implicit session =>
          val q: Query[(Tweets, Users), (Tweet, User), Seq] = for {
            t <- Tweets.table
            u <- Users.table
            if t.user_id === u.id
          } yield (t, u)
          q.sortBy(_._1.created_at desc).filter(_._1.created_at > DateTime.now.minusMillis(interval)).firstOption
        }
        tweetOption match {
          case Some(t) => {
            val second = ((interval + t._1.createdAt.getMillis - DateTime.now.getMillis) / 1000) + 1
            val message = s"連続投稿はできません。${second}秒後にお試しください。"
            val json = Json.toJson(Errors(List(message)))
            TooManyRequest(json)
          }
          case None => {
            val tu = Tweets.insert(userId = loggedIn.id.get, text = success, now = DateTime.now())
            Created(Json.toJson(models.json.response.Tweet.t2t(tu._1, tu._2)))
          }
        }
      }
    )
  }

  def read(
    sinceId: Long,
    count: Int,
    maxId: Long,
    sexes: List[Sex],
    prefectures: List[Prefecture],
    name: Option[String],
    skypeId: Option[String],
    text: Option[String]) = Action { implicit request =>
    val interval = Play.configuration.getInt("application.posts.crawl.interval").getOrElse(Int.MaxValue)
    val length = DB.withTransaction { implicit session =>
      val q: Query[(Tweets, Users), (Tweet, User), Seq] = for {
        t <- Tweets.table
        u <- Users.table
        if t.user_id === u.id
        if t.created_at > DateTime.now.minusMillis(interval)
        if t.deleted_at.?.isEmpty
        if u.source.? === "skypech.com"
        if u.deleted_at.?.isEmpty
      } yield (t, u)
      q.length.run
    }
    val tu = DB.withTransaction { implicit session =>
      val q: Query[(Tweets, Users), (Tweet, User), Seq] = for {
          t <- Tweets.table
          u <- Users.table
          if t.id > sinceId
          if t.id <= maxId
          if t.user_id === u.id
          if t.deleted_at.?.isEmpty
          if u.deleted_at.?.isEmpty
        } yield (t, u)
      val adjustedCount = if (MAX_COUNT < count) MAX_COUNT else count
      val q1 = (if (sexes.isEmpty) q else q.filter(_._2.sex inSetBind sexes))
      (if (prefectures.isEmpty) q1 else q1.filter(_._2.prefecture inSetBind prefectures))
        .filter(_._2.name like s"%${name.getOrElse("")}%")
        .filter(_._2.skype_id like s"%${skypeId.getOrElse("")}%")
        .filter(_._1.text like s"%${text.getOrElse("")}%")
        .sortBy(_._1.id desc)
        .take(adjustedCount)
        .list
    } map { case (t, u) =>
      models.json.response.Tweet.t2t(t, u)
    }
    Ok(Json.toJson(tu))
  }

  def remove = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    Forms.removePostForm.bindFromRequest.fold(
      hasErrors=> {
        BadRequest(hasErrors.errorsAsJson)
      },
      id => {
        val count = Tweets.delete(loggedIn.id.get, id)
        if (count > 0) Ok else NotFound
      }
    )
  }

  def histories(sinceId: Long, count: Int, maxId: Long) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val userId = loggedIn.id.get
    val tu = DB.withTransaction { implicit session =>
      val q: Query[(Tweets, Users), (Tweet, User), Seq] = for {
        t <- Tweets.table
        u <- Users.table
        if t.id > sinceId
        if t.id <= maxId
        if t.user_id === u.id
        if t.deleted_at.?.isEmpty
        if u.id === userId
        if u.deleted_at.?.isEmpty
      } yield (t, u)
      val adjustedCount = if (MAX_COUNT < count) MAX_COUNT else count
      q.sortBy(_._1.id desc).take(adjustedCount).list
    } map { case (t, u) =>
      models.json.response.Tweet.t2t(t, u)
    }
    Ok(Json.toJson(tu))
  }
}
