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
 * Trait for pushing data to a Kafka producer.
 * @tparam T The expected final type of the data. This will need to match what you set in your
 *           [[dev.cptlobster.aggregation_framework.collector.Collector Collector]]s.
 */
trait KafkaDatastore[T] {
  val topic: String
  val props: Properties
  private val producer = new KafkaProducer[String, T](props)

  def send(key: String, value: T): Unit = {
    val record = new ProducerRecord[String, T](topic, key, value)
    producer.send(record, new ProducerCallback)
  }

  def flush(): Unit = {
    producer.flush()
  }

  private class ProducerCallback extends Callback {
    @Override
    override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
      if (exception != null) {
        exception.printStackTrace()
      }
    }
  }
}
