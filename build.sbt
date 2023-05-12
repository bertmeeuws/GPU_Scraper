ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "gpu_scraper"
  )

  addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.2" cross CrossVersion.full)
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")

val circeVersion = "0.14.5"


libraryDependencies += "org.jsoup" % "jsoup" % "1.15.3"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.4.8"
libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.8.13"
libraryDependencies += "com.softwaremill.sttp.client3" %% "circe" % "3.8.15"


libraryDependencies += "io.circe" %% "circe-generic" % circeVersion
libraryDependencies += "io.circe" %% "circe-core" % circeVersion
libraryDependencies += "io.circe" %% "circe-parser" % circeVersion


val http4sVersion = "0.23.18"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-circe"        % http4sVersion,
)

libraryDependencies += "com.auth0" % "java-jwt" % "4.2.1"

val JwtVersion = "1.2.0"

libraryDependencies += "dev.profunktor" %% "http4s-jwt-auth" % JwtVersion

libraryDependencies += "org.tpolecat" %% "doobie-core"      % "1.0.0-RC1"
libraryDependencies += "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC1"
libraryDependencies += "org.tpolecat"          %% "doobie-h2"            % "1.0.0-RC1"
libraryDependencies += "org.tpolecat"          %% "doobie-hikari"        % "1.0.0-RC1"

libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.17.3"
libraryDependencies += "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.17.3"
libraryDependencies += "com.github.geirolz" %% "fly4s-core" % "0.0.17"



