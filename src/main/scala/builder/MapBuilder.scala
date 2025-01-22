package dev.cptlobster.aggregation_framework
package builder

trait MapBuilder extends QueryBuilder {
  val map: Map[String, String]
  def append(key: String, value: String): MapBuilder
  def generate(endpoint: String): String
}
