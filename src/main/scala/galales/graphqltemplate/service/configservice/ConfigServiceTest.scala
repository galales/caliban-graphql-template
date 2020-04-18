package galales.graphqltemplate.service.configservice

import galales.graphqltemplate.config._
import galales.graphqltemplate.config.ServerConfig
import org.log4s.getLogger
import zio.{Task, ZLayer}

import scala.util.Try

object ConfigServiceTest {

  val test: ZLayer[Any, Nothing, ConfigService] = ZLayer.succeed {
    new ConfigService.Service {
      private[this] val logger = getLogger

      override def getConfigurations: Task[Configurations] =
        Task
          .fromTry {
            for {
              serverConfig <- Try(serverConfig)
            } yield Configurations(serverConfig)
          }
          .mapError { e =>
            logger.error(e.getMessage)
            e
          }

      val serverConfig: ServerConfig = ServerConfig("0.0.0.0", 8088)
    }
  }
}
