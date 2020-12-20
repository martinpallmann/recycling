Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := "3.0.0-M1"
ThisBuild / version := "0.0.0"

mainClass in sbt.Compile := Some("recycling.Main")
assemblyJarName in assembly := "recycling.jar"

libraryDependencies ++= Seq()
