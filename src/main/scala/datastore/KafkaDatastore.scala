/* Copyright (C) 2025  Dustin Thomas <io@cptlobster.dev>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package dev.cptlobster.aggregation_framework
package datastore

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import java.util.Properties

/**
 * Trait for pushing data to a Kafka topic.
 * @tparam K The type of your database key. This could be something like a [[String]], [[java.util.Date Date]], or a
 *           [[java.util.UUID UUID]], or something else; it depends on how you want to index your data internally. You
 *           will need to make sure that this type implements a
 *           [[org.apache.kafka.common.serialization.Serializer Serializer]].
 * @tparam V The expected final type of the data. This will need to match what you set in your
 *           [[dev.cptlobster.aggregation_framework.collector.Collector Collector]]s, and implement a
 *           [[org.apache.kafka.common.serialization.Serializer Serializer]].
 */
trait KafkaDatastore[K, V] extends Datastore[K, V] {
  /** The name of the Kafka topic */
  val kafkaTopic: String
  /** Properties to pass to the Kafka producer */
  val kafkaProps: Properties
  private val producer = new KafkaProducer[K, V](kafkaProps)

  def push(key: K, value: V): Unit = {
    val record = new ProducerRecord[K, V](kafkaTopic, key, value)
    producer.send(record, new ProducerCallback)
  }

  /**
   * Flush the [[KafkaProducer]]'s records.
   */
  def flush(): Unit = {
    producer.flush()
  }

  /** Since the Producer is being used asynchronously, setup a callback handler to deal with exceptions */
  private class ProducerCallback extends Callback {
    override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
      if (exception != null) { throw exception }
    }
  }
}
