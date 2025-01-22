package dev.cptlobster.aggregation_framework

import collector.Collector
import datastore.Datastore

trait Consumer[K, V] extends Collector[V] with Datastore[K, V]{
  def collect(): Unit
}
