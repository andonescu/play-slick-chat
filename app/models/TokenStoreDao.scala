package models

import scala.slick.driver.H2Driver.simple._

/**
 * Created by Ionut Andonescu <ionut.andonescu@pure360.com>
 */
object TokenStoreDao {

  def findAllByUser(user: String) = database.withSession(implicit session =>
    TokenStores.findAllByUser(user)

  )


  def save(tokenStore: TokenStore) = database.withSession(implicit session =>
    TokenStores.all += tokenStore

  )

}
