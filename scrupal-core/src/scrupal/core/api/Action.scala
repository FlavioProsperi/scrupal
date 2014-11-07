/**********************************************************************************************************************
  * This file is part of Scrupal a Web Application Framework.                                                          *
  *                                                                                                                    *
  * Copyright (c) 2013, Reid Spencer and viritude llc. All Rights Reserved.                                            *
  *                                                                                                                    *
  * Scrupal is free software: you can redistribute it and/or modify it under the terms                                 *
  * of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,   *
  * or (at your option) any later version.                                                                             *
  *                                                                                                                    *
  * Scrupal is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied      *
  * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more      *
  * details.                                                                                                           *
  *                                                                                                                    *
  * You should have received a copy of the GNU General Public License along with Scrupal. If not, see either:          *
  * http://www.gnu.org/licenses or http://opensource.org/licenses/GPL-3.0.                                             *
  **********************************************************************************************************************/
package scrupal.core.api

import play.twirl.api.Html
import reactivemongo.bson.BSONDocument

/** Encapsulation Of An
  *
  * Requests are generated from a request receiver such as scrupal-http. They encapsulate an entity, an instance of
  * that entity, and the invocation arguments. This request information is passed to an Action when it is invoked.
  * @tparam E The type of entity to which the request is addressed.
  */
trait Request[E] {
  val entity: E
  val instance: Instance
  val args: Map[String,Any]
}

/** Encapsulation of an Action's Result
  *
  * Results are generated from an action processing a request. They encapsulate a payload and a disposition. The
  * disposition provides a quick summary of the result while the payload provides access to the actual resulting
  * information.
  * @tparam P
  */
trait Result[P] {
  val disposition : Disposition
  val payload: P
}

trait BSONResult extends Result[BSONDocument]
trait HTMLResult extends Result[Html]
trait TextResult extends Result[String]

/** An Action Invokable On An Entity
  *
  * Actions bring behavior to entities. Each entity can declare a set of actions that its users can invoke from the
  * REST api. Unlike the methods provided by the [[Entity]] class, these Actions operate on the Instances of the
  * entity themselves. The Entity methods are more like operations on the collection of Entities.
  */
trait Action[E <: Entity,T] extends ( Request[E] => Result[T]) {
  /** Objects mixing in this trait will define apply to implement the Action.
    * Note that the result type is fixed to return a BSONDocument because Methods are only invokable from the REST api
    * which requires results to be in the form of a BSONDocument
    * @return The Play Action that results in a JsObject to send to the client
    */
  def apply(request: Request[E]) : Result[T]
}