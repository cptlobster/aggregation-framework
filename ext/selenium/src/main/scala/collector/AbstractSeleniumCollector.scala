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

import org.openqa.selenium.WebDriver

/**
 * Uses a Selenium WebDriver to interact with a website and extract data. This takes the most work to implement, but
 * will be the most powerful trait you can use (since it simulates a full web browser).
 *
 * @tparam T The expected final type of the data. You will need to convert to this yourself.
 */
trait AbstractSeleniumCollector[T] extends Collector[T] {
  val browser: String
  override val collectorStr: String = s"Selenium WebDriver Collector ($browser)"
  protected val driver: WebDriver

  def get(endpoint: String): Unit = {
    driver.get(s"$baseUrl$endpoint")
  }
}
