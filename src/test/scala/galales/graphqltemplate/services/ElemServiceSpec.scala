package galales.graphqltemplate.services

import caliban.CalibanError.ExecutionError
import galales.graphqltemplate.datasource.database.ElemRecord
import galales.graphqltemplate.graphql.Order
import galales.graphqltemplate.graphql.internals.InnerListElems
import galales.graphqltemplate.graphql.requests.{GetElem, ListElems}
import galales.graphqltemplate.graphql.responses.{Elem, Pagination}
import galales.graphqltemplate.service.configservice.ConfigServiceProd
import galales.graphqltemplate.service.elemrepository.ElemRepositoryInMem
import galales.graphqltemplate.service.elemservice
import galales.graphqltemplate.service.elemservice.ElemServiceProd
import galales.graphqltemplate.{AppEnvironment, buildEnv}
import zio.test.Assertion._
import zio.test._
import zio.{Ref, ZLayer}

object ElemServiceSpec {
  import ElemServiceSuite._

  val getSuite: Spec[Any, TestFailure[Throwable], TestSuccess] =
    suite("Query Operations")(
      testM("Get existing element") {
        assertM {
          for {
            data <- Ref.make(List(elemRecord1, elemRecord2))
            env     = getEnv(data)
            request = GetElem("id1")
            result <- elemservice.getElem(request).provideLayer(env)
          } yield result
        }(equalTo(elemRecord1.toElem))
      },
      testM("Get non existing element") {
        assertM {
          for {
            data <- Ref.make(List(elemRecord1, elemRecord2))
            env     = getEnv(data)
            request = GetElem("nonExistingId")
            result <- elemservice.getElem(request).provideLayer(env).run
          } yield result
        }(fails(isSubtype[ExecutionError](anything)))
      }
    )

  val listSuite: Spec[Any, TestFailure[Throwable], TestSuccess] =
    suite("List Operations")(
      testM("List elements Ascending") {
        assertM {
          for {
            data <- Ref.make(List(elemRecord1, elemRecord2, elemRecord3))
            env     = getEnv(data)
            request = InnerListElems("esc", 10, Order.ASC, None)
            result <- elemservice.listElems(request).provideLayer(env)
          } yield result
        }(equalTo(Pagination(List(elemRecord1.toElem, elemRecord2.toElem), None, None)))
      },
      testM("List elements Descending") {
        assertM {
          for {
            data <- Ref.make(List(elemRecord1, elemRecord2, elemRecord3))
            env     = getEnv(data)
            request = InnerListElems("esc", 10, Order.DESC, None)
            result <- elemservice.listElems(request).provideLayer(env)
          } yield result
        }(equalTo(Pagination(List(elemRecord2.toElem, elemRecord1.toElem), None, None)))
      },
      testM("Empty list") {
        assertM {
          for {
            data <- Ref.make(List(elemRecord1, elemRecord2, elemRecord3))
            env     = getEnv(data)
            request = InnerListElems("nonExistingDescription", 10, Order.ASC, None)
            result <- elemservice.listElems(request).provideLayer(env)
          } yield result
        }(equalTo(Pagination(List.empty[Elem], None, None)))
      },
      testM("List elements with pagination") {
        assertM {
          for {
            data <- Ref.make(List(elemRecord1, elemRecord2, elemRecord3))
            env     = getEnv(data)
            request = InnerListElems("esc", 1, Order.ASC, None)
            result <- elemservice.listElems(request).provideLayer(env)
          } yield result
        }(
          hasField("items", (r: Pagination[Elem]) => r.items, equalTo(List(elemRecord1.toElem))) &&
            hasField("previous", (r: Pagination[Elem]) => r.previous, isNone) &&
            hasField("next", (r: Pagination[Elem]) => r.next, isSome(anything))
        )
      },
      testM("List elements - next page") {
        assertM {
          for {
            data <- Ref.make(List(elemRecord1, elemRecord2, elemRecord3))
            env     = getEnv(data)
            request = InnerListElems("esc", 1, Order.ASC, None)
            result <- elemservice.listElems(request).provideLayer(env)
            nextPageRequest = ListElems("esc", 1, Order.ASC, None, result.next)
            nextPage <- elemservice.listElems(nextPageRequest.toInternal).provideLayer(env)
          } yield nextPage
        }(
          hasField("items", (r: Pagination[Elem]) => r.items, equalTo(List(elemRecord2.toElem))) &&
            hasField("previous", (r: Pagination[Elem]) => r.previous, isSome(anything)) &&
            hasField("next", (r: Pagination[Elem]) => r.next, isNone)
        )
      },
      testM("List elements - previous page") {
        assertM {
          for {
            data <- Ref.make(List(elemRecord1, elemRecord2, elemRecord3))
            env     = getEnv(data)
            request = InnerListElems("esc", 1, Order.ASC, None)
            result <- elemservice.listElems(request).provideLayer(env)
            nextPageRequest = ListElems("esc", 1, Order.ASC, None, result.next)
            nextPage <- elemservice.listElems(nextPageRequest.toInternal).provideLayer(env)
            previousPageRequest = ListElems("esc", 1, Order.ASC, nextPage.previous, None)
            previousPage <- elemservice.listElems(previousPageRequest.toInternal).provideLayer(env)
          } yield previousPage
        }(
          hasField("items", (r: Pagination[Elem]) => r.items, equalTo(List(elemRecord1.toElem))) &&
            hasField("previous", (r: Pagination[Elem]) => r.previous, isNone) &&
            hasField("next", (r: Pagination[Elem]) => r.next, isSome(anything))
        )
      }
    )
}

object ElemServiceSuite extends DefaultRunnableSpec {
  def getEnv(source: Ref[List[ElemRecord]]): ZLayer[Any, Throwable, AppEnvironment] =
    buildEnv(
      elemService = ElemServiceProd.live,
      elemRepository = ElemRepositoryInMem.mem(source),
      configService = ConfigServiceProd.live
    )

  def spec: Spec[Environment, TestFailure[Throwable], TestSuccess] =
    suite("Element Service")(ElemServiceSpec.getSuite, ElemServiceSpec.listSuite)

  val elemRecord1: ElemRecord = ElemRecord("id1", "description1", 1L)
  val elemRecord2: ElemRecord = ElemRecord("id2", "description2", 2L)
  val elemRecord3: ElemRecord = ElemRecord("id3", "aaa", 3L)

}
