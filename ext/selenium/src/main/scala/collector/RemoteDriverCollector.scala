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

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver

import java.net.URI

/**
 * Uses a Selenium WebDriver to interact with a website and extract data. This takes the most work to implement, but
 * will be the most powerful trait you can use (since it simulates a full web browser). This specific trait is
 * preconfigured for a remotely operated Chrome WebDriver and provides a sensible default configuration.
 *
 * @tparam T The expected final type of the data. You will need to convert to this yourself.
 */
trait RemoteChromeCollector[T] extends ChromeCollector[T] {
  protected val driverUrl: String
  override protected val driver: WebDriver = new RemoteWebDriver(URI.create(driverUrl).toURL, options)
}

/**
 * Uses a Selenium WebDriver to interact with a website and extract data. This takes the most work to implement, but
 * will be the most powerful trait you can use (since it simulates a full web browser). This specific trait is
 * preconfigured for a remotely operated Firefox WebDriver and provides a sensible default configuration.
 *
 * @tparam T The expected final type of the data. You will need to convert to this yourself.
 */
trait RemoteFirefoxCollector[T] extends FirefoxCollector[T] {
  protected val driverUrl: String
  override protected val driver: WebDriver = new RemoteWebDriver(URI.create(driverUrl).toURL, options)
}
