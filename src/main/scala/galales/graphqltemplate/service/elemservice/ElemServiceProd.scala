package galales.graphqltemplate.service.elemservice

import caliban.CalibanError.ExecutionError
import galales.graphqltemplate.graphql.internals.InnerListElems
import galales.graphqltemplate.graphql.requests.{CreateElem, DeleteElem, GetElem}
import galales.graphqltemplate.graphql.responses.{Elem, Pagination}
import galales.graphqltemplate.service.elemrepository.ElemRepository
import zio.{Task, ZLayer}

object ElemServiceProd {

  val live: ZLayer[ElemRepository, Nothing, ElemService] =
    ZLayer.fromService[ElemRepository.Service, ElemService.Service] { elemRepository =>
      new ElemService.Service {
        override def getElem(request: GetElem): Task[Elem] =
          elemRepository.getElem(request.id).map(_.toElem)

        override def listElems(request: InnerListElems): Task[Pagination[Elem]] =
          elemRepository
            .listElems(request)
            .bimap(
              e => ExecutionError(s"Error on list elems: ${e.getMessage}"),
              t => Pagination(t.elems.map(_.toElem), t.previousToken, t.nextToken)
            )

        override def createElem(request: CreateElem): Task[Elem] =
          elemRepository.createElem(request).map(_.toElem)

        override def deleteElem(request: DeleteElem): Task[Boolean] =
          elemRepository.deleteElem(request.id)

      }
    }

}
