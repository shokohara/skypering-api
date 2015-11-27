package controllers

import controllers.UsersController._
import jp.t2v.lab.play2.auth._
import models._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SessionController extends Controller with LoginLogout with AuthConfigImpl {

  def create = Action.async { implicit request =>
    Forms.signInForm.bindFromRequest.fold(
      hasErrors => Future.successful(BadRequest(hasErrors.errorsAsJson)),
      user => gotoLoginSucceeded(user.id)
    )
  }

  def delete = Action.async { implicit request =>
    gotoLogoutSucceeded
  }
}
