package dev.cptlobster.aggregation_framework
package builder

case class GetParamBuilder(map: Map[String, String]) extends MapBuilder {
  def append(key: String, value: String): GetParamBuilder = GetParamBuilder(map + (key, value))

  override def generate(endpoint: String): String = {
    endpoint ++ map.iterator.map(kvp => s"${kvp._1}=${kvp._2}").mkString("?", "&", "")
  }
}
