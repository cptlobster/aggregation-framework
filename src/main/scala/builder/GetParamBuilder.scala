package dev.cptlobster.aggregation_framework
package builder

case class GetParamBuilder(map: Map[String, String]) extends MapBuilder {
  def append(key: String, value: String): GetParamBuilder = {
    val new_map = map
    GetParamBuilder(new_map)
  }

  override def generate: String = { map.iterator.map(kvp => s"${kvp._1}=${kvp._2}").mkString("?", "&", "")}
}
