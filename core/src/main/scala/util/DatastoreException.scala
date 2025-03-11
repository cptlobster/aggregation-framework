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
package util

/** Base class for any datastore related error. */
class DatastoreException(desc: String) extends Exception {
  val message: String = s"Datastore error: $desc"
}

/** Datastore connection error. This should be used if the Consumer fails to connect to the Datastore for any reason. */
class DatastoreConnectError(desc: String) extends DatastoreException(desc) {
  override val message: String = s"Failed to connect to datastore: $desc"
}

/** Datastore authentication error. This should be used if auth credentials are incorrect. */
class DatastoreAuthError(desc: String) extends DatastoreConnectError(desc) {
  override val message: String = s"Error authenticating with datastore: $desc"
}

/** Datastore push error. This should be used if the Consumer fails to push newly read data to the Datastore. */
class DatastorePushError(desc: String) extends DatastoreException(desc) {
  override val message: String = s"Error pushing entry to datastore: $desc"
}