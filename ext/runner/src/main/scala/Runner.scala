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
import picocli.CommandLine.{Command, Help, Option, Parameters}
import jdk.internal.misc.Signal

import java.time.Instant
import util.ScheduledThreadPoolExecutor

import java.util.concurrent.{Callable, CountDownLatch}
import org.slf4j.{Logger, LoggerFactory}
import picocli.CommandLine.Help.Ansi

@Command(name = "aggregation-framework", mixinStandardHelpOptions = true,
  version = Array("aggregation-framework 0.0.1-SNAPSHOT"), description = Array(
    "   ___                              __  _         ",
    "  / _ |___ ____ ________ ___ ____ _/ /_(____  ___ ",
    " / __ / _ `/ _ `/ __/ -_/ _ `/ _ `/ __/ / _ \\/ _ \\",
    "/_/ |_\\_, /\\_, /_/  \\__/\\_, /\\_,_/\\__/_/\\___/_//_/",
    "     /___//___/        /___/                  __  ",
    "       / __/______ ___ _ ___ _    _____  ____/ /__",
    "      / _// __/ _ `/  ' / -_| |/|/ / _ \\/ __/  '_/",
    "     /_/ /_/  \\_,_/_/_/_\\__/|__,__/\\___/_/ /_/\\_\\ ",
    "",
    "A Swiss-army knife library for scraping and processing data from the web. This is the consumer runner application.",
    "For more info and usage guides: https://forge.cptlobster.dev/cptlobster/aggregation-framework"
  ), exitCodeOnInvalidInput = 2, exitCodeOnExecutionException = 1)
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

  private def printAsciiArt(): Unit = {
    System.err.println("   ___                              __  _         ")
    System.err.println("  / _ |___ ____ ________ ___ ____ _/ /_(____  ___ ")
    System.err.println(" / __ / _ `/ _ `/ __/ -_/ _ `/ _ `/ __/ / _ \\/ _ \\")
    System.err.println("/_/ |_\\_, /\\_, /_/  \\__/\\_, /\\_,_/\\__/_/\\___/_//_/")
    System.err.println("     /___//___/        /___/                  __  ")
    System.err.println("       / __/______ ___ _ ___ _    _____  ____/ /__")
    System.err.println("      / _// __/ _ `/  ' / -_| |/|/ / _ \\/ __/  '_/")
    System.err.println("     /_/ /_/  \\_,_/_/_/_\\__/|__,__/\\___/_/ /_/\\_\\ ")
  }

  @Option(names = Array("-d", "--dry-run"), description = Array("Do not push to database; just print to stdout."))
  var dryRun: Boolean = false
  @Option(names = Array("-j", "--jobs"), description = Array("How many consumers can be run concurrently."))
  var jobs: Int = 2

  /** Run a single consumer, print errors to console. */
  private def run(cons: Consumer[Any, Any]): Int = {
    try {
      if (dryRun) { cons.dryRun() } else { cons.run() }
      logger.info(s"Consumer succeeded.")
      1
    } catch {
      case e: Exception =>
        logger.error(s"Consumer failed after ${cons.retries} attempts: ${e.getMessage}")
        0
    }
  }

  /** Run all one-shot consumers, print errors to console. */
  @Command(name = "oneshot", description = Array("Run all one-shot consumers in order."))
  private def runOneshot(): Int = {
    @Option(names = Array("-S", "--run-scheduled-consumers"), description = Array("Run scheduled consumers as if they are one-shot consumers."))
    var runScheduledAsOneShot: Boolean = false

    printAsciiArt()

    // TODO: make this threaded
    // throw an error if consumers is empty
    if (consumers.isEmpty) {
      logger.error("No consumers are defined! You should extend the Runner class and override the consumers variable.")
      1
    }
    // if there are consumers, check if --run-scheduled-consumers is true, and run all consumers if so
    else if (runScheduledAsOneShot) {
      logger.info("Running all consumers...")
      val result = (for (cons <- consumers) yield {
        run(cons)
      }).sum
      logger.info("Completed, {} out of {} consumers ran successfully.", result, consumers.size)
      if (result == consumers.size) { 0 } else { 1 }
    }
    // otherwise, we're only running oneshot consumers. throw an error if there aren't any.
    else if (oneshot.isEmpty) {
      logger.error("No oneshot consumers are defined! You may want to run scheduled jobs instead, set --run-scheduled-consumers, or add a consumer to your Runner class.")
      1
    }
    // we should have consumers at this point. run all one-shot consumers sequentially.
    else {
      logger.info("Running oneshot consumers...")
      val result = (for (cons <- oneshot) yield {
        run(cons)
      }).sum
      logger.info("Completed, {} out of {} consumers ran successfully.", result, oneshot.size)
      if (result == oneshot.size) { 0 } else { 1 }
    }
  }

  @Command(name = "daemon", description = Array("Run the scheduled consumer daemon."))
  private def runDaemon(): Int = {
    printAsciiArt()
    logger.debug("Initializing scheduler and thread pool...")
    val latch: CountDownLatch = new CountDownLatch(1);
    // create the ScheduledThreadPoolExecutor
    val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(jobs)
    executor.setRunScheduledTasksAfterShutdown(false)

    // Stop execution if SIGINT is triggered (Ctrl+C)
    Signal.handle(new Signal("INT"),  // SIGINT
      _ => {
        logger.debug("User interrupt. Shutting down thread pool.")
        executor.shutdown()
        latch.countDown()
      }
    )

    // add all ScheduledConsumers to the thread pool
    for (cons <- scheduled) {
      val initialDelay = cons.timeToNext(Instant.now())
      executor.scheduleRepeating(Task(cons, dryRun), initialDelay, cons.interval)
    }

    logger.info("All jobs scheduled successfully. Runner can be stopped using SIGINT (Ctrl+C).")
    latch.await()
    logger.info("Completed.")
    0
  }

  @Command(name = "list", description = Array("Print information on all registered consumers"))
  private def info(): Int = {
    if (consumers.isEmpty) {
      logger.error("No consumers are defined! You should extend the Runner class and override the consumers variable.")
      1
    }
    else {
      logger.info("Listing all consumers...")
      println(consumers.mkString("\n"))
      0
    }
  }

  def call(): Int = {
    printAsciiArt()
    logger.error("No subcommand was provided. Use --help for all available commands.")
    2
  }
}

object Runner extends App {
  private val logger: Logger = LoggerFactory.getLogger(classOf[Runner])
  private val cli: CommandLine = new CommandLine(new Runner())
    .setColorScheme(Help.defaultColorScheme(Ansi.AUTO))
  System.exit(cli.execute(args: _*))
}

case class Task(cons: Consumer[Any, Any], dryRun: Boolean) extends Runnable {
  val logger: Logger = LoggerFactory.getLogger(classOf[Task])
  override def run(): Unit = try {
    if (dryRun) { cons.dryRun() } else { cons.run() }
    logger.debug(s"Consumer succeeded.")
  } catch {
    case e: Exception =>
      logger.error(s"Consumer failed after ${cons.retries} attempts: ${e.getMessage}")
  }
}