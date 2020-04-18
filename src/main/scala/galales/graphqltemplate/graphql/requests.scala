package galales.graphqltemplate.graphql

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
  ) extends PaginatedRequest

  final case class CreateElem(id: String, description: String)
  final case class DeleteElem(id: String)

}
