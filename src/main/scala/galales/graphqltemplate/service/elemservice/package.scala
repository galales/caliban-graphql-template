package galales.graphqltemplate.service

import galales.graphqltemplate.graphql.requests.{CreateElem, DeleteElem, GetElem, ListElems}
import galales.graphqltemplate.graphql.responses.{Elem, Pagination}
import zio.{Has, RIO, Task}

package object elemservice {

  type ElemService = Has[ElemService.Service]

  object ElemService {

    trait Service {
      def getElem(request: GetElem): Task[Elem]
      def listElems(request: ListElems): Task[Pagination[Elem]]
      def createElem(request: CreateElem): Task[Elem]
      def deleteElem(request: DeleteElem): Task[Boolean]
    }

  }
  def getElem(request: GetElem): RIO[ElemService, Elem] =
    RIO.accessM[ElemService](_.get.getElem(request))

  def listElems(request: ListElems): RIO[ElemService, Pagination[Elem]] =
    RIO.accessM[ElemService](_.get.listElems(request))

  def createElem(request: CreateElem): RIO[ElemService, Elem] =
    RIO.accessM[ElemService](_.get.createElem(request))

  def deleteElem(request: DeleteElem): RIO[ElemService, Boolean] =
    RIO.accessM[ElemService](_.get.deleteElem(request))

}
