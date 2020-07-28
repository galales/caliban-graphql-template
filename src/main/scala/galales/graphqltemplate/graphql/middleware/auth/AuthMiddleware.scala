//package galales.graphqltemplate.graphql.middleware.auth
//
//import caliban.Http4sAdapter
//import galales.graphqltemplate.service.authservice.{AuthService, AuthUser}
//import org.http4s.HttpRoutes
//import org.http4s.util.CaseInsensitiveString
//import zio.{RIO, Task, ZLayer}
//
//object AuthMiddleware {
//  type AuthTask[A] = RIO[AuthService, A]
//
//  def apply(route: HttpRoutes[AuthTask]): HttpRoutes[Task] =
//    Http4sAdapter.provideLayerFromRequest(
//      route,
//      _.headers.get(CaseInsensitiveString("token")) match {
//        case Some(value) => ZLayer.succeed(new AuthService.Service { override def getUser: Task[AuthUser] = value.value })
//        case None        => ZLayer.fail(MissingToken())
//      }
//    )
//}