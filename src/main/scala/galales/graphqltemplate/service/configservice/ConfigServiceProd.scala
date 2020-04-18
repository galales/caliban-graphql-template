package galales.graphqltemplate.service.configservice

import galales.graphqltemplate.config._
import org.log4s.getLogger
import zio.{Task, ZLayer}

object ConfigServiceProd {

  val live: ZLayer[Any, Nothing, ConfigService] = ZLayer.succeed {
    new ConfigService.Service {
      private[this] val logger = getLogger

      override def getConfigurations: Task[Configurations] =
        Task
          .fromTry {
            for {
              serverConfig <- ServerConfig()
            } yield Configurations(serverConfig)
          }
          .mapError { e =>
            logger.error(e.getMessage)
            e
          }
    }
  }
}
