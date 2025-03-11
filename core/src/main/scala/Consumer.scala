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

import collector.Collector
import datastore.Datastore

/**
 * A consumer is a combination of a [[Collector]] and a [[Datastore]], and the highest level interface with Aggregation
 * Framework. Running the [[collect]] function will cause the [[Collector]] to execute a query against the
 * API/application, parse the result accordingly, and then push it to the [[Datastore]] of your choice.
 *
 * ## Usage
 *
 * When creating your [[Consumer]], you will need to extend it with your [[Collector]] and [[Datastore]] of choice. For
 * example:
 * {{{
 * import java.util.Date
 * import dev.cptlobster.aggregation-framework.Consumer
 * import dev.cptlobster.aggregation-framework.collector.SttpCollector
 * import dev.cptlobster.aggregation-framework.datastore.SQLDatastore
 *
 * // for this consumer, we'll use Date as our key and String as our value
 * class ExampleConsumer
 *   extends Consumer[Date, String]  // extend the consumer interface
 *   with SttpCollector[String]      // we use sttp to collect our data...
 *   with SQLDatastore[Date, String] // ... and then push it to an SQL datastore
 * {
 *   // implement stuff here...
 * }
 * }}}
 *
 * @tparam K The type of your database key. This could be something like a [[String]], [[java.util.Date Date]], or a
 *           [[java.util.UUID UUID]], or something else; it depends on how you want to index your data internally (and
 *           what your target datastore supports). Note that this must be a unique value, and ideally this would be
 *           associated with the date/time that a record is created.
 * @tparam V The expected final type of the data. This will need to match what you set in your [[Collector]]s.
 */
trait Consumer[K, V] extends Collector[V] with Datastore[K, V]{
  def collect(): Unit
}
