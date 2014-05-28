import play.api._
import play.api.libs.json._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

object Global extends GlobalSettings {

  override def onStart(app: Application) = {
    Logger.info("Application has started")
    Logger.info("Configuration file: " + Play.current.configuration.getString("conf.filename").getOrElse("<none>"))
    models.ChatRoom.createTables
  }

  override def onStop(app: Application) = {
    Logger.info("Application shutdown...")
    models.ChatRoom.dropTables
  }

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    Logger.info(request.method + " " + request.uri)
    Logger.info(request.headers.toString)
    super.onRouteRequest(request)
  }

  override def onBadRequest(request: RequestHeader, error: String) = {
    Logger.error("Error 400: " + request.method + " " + request.uri + " " + error)
    Future(BadRequest(Json.obj(
      "method" -> request.method,
      "uri"    -> request.uri,
      "error" -> error
    )))
  }

  override def onHandlerNotFound(request: RequestHeader) = {
    Logger.error("Error 404: " + request.method + " " + request.uri)
    Future(NotFound(Json.obj(
      "method" -> request.method,
      "uri"    -> request.uri
    )))
  }

  override def onError(request: RequestHeader, exn: Throwable) = {
    Logger.error("Error 500: " + request.method + " " + request.uri + " " + exn.getMessage, exn)
    Future(InternalServerError(Json.obj(
      "method" -> request.method,
      "uri"    -> request.uri,
      "error"  -> exn.getMessage
    )))
  }
}