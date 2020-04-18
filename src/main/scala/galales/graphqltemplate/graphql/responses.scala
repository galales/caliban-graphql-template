package galales.graphqltemplate.graphql

import java.time.LocalDateTime

object responses {

  final case class Elem(id: String, description: String, createdTime: LocalDateTime)

  final case class Pagination[A](items: List[A], previous: Option[String], next: Option[String])

}
