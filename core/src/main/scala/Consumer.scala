/* Copyright (C) 2025  Dustin Thomas <io@cptlobster.dev>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License (and the GNU General Public License) along
 * with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package dev.cptlobster.aggregation_framework

import collector.Collector
import datastore.Datastore
import util.{ExecutionRule, OneshotRule, RecoverableException}

import java.time.Duration
import scala.annotation.tailrec
import org.slf4j.{Logger, LoggerFactory}

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
 * See the Tutorial for more detailed instructions on how to use.
 *
 * @tparam K The type of your database key. This could be something like a [[String]], [[java.util.Date Date]], or a
 *           [[java.util.UUID UUID]], or something else; it depends on how you want to index your data internally (and
 *           what your target datastore supports). Note that this must be a unique value, and ideally this would be
 *           associated with the date/time that a record is created.
 * @tparam V The expected final type of the data. This will need to match what you set in your [[Collector]]s.
 */
trait Consumer[K, V] extends Collector[V] with Datastore[K, V] with Runnable {
  /** The name of this collector. Must be unique between all collectors. */
  val name: String
  /** The tags that this collector has.  */
  val tags: List[String]
  /** The amount of times a function should retry on failure. */
  val retries: Int = 2
  /** How long to wait after a failed attempt. */
  val retryDelay: Duration = Duration.ofSeconds(5)
  /** This consumer's execution rule. */
  val executionRule: ExecutionRule = OneshotRule()
  /** slf4j logger */
  override val logger: Logger = LoggerFactory.getLogger(classOf[Consumer[K, V]])

  /**
   * User-defined collection function. This would query your target endpoint, but NOT store it. Retries and errors are
   * handled by the [[run()]] function, so they will not need to be implemented here.
   */
  def collect(): (K, V)

  /**
   * Attempt to run [[collect()]] and store its result to the datastore. In the event that [[collect()]] has a
   * recoverable failure, it will be rerun after the duration of [[retryDelay]] passes; this will happen [[retries]]
   * amount of times before failing.
   */
  def run(): Unit = {
    logger.debug("Beginning collection.")
    val (k: K, v: V) = runCollect(retries + 1)
    logger.debug("{}: {}", k, v)
    push(k, v)
  }

  /**
   * Attempt to run [[collect()]] and print to the console. In the event that [[collect()]] has a recoverable failure,
   * it will be rerun after the duration of [[retryDelay]] passes; this will happen [[retries]] amount of times before
   * failing.
   */
  def dryRun(): Unit = {
    val (k: K, v: V) = runCollect(retries + 1)
    println(s"$k: $v")
  }

  /**
   * Attempt to run [[collect()]]. In the event that [[collect()]] has a recoverable failure, it will be rerun after the
   * duration of [[retryDelay]] passes; this will happen [[retries]] amount of times before failing. This is the
   * recursive call that handles retries.
   *
   * @param attempts The amount of attempts this function has left.
   */
  private def runCollect(attempts: Int): (K, V) = {
    try {
      collect()
    } catch {
      case e: RecoverableException => if (attempts > 0) {
        logger.error(e.getMessage)
        wait(e.retryAfter.getOrElse(retryDelay).toMillis)
        logger.info("Retrying... ({} attempt(s) left)", attempts)
        runCollect(attempts - 1)
      } else {
        throw e
      }
      case e: Exception => throw e
    }
  }

  override def toString: String = {
    s"Consumer $name: $collectorStr -> $datastoreStr" +
      s"  Tags: ${tags.mkString(", ")}"
  }
}
