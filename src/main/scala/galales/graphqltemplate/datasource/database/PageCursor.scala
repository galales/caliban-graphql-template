package galales.graphqltemplate.datasource.database

import caliban.CalibanError.ExecutionError
import galales.graphqltemplate.graphql.CursorDirection
import galales.graphqltemplate.util.codecs.{fromBase64, toBase64}
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._

sealed trait PageCursor {
  def toToken: String
  def direction: CursorDirection
}

object PageCursor {
  def fromToken[A <: PageCursor: Decoder](token: String): A = {
    val decoded = fromBase64(token)
    decode[A](decoded).getOrElse(throw ExecutionError("Unable to decode page cursor"))
  }
}

final case class ElemCursor(offset: Int, direction: CursorDirection) extends PageCursor {
  def toToken: String = toBase64(this.asJson.noSpaces)
}
