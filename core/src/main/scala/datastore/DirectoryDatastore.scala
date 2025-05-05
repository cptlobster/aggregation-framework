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

import java.nio.file.Path
import util.{DatastoreNotFoundError, DatastorePushError, DatastoreReadError}

import java.io.{File, FileWriter}
import java.util.Comparator
import scala.io.Source

/**
 * Trait for appending data as files to a directory. Your Key will be the name of the file (before the extension, and
 * your values will be serialized and inserted into the file.
 *
 * @tparam K The type of your database's primary key. This could be something like a [[String]],
 *           [[java.util.Date Date]], or a [[java.util.UUID UUID]], or something else; it depends on how you want to
 *           index your data internally. You will need to implement a way to convert this to a String (or use a method
 *           that handles that for you).
 * @tparam V The expected final type of the data. This will need to match what you set in your
 *           [[dev.cptlobster.aggregation_framework.collector.Collector Collector]]s.
 */
trait DirectoryDatastore[K, V] extends Datastore[K, V] {
  val directory: Path
  val extension: Option[String] = None

  def push(key: K, value: V): Unit = {
    val d = directory.toFile
    if (d.exists) {
      if (d.isDirectory) {
        val targetPath = directory.resolve(makeFileName(key))
        val targetFile = targetPath.toFile
        val writer = new FileWriter(targetFile)
        writer.append(serializeVal(value))
        writer.close()
      }
      else {
        throw new DatastorePushError(s"$directory is not a directory")
      }
    }
  }

  private def makeFileName(key: K): String = extension match {
    case Some(s) => s"${serializeKey(key)}.$s"
    case None => serializeKey(key)
  }

  protected def serializeVal(value: V): String = value.toString
  protected def serializeKey(key: K): String = key.toString

  protected def deserializeVal(value: String): V
  protected def deserializeKey(key: String): K

  /** Get the key of the newest record pushed to the database. */
// TODO: make this work despite the fact that I can't set an implicit Ordering[K]
//
//  def latestKey: K = {
//    val d = directory.toFile
//    if (d.exists && d.isDirectory) {
//
//    }
//    else {
//      throw new DatastoreReadError(s"$directory either does not exist or is not a directory")
//    }
//  }

  /** Get a value from the database by key. */
  def get(key: K): V = {
    val d = directory.toFile
    if (d.exists && d.isDirectory) {
      val targetPath = directory.resolve(makeFileName(key))
      val targetFile = targetPath.toFile
      if (targetFile.exists) {
        val source = Source.fromFile(targetFile)
        val content = deserializeVal(source.getLines().mkString("\n"))
        source.close()
        content
      }
      else {
        throw new DatastoreNotFoundError()
      }
    }
    else {
      throw new DatastoreReadError(s"$directory either does not exist or is not a directory")
    }
  }

  /** Get the value of the newest record pushed to the database. */
  def latestValue: V = get(latestKey)
}
