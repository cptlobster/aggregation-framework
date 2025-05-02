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

import java.util.Date

/**
 * The simplest data consumer implementation. You can use this as an example to setup your own consumers, or extend it
 * with specific Consumer traits.
 *
 * @tparam T The expected final type of the data. You will need to convert to this yourself.
 */
abstract class SimpleConsumer[T] extends Consumer[Date, T] {
  val endpoint: String
  def collect(): (Date, T) = {
    val result: T = query(endpoint)
    (new Date(), result)
  }
}
