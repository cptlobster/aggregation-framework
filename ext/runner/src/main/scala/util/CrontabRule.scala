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