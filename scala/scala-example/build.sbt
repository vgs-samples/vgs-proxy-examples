import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.verygoodsecurity",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT"
    )
  )
)

  libraryDependencies += scalaTest % Test
  libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.3.0"
  libraryDependencies += "it.bitbl" %% "scala-faker" % "0.4"
  libraryDependencies += "io.spray" %%  "spray-json" % "1.3.3"
