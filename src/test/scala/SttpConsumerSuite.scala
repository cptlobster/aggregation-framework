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

import dev.cptlobster.aggregation_framework.consumer.TestSttpConsumer
import org.testcontainers.containers.{GenericContainer, PostgreSQLContainer}

import java.sql.{Connection, DriverManager}

case class Container(imageName: String) extends GenericContainer[Container](imageName)
case class PostgresContainer(imageName: String) extends PostgreSQLContainer[PostgresContainer](imageName)

class SttpConsumerSuite extends munit.FunSuite {
  val apiImage = "forge.cptlobster.dev/cptlobster/testing-apis:latest"
  /** Postgres database fixture */
  val postgres = FunFixture[PostgresContainer](
    setup = { test =>
      val container: PostgresContainer = PostgresContainer("postgres:17")
        .withExposedPorts(5432)
      container.start()
      println("Postgres started")
      container
    },
    teardown = { container =>
      container.stop()
    }
  )

  /** Hello World API Fixture */
  val hello = FunFixture[Container](
    setup = { test =>
      val apiContainer = Container(apiImage)
        .withExposedPorts(8000)
        .withCommand("hello")
      apiContainer.start()
      println("Hello API started")
      apiContainer
    },
    teardown = { container =>
      container.stop()
    }
  )

  val helloAndPg = FunFixture.map2(postgres, hello)

  helloAndPg.test("Test hello world") { case (pgContainer, apiContainer) =>
    val apiHost = apiContainer.getHost
    val apiPort = apiContainer.getMappedPort(8000)
    val pgHost = pgContainer.getHost
    val pgPort = pgContainer.getMappedPort(5432)
    val pgUser = pgContainer.getUsername
    val pgPassword = pgContainer.getPassword
    val consumer = TestSttpConsumer(s"http://$apiHost:$apiPort", s"jdbc:postgresql://$pgHost:$pgPort/postgres", pgUser, pgPassword)
    consumer.run()
    val conn = DriverManager.getConnection(s"jdbc:postgresql://$pgHost:$pgPort/postgres", pgUser, pgPassword)
    val stmt = conn.createStatement()
    val rs = stmt.executeQuery("SELECT val FROM hello WHERE key = 1")
    rs.next()
    val result = rs.getString("val")
    stmt.close()
    conn.close()
    assertEquals(clue(result),  "Hello, world!")
  }
}
