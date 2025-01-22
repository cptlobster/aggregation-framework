package dev.cptlobster.aggregation_framework
package builder

import collector.Collector

trait QueryBuilder {
  def generate(endpoint: String): String
}