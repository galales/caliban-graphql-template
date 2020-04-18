package galales.graphqltemplate.datasource.database

import java.time.{LocalDateTime, ZoneOffset}

import galales.graphqltemplate.graphql.responses.Elem

final case class ElemRecord(id: String, description: String, createdTime: Long) {
  def toElem: Elem = Elem(id, description, LocalDateTime.ofEpochSecond(createdTime, 0, ZoneOffset.UTC))
}
