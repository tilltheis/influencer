import sbt._

object Dependencies {
  lazy val scalaTest = Seq("org.scalatest" %% "scalatest" % "3.2.9" % Test)

  lazy val mockito = Seq("org.mockito" % "mockito-scala-scalatest_2.13" % "1.17.0" % Test)

  private val http4sVersion = "0.23.9"
  lazy val http4s = Seq(
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-twirl"        % http4sVersion,
    "org.http4s" %% "http4s-client"       % http4sVersion % Test
  )

  lazy val twirl = Seq("com.typesafe.play" %% "twirl-api" % "1.5.1")

  private val doobieVersion = "1.0.0-RC1"
  lazy val doobie = Seq(
    "org.tpolecat" %% "doobie-core"      % doobieVersion,
    "org.tpolecat" %% "doobie-hikari"    % doobieVersion, // HikariCP transactor.
    "org.tpolecat" %% "doobie-postgres"  % doobieVersion, // Postgres driver 42.3.1 + type mappings.
    "org.tpolecat" %% "doobie-scalatest" % doobieVersion % Test // ScalaTest support for typechecking statements.
  )

  lazy val cats = Seq("org.typelevel" %% "cats-effect" % "3.3.5")

  lazy val logback = Seq("ch.qos.logback" % "logback-classic" % "1.2.10")

}
