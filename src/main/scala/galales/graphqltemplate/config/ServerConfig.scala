package galales.graphqltemplate.config

import scala.util.Try

final case class ServerConfig(host: String, port: Int)

object ServerConfig {
  def apply(): Try[ServerConfig] = Try {

    val host: String = Option(System.getenv("HOST"))
      .getOrElse(throw new IllegalArgumentException("Host Not Found"))
    val port: Int = Option(System.getenv("PORT"))
      .map(_.toInt)
      .getOrElse(throw new IllegalArgumentException("Port Not Found"))

    ServerConfig(host, port)
  }
}
