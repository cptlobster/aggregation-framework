/* Copyright (C) 2025  Dustin Thomas <io@cptlobster.dev>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package dev.cptlobster.aggregation_framework

import picocli.CommandLine
import picocli.CommandLine.{Command, Option, Parameters}
import jdk.internal.misc.Signal

import java.time.Instant
import util.ScheduledThreadPoolExecutor

import java.util.concurrent.Callable

import org.slf4j.{Logger, LoggerFactory}

@Command
class Runner extends Callable[Int] {
  /** All declared consumers that can be run. */
  protected val consumers: List[Consumer[Any, Any]] = List()
  /** slf4j */
  val logger: Logger = LoggerFactory.getLogger(classOf[Runner])

  /** Split out ScheduledConsumers */
  private val scheduled: List[ScheduledConsumer[Any, Any]] =
    consumers
      .filter(_.getClass == classOf[ScheduledConsumer[_, _]])
      .asInstanceOf[List[ScheduledConsumer[Any, Any]]]

  /** All non-scheduled consumers, these will be run all at once */
  private val oneshot: List[Consumer[Any, Any]] = consumers diff scheduled

  /** Run a single consumer, print errors to console. */
  private def run(cons: Consumer[Any, Any]): Int = {
    try {
      cons.run()
      logger.info(s"Consumer succeeded.")
      1
    } catch {
      case e: Exception =>
        logger.error(s"Consumer failed after ${cons.retries} attempts: ${e.getMessage}")
        0
    }
  }

  /** Run all one-shot consumers, print errors to console. */
  private def runOneshot(): Int = {
    if (oneshot.isEmpty) {
      logger.error("No oneshot consumers are defined! You may want to run scheduled jobs instead, or add a consumer to your Runner class.")
      0
    } else {
      logger.info("Running oneshot consumers...")
      (for (cons <- oneshot) yield {
        run(cons)
      }).sum
    }
  }

  private def runScheduled(): Unit = {
    logger.info("Initializing scheduler and thread pool...")
    // create the ScheduledThreadPoolExecutor
    val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2)
    executor.setRunScheduledTasksAfterShutdown(false)

    // Stop execution if SIGINT is triggered (Ctrl+C)
    Signal.handle(new Signal("INT"),  // SIGINT
      _ => {
        logger.info("User interrupt. Shutting down thread pool.")
        executor.shutdown()
      }
    )

    // add all ScheduledConsumers to the thread pool
    for (cons <- scheduled) {
      val initialDelay = cons.timeToNext(Instant.now())
      executor.scheduleRepeating(cons.run, initialDelay, cons.interval)
    }

    logger.info("All jobs scheduled successfully. Runner can be stopped using SIGINT (Ctrl+C).")
  }

  def call(): Int = {
    logger.info("   ___                              __  _         ")
    logger.info("  / _ |___ ____ ________ ___ ____ _/ /_(____  ___ ")
    logger.info(" / __ / _ `/ _ `/ __/ -_/ _ `/ _ `/ __/ / _ \\/ _ \\")
    logger.info("/_/ |_\\_, /\\_, /_/  \\__/\\_, /\\_,_/\\__/_/\\___/_//_/")
    logger.info("     /___//___/        /___/                  __  ")
    logger.info("       / __/______ ___ _ ___ _    _____  ____/ /__")
    logger.info("      / _// __/ _ `/  ' / -_| |/|/ / _ \\/ __/  '_/")
    logger.info("     /_/ /_/  \\_,_/_/_/_\\__/|__,__/\\___/_/ /_/\\_\\ ")
    logger.info("")
    logger.info("Aggregation Framework Runner")

    if (consumers.isEmpty) {
      logger.error("No consumers are defined! You should extend the Runner class and override the consumers variable.")
    }
    else {
      val successes: Int = runOneshot()
      logger.info(s"Completed, $successes out of ${oneshot.size} consumers succeeded.")
    }
    0
  }
}

object Runner extends App {
  System.exit(new CommandLine(new Runner()).execute(args: _*))
}

case class Task(cons: Consumer[Any, Any]) extends Runnable {
  val logger: Logger = LoggerFactory.getLogger(classOf[Task])
  override def run(): Unit = try {
    cons.run()
    logger.debug(s"Consumer succeeded.")
  } catch {
    case e: Exception =>
      logger.error(s"Consumer failed after ${cons.retries} attempts: ${e.getMessage}")
  }
}