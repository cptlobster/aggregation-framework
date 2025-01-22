ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

// Core project
lazy val root = (project in file("."))
  .settings(
    name := "aggregation_framework",
    idePackagePrefix := Some("dev.cptlobster.aggregation_framework"),
  )

// Dependency versions
lazy val SttpVersion = "3.10.2"
lazy val Json4sVersion = "4.0.7"
lazy val ScalaScraperVersion = "3.1.2"
lazy val SeleniumVersion = "4.28.0"
lazy val KafkaVersion = "3.9.0"

// Dependencies
libraryDependencies ++= {
  Seq(
    // sttp: used for all Collectors
    "com.softwaremill.sttp.client3" %% "core" % SttpVersion,
    // json4s: used for parsing JSON in JsonCollectors
    "org.json4s" %% "json4s-native" % Json4sVersion,
    // scala-scraper: used for parsing HTML in HTMLCollector
    "net.ruippeixotog" %% "scala-scraper" % ScalaScraperVersion,
    // selenium: used for interacting with web applications in SeleniumCollector
    "org.seleniumhq.selenium" % "selenium-java" % SeleniumVersion,
    // kafka: used for Kafka producer classes
    "org.apache.kafka" %% "kafka" % KafkaVersion,
    "org.apache.kafka" % "kafka-clients" % KafkaVersion,
  )
}