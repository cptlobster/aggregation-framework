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
import java.util.concurrent.{Callable, RejectedExecutionHandler, ScheduledFuture, ThreadFactory, TimeUnit, ScheduledThreadPoolExecutor => JScheduledThreadPoolExecutor}
import scala.jdk.CollectionConverters._

/**
 * A Scala-like and more sensible wrapper for the [[java.util.concurrent.ScheduledThreadPoolExecutor]].
 * @param threads The amount of threads to create the thread pool with
 * @param rejectedExecutionHandler A custom [[RejectedExecutionHandler]]
 * @param threadFactory A custom [[ThreadFactory]]
 */
class ScheduledThreadPoolExecutor(threads: Int,
                                  rejectedExecutionHandler: RejectedExecutionHandler = null,
                                  threadFactory: ThreadFactory = null) {
  /** The underlying [[java.util.concurrent.ScheduledThreadPoolExecutor]] */
  private val executor = (rejectedExecutionHandler, threadFactory) match {
    case (null, null) => new JScheduledThreadPoolExecutor(threads)
    case (reh, null) => new JScheduledThreadPoolExecutor(threads, reh)
    case (null, tf) => new JScheduledThreadPoolExecutor(threads, tf)
    case (reh, tf) => new JScheduledThreadPoolExecutor(threads, tf, reh)
  }

  /**
   * Execute a function with no required delay.
   *
   * @param f The function to execute
   */
  def execute(f: () => Unit): Unit = executor.execute(Task(f))

  /**
   * Schedule a function to execute at a specific time.
   * @param task the [[Runnable]] to execute
   * @param time The [[Instant]] at which this function should run
   * @return A [[ScheduledFuture]] for the function run
   */
  def schedule(task: Runnable, time: Instant): ScheduledFuture[_] = {
    executor.schedule(task, toMillis(time), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a function to execute after a delay.
   * @param task the [[Runnable]] to execute
   * @param delay The [[Duration]] this function should wait to run
   * @return A [[ScheduledFuture]] for the function run
   */
  def schedule(task: Runnable, delay: Duration): ScheduledFuture[_] = {
    executor.schedule(task, toMillis(delay), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a function to execute at a specific time.
   * @tparam V The return type of the function to run
   * @param f The function to execute
   * @param time The [[Instant]] at which this function should run
   * @return A [[ScheduledFuture]] for the function run
   */
  def schedule[V](f: () => V, time: Instant): ScheduledFuture[V] = {
    executor.schedule(ReturningTask(f), toMillis(time), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a function to execute at a specific time.
   * @tparam V The return type of the function to run
   * @param task the [[Callable]] to execute
   * @param time The [[Instant]] at which this function should run
   * @return A [[ScheduledFuture]] for the function run
   */
  def schedule[V](task: Callable[V], time: Instant): ScheduledFuture[V] = {
    executor.schedule(task, toMillis(time), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a function to execute after a delay.
   * @tparam V The return type of the function to run
   * @param f The function to execute
   * @param delay The [[Duration]] this function should wait to run
   * @return A [[ScheduledFuture]] for the function run
   */
  def schedule[V](f: () => V, delay: Duration): ScheduledFuture[V] = {
    executor.schedule(ReturningTask(f), toMillis(delay), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a function to execute after a delay.
   * @tparam V The return type of the function to run
   * @param task The [[Callable]] to execute
   * @param delay The [[Duration]] this function should wait to run
   * @return A [[ScheduledFuture]] for the function run
   */
  def schedule[V](task: Callable[V], delay: Duration): ScheduledFuture[V] = {
    executor.schedule(task, toMillis(delay), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a task to repeat automatically.
   * @param f The function to execute
   * @param initialTime The time that this function should first run
   * @param period The delay between each subsequent execution
   * @return A [[ScheduledFuture]] for the function run
   */
  def scheduleRepeating(f: () => Unit, initialTime: Instant, period: Duration): ScheduledFuture[_] = {
    executor.scheduleAtFixedRate(Task(f), toMillis(initialTime), toMillis(period), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a task to repeat automatically.
   * @param f The function to execute
   * @param initialDelay The time that this function should wait to run for the first time
   * @param period The delay between each subsequent execution
   * @return A [[ScheduledFuture]] for the function run
   */
  def scheduleRepeating(f: () => Unit, initialDelay: Duration, period: Duration): ScheduledFuture[_] = {
    executor.scheduleAtFixedRate(Task(f), toMillis(initialDelay), toMillis(period), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a task to run instantly and repeat automatically.
   * @param f The function to execute
   * @param period The delay between each subsequent execution
   * @return A [[ScheduledFuture]] for the function run
   */
  def scheduleRepeating(f: () => Unit, period: Duration): ScheduledFuture[_] = {
    executor.scheduleAtFixedRate(Task(f), 0, toMillis(period), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a task to repeat automatically.
   * @param task the [[Runnable]] to execute
   * @param initialTime The time that this function should first run
   * @param period The delay between each subsequent execution
   * @return A [[ScheduledFuture]] for the function run
   */
  def scheduleRepeating(task: Runnable, initialTime: Instant, period: Duration): ScheduledFuture[_] = {
    executor.scheduleAtFixedRate(task, toMillis(initialTime), toMillis(period), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a task to repeat automatically.
   * @param task the [[Runnable]] to execute
   * @param initialDelay The time that this function should wait to run for the first time
   * @param period The delay between each subsequent execution
   * @return A [[ScheduledFuture]] for the function run
   */
  def scheduleRepeating(task: Runnable, initialDelay: Duration, period: Duration): ScheduledFuture[_] = {
    executor.scheduleAtFixedRate(task, toMillis(initialDelay), toMillis(period), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a task to run instantly and repeat automatically.
   * @param task the [[Runnable]] to execute
   * @param period The delay between each subsequent execution
   * @return A [[ScheduledFuture]] for the function run
   */
  def scheduleRepeating(task: Runnable, period: Duration): ScheduledFuture[_] = {
    executor.scheduleAtFixedRate(task, 0, toMillis(period), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a task to run once, wait a time, and then run again.
   * @param f The function to execute
   * @param initialTime The time that this function should first run
   * @param delay The delay between the first and second execution
   * @return A [[ScheduledFuture]] for the function run
   */
  def scheduleDelayed(f: () => Unit, initialTime: Instant, delay: Duration): ScheduledFuture[_] = {
    executor.scheduleWithFixedDelay(Task(f), toMillis(initialTime), toMillis(delay), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a task to run once, wait a time, and then run again.
   * @param f The function to execute
   * @param initialDelay The time that this function should wait to run for the first time
   * @param delay The delay between the first and second execution
   * @return A [[ScheduledFuture]] for the function run
   */
  def scheduleDelayed(f: () => Unit, initialDelay: Duration, delay: Duration): ScheduledFuture[_] = {
    executor.scheduleWithFixedDelay(Task(f), toMillis(initialDelay), toMillis(delay), TimeUnit.MILLISECONDS)
  }

  /**
   * Schedule a task to run instantly, wait a time, and then run again.
   * @param f The function to execute
   * @param delay The delay between the first and second execution
   * @return A [[ScheduledFuture]] for the function run
   */
  def scheduleDelayed(f: () => Unit, delay: Duration): ScheduledFuture[_] = {
    executor.scheduleWithFixedDelay(Task(f), 0, toMillis(delay), TimeUnit.MILLISECONDS)
  }

  /** Set the policy for whether queued repeating tasks should be run after [[shutdown()]] is called. */
  def setRunRepeatingTasksAfterShutdown(value: Boolean): Unit = executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(value)
  /** Set the policy for whether queued delay tasks should be run after [[shutdown()]] is called. */
  def setRunDelayedTasksAfterShutdown(value: Boolean): Unit = executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(value)
  /** Set the policy for whether queued delay or repeating tasks should be run after [[shutdown()]] is called. */
  def setRunScheduledTasksAfterShutdown(value: Boolean): Unit = {
    setRunRepeatingTasksAfterShutdown(value)
    setRunDelayedTasksAfterShutdown(value)
  }

  /**
   * Gracefully shut down the runner, allowing for tasks to finish per runner policies.
   */
  def shutdown(): Unit = {
    executor.shutdown()
  }

  /**
   * Force the executor to shut down immediately, stopping all actively running and queued tasks.
   * @return A [[List]] of all the tasks awaiting execution
   */
  def shutdownNow(): List[Runnable] = {
    executor.shutdownNow().asScala.toList
  }

  private def toMillis(time: Instant): Long = Instant.now().until(time).toMillis
  private def toMillis(time: Duration): Long = time.toMillis
  private def toMillis(time: Long, unit: TimeUnit): Long = unit.toMillis(time)
}

object ScheduledThreadPoolExecutor {
  def apply(threads: Int): ScheduledThreadPoolExecutor = {
    new ScheduledThreadPoolExecutor(threads)
  }
  def apply(threads: Int, rejectedExecutionHandler: RejectedExecutionHandler): ScheduledThreadPoolExecutor = {
    new ScheduledThreadPoolExecutor(threads, rejectedExecutionHandler)
  }
  def apply(threads: Int, threadFactory: ThreadFactory): ScheduledThreadPoolExecutor = {
    new ScheduledThreadPoolExecutor(threads, threadFactory = threadFactory)
  }
  def apply(threads: Int, rejectedExecutionHandler: RejectedExecutionHandler, threadFactory: ThreadFactory): ScheduledThreadPoolExecutor = {
    new ScheduledThreadPoolExecutor(threads, rejectedExecutionHandler, threadFactory)
  }
}

case class Task(f: () => Unit) extends Runnable {
  override def run(): Unit = f()
}

case class ReturningTask[V](f: () => V) extends Callable[V] {
  override def call(): V = f()
}