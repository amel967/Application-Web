
version := "0.1"

scalaVersion := "2.13.14"

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.14.3",
  "org.scalatest" %% "scalatest" % "3.3.0-SNAP2" % Test,
  "org.scalaj" %% "scalaj-http" % "2.4.2"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http"   % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.9",



)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.4.0",
  "org.apache.spark" %% "spark-sql" % "3.4.0" % "provided",
  "org.scalatest" %% "scalatest" % "3.3.0-SNAP4" % Test,
  "org.jsoup" % "jsoup" % "1.14.3",
  "org.apache.spark" %% "spark-mllib" % "3.4.0" % "provided"

)