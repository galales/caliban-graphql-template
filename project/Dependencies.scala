import Versions._
import sbt._

object Dependencies {

  object enumeratum {
    lazy val namespace  = "com.beachape"
    lazy val enumeratum = namespace %% "enumeratum" % enumeratumVersion
    lazy val circle     = namespace %% "enumeratum-circe" % enumeratumCirceVersion
  }

  object aws {
    lazy val namespace = "com.amazonaws"
    lazy val dynamodb  = namespace % "aws-java-sdk-dynamodb" % awsDynamoDbVersion

  }

  object zio {
    lazy val namespace = "dev.zio"
    lazy val test      = namespace %% "zio-test" % zioVersion
    lazy val testsbt   = namespace %% "zio-test-sbt" % zioVersion
  }

  object circe {
    lazy val namespace = "io.circe"
    lazy val core      = namespace %% "circe-core" % circeVersion
    lazy val generic   = namespace %% "circe-generic" % circeVersion
    lazy val parser    = namespace %% "circe-parser" % circeVersion

  }

  object caliban {
    lazy val namespace     = "com.github.ghostdogpr"
    lazy val caliban       = namespace %% "caliban" % calibanVersion
    lazy val calibanCats   = namespace %% "caliban-cats" % calibanVersion
    lazy val calibanHttp4s = namespace %% "caliban-http4s" % calibanVersion
  }

  object typesafe {
    lazy val namespace = "com.typesafe"
    lazy val config    = namespace % "config" % "1.4.0"
  }

  object logback {
    lazy val namespace = "ch.qos.logback"
    lazy val classic   = namespace % "logback-classic" % logbackVersion
  }

  object mockito {
    lazy val namespace = "org.mockito"
    lazy val all       = namespace %% "mockito-scala" % mockitoVersion
  }

  object http4s {
    lazy val namespace = "org.http4s"

    lazy val dsl         = namespace %% "http4s-dsl"          % http4sVersion
    lazy val blazeClient = namespace %% "http4s-blaze-client" % http4sVersion
  }
  object auth0 {
    lazy val namespace = "com.auth0"
    lazy val javaJwt   = namespace % "java-jwt" % javaJwtVersion
    lazy val jwksRsa   = namespace % "jwks-rsa" % jwksRsaVersion
  }

  object testcontainer {
    lazy val namespace  = "org.testcontainers"
    lazy val localstack = namespace % "localstack" % testcontainersVersion
  }

  object Jars {
    lazy val `server`: Seq[ModuleID] = Seq(
      // For making Java 12 happy
      "javax.annotation" % "javax.annotation-api" % "1.3.2" % "compile",
      //
      auth0.javaJwt            % "compile",
      auth0.jwksRsa            % "compile",
      caliban.caliban          % "compile",
      caliban.calibanHttp4s    % "compile",
      typesafe.config          % "compile",
      logback.classic          % "compile",
      aws.dynamodb             % "compile",
      circe.core               % "compile",
      circe.generic            % "compile",
      circe.parser             % "compile",
      http4s.dsl               % "compile",
      http4s.blazeClient       % "compile",
      enumeratum.enumeratum    % "compile",
      enumeratum.circle        % "compile",
      mockito.all              % "compile,test", // TODO Not working without compile...?
      zio.test                 % "test",
      zio.testsbt              % "test",
      testcontainer.localstack % "test"
    )
  }
}
