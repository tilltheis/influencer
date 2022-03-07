import Dependencies._

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "influencer",
    fork := true,
    run / connectInput := true,
    javaOptions += "-Duser.timezone=UTC",
    libraryDependencies ++= cats ++ scalaTest ++ mockito ++ twirl ++ http4s ++ doobie ++ logback
  )
  .enablePlugins(SbtTwirl)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
