name := "chat"

version := "1.0"

scalaVersion := "2.11"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "2.0.2",
  "com.h2database"     %  "h2"    % "1.3.173"
)

playScalaSettings

parallelExecution in Test := false