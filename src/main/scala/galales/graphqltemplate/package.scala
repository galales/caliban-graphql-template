package galales

import galales.graphqltemplate.datasource.database.ElemRecord
import galales.graphqltemplate.service.configservice.{ConfigService, ConfigServiceTest}
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

  val getLiveEnv: Task[ZLayer[Any, Throwable, AppEnvironment]] = {

    val source = Ref.make(List(ElemRecord("id1", "description1", 1L), ElemRecord("id2", "description2", 2L)))

    source.map(
      s =>
        buildEnv(
          elemService = ElemServiceProd.live,
          elemRepository = ElemRepositoryInMem.mem(s),
          configService = ConfigServiceTest.test
      )
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
