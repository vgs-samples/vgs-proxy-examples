import Dependencies._

lazy val root = (project in file(".")).
    settings(
        inThisBuild(List(
            organization := "com.verygoodsecurity",
            scalaVersion := "2.12.3",
            version := "0.1.0-SNAPSHOT"
        )
        )
    )

libraryDependencies ++= Seq(
    "it.bitbl" %% "scala-faker" % "0.4",
    "com.softwaremill.sttp" %% "json4s" % "1.1.0",
    "com.softwaremill.sttp" %% "core" % "1.1.0",
    "com.softwaremill.sttp" %% "okhttp-backend" % "1.1.0"

)

