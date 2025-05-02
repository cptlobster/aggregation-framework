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

/**
 * CronExpr representation for declaring [[ScheduledConsumer]]s. The class stores values as [[Option]]s, if they are set
 * to [[None]] that will count as a *.
 *
 * @param minute The minute at which the job should execute.
 * @param hour The hour at which the job should execute.
 * @param dayOfMonth The day of the month that the job should execute on.
 * @param month The month of the year that the job should execute on.
 * @param dayOfWeek The day of the week that the job should execute on.
 */
class CronExpr(minute: Option[Int],
               hour: Option[Int],
               dayOfMonth: Option[Int],
               month: Option[Int],
               dayOfWeek: Option[Int]) {

  /** Check if the numeric values of the crontab are valid. */
  def validate(): Boolean = {
    minute match { case Some(n) => if (n < 0 || n > 59) { false } }
    hour match { case Some(n) => if (n < 0 || n > 23) { false } }
    dayOfMonth match { case Some(n) => if (n < 0 || n > 31) { false } }
    month match { case Some(n) => if (n < 0 || n > 12) { return false } }
    dayOfWeek match { case Some(n) => if (n < 0 || n > 7) { return false } }
    true
  }

  if (!validate()) {
    throw new IllegalArgumentException("CronExpr argument is invalid")
  }

  override def toString: String = {
    List(
      minute.getOrElse("*").toString,
      hour.getOrElse("*").toString,
      dayOfMonth.getOrElse("*").toString,
      month.getOrElse("*").toString,
      dayOfWeek.getOrElse("*").toString
    ).mkString(" ")
  }
}

object CronExpr {
  def apply(crontab: String): CronExpr = {
    ???
  }

  def apply(minute: Int, hour: Int, dayOfMonth: Int, month: Int, dayOfWeek: Int): CronExpr = {

    ???
  }
}