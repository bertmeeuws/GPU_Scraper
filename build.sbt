ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "gpu_scraper"
  )

libraryDependencies += "org.jsoup" % "jsoup" % "1.15.3"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.4.8"
