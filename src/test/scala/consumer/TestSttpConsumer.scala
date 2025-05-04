/* Copyright (C) 2025  Dustin Thomas <io@cptlobster.dev>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License (and the GNU General Public License) along
 * with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package dev.cptlobster.aggregation_framework
package consumer

import collector.SttpCollector
import datastore.SQLDatastore

import org.postgresql.util.PSQLException

import java.sql.{PreparedStatement, Statement}

/**
 * Test Consumer #1: use sttp to query the Hello World API and save it to an SQL database. URL parameters are provided
 * in the constructor since testcontainers generates them automatically
 * @param baseUrl The API URL
 * @param jdbcUrl The Postgres database URL
 * @param jdbcUsername The database username
 * @param jdbcPassword The database password
 */
case class TestSttpConsumer(baseUrl: String, jdbcUrl: String, jdbcUsername: String, jdbcPassword: String)
  extends Consumer[Int, String]
  with SttpCollector[String]
  with SQLDatastore[Int, String] {

  override val name: String = "TestSttpConsumer"
  override val tags: List[String] = List("test", "sttp", "hello")

  override def collect(): (Int, String) = (latestKey + 1, get("/"))

  override def convert(content: String): String = content

  override val schema: PreparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS hello (key INTEGER, val VARCHAR(30));")

  override def buildQuery(key: Int, value: String): PreparedStatement = {
    val stmt = connection.prepareStatement("INSERT INTO hello(key, val) VALUES (?, ?);")
    stmt.setInt(1, key)
    stmt.setString(2, value)
    stmt
  }

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

  override def get(key: Int): String = {
    val stmt = connection.prepareStatement("SELECT val FROM hello WHERE key = ?;")
    stmt.setInt(1, key)
    val result = stmt.executeQuery().getString("val")
    stmt.close()
    result
  }
}
