package galales.graphqltemplate.service.elemrepository

import caliban.CalibanError.ExecutionError
import galales.graphqltemplate.datasource.database.{ElemCursor, ElemRecord, ElemRecordsPage}
import galales.graphqltemplate.graphql.{Next, Order, Previous}
import galales.graphqltemplate.graphql.internals.InnerListElems
import galales.graphqltemplate.graphql.requests.CreateElem
import zio.{Has, Ref, Task, ZLayer}

object ElemRepositoryInMem {

  def mem: ZLayer[Has[Ref[List[ElemRecord]]], Nothing, ElemRepository] =
    ZLayer.fromService[Ref[List[ElemRecord]], ElemRepository.Service] { source =>
      new ElemRepository.Service {

        def getElem(id: String): Task[ElemRecord] =
          source.get.flatMap(
            _.find(_.id == id)
              .fold[Task[ElemRecord]](Task.fail(ExecutionError(s"Unable to find record with id $id")))(Task.succeed(_))
          )

        def listElems(request: InnerListElems): Task[ElemRecordsPage] =
          source.get.map { list =>
            val offset: Int = request.cursor.map(_.offset).getOrElse(0)
            val resultList = list
              .filter(_.description.contains(request.description))
              .sortWith(
                (e1, e2) =>
                  request.order match {
                    case Order.ASC  => e1.createdTime <= e2.createdTime
                    case Order.DESC => e1.createdTime > e2.createdTime
                }
              )
              .slice(offset, offset + request.limit)

            val prevCursor: Option[ElemCursor] =
              if (offset == 0) None else Some(ElemCursor((offset - request.limit).max(0), Previous))
            val nextCursor: Option[ElemCursor] =
              if (offset + request.limit >= list.length) None
              else Some(ElemCursor(offset + request.limit, Next))

            ElemRecordsPage(resultList, prevCursor, nextCursor)
          }

        def createElem(request: CreateElem): Task[ElemRecord] = ???
        def deleteElem(id: String): Task[Boolean]             = ???
      }
    }

}
