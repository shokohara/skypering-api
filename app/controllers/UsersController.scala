package controllers

import jp.t2v.lab.play2.auth._
import models.slick.Users
import models.{User, _}
import org.apache.commons.lang3.RandomStringUtils
import org.joda.time.DateTime
import play.api.Play
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.i18n.Lang
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object UsersController extends Controller with LoginLogout with AuthConfigImpl with AuthElement {

  implicit val lang = Lang("ja")
  lazy val ID: String = Play.maybeApplication.flatMap(_.configuration.getString("session.id")) getOrElse ("id")
  lazy val PASSWORD: String = Play.maybeApplication.flatMap(_.configuration.getString("session.password")) getOrElse ("password")
  val MAX_COUNT = 200

  def generatePassword() = RandomStringUtils.randomAlphanumeric(1024)

  def create = Action.async { implicit request =>
    models.Forms.userCreateForm.bindFromRequest.fold(
      hasErrors => Future { BadRequest(hasErrors.errorsAsJson) },
      success => {
        val password = generatePassword()
        val now = DateTime.now()
        val user = User(
          None,
          NormalUser,
          password,
          success.skypeId,
          success.name,
          Some(success.sex),
          Some(success.prefecture),
          None,
          None,
          None,
          now,
          now,
          None)
        val id = Users.autoInc(user).id.get
        val idCookie = Cookie(name = ID, value = id.toString, maxAge = Some(Int.MaxValue), httpOnly = false)
        val passwordCookie = Cookie(name = PASSWORD, value = password, maxAge = Some(Int.MaxValue), httpOnly = false)
        val cookies = Array(idCookie, passwordCookie)
        gotoLoginSucceeded(id).map(_.withCookies(cookies: _*))
      }
    )
  }

  def read = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    DB.withTransaction { implicit session =>
      Users.table.filter(_.id === loggedIn.id.get).firstOption
    } match {
      case Some(user) => Ok(Json.toJson(models.json.response.User.u2u(user)))
      case None => NotFound
    }
  }

  def update = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    models.Forms.userCreateForm.bindFromRequest.fold(
      hasErrors => BadRequest(hasErrors.errorsAsJson),
      success => {
        val user: User = loggedIn.copy(
          skypeId = success.skypeId,
          password = loggedIn.password,
          name = success.name,
          sex = Some(success.sex),
          prefecture = Some(success.prefecture))
        val id = Users.updateUser(user)
        val newUser = Users.findById(id).get
        Ok(Json.toJson(models.json.response.User.u2u(newUser)))
      }
    )
  }
}
