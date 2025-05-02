package dev.cptlobster.aggregation_framework
package util

import java.time.Instant

/**
 * Scheduling rule for a one-shot job. The job will run instantly, and only once. This job exists for compatibility.
 */
case class OneshotRule() extends ExecutionRule {

  /** If enabled, job will only run once. */
  override val oneshot: Boolean = true

  /**
   * Return the next time the attached [[dev.cptlobster.aggregation_framework.collector.Collector Collector]] is
   * scheduled to run. For the [[OneshotRule]] class, this will always return the input time.
   *
   * @param from The [[Instant]] to calculate next execution time from.
   * @return The [[Instant]] this job should execute.
   */
  override def next(from: Instant): Instant = from
}
