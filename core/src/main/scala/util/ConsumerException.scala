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
package util

/** Base class for any consumer-related exception. */
class ConsumerException(desc: String) extends Exception {
  val message: String = s"Consumer error: $desc"
}

/** Base class for API-induced errors (for example, non-200 HTTP error codes) */
class APIError(status: Int, endpoint: String = "", desc: String = "", body: String = "") extends ConsumerException(desc) {
  override val message: String = s"Endpoint error HTTP $status: $desc"
}

/**
 * If you hit an API ratelimit, this should be called; you can configure your Collector to retry after the limit
 * expires.
 */
class RateLimitError(status: Int, endpoint: String = "", desc: String = "", body: String = "")
  extends APIError(status, endpoint, desc, body) {
  override val message: String = s"Endpoint ratelimit reached (HTTP $status: $desc)"
}

/** Parser-induced error. */
class ParseError(desc: String) extends ConsumerException(desc) {
  override val message: String = s"Consumer parser error: $desc"
}

/** Errors specifically induced by navigator classes. */
class NavigatorError(selector: String, desc: String) extends ParseError(desc) {
  override val message: String = s"Navigator error on `$selector`: $desc"
}