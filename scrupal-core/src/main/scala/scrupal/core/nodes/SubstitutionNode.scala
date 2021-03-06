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

package scrupal.core.nodes

import java.time.Instant

import akka.http.scaladsl.model.{MediaTypes, MediaType}
import scrupal.api._

import scala.concurrent.Future

/** Generate Content by substituting values in a template
  * This allows users to create template type content in their browser. It is simply
  * a bunch of bytes to generate but with @{...} substitutions. What goes in the ... is essentially a function call.
  * You can substitute a node (@{node('mynode}), values from the [[scrupal.api.Context]] (@{context.`var_name`}),
  * predefined variables/functions (@{datetime}), etc.
  */
case class SubstitutionNode (
  name : String,
  description: String,
  script: String,
  subordinates: Map[String, Either[Node.Ref,Node]] = Map.empty[String, Either[Node.Ref,Node]],
  modified: Option[Instant] = Some(Instant.now()),
  created: Option[Instant] = Some(Instant.now())
) extends Node {

  final val mediaType: MediaType = MediaTypes.`text/html`

  def apply(context : Context) : Future[Response] = {
    Future.successful { UnimplementedResponse("SubstitutionNode") } // FIXME: Return correct results
  }

  def resolve(ctxt: Context, tags: Map[String,(Node,Response)]) : Response = {
    // val layout = Layout(layoutId).getOrElse(Layout.default)
    val template: Array[Byte] = script.getBytes(utf8)
    // FIXME: Reinstate LayoutProducer in: EnumeratorResult(LayoutProducer(template, tags).buildEnumerator, mediaType)
    StringResponse("foo", Successful)
  }
}
