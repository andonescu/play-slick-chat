/**
 * Created by Ionut Andonescu <ionut.andonescu@pure360.com>
 */

import models.ChatRoom;
import org.specs2.mutable.Specification;

/**
 * Created by Ionut Andonescu <ionut.andonescu@pure360.com>
 */
class ChatRoomSpecs2 extends Specification {

  "Chat room.createTables" should {

    "add a blank database" in {

      try {
        ChatRoom.createTables
        ChatRoom.messages mustEqual Nil
      } finally {
        ChatRoom.dropTables
      }
    }

  }


  "Chat.post" should {

    "add a message database" in dbExample {

      ChatRoom.post("hi")
      ChatRoom.messages.head.text mustEqual "hi"

    }

  }

  def dbExample[A](fn: => A) = {
    try {
      ChatRoom.createTables
      fn
    } finally {
      ChatRoom.downcast
    }

  }

}
