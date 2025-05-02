package dev.cptlobster.aggregation_framework
package util

import java.time.Instant

class CrontabRule(crontab: Crontab) extends ExecutionRule {
  /** If enabled, job will only run once. */
  override val oneshot: Boolean = false

  /**
   * Return the next time the attached [[dev.cptlobster.aggregation_framework.collector.Collector Collector]] is
   * scheduled to run.
   *
   * @param from The [[Instant]] to calculate next execution time from.
   * @return The [[Instant]] this job should execute.
   */
  override def next(from: Instant): Instant = ???
}

object CrontabRule {
  def apply(crontab: Crontab): CrontabRule = {
    new CrontabRule(crontab)
  }
}