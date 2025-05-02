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

import java.time.{Duration, Instant}
import scala.annotation.tailrec

/**
 * Scheduling rule for a job that repeats on a regular interval. This uses a start time and an interval, and calculates
 * next execution times by adding the interval repeatedly to the start time.
 * @param start The [[Instant]] that the "first" job should happen.
 * @param interval The [[Duration]] between repeated jobs.
 */
class SimpleScheduledRule(start: Instant, interval: Duration) extends ExecutionRule {
  override val oneshot: Boolean = false

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