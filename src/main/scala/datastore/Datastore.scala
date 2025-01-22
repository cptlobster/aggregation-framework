package dev.cptlobster.aggregation_framework
package datastore

/**
 * Trait for pushing data to a data store.
 * @tparam K The type of your database key. This could be something like a [[String]], [[java.util.Date Date]], or a
 *           [[java.util.UUID UUID]], or something else; it depends on how you want to index your data internally (and
 *           what your target datastore supports).
 * @tparam V The expected final type of the data. This will need to match what you set in your
 *           [[dev.cptlobster.aggregation_framework.collector.Collector Collector]]s.
 */
trait Datastore[K, V] {
  /**
   * Push a key/value pair to your datastore.
   * @param key Your data key.
   * @param value Your data value.
   */
  def push(key: K, value: V): Unit
}
