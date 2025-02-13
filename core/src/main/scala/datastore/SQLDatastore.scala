/* Copyright (C) 2025  Dustin Thomas <io@cptlobster.dev>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package dev.cptlobster.aggregation_framework
package datastore

import java.sql.{Connection, DriverManager, PreparedStatement}

/**
 * Trait for pushing data to an SQL datastore (using JDBC).
 *
 * @tparam K The type of your database's primary key. This could be something like a [[String]],
 *           [[java.util.Date Date]], or a [[java.util.UUID UUID]], or something else; it depends on how you want to
 *           index your data internally. You will need to implement a way to convert this to a String (or use a method
 *           that handles that for you).
 * @tparam V The expected final type of the data. This will need to match what you set in your
 *           [[dev.cptlobster.aggregation_framework.collector.Collector Collector]]s. You will need to implement a way
 *           to convert this to a [[PreparedStatement]], which you can implement the [[buildQuery]] function for.
 */
trait SQLDatastore[K, V] extends Datastore[K, V] {
  /** The JDBC URL for your SQL database. */
  val jdbcUrl: String
  /** The username for your SQL database. */
  val jdbcUsername: String
  /** The password for your SQL database. */
  val jdbcPassword: String

  /** The SQL connection. You will need to use this when assembling your Statements. */
  val connection: Connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)

  def push(key: K, value: V): Unit = {
    val stmt: PreparedStatement = buildQuery(key, value)
    stmt.executeQuery()
  }

  /**
   * Build a [[PreparedStatement]] using your query results. You will need to define how the data is inserted into your
   * statement based on your database schema.
   * @param key The primary key of your database
   * @param value The values
   * @return A [[PreparedStatement]] containing all your data.
   */
  def buildQuery(key: K, value: V): PreparedStatement
}
