package galales

import galales.graphqltemplate.datasource.database.ElemRecord
import galales.graphqltemplate.service.configservice.{ConfigService, ConfigServiceProd}
import galales.graphqltemplate.service.elemrepository.{ElemRepository, ElemRepositoryInMem}
import galales.graphqltemplate.service.elemservice.{ElemService, ElemServiceProd}
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console

package object graphqltemplate {

  type RunTask[A]      = RIO[Console with Clock, A]
  type ResponseTask[A] = RIO[AppEnvironment, A]

  type AppEnvironment = ElemService with ElemRepository with ConfigService with Blocking with Console with Clock

  def getLiveEnv: ZLayer[Any, Throwable, AppEnvironment] = {

    val source: ZLayer[Any, Nothing, Has[Ref[List[ElemRecord]]]] =
      ZLayer.fromEffect(Ref.make(List(ElemRecord("id1", "description1", 1L), ElemRecord("id2", "description2", 2L))))
    buildEnv(
      elemService = ElemServiceProd.live,
      elemRepository = source >>> ElemRepositoryInMem.mem,
      configService = ConfigServiceProd.live
    )
  }

  def buildEnv(
    elemService: ZLayer[ElemRepository, Throwable, ElemService],
    elemRepository: ZLayer[ConfigService, Nothing, ElemRepository],
    configService: ZLayer[Any, Nothing, ConfigService]
  ): ZLayer[Any, Throwable, AppEnvironment] = {

    val repository: ZLayer[Any, Nothing, ElemRepository] = configService >>> elemRepository
    val service: ZLayer[Any, Throwable, ElemService]     = repository >>> elemService

    repository ++
      service ++
      configService ++
      Blocking.live ++
      Console.live ++
      Clock.live

  }

}
