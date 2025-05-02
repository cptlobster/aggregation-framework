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
package collector

import org.slf4j.{Logger, LoggerFactory}

/**
 * Base trait for collecting data and parsing it into some type. This will be extended by other traits for specific HTTP
 * implementations (such as [[SttpCollector]] for basic API interactions, or more complex HTTP clients such as
 * scala-scraper or selenium).
 *
 * @tparam T The expected final type of the data. You will need to convert to this yourself.
 */
trait Collector[T] {
  val collectorStr: String
  /**
   * Base URL to access. Queries will add endpoint arguments onto this.
   */
  val baseUrl: String

  val logger: Logger = LoggerFactory.getLogger(classOf[Collector[T]])

  /**
   * This function should be called for all query processing. It will submit an HTTP query to the endpoint, run the
   * parser function on it, and return the result in the specified type. This will throw an exception in case of any
   * failures along the way. You will either need to implement this yourself or use the helper methods in other traits
   * that implement this one.
   * @param endpoint The endpoint (added to [[baseUrl]])
   * @return A fully processed value
   * @throws APIError if the API returns a non-200 status code
   * @throws ParseError if the data returns successfully, but the parser / navigator fails to parse the returned data
   * @throws ConsumerException on any other exception
   */
  def query(endpoint: String): T
}
