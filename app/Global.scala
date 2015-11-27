import org.joda.time.DateTimeZone
import play.api.mvc.Results._
import play.api.mvc._
import play.api.{Application, Logger, UsefulException}
import play.filters.csrf.CSRFFilter
import play.filters.gzip.GzipFilter

import scala.concurrent.Future

object Global extends WithFilters(LoggingFilter, JsonFilter ,OriginFilter, new GzipFilter(), CSRFFilter()) {

  override def beforeStart(app: Application): Unit = {
    super.beforeStart(app)
    Logger.info("Application is starting...")
    DateTimeZone.setDefault(DateTimeZone.UTC)
  }

  override def onStart(app: Application) {
    super.onStart(app)
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    super.onStop(app)
    Logger.info("Application shutdown...")
  }
}
