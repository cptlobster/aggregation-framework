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

import java.time.{Duration, Instant}
import scala.annotation.tailrec

/**
 * The [[ScheduledConsumer]] has extra metadata for which intervals to run on. When given a [[start]] time and an
 * [[interval]], the job runner can handle running this once every interval. This can be extended if you have more
 * complex scheduling needs, such as limiting to a certain time of day.
 *
 * ## Usage
 *
 * When creating your [[Consumer]], you will need to extend it with your [[collector.Collector Collector]] and
 * [[datastore.Datastore Datastore]] of choice. For example:
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
 * @tparam V The expected final type of the data. This will need to match what you set in your
 *           [[collector.Collector Collector]]s.
 */
trait ScheduledConsumer[K, V] extends Consumer[K, V] {
  /** The start date of this job. */
  val start: Instant
  /** The interval that this job should be run on. */
  val interval: Duration

  /**
   * Get the next time that this job is scheduled to run.
   * @param current The time that the next job should be calculated from.
   * @return The [[Instant]] that the next job should run at.
   */
  def next(current: Instant): Instant = {
    next(current, start)
  }

  /**
   * Get the next time that this job is scheduled to run. This is the recursive call that handles calculating the next
   * time; it adds [[interval]] to the previous value
   *
   * @param current The time that the next job should be calculated from.
   * @param compare The intermediary accumulator; Once current is less than this value, returns the result.
   * @return The [[Instant]] that the next job should run at.
   */
  @tailrec private def next(current: Instant, compare: Instant): Instant = {
    if (current.getEpochSecond < compare.getEpochSecond) {
      compare
    } else {
      next(current, compare.plus(interval))
    }
  }
}
