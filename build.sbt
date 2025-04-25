// common build configuration
lazy val libraryVersion = "0.1.0-SNAPSHOT"

lazy val common = Seq(
  version := libraryVersion,
  scalaVersion  := "2.13.16",
  idePackagePrefix := Some("dev.cptlobster.aggregation_framework"),
  credentials += Credentials(Path.userHome / ".sbt" / ".credentials"),
  publishTo := Some("forge" at "https://forge.cptlobster.dev/api/packages/cptlobster/maven")
)

// common meta information
inThisBuild(
  Seq(
    homepage      := Some(url("https://github.com/cptlobster/aggregation-framework")),
    organization  := "dev.cptlobster",
    licenses      := Seq("GPLv3" -> url("https://www.gnu.org/licenses/gpl-3.0.en.html")),
    developers    := List(
      Developer("cptlobster", "Dustin Thomas", "io@cptlobster.dev", url("https://cptlobster.dev"))
    ),
  )
)

// Dependency versions
lazy val TypesafeConfigVersion = "1.4.3"
lazy val SttpVersion = "3.10.3"
lazy val Json4sVersion = "4.0.7"
lazy val ScalaScraperVersion = "3.1.3"
lazy val SeleniumVersion = "4.29.0"
lazy val KafkaVersion = "3.9.0"

// Core project
lazy val core = (project in file("core"))
  .settings(
    common,
    name := "aggregation_framework_core",
    description := "Core API and abstractions for Aggregation Framework",
    libraryDependencies := Seq(
      // typesafe config: used for configuration
      "com.typesafe" % "config" % TypesafeConfigVersion,
      // sttp: used for most Collectors
      "com.softwaremill.sttp.client3" %% "core" % SttpVersion,
      // scala-scraper: used for parsing HTML in HTMLCollector
      "net.ruippeixotog" %% "scala-scraper" % ScalaScraperVersion
    )
  )

// JSON processing extension
lazy val json = (project in file("ext/json"))
  .dependsOn(core)
  .settings(
    common,
    name := "aggregation_framework_json",
    description := "JSON parsing and processing extension for Aggregation Framework",
    libraryDependencies := Seq(
      // json4s: used for parsing JSON in JsonCollectors
      "org.json4s" %% "json4s-native" % Json4sVersion
    )
  )

// Selenium WebDriver/collector extension
lazy val selenium = (project in file("ext/selenium"))
  .dependsOn(core)
  .settings(
    common,
    name := "aggregation_framework_selenium",
    description := "Selenium WebDriver data collector extension for Aggregation Framework",
    libraryDependencies := Seq(
      // selenium: used for interacting with web applications in SeleniumCollector
      "org.seleniumhq.selenium" % "selenium-java" % SeleniumVersion
    )
  )

// Kafka datastore
lazy val kafka = (project in file("ext/kafka"))
  .dependsOn(core)
  .settings(
    common,
    name := "aggregation_framework_kafka",
    libraryDependencies := Seq(
      // kafka: used for Kafka producer classes
      "org.apache.kafka" %% "kafka" % KafkaVersion,
      "org.apache.kafka" % "kafka-clients" % KafkaVersion
    )
  )

// Full project build
lazy val root = (project in file("."))
  .aggregate(
    core,
    json,
    kafka,
    selenium
  )
  .settings(
    common,
    name := "aggregation_framework",
    description := "A Swiss-army knife data scraping and processing framework.",
  )