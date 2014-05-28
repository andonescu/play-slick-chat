import models.{TokenStore, TokenStoreDao, ChatRoom}
import org.specs2.mutable.Specification

/**
 * Created by Ionut Andonescu <ionut.andonescu@pure360.com>
 */
class TokenStoreSpecs extends  Specification{
  "TokenStoreSpecs " should {

    "save a new user" in {

      try {
        ChatRoom.createTables

        TokenStoreDao.save(new TokenStore(None, "John", "adlkahdhakhdkh"))

        TokenStoreDao.findAllByUser("John").length mustEqual(1)


      } finally {
        ChatRoom.dropTables
      }
    }

    "saved user is John" in {

      try {
        ChatRoom.createTables

        TokenStoreDao.save(new TokenStore(None, "John", "adlkahdhakhdkh"))

        TokenStoreDao.findAllByUser("John").head.user mustEqual("John")


      } finally {
        ChatRoom.dropTables
      }
    }


  }
}
