package controllers

import response.ResponseForm
import response.ResponseFormError
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.json.JsPath

import play.api.mvc._

import models._
import java.util.UUID

object ChatController extends Controller with AuthHelpers {
  implicit val format = Json.format[Message]
  implicit val responseFormErrorFormat = Json.format[ResponseFormError]
  implicit val responseFormFormat = Json.format[ResponseForm]

  val UserTokenHeader = "USER_TOKEN_HEADER"
  val TokenIdRegex = "^Bearer (.*)$".r

  def TokenDatabaseFormat(token: String) = s"($token)"

  def TokenHeaderFormat(token: String) = s"Bearer ($token)"

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
            case Some(dbUser) =>
              Ok(Json.toJson("user exists")).withHeaders((UserTokenHeader -> dbUser.token))
            case None =>
              // we don't have the user, so we create one
              val tokenId = TokenDatabaseFormat(UUID.randomUUID().toString)
              TokenStoreDao.save(new TokenStore(None, user, tokenId))
              Ok(Json.toJson("user created")).withHeaders((UserTokenHeader -> tokenId))

          }
        case _ =>

          //TODO: add a better validation here --- or it already has the header
          BadRequest(Json.toJson(Map("error" -> "no user in request")))

      }
  }

  def post() = AuthAction {
    (request, token) =>

      //TODO : add user to the message in db
      handleRequest(request)

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