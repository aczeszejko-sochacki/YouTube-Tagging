name := "yt-tagging-api"

version := "1.0"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.11",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11",
  "com.typesafe.akka" %% "akka-stream" % "2.6.3",
  "org.scalatest" % "scalatest_2.13" % "3.1.0" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.3",
  "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
  "net.liftweb" %% "lift-json" % "3.4.1"
)