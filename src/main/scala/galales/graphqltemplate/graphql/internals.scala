package galales.graphqltemplate.graphql

import galales.graphqltemplate.datasource.database.{ElemCursor, PageCursor}
import io.circe.Decoder

object internals {

  def getCursor[A <: PageCursor: Decoder](previous: Option[String], next: Option[String]): Option[A] =
    previous.orElse(next).map(PageCursor.fromToken[A])

  sealed trait InnerPagination {
    def limit: Int
    def cursor: Option[PageCursor]
  }

  final case class InnerListElems(description: String, limit: Int, order: Order, cursor: Option[ElemCursor])
      extends InnerPagination
}
