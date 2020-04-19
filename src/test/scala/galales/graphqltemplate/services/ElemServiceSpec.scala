package galales.graphqltemplate.services

import caliban.CalibanError.ExecutionError
import galales.graphqltemplate.datasource.database.ElemRecord
import galales.graphqltemplate.graphql.Order
import galales.graphqltemplate.graphql.internals.InnerListElems
import galales.graphqltemplate.graphql.requests.GetElem
import galales.graphqltemplate.graphql.responses.Pagination
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
      testM("List elements") {
        assertM {
          for {
            data <- Ref.make(List(elemRecord1, elemRecord2, elemRecord3))
            env     = getEnv(data)
            request = InnerListElems("esc", 10, Order.ASC, None)
            result <- elemservice.listElems(request).provideLayer(env)
          } yield result
        }(equalTo(Pagination(List(elemRecord1.toElem, elemRecord2.toElem), None, None)))
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
