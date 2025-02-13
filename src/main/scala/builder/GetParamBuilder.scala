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
package builder

case class GetParamBuilder(map: Map[String, String]) extends MapBuilder {
  def append(key: String, value: String): GetParamBuilder = GetParamBuilder(map + (key, value))

  override def generate(endpoint: String): String = {
    endpoint ++ map.iterator.map(kvp => s"${kvp._1}=${kvp._2}").mkString("?", "&", "")
  }
}
