package controllers

import play.api.mvc._

object Application extends Controller {
  def index = Action(Ok)

  def options(url: String) = Action(NoContent)
}
