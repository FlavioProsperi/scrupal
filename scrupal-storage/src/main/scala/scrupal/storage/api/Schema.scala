/** ********************************************************************************************************************
  * This file is part of Scrupal, a Scalable Reactive Web Application Framework for Content Management                 *
  *                                                                                                   *
  * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.                                                   *
  *                                                                                                   *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     *
  * with the License. You may obtain a copy of the License at                                                          *
  *                                                                                                   *
  * http://www.apache.org/licenses/LICENSE-2.0                                                                     *
  *                                                                                                   *
  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   *
  * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  *
  * the specific language governing permissions and limitations under the License.                                     *
  * ********************************************************************************************************************
  */

package scrupal.storage.api

import scrupal.utils.Validation.Results

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex

/** A group of related collections that are treated as an entity */
trait Schema extends StorageLayer {

  /** The storage entity that this schema lives in */
  def store : Store

  def design : SchemaDesign

  override def toString = { s"Schema $name in $store" }

  /** The set of collections that this schema provides */
  def collections : Map[String, Collection[_]]

  def addCollection[S <: Storable](name : String)(implicit ec: ExecutionContext) : Future[Collection[S]]

  def dropCollection[S <:Storable](name : String)(implicit ec: ExecutionContext) : Future[WriteResult]

  /** Find and return a Collection of a specific name */
  def collectionFor[S <: Storable](name : String) : Option[Collection[S]]

  /** Find collections matching a specific name pattern and return a Map of them */
  def collectionsFor(namePattern : Regex) : Map[String, Collection[_]]

  /** Get the set of collection names */
  def collectionNames : Iterable[String]

  def withCollection[S <: Storable,T](name : String)(f : Collection[S] ⇒ T) : T


  def construct : Future[Results[Schema]] = {
    // Construct this schema's contents
    design.construct(this)
  }

}

