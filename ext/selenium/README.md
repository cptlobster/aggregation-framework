# aggregation-framework-selenium

Extension package that allows for using [Selenium WebDriver](https://selenium.dev) as an HTTP client. The Selenium
consumers are more complex to implement, but provides the functionality of a full web browser when scraping/collecting
data.

## Dependencies

This extension requires that you have the web browser you intend to use installed (either Firefox or Chrome). You can
optionally install the WebDriver for your respective browser, but this is not required (as Selenium Manager will handle
installing the driver as of Selenium 4.6).

To install the drivers yourself, consult the following:
- Chromium: https://googlechromelabs.github.io/chrome-for-testing
- Firefox: https://github.com/mozilla/geckodriver/releases

Alternatively, you can target a remote WebDriver, such as a Selenium standalone docker image or a Selenium Grid.

- Docker Hub: https://hub.docker.com/u/selenium
- Documentation / Source: https://github.com/SeleniumHQ/docker-selenium

## Extensions

- `AbstractSeleniumCollector`: Basic Collector class built for Selenium WebDriver.
  - `ChromeCollector`: Collector class derivation for use with Google Chrome as a WebDriver.
    - `RemoteChromeCollector`: Derivation for a remotely controlled Google Chrome WebDriver.
  - `FirefoxCollector`: Collector class derivation for use with Mozilla Firefox as a WebDriver.
    - `RemoteFirefoxCollector`: Derivation for a remotely controlled Mozilla Firefox WebDriver.

## License
This program is licensed under the [GNU General Public License, version 3](../../LICENSE.md).

*This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
version.*<br />
*This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.*
<br />
*You should have received a copy of the GNU General Public License along with this program. If not, see
https://www.gnu.org/licenses/.*