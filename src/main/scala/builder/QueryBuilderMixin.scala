package dev.cptlobster.aggregation_framework
package builder

import collector.Collector

trait QueryBuilderMixin[Q <: QueryBuilder, T] extends Collector[T] {
  def query(endpoint: String, params: Q): T = query(params.generate(endpoint))
}
