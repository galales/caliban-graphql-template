package galales

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

  def getLiveEnv: ZLayer[Any, Throwable, AppEnvironment] =
    buildEnv(
      elemService = ElemServiceProd.live,
      elemRepository = ElemRepositoryInMem.mem,
      configService = ConfigServiceProd.live
    )

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