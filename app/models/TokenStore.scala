package models

import scala.slick.driver.H2Driver.simple._

/**
 * Created by Ionut Andonescu <ionut.andonescu@pure360.com>
 */


case class TokenStore(id: Option[Int], user: String, token: String, createdAt: Long = System.currentTimeMillis)

class TokenStores(tag: Tag) extends Table[TokenStore](tag, "token_store") {

  def id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

  def user = column[String]("user", O.NotNull)

  def token = column[String]("token")

  def createdAt = column[Long]("created_at")

  def * = (id.?, user, token, createdAt) <>(TokenStore.tupled, TokenStore.unapply)
}

object TokenStores {

  lazy val all = TableQuery[TokenStores]


  def findByUser(user : String)(implicit session: Session) = all.filter(f => f.user === user).firstOption

  def findByToken(token : String) (implicit session: Session) = all.filter(_.token == token).firstOption


}

