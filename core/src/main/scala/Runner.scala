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

case class Runner() {
  /** All declared consumers that can be run. */
  protected val consumers: List[Consumer[Any, Any]] = List()

  /** Split out ScheduledConsumers */
  private val scheduled: List[ScheduledConsumer[Any, Any]] =
    consumers
      .filter(_.getClass == classOf[ScheduledConsumer[_, _]])
      .asInstanceOf[List[ScheduledConsumer[Any, Any]]]

  /** All non-scheduled consumers, these will be run all at once */
  private val oneshot: List[Consumer[Any, Any]] = consumers diff scheduled

  def main(args: Array[String]): Unit = {
    println("   ___                              __  _         ")
    println("  / _ |___ ____ ________ ___ ____ _/ /_(____  ___ ")
    println(" / __ / _ `/ _ `/ __/ -_/ _ `/ _ `/ __/ / _ \\/ _ \\")
    println("/_/ |_\\_, /\\_, /_/  \\__/\\_, /\\_,_/\\__/_/\\___/_//_/")
    println("     /___//___/        /___/                  __  ")
    println("       / __/______ ___ _ ___ _    _____  ____/ /__")
    println("      / _// __/ _ `/  ' / -_| |/|/ / _ \\/ __/  '_/")
    println("     /_/ /_/  \\_,_/_/_/_\\__/|__,__/\\___/_/ /_/\\_\\ ")
    println("")
    println("Aggregation Framework Runner")

    if (consumers.isEmpty) {
      println("No consumers are defined! You should extend the Runner class and override the consumers variable.")
    }
    else {
      val successes: Int = runOneshot()
      println(s"Completed, $successes out of ${oneshot.size} consumers succeeded.")
    }
  }

  /** Run a single consumer, print errors to console. */
  private def run(cons: Consumer[Any, Any]): Int = {
    try {
      cons.run()
      println(s"Consumer succeeded.")
      1
    } catch {
      case e: Exception =>
        println(s"Consumer failed after ${cons.retries} attempts: ${e.getMessage}")
        0
    }
  }

  /** Run all one-shot consumers, print errors to console. */
  private def runOneshot(): Int = {
    if (oneshot.isEmpty) {
      println("No oneshot consumers are defined! You may want to run scheduled jobs instead, or add a consumer to your Runner class.")
      0
    } else {
      println("Running oneshot consumers...")
      (for (cons <- oneshot) yield {
        run(cons)
      }).sum
    }
  }

  private def runScheduled(): Unit = {

  }
}
