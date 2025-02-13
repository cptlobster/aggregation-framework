package dev.cptlobster.aggregation_framework
package navigator

import org.json4s.{DefaultFormats, Formats, JArray, JValue}
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

/**
 * Document tree navigation for [[org.json4s json4s]] ASTs.
 * @param tree The initial AST.
 */
case class JsonNavigator(tree: JValue) extends TreeNavigator[JValue, String] {
  implicit val formats: Formats = DefaultFormats

  override def \(query: String): JsonNavigator = JsonNavigator(tree \ query)
  override def \\(query: String): List[JsonNavigator] = {
    val result = tree \\ query
    result.extract[List[JValue]].map(a => JsonNavigator(a))
  }
}
