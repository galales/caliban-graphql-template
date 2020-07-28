package galales.graphqltemplate.graphql.middleware.auth

sealed trait AuthErrors extends Throwable

case class MissingToken() extends AuthErrors
case class Authentication() extends AuthErrors
case class Unauthorized() extends AuthErrors
