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

import java.time
import java.time.temporal.TemporalUnit
import java.time.{Duration, Instant}
import java.util.concurrent.TimeUnit
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
    val duration = Duration.between(current, next(current))
    if (duration >= Duration.ofSeconds(0)) {
      duration
    }
    else {
      Duration.ofSeconds(0)
    }
  }
}
