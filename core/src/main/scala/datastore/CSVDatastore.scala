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
package datastore

import dev.cptlobster.aggregation_framework.util.DatastoreNotFoundError
import org.apache.commons.text.{StringEscapeUtils, StringTokenizer}

import scala.jdk.CollectionConverters._
import scala.io.{BufferedSource, Source}
import java.io.{File, FileReader, FileWriter}
import java.nio.file.Path
import scala.annotation.tailrec

/**
 * Trait for appending data as lines to a CSV file. Your Key will be the first column, and your values will be split
 * across the remaining columns.
 *
 * @tparam K The type of your database's primary key. This could be something like a [[String]],
 *           [[java.util.Date Date]], or a [[java.util.UUID UUID]], or something else; it depends on how you want to
 *           index your data internally. You will need to implement a way to convert this to a String (or use a method
 *           that handles that for you).
 * @tparam V The expected final type of the data. This will need to match what you set in your
 *           [[dev.cptlobster.aggregation_framework.collector.Collector Collector]]s.
 */
trait CSVDatastore[K, V] extends Datastore[K, V] {
  val file: Path
  val headers: List[String]

  def push(key: K, value: V): Unit = {
    val transformedVals = serialize(value).map(escape).mkString(",")
    val line = s"${escape(key.toString)},${transformedVals}"
    val f: File = file.toFile
    if (f.exists) {
      val writer = new FileWriter(f)
      writer.append(line)
      writer.close()
    }
    else {
      logger.info("File empty. Creating new...")
      val writer = new FileWriter(f)
      writer.append(headers.map(escape).mkString("", ",", "\n"))
      writer.append(line)
      writer.close()
    }
  }

  private def escape(value: String): String = {
    StringEscapeUtils.escapeCsv(value)
  }

  /**
   * Transform a complex data structure into a list of columns.
   * @param value
   * @return
   */
  def serialize(value: V): List[String]

  /** Transform a list of columns into the original data structure. */
  def deserialize(key: String): K
  /** Transform a list of columns into the original data structure. */
  def deserialize(value: List[String]): V
  /** Transform a list of columns into the original data structure. */
  def deserialize(key: String, value: List[String]): (K, V)

  /** Get the key of the newest record pushed to the database. */
  def latestKey: K = {
    val f = file.toFile
    if (f.exists) {
      val reader = Source.fromFile(f)
      val line = try end(reader.getLines) finally reader.close()
      deserialize(new StringTokenizer(line).getTokenList.asScala.toList.head)
    }
    else {
      throw new DatastoreNotFoundError()
    }
  }

  def get(key: K): V = {
    val kStr: String = escape(key.toString)
    val f = file.toFile
    if (f.exists) {
      val reader = Source.fromFile(f)
      val line = try matchLine(reader.getLines, kStr) finally reader.close()
      deserialize(new StringTokenizer(line).getTokenList.asScala.toList.tail)
    }
    else {
      throw new DatastoreNotFoundError()
    }
  }

  @tailrec private def matchLine(iter: Iterator[String], key: String): String = {
    if (iter.hasNext) {
      val next = iter.next
      if (next.startsWith(key)) {
        next
      } else {
        matchLine(iter, key)
      }
    }
    else {
      throw new DatastoreNotFoundError()
    }
  }

  @tailrec private def end[T](iter: Iterator[T]): T = {
    val next = iter.next
    if (iter.hasNext) { end(iter) } else { next }
  }
}
