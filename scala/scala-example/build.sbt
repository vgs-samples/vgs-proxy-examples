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
  libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.13.2"
