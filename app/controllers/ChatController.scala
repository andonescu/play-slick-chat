package controllers

import play.api._
import play.api.libs.json._
import play.api.mvc._

import models._
import java.util.UUID

object ChatController extends Controller {
  implicit val format = Json.format[Message]

  val UserTokenHeader = "USER_TOKEN_HEADER"

  def search = Action { request =>
    Ok(Json.toJson(ChatRoom.messages))
  }

  def create = Action { request =>
    val json = request.body.asJson.getOrElse(JsNull)

    (json \ "text").validate[String].fold(
      invalid = { errors =>
        BadRequest(errors.toString)
      },
      valid = { text =>
        ChatRoom.post(text)
        Ok(Json.toJson(ChatRoom.messages))
      }
    )
  }

  def login(username: Option[String]) = Action {
    request =>

      username match {
        case Some(user) =>
          val dbRecords = TokenStoreDao.findAllByUser(user)

          dbRecords match {
            case head :: _ =>
              Ok(Json.toJson("user exists")).withHeaders((UserTokenHeader -> head.token))
            case _ =>
              // we don't have the user, so we create one
              val tokenId = UUID.randomUUID().toString
              TokenStoreDao.save(new TokenStore(None, user, tokenId))
              Ok(Json.toJson("user exists")).withHeaders((UserTokenHeader -> tokenId))

          }
        case _ =>

          //TODO: add a better validation here
          BadRequest(Json.toJson(Map("error" -> "no user in request")))

      }
  }

  def post() = Action {
    request =>


      val json = request.body.asJson.getOrElse(JsNull)

      (json \ "message").validate[String].fold(
        invalid = { errors =>
          BadRequest(errors.toString)
        },
        valid = { message =>
         ???
        }
      )

  }
}