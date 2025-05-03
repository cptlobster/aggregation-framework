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

import util.APIError

import sttp.client3.{HttpClientSyncBackend, Identity, Response, SttpBackend, UriContext, basicRequest}

/**
 * Handles collecting data from an HTTP endpoint and parsing it into some type. All collectors should implement some
 * variant of this trait (either directly or through existing implementations such as [[HTMLCollector]] or
 * [[dev.cptlobster.aggregation_framework.collector.JsonCollector JsonCollector]]
 *
 * @tparam T The expected final type of the data. You will need to implement a [[convert]] function that can handle this
 *           data.
 */
trait SttpCollector[T] extends Collector[T] {
  override val collectorStr: String = "Basic HTTP Collector (sttp)"
  private val sttpBackend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  def query(endpoint: String): T = {
    this.convert(this.get(endpoint))
  }

  /**
   * Submit an HTTP GET request against an endpoint and receive the response as a string. You can override this with
   * other HTTP clients or scraping tools (i.e. Selenium).
   * @param endpoint The endpoint (added to [[baseUrl]])
   * @return The response body if successful.
   */
  protected def get(endpoint: String): String = {
    val url = uri"$baseUrl$endpoint"
    val response = basicRequest.get(url).send(sttpBackend)

    handleResponse(endpoint, response)
  }

  /**
   * Submit an HTTP POST request against an endpoint and receive the response as a string. You can override this with
   * other HTTP clients or scraping tools (i.e. Selenium).
   * @param endpoint The endpoint (added to [[baseUrl]])
   * @param body The request body
   * @return The response body if successful.
   */
  protected def post(endpoint: String, body: String): String = {
    val url = uri"$baseUrl$endpoint"
    val response = basicRequest.body(body).post(url).send(sttpBackend)

    handleResponse(endpoint, response)
  }

  /**
   * Determine if the response was successful or failing; return the result if successful, throw an exception otherwise
   *
   * @param endpoint The endpoint (added to [[baseUrl]])
   * @param response The response object returned by sttp
   * @return The response body if successful
   * @throws APIError if the API returns a non-200 status code
   */
  def handleResponse(endpoint: String, response: Response[Either[String, String]]): String = {
    response.body match {
      case Right(v) => v
      case Left(e) => httpErrorHandler(response.code.code, endpoint, response.statusText, e)
    }
  }

  /**
   * Parse a string input and convert it into your intended type. You will need to define this based on the input format
   * you're using, but other collector traits (such as
   * [[dev.cptlobster.aggregation_framework.collector.JsonCollector JsonCollector]]) will implement this for you.
   * @param content The response body returned from [[get]] or [[post]]
   * @return The intended response data
   */
  def convert(content: String): T
}
