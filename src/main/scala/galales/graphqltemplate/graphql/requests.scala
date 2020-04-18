package galales.graphqltemplate.graphql

import galales.graphqltemplate.datasource.database.ElemCursor
import galales.graphqltemplate.graphql.internals.{InnerListElems, getCursor}
import io.circe.generic.auto._

object requests {

  sealed trait PaginatedRequest {
    def limit: Int
    def previous: Option[String]
    def next: Option[String]
  }

  final case class GetElem(id: String)

  final case class ListElems(
    description: String,
    limit: Int,
    order: Order,
    previous: Option[String],
    next: Option[String]
  ) extends PaginatedRequest {
    def toInternal: InnerListElems =
      InnerListElems(description, limit, order, getCursor[ElemCursor](previous, next))
  }

  final case class CreateElem(id: String, description: String)
  final case class DeleteElem(id: String)

}
