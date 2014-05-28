import models.{TokenStore, TokenStoreDao}
import play.api.test.{FakeRequest, WithApplication, PlaySpecification}

/**
 * Created by Ionut Andonescu <ionut.andonescu@pure360.com>
 */
class ChatControllerTest extends PlaySpecification {


  "Chat Controller - login " should {

    "should register a new user 'John' " in new WithApplication {
      val result = controllers.ChatController.login(Some("John"))(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
    }


    "should give an already registered user called 'John' " in new WithApplication {
      TokenStoreDao.save(new TokenStore(None, "John", "adlkahdhakhdkh"))

      val result = controllers.ChatController.login(Some("John"))(FakeRequest())
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")

      header(controllers.ChatController.UserTokenHeader, result) must beEqualTo(Some("adlkahdhakhdkh"))
    }


    "should give an error if no user is supplied " in new WithApplication {

      val result = controllers.ChatController.login(None)(FakeRequest())
      status(result) must equalTo(BAD_REQUEST)
    }

  }
}
