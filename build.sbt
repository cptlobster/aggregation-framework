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
    licenses      := Seq("LGPLv3" -> url("https://www.gnu.org/licenses/lgpl-3.0.html")),
    developers    := List(
      Developer("cptlobster", "Dustin Thomas", "io@cptlobster.dev", url("https://cptlobster.dev"))
    ),
  )
)

// Dependency versions
lazy val commonsVersion = "1.13.1"
lazy val TypesafeConfigVersion = "1.4.3"
lazy val SttpVersion = "4.0.3"
lazy val Json4sVersion = "4.0.7"
lazy val ScalaScraperVersion = "3.1.3"
lazy val SeleniumVersion = "4.31.0"
lazy val KafkaVersion = "4.0.0"
lazy val picoCliVersion = "4.7.7"
lazy val slf4jVersion = "2.0.17"
lazy val pgDriverVersion = "42.7.5"
// testing dependency versions
lazy val testContainersVersion = "1.21.0"
lazy val munitVersion = "1.1.1"

// Core project
lazy val core = (project in file("core"))
  .settings(
    common,
    name := "aggregation_framework_core",
    description := "Core API and abstractions for Aggregation Framework",
    libraryDependencies := Seq(
      // Apache Commons libraries
      "org.apache.commons" % "commons-text" % commonsVersion,
      // slf4j: used for logging
      "org.slf4j" % "slf4j-api" % slf4jVersion,
      // typesafe config: used for configuration
      "com.typesafe" % "config" % TypesafeConfigVersion,
      // sttp: used for most Collectors
      "com.softwaremill.sttp.client4" %% "core" % SttpVersion,
      // scala-scraper: used for parsing HTML in HTMLCollector
      "net.ruippeixotog" %% "scala-scraper" % ScalaScraperVersion,
      // PostgreSQL JDBC driver
      "org.postgresql" % "postgresql" % pgDriverVersion
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
    description := "Kafka datastore extension for Aggregation Framework",
    libraryDependencies := Seq(
      // kafka: used for Kafka producer classes
      "org.apache.kafka" %% "kafka" % KafkaVersion,
      "org.apache.kafka" % "kafka-clients" % KafkaVersion
    )
  )

// Kafka datastore
lazy val runner = (project in file("ext/runner"))
  .dependsOn(core)
  .settings(
    common,
    name := "aggregation_framework_runner",
    libraryDependencies := Seq(
      // picocli
      "info.picocli" % "picocli" % picoCliVersion,
      "info.picocli" % "picocli-codegen" % picoCliVersion,
      // logging
      "org.slf4j" % "slf4j-simple" % slf4jVersion
    )
  )

// Full project build
lazy val root = (project in file("."))
  .dependsOn(core)
  .dependsOn(kafka)
  .dependsOn(selenium)
  .dependsOn(runner)
  .dependsOn(json)
  .settings(
    common,
    name := "aggregation_framework",
    description := "A Swiss-army knife data scraping and processing framework.",
    libraryDependencies := Seq(
      "org.scalameta" %% "munit" % munitVersion % Test,
      "org.testcontainers" % "testcontainers" % testContainersVersion % Test,
      "org.testcontainers" % "postgresql" % testContainersVersion % Test
    )
  )