package galales.graphqltemplate.datasource.database

final case class ElemRecordsPage(
  elems: List[ElemRecord],
  previousPage: Option[ElemCursor],
  nextPage: Option[ElemCursor]
) {
  def previousToken: Option[String] = previousPage.map(_.toToken)
  def nextToken: Option[String]     = nextPage.map(_.toToken)
}
