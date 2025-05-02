package dev.cptlobster.aggregation_framework
package util

import java.time
import java.time.{Duration, Instant}
import scala.math.Ordered.orderingToOrdered

/**
 * Basic interface for execution rules.
 */
abstract class ExecutionRule() {
  /** If enabled, job will only run once. */
  val oneshot: Boolean

  /**
   * Return the next time the attached [[dev.cptlobster.aggregation_framework.collector.Collector Collector]] is
   * scheduled to run.
   * @param from The [[Instant]] to calculate next execution time from.
   * @return The [[Instant]] this job should execute.
   */
  def next(from: Instant): Instant

  /**
   * Return the next time the attached [[dev.cptlobster.aggregation_framework.collector.Collector Collector]] is
   * scheduled to run, measuring from the current time.
   * @return The [[Instant]] this job should execute.
   */
  def next(): Instant = next(Instant.now())

  /**
   * Return the amount of time until the next [[dev.cptlobster.aggregation_framework.collector.Collector Collector]] is
   * scheduled to run.
   * @param current The [[Instant]] to calculate next execution time from.
   * @return The [[Duration]] until the next execution time; Returns zero if before the next time.
   */
  def timeToNext(current: Instant): Duration = {
    val duration = next(current).until(current)
    if (duration >= Duration.ofSeconds(0)) {
      duration
    }
    else {
      Duration.ofSeconds(0)
    }
  }
}
