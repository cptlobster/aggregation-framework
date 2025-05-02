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
package navigator

/**
 * Standardized document tree navigation class. This can be implemented to navigate any tree-like data model, such as
 * JSON or HTML, or for any framework such as Selenium or scala-scraper.
 *
 * @tparam T The document tree type.
 * @tparam Q The query parameter type. Could be [[String]], or in Selenium's case, [[org.openqa.selenium.By By]]
 */
abstract class TreeNavigator[T, Q] {
  val tree: T
  /**
   * Get the first element from a document tree that matches the query. This will throw an exception if no such
   * element exists.
   * @param query The query to execute against the document tree
   * @return The first element to match the query
   */
  def \(query: Q): TreeNavigator[T, Q]

  /**
   * Get the first element from a document tree that matches the query.
   *
   * @param query The query to execute against the document tree
   * @return All elements that match the query
   */
  def \\(query: Q): List[TreeNavigator[T, Q]]

  /**
   * Get the original value.
   * @return The current tree element
   */
  def get: T = this.tree
}
