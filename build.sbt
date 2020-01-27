Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.0.0"

mainClass in sbt.Compile := Some("recycling.Main")
assemblyJarName in assembly := "recycling.jar"

libraryDependencies ++= Seq(
//  "com.softwaremill.sttp" %% "core" % "1.5.0",
//  "io.circe" %% "circe-core" % "0.12.3",
//  "io.circe" %% "circe-parser" % "0.12.3",
//  "org.jsoup" % "jsoup" % "1.11.3",
//  "org.apache.pdfbox" % "pdfbox" % "2.0.13",
//  "com.itextpdf" % "itext7-core" % "7.1.4",
//  "com.lihaoyi" %% "fastparse" % "2.1.0"
)
