ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

// Core project
lazy val root = (project in file("."))
  .settings(
    name := "aggregation_framework",
    idePackagePrefix := Some("dev.cptlobster.aggregation_framework"),
  )

// Dependencies
libraryDependencies ++= {
  Seq(
    // sttp: used for all Collectors
    "com.softwaremill.sttp.client3" %% "core" % "3.10.2",
    // json4s: used for parsing JSON in JsonCollectors
    "org.json4s" %% "json4s-native" % "4.0.7",
    // kafka: used for Kafka producer classes
    "org.apache.kafka" %% "kafka" % "3.9.0",
    "org.apache.kafka" % "kafka-clients" % "3.9.0",
  )
}