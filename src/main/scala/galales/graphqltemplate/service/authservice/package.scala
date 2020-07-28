package galales.graphqltemplate.service

import zio.{Has, RIO, Task}

package object authservice {
  type AuthService = Has[AuthService.Service]

  object AuthService {

    trait Service {
      def getUser: Task[AuthUser]
    }

  }
  def getUser: RIO[AuthService, AuthUser] = RIO.accessM[AuthService](_.get.getUser)
}
