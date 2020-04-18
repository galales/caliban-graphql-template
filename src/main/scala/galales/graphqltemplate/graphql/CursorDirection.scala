package galales.graphqltemplate.graphql

sealed trait CursorDirection
object Previous extends CursorDirection
object Next     extends CursorDirection
