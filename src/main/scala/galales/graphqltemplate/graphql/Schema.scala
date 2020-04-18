package galales.graphqltemplate.graphql

import caliban.GraphQL.graphQL
import caliban.schema.GenericSchema
import caliban.wrappers.ApolloTracing.apolloTracing
import caliban.wrappers.Wrappers.{maxDepth, maxFields, printSlowQueries, timeout}
import caliban.{GraphQL, RootResolver}
import galales.graphqltemplate.graphql.requests.{CreateElem, DeleteElem, GetElem, ListElems}
import galales.graphqltemplate.graphql.responses._
import galales.graphqltemplate.service.elemservice
import galales.graphqltemplate.{AppEnvironment, ResponseTask}
import zio.clock.Clock
import zio.console.Console
import zio.duration._

import scala.language.postfixOps

final case class Queries(getElem: GetElem => ResponseTask[Elem], listElems: ListElems => ResponseTask[Pagination[Elem]])

final case class Mutations(
  createElem: CreateElem => ResponseTask[Elem],
  deleteElem: DeleteElem => ResponseTask[Boolean]
)

object Schema extends GenericSchema[AppEnvironment] with Scalars {

  val api: GraphQL[Console with Clock with AppEnvironment] =
    graphQL(
      RootResolver(
        Queries(getElem => elemservice.getElem(getElem), listElems => elemservice.listElems(listElems)),
        Mutations(createElem => elemservice.createElem(createElem), deleteElem => elemservice.deleteElem(deleteElem))
      )
    ) @@
      maxFields(200) @@
      maxDepth(30) @@
      timeout(3 seconds) @@
      printSlowQueries(500 millis) @@
      apolloTracing // wrapper for https://github.com/apollographql/apollo-tracing
}
