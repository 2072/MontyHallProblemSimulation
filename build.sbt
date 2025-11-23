import sbt.Keys.libraryDependencies

ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "3.7.4"

lazy val root = (project in file("."))
  .settings(
    name := "MontyHallProblem",

    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
  )
