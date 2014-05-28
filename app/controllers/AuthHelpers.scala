package controllers

import models._
import play.api.libs.json._
import play.api.mvc._
import play.api.mvc.BodyParsers._

/**
 * Created by Ionut Andonescu <ionut.andonescu@pure360.com>
 */
trait AuthHelpers {
  this: Results =>


  val TokenIdRegex = "^Bearer (.*)$".r


  def AuthAction(func: (Request[AnyContent], TokenStore) => Result): Action[AnyContent] =
    AuthAction(parse.anyContent)(func)

  def AuthAction(parser: BodyParser[AnyContent])(func: (Request[AnyContent], TokenStore) => Result): Action[AnyContent] =
    Action(parser) { implicit request =>
      val optionalHeader =
        request.headers.get(ChatController.UserTokenHeader) orElse
          request.getQueryString("auth")

      val token =
        for {
          TokenIdRegex(id) <- optionalHeader
          token <- TokenStoreDao.read(id)
        } yield token

      token match {
        case Some(token) => func(request, token)
        case None => Unauthorized(Json.obj("status" -> "error"))
      }
    }

  implicit class AuthResultOps(result: Result) {
    def withAuthToken(token: TokenStore): Result =
      result.withHeaders(
        "Authorization" -> s"Bearer ${token.token}"
      )
  }

}