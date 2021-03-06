package galales.graphqltemplate

import caliban.Http4sAdapter
import cats.data.Kleisli
import cats.effect.Blocker
import galales.graphqltemplate.graphql.Schema
import galales.graphqltemplate.service.configservice
import org.http4s.StaticFile
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import zio._
import zio.blocking.Blocking
import zio.console.putStrLn
import zio.interop.catz._

object Main extends CatsApp {

  type ExampleTask[A] = RIO[ZEnv, A]

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    (for {
      blocker     <- ZIO.access[Blocking](_.get.blockingExecutor.asEC).map(Blocker.liftExecutionContext)
      env         <- getLiveEnv
      interpreter <- Schema.api.interpreter.map(_.provideCustomLayer(env))
      serverConfig <- configservice.getConfigurations.provideLayer(env).map(_.serverConfig)
      _ <- BlazeServerBuilder[ExampleTask]
        .bindHttp(serverConfig.port, serverConfig.host)
        .withHttpApp(
          Router[ExampleTask](
            "/api/graphql" -> CORS(Http4sAdapter.makeHttpService(interpreter)),
            "/ws/graphql"  -> CORS(Http4sAdapter.makeWebSocketService(interpreter)),
            "/graphiql"    -> Kleisli.liftF(StaticFile.fromResource("/graphiql.html", blocker, None))
          ).orNotFound
        )
        .resource
        .toManaged
        .useForever
    } yield 0).catchAll(err => putStrLn(err.toString).as(1))

}
