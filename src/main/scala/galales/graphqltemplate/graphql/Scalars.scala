package galales.graphqltemplate.graphql

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import caliban.CalibanError.ExecutionError
import caliban.Value.StringValue
import caliban.schema.Schema.scalarSchema
import caliban.schema._

import scala.util.Try

trait Scalars {
  import Scalars._

  implicit val dateTimeSchema: Schema[Any, LocalDateTime] =
    scalarSchema("DateTime", Some(s"String value in the format $dateTimePattern"), dt => StringValue(dateToString(dt)))

  implicit val dateTimeArgBuilder: ArgBuilder[LocalDateTime] = ArgBuilder.string.flatMap(
    dateTime =>
      Try(stringToDate(dateTime))
        .fold(_ => Left(ExecutionError(s"Invalid DateTime $dateTime Please use the format $dateTimePattern")), Right(_))
  )

}

object Scalars {

  private val dateTimePattern   = "yyyy-MM-dd'T'HH:mm:ss"
  private val dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern)

  def stringToDate(str: String): LocalDateTime  = LocalDateTime.parse(str, dateTimeFormatter)
  def dateToString(date: LocalDateTime): String = date.format(dateTimeFormatter)

}
