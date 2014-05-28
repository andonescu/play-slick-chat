package controllers

import play.api._
import response.ResponseForm
import response.ResponseFormError
import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.Reads._
import play.api.libs.json.JsPath

import play.api.libs.functional.syntax._

import play.api.mvc._

import models._
import java.util.UUID
import play.api.data.validation.ValidationError

object ChatController extends Controller {
  implicit val format = Json.format[Message]
  implicit val responseFormErrorFormat = Json.format[ResponseFormError]
  implicit val responseFormFormat = Json.format[ResponseForm]

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

          //TODO: add a better validation here --- or it already has the header
          BadRequest(Json.toJson(Map("error" -> "no user in request")))

      }
  }

  def post() = Action {
    request =>
      val optionHeader = request.headers.get(UserTokenHeader)
      if (!optionHeader.isDefined) {
        Unauthorized(" invalid request, no header")
      } else {
        handleRequest(request)
      }
  }


  /**
   * http://www.playframework.com/documentation/2.1.0/ScalaJsonCombinators
   * http://mandubian.com/2012/09/08/unveiling-play-2-dot-1-json-api-part1-jspath-reads-combinators/
   */
  val validateMessage: Reads[String] = (JsPath \ "message").read[String]


  def handleRequest(request: Request[AnyContent]): SimpleResult = {
    val json = request.body.asJson.getOrElse(JsNull)

    val errors = validateMessage.reads(json).fold(
      invalid = { errors =>

        errors.map(error => error._2.map(errorMessage => new ResponseFormError(error._1.path.map(_.toString).mkString(""), errorMessage.message)))

      },
      valid = { message =>
        ChatRoom.post(message)
        List()
      }
    )


    if (errors.isEmpty) {
      Ok(Json.toJson(new ResponseForm(true)))
    } else {
      BadRequest(Json.toJson(errors))
    }
  }
}