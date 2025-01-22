package dev.cptlobster.aggregation_framework

import java.util.Date

/**
 * The simplest data consumer implementation. You can use this as an example to setup your own consumers, or extend it
 * with specific Consumer traits.
 *
 * @tparam T The expected final type of the data. You will need to convert to this yourself.
 */
abstract class SimpleConsumer[T] extends Consumer[Date, T] {
  val endpoint: String
  def collect(): Unit = {
    val result: T = query(endpoint)
    push(new Date(), result)
  }
}
