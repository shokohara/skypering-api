import play.api.http.HeaderNames._
import play.api.http.HttpVerbs._
import play.api.libs.json.Json
import play.api.mvc.{RequestHeader, EssentialAction, EssentialFilter}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.http.ContentTypes._

object JsonFilter extends EssentialFilter {
  def apply(next: EssentialAction) = new EssentialAction {
    def apply(requestHeader: RequestHeader) = {
      next(requestHeader).map { r =>
        //        r.header.headers.find(_ == CONTENT_TYPE -> JSON) match {
        //          case Some(_) => r.copy(body = Json.parse(r.body))
        //          case None => r
        //        }
        r
      }
    }
  }
}
