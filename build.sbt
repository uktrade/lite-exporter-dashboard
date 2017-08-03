name := "lite-exporter-dashboard"

version := "1.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .dependsOn(`zzz-common`)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "buildinfo"
  )

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.flywaydb" %% "flyway-play" % "3.1.0",
  "org.xerial" % "sqlite-jdbc" % "3.19.3",
  "org.jdbi" % "jdbi" % "2.78"
)

// Contains all files and libraries shared across other projects
lazy val `zzz-common` = project.in(file("subprojects/lite-play-common")).enablePlugins(PlayJava)

//Add build time and git commit to the build info object

buildInfoKeys ++= Seq[BuildInfoKey](
  BuildInfoKey.action("gitCommit") {
    try {
      "git rev-parse HEAD".!!.trim
    } catch {
      case e: Throwable => "unknown (" + e.getMessage + ")"
    }
  }
)

buildInfoOptions += BuildInfoOption.BuildTime
buildInfoOptions += BuildInfoOption.ToJson
PlayKeys.externalizeResources := false