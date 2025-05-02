package dev.cptlobster.aggregation_framework
package util

import java.time.{Duration, Instant}
import scala.annotation.tailrec

class SimpleScheduledRule(start: Instant, interval: Duration) extends ExecutionRule {
  /** If enabled, job will only run once. */
  override val oneshot: Boolean = false

  /**
   * Return the next time the attached [[dev.cptlobster.aggregation_framework.collector.Collector Collector]] is
   * scheduled to run.
   *
   * @param from The [[Instant]] to calculate next execution time from.
   * @return The [[Instant]] this job should execute.
   */
  override def next(from: Instant): Instant = next(from, start)

  @tailrec private def next(from: Instant, acc: Instant): Instant = {
    if (acc.getEpochSecond < from.getEpochSecond) {
      acc
    } else {
      next(from, acc.plus(interval))
    }
  }
}

object SimpleScheduledRule {
  def apply(start: Instant, interval: Duration): SimpleScheduledRule = {
    new SimpleScheduledRule(start, interval)
  }

  def apply(interval: Duration): SimpleScheduledRule = {
    new SimpleScheduledRule(Instant.now(), interval)
  }
}