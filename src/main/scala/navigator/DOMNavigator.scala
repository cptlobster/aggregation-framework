package dev.cptlobster.aggregation_framework
package navigator

/**
 * Extended [[TreeNavigator]]. This adds dedicated functionality for handling CSS selectors or XPath.
 *
 * @tparam T The document tree type.
 * @tparam Q The query parameter type. Could be [[String]], or in Selenium's case, [[org.openqa.selenium.By By]]
 */
abstract class DOMNavigator[T, Q] extends TreeNavigator[T, Q] {
  /**
   * Get the first element from a document tree that matches the given ID. This will throw an exception if no such
   * element exists.
   *
   * @param id The ID to search for
   * @return The first element to match the query
   */
  def #>>(id: String): Option[DOMNavigator[T, Q]] = {
    try { Some(this !#>> id) }
    catch {
      case _: Exception => None
    }
  }
  /**
   * Get the first element from a document tree that matches the given ID.
   *
   * @param id The ID to search for
   * @return The first element to match the query
   */
  def !#>>(id: String): DOMNavigator[T, Q] = this #>> id match {
    case Some(v) => v
    case None => throw new Exception(s"Element not found matching id: $id")
  }
  /**
   * Get all elements from a document tree that match the given ID. This will throw an exception if no such elements exist.
   *
   * @param id The ID to search for
   * @return All elements that match the query
   */
  def :#>>(id: String): Option[List[DOMNavigator[T, Q]]] = {
    try { Some(this !:#>> id) }
    catch {
      case _: Exception => None
    }
  }
  /**
   * Get all elements from a document tree that match the given ID.
   *
   * @param id The ID to search for
   * @return All elements that match the query
   */
  def !:#>>(id: String): List[DOMNavigator[T, Q]] = this :#>> id match {
    case Some(v) => if (v.nonEmpty) { v } else { throw new Exception(s"No elements found matching query: $query") }
    case None => throw new Exception(s"No elements found matching id: $id")
  }
  def @>>(className: String): Option[DOMNavigator[T, Q]] = {
    try { Some(this !@>> className) }
    catch {
      case _: Exception => None
    }
  }
  def !@>>(className: String): DOMNavigator[T, Q] = this @>> className match {
    case Some(v) => v
    case None => throw new Exception(s"Element not found matching class: $className")
  }
  def :@>>(className: String): Option[List[DOMNavigator[T, Q]]] = {
    try { Some(this !:@>> className) }
    catch {
      case _: Exception => None
    }
  }
  def !:@>>(className: String): List[DOMNavigator[T, Q]] = this :@>> className match {
    case Some(v) => if (v.nonEmpty) { v } else { throw new Exception(s"No elements found matching query: $query") }
    case None => throw new Exception(s"No elements found matching class: $className")
  }
}
