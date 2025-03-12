# aggregation-framework-json

Extension package for handling JSON APIs in Aggregation Framework. This uses [json4s](https://github.com/json4s/json4s)
for parsing.

## Extensions
- `JsonCollector`: extension of standard sttp collector that parses response as JSON using json4s.
- `JsonNavigator`: Extension of `TreeNavigator` for navigating structured JSON data.

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