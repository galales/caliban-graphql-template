package galales.graphqltemplate.service.elemrepository

import galales.graphqltemplate.datasource.database.{ElemRecord, ElemRecordsPage}
import galales.graphqltemplate.graphql.requests.{CreateElem, ListElems}
import galales.graphqltemplate.service.configservice.ConfigService
import galales.graphqltemplate.datasource.database.ElemRecord
import zio.{Task, ZLayer}

object ElemRepositoryInMem {

  val mem: ZLayer[ConfigService, Nothing, ElemRepository] =
    ZLayer.fromService[ProgramConfig.Service, ElemRepository.Service] { config =>
      new ElemRepository.Service {

        def getElem(id: String): Task[ElemRecord] = ???
        def listElems(request: ListElems): Task[ElemRecordsPage] = ???
        def createElem(request: CreateElem): Task[ElemRecord] = ???
        def deleteElem(id: String): Task[Boolean] = ???
      }
    }

}
