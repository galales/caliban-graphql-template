package galales.graphqltemplate.graphql

import enumeratum._

sealed trait Order extends EnumEntry {
  def isAscending: Boolean = this match {
    case Order.ASC  => true
    case Order.DESC => false
  }
}

object Order extends Enum[Order] {
  val values = findValues

  case object ASC  extends Order
  case object DESC extends Order

}
