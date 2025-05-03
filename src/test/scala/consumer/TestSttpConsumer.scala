package dev.cptlobster.aggregation_framework
package consumer

import collector.SttpCollector
import datastore.SQLDatastore

import org.postgresql.util.PSQLException

import java.sql.{PreparedStatement, Statement}

case class TestSttpConsumer(baseUrl: String, jdbcUrl: String, jdbcUsername: String, jdbcPassword: String)
  extends Consumer[Int, String]
  with SttpCollector[String]
  with SQLDatastore[Int, String] {

  /** The name of this collector. Must be unique between all collectors. */
  override val name: String = "TestSttpConsumer"
  /** The tags that this collector has. */
  override val tags: List[String] = List("test", "sttp", "hello")

  /**
   * User-defined collection function. This would query your target endpoint, but NOT store it. Retries and errors are
   * handled by the [[run()]] function, so they will not need to be implemented here.
   */
  override def collect(): (Int, String) = (latestKey + 1, get("/"))

  /**
   * Parse a string input and convert it into your intended type. You will need to define this based on the input format
   * you're using, but other collector traits (such as
   * [[dev.cptlobster.aggregation_framework.collector.JsonCollector JsonCollector]]) will implement this for you.
   *
   * @param content The response body returned from [[get]] or [[post]]
   * @return The intended response data
   */
  override def convert(content: String): String = content

  override val schema: PreparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hello (key INTEGER, val VARCHAR(30));")

  /**
   * Build a [[PreparedStatement]] using your query results. You will need to define how the data is inserted into your
   * statement based on your database schema.
   *
   * This statement will likely follow the form of:
   * {{{
   * INSERT INTO table (key, value) VALUES (?, ?);
   * }}}
   *
   * @param key   The primary key of your database
   * @param value The values
   * @return A [[PreparedStatement]] containing all your data.
   */
  override def buildQuery(key: Int, value: String): PreparedStatement = {
    val stmt = connection.prepareStatement("INSERT INTO hello(key, val) VALUES (?, ?);")
    stmt.setInt(1, key)
    stmt.setString(2, value)
    stmt
  }

  /** Get the key of the newest record pushed to the database. */
  override def latestKey: Int = {
    val stmt = connection.createStatement()
    val result = try {
      stmt.executeQuery("SELECT COUNT(*) FROM hello;").getInt("key")
    }
    catch {
      case _: PSQLException => 0
    }
    stmt.close()
    result
  }

  /** Get a value from the database by key. */
  override def get(key: Int): String = {
    val stmt = connection.prepareStatement("SELECT val FROM hello WHERE key = ?;")
    stmt.setInt(1, key)
    val result = stmt.executeQuery().getString("val")
    stmt.close()
    result
  }
}
