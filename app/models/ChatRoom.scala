package models

import scala.slick.driver.H2Driver.simple._

object ChatRoom {
  def createTables =
    database.withSession { implicit session =>
      Messages.ddl.create
    }

  def post(text: String): Unit =
    database.withSession { implicit session =>
      Messages += Message(text)
    }

  def messages: List[Message] =
    database.withSession { implicit session =>
      Messages.list
    }
}