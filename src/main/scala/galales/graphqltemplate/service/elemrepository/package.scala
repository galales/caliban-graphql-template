package galales.graphqltemplate.service

import galales.graphqltemplate.datasource.database.{ElemRecord, ElemRecordsPage}
import galales.graphqltemplate.graphql.internals.InnerListElems
import galales.graphqltemplate.graphql.requests.CreateElem
import zio.{Has, RIO, Task}

package object elemrepository {

  type ElemRepository = Has[ElemRepository.Service]

  object ElemRepository {

    trait Service {
      def getElem(id: String): Task[ElemRecord]
      def listElems(request: InnerListElems): Task[ElemRecordsPage]
      def createElem(request: CreateElem): Task[ElemRecord]
      def deleteElem(id: String): Task[Boolean]
    }

  }

  def getElem(id: String): RIO[ElemRepository, ElemRecord] =
    RIO.accessM[ElemRepository](_.get.getElem(id))

  def listElems(request: InnerListElems): RIO[ElemRepository, ElemRecordsPage] =
    RIO.accessM[ElemRepository](_.get.listElems(request))

  def createElem(request: CreateElem): RIO[ElemRepository, ElemRecord] =
    RIO.accessM[ElemRepository](_.get.createElem(request))

  def deleteElem(id: String): RIO[ElemRepository, Boolean] =
    RIO.accessM[ElemRepository](_.get.deleteElem(id))

}
