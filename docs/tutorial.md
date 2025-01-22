# Tutorials

These will give you a basic idea of how to use Aggregation Framework.

## Write a Basic Data Consumer

This is an example of a basic data consumer that reads JSON data and pushes it to a Kafka datastore. It doesn't do any
complex parsing or query magic, but you can extend with other traits / implement different functions to do more complex
data operations.

```scala
import dev.cptlobster.aggregation_framework.collector.JsonCollector
import dev.cptlobster.aggregation_framework.datastore.KafkaDatastore
import dev.cptlobster.aggregation_framework.Consumer

import java.util.Properties
import java.util.Date

// Assemble your first consumer!
case class TutorialConsumer()
  extends Consumer[String, String] // always extend the Consumer trait first!
  with JsonCollector[String] // Since we're parsing JSON data, extend the JSONCollector trait
  with KafkaDatastore[String, String] // Since we're pushing to a Kafka datastore, extend the KafkaDatastore trait
{
  // Collectors require that we set a base URL
  val baseUrl = "https://example.com"
  // the KafkaDatastore requires that we set a topic and any required Properties
  val kafkaTopic: String = "example"
  val kafkaProps: Properties = Properties() // pass the correct properties for your environment
  // This is the function you should call to execute the consumer.
  def collect(): Unit = {
    // Query your target endpoint
    val result: String = query("/")
    // Push the string to your Kafka datastore.
    push(new Date().toString, result)
  }
  // We are going to override the convert() method to just pass the content along as a string.
  // In an actual implementation, you could leverage Json4s to parse your data and read specific fields; you can
  // override the find() function to navigate to specific subkeys and use a type other than String, which json4s will
  // convert for you
  override def convert(content: String): String = content
}
```

### Using a Navigator to Parse Document Trees

Remove the `convert()` method we wrote in the first tutorial. Add the following import:

```scala
import dev.cptlobster.aggregation_framework.navigator.JsonNavigator
```

Add this code in your `TutorialConsumer`:

```scala
// We're going to use the default implementation of convert. It calls find() to navigate through a JSON document tree
// to the expected value, which we will implement.
override def find(value: JsonNavigator): JsonNavigator = {
  // We will use the JsonNavigator class to parse our JSON document.
  value !>> "message" !>> "something" !>> "value"
}
```

In the following JSON document, this `find()` would return the value "beans":
```json
{
  "message": {
    "something": {
      "value": "beans"
    }
  }
}
```

If you prefer, you can use json4s directly to parse your values. In that case, you will need to import
`org.json4s.JValue` and implement `find()` using the `JValue` type:
```scala
override def find(value: JValue): JValue = {
  value \ "message" \ "something" \ "value"
}
```
The main advantage of using Navigators is that the syntax is equivalent between document formats.

*Tip: `find(JValue)`'s default implementation converts the `JValue` into a `JsonNavigator`, calls `find()` on the
`JsonNavigator`, and then gets the final result. Therefore, you do not need to implement both functions. However, if you
override `find(JValue)`, then `find(JsonNavigator)` will not be called unless you explicitly call it.*