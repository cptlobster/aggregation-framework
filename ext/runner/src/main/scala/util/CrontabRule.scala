package dev.cptlobster.aggregation_framework
package util

import java.time.Instant

/**
 * Scheduling rule for a job repeating on a regular interval. In this case, the job is defined by a cron expression.
 * @param crontab The [[CronExpr]] object that defines this job's repeated execution.
 */
class CrontabRule(crontab: CronExpr) extends ExecutionRule {
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
  def apply(crontab: CronExpr): CrontabRule = {
    new CrontabRule(crontab)
  }
}