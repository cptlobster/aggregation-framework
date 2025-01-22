# Tutorials

These will give you a basic idea of how to use Aggregation Framework.

## Write a Basic Data Consumer

```scala
import dev.cptlobster.aggregation_framework.collector.JsonCollector
import dev.cptlobster.aggregation_framework.datastore.KafkaDatastore

// Extend your class with a collector and a datastore
case class TutorialConsumer() extends JsonCollector[String] with KafkaDatastore[String] {
  // Implement required functions for parsing your data
}
```