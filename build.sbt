name := "Fessi_Tloh_exam"

version := "0.1"

scalaVersion := "2.11.11"

lazy val root = (project in file("."))
  .settings(
    name := "compliance-system"
  )
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.8"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.8"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.6"
libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1"
libraryDependencies += "com.lihaoyi" %% "os-lib" % "0.8.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test