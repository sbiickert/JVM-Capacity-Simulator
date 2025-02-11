ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.2"

lazy val root = (project in file("."))
  .settings(
    name := "Capacity Simulator",
    idePackagePrefix := Some("ca.esri.capsim")
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
