package galales.graphqltemplate.service

import galales.graphqltemplate.config.Configurations
import zio.{Has, RIO, Task}

package object configservice {

  type ConfigService = Has[ConfigService.Service]

  object ConfigService {

    trait Service {
      def getConfigurations: Task[Configurations]
    }

  }
  def getConfigurations: RIO[ConfigService, Configurations] = RIO.accessM[ConfigService](_.get.getConfigurations)
}
