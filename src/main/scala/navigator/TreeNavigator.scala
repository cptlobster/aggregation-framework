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
   * Get the first element match from a document tree that match the query. This will throw an exception if no such
   * element exists.
   * @param query The query to execute against the document tree
   * @return The first element to match the query
   */
  def !>>(query: Q): TreeNavigator[T, Q] = this >> query match {
    case Some(v) => v
    case None => throw new Exception(s"Element not found matching query: $query")
  }
  /**
   * Get all elements from a document tree that match the query. This will throw an exception if no such elements exist.
   *
   * @param query The query to execute against the document tree
   * @return All elements that match the query
   */
  def !:>>(query: Q): List[TreeNavigator[T, Q]] = this :>> query match {
    case Some(v) => if (v.nonEmpty) { v } else { throw new Exception(s"No elements found matching query: $query") }
    case None => throw new Exception(s"No elements found matching query: $query")
  }
  /**
   * Get all elements from a document tree that match the query. This will throw an exception if no such elements exist.
   *
   * @param query The query to execute against the document tree
   * @return All elements that match the query
   */
  def >>(query: Q): Option[TreeNavigator[T, Q]] = {
    try {
      Some(this !>> query)
    }
    catch {
      case _: Exception => None
    }
  }
  /**
   * Get the first element match from a document tree that match the query.
   *
   * @param query The query to execute against the document tree
   * @return All elements that match the query.
   */
  def :>>(query: Q): Option[List[TreeNavigator[T, Q]]] = {
    try {
      Some(this !:>> query)
    }
    catch {
      case _: Exception => None
    }
  }

  /**
   * Extract the original class element.
   * @return The current tree element
   */
  def extract: T = this.tree
}
