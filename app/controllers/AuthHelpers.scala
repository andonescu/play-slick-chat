package controllers

import models._
import play.api.libs.json._
import play.api.mvc._
import play.api.mvc.BodyParsers._
import play.api.Logger

/**
 * Created by Ionut Andonescu <ionut.andonescu@pure360.com>
 */
trait AuthHelpers {
  this: Results =>

  def AuthAction(func: (Request[AnyContent], TokenStore) => Result): Action[AnyContent] =
    AuthAction(parse.anyContent)(func)

  def AuthAction(parser: BodyParser[AnyContent])(func: (Request[AnyContent], TokenStore) => Result): Action[AnyContent] =
    Action(parser) { implicit request =>
      val optionalHeader =
        request.headers.get(ChatController.UserTokenHeader) orElse
          request.getQueryString("auth")
      Logger.info(" auth header : " + optionalHeader)
      val token =
        for {
          ChatController.TokenIdRegex(id) <- optionalHeader
          token <- TokenStoreDao.read(id)
        } yield token

      Logger.info(" token : " + optionalHeader)

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