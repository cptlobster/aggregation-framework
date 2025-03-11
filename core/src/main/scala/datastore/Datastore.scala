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

/**
 * Trait for pushing data to a data store.
 * @tparam K The type of your database key. This could be something like a [[String]], [[java.util.Date Date]], or a
 *           [[java.util.UUID UUID]], or something else; it depends on how you want to index your data internally (and
 *           what your target datastore supports). Note that this must be a unique value, and ideally this would be
 *           associated with the date/time that a record is created.
 * @tparam V The expected final type of the data. This will need to match what you set in your
 *           [[dev.cptlobster.aggregation_framework.collector.Collector Collector]]s.
 */
trait Datastore[K, V] {
  /** Push a key/value pair to your datastore. */
  def push(key: K, value: V): Unit

  /** Get the key of the newest record pushed to the database. */
  def latestKey: K

  /** Get a value from the database by key. */
  def get(key: K): V

  /** Get the value of the newest record pushed to the database. */
  def latestValue: V = get(latestKey)
}
