/**********************************************************************************************************************
 * This file is part of Scrupal, a Scalable Reactive Web Application Framework for Content Management                 *
 *                                                                                                                    *
 * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.                                                   *
 *                                                                                                                    *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     *
 * with the License. You may obtain a copy of the License at                                                          *
 *                                                                                                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                                                                     *
 *                                                                                                                    *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   *
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  *
 * the specific language governing permissions and limitations under the License.                                     *
 **********************************************************************************************************************/

package scrupal.api

import java.time.Instant

import scrupal.api.html.Icon
import scrupal.storage.api.{Query, Queries, Storable}

/** Representation of an alert message that is shown at the top of every page. Alerts are queued and each user
  * has a "latest" alert they've seen. Alerts expire, however, so it is possible for a user to miss an alert.
  * The intent is to provide a way to provide cross-site alerting of system notices such as down time or new
  * feature enhancements.
  * @param name Name of the Alert
  * @param description Brief description of the alert
  * @param message Text of the message to deliver to users
  * @param alertKind The kind of alert
  * @param icon The icon to use in the alert
  * @param prefix The prefix label to use in the alert
  * @param cssClass The cssClass name to use in the alert
  * @param expiry The time at which the alert expires
  */
case class Alert(
  _id : Identifier,
  name : String,
  description : String,
  message : String,
  alertKind : AlertKind,
  icon : Icon,
  prefix : String,
  cssClass : String,
  expiry : Option[Instant],
  override val modified : Option[Instant] = Some(Instant.now),
  override val created : Option[Instant] = Some(Instant.now)
) extends Storable with Nameable with Describable with Modifiable with Expirable {
  /** A shorthand constructor for Alerts.
    * This makes it possible to construct alerts with fewer parameters. The remaining parameters are chosen sanely
    * based on the alertKind parameter, which defaults to a Note
    * @param name Label of the Alert
    * @param description Brief description of the alert
    * @param message Text of the message to deliver to users
    * @param alertKind The kind of alert
    */
  def this(id : Identifier, name : String, description : String, message : String, alertKind : AlertKind) =
    {
      this(id, name, description, message, alertKind, alertKind.icon, alertKind.prefix, alertKind.css,
           Some(alertKind.expiry), Some(Instant.now()), Some(Instant.now()))
    }
}

trait AlertQueries extends Queries[Alert] {
  def unexpired : Query[Alert]
}
