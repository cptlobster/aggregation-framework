package dev.cptlobster.aggregation_framework
package navigator

/**
 * Extended [[TreeNavigator]] for use on HTML or XML document trees. This provides some built-in extensions for class
 * and ID searching, which should make it more convenient for libraries like Selenium that use their own parameter
 * types.
 *
 * @tparam T The return type.
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
  def #\(id: String): DOMNavigator[T, Q]
  /**
   * Get all elements from a document tree that match the given ID. This will throw an exception if no such elements exist.
   *
   * @param id The ID to search for
   * @return All elements that match the query
   */
  def #\\(id: String): Option[List[DOMNavigator[T, Q]]]
  /**
   * Get the first element from a document tree that matches the given class. This will throw an exception if no such
   * element exists.
   *
   * @param className The class to search for
   * @return The first element to match the query
   */
  def @\(className: String): Option[DOMNavigator[T, Q]]
  /**
   * Get all elements from a document tree that match the given class. This will throw an exception if no such elements
   * exist.
   *
   * @param className The class to search for
   * @return All elements that match the query
   */
  def @\\(className: String): Option[List[DOMNavigator[T, Q]]]
}
