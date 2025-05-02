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

import util.{ConsumerException, ParseError}

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.ValidationException
import net.ruippeixotog.scalascraper.model.Document

/**
 * Handles collecting HTML data from an HTTP endpoint and parsing it into some type.
 *
 * @tparam T The expected final type of the data. You will need to implement a [[convert]] function that converts a
 *           Scala-scraper [[Document]] to this datatype.
 */
trait HTMLCollector[T] extends Collector[T] {
  override val collectorStr: String = "HTML Collector (scala-scraper)"
  protected val browser: Browser = JsoupBrowser()

  override def query(endpoint: String): T = {
    convert(request(endpoint))
  }

  def request(endpoint: String): Document = {
    try {
      browser.get(s"$baseUrl$endpoint")
    }
    catch {
      case e: ValidationException => throw new ParseError(e.getMessage)
      case e: NoSuchElementException => throw new ParseError(e.getMessage)
      case e: Exception => throw new ConsumerException(e.getMessage)
    }
  }

  def convert(content: Document): T
}
