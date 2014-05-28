import models.{ChatRoom, TokenStore, TokenStoreDao}
import play.api.libs.json.Json
import play.api.test.{FakeRequest, WithApplication, PlaySpecification}

/**
 * Created by Ionut Andonescu <ionut.andonescu@pure360.com>
 */
class ChatControllerPostTest extends PlaySpecification {


  "Chat Controller - post " should {

    "let the user to post a new message if it has the token in header" in new WithApplication {
      TokenStoreDao.save(new TokenStore(None, "John", "adlkahdhakhdkh"))

      val result = controllers.ChatController.post()(FakeRequest().withHeaders(
        (controllers.ChatController.UserTokenHeader -> "adlkahdhakhdkh")).withJsonBody(Json.toJson(Map("message" -> "hahaha"))))
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")


      ChatRoom.messages.size must beEqualTo(1)
    }


    "'BadRequest' if no message in the request" in new WithApplication {
      val result = controllers.ChatController.post()(FakeRequest().withHeaders(
        (controllers.ChatController.UserTokenHeader -> "adlkahdhakhdkh")))
      status(result) must equalTo(BAD_REQUEST)
    }

    "'UNAUTHORIZED' if no token in the header but it has the message" in new WithApplication {

      val result = controllers.ChatController.post()(FakeRequest().withJsonBody(Json.toJson(Map("message" -> "hahaha"))))
      status(result) must equalTo(UNAUTHORIZED)
    }
  }

  "'UNAUTHORIZED' if no token in the header and no message" in new WithApplication {

    val result = controllers.ChatController.post()(FakeRequest())
    status(result) must equalTo(UNAUTHORIZED)
  }
}