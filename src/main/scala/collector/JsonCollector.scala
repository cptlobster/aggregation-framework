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
package collector

import navigator.JsonNavigator

import org.json4s._
import org.json4s.native.JsonMethods._

/**
 *  Handles collecting JSON data from an HTTP endpoint and parsing it into some type. This uses [[org.json4s json4s]]
 *  for parsing JSON data; you can either create a type that fits your endpoint's expected data structure and json4s
 *  will handle parsing the types for you, or you can use
 *  [[https://github.com/json4s/json4s?tab=readme-ov-file#querying-json query methods]] to navigate the object tree and
 *  extract certain values.
 *
 * @tparam T The expected final type of the data. You will need to implement a [[convert]] function that can handle this
 *           data.
 */
trait JsonCollector[T] extends SttpCollector[T] {
  implicit val formats: Formats = DefaultFormats

  /**
   * Parse a response as JSON.
   *
   * @param content The response body returned from [[request]]
   *  @return The intended response data
   */
  def convert(content: String): T = {
    find(parse(content)).extract[T]
  }

  /**
   * Find a specific [[JValue]] in your source tree. By default, returns the JSON output as-is, but you can override it
   * to target specific JSON values. There is a version of this function that uses [[JsonNavigator]]s to parse values.
   * @param value the original JSON tree
   * @return the JSON tree that you targeted with this function
   */
  def find(value: JValue): JValue = find(JsonNavigator(value)).get

  /**
   * Find a specific [[JValue]] in your source tree. By default, returns the JSON output as-is, but you can override it
   * to target specific JSON values.
   * @param value the original JSON tree
   * @return the JSON tree that you targeted with this function
   */
  def find(value: JsonNavigator): JsonNavigator = value
}
