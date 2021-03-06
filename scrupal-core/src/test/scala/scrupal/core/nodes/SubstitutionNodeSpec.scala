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

import akka.http.scaladsl.model.MediaTypes
import scrupal.api._
import scrupal.test.{NodeTest, ScrupalSpecification}

/** Test Case For CommandNode */
class SubstitutionNodeSpec extends ScrupalSpecification("MessageNode") with NodeTest {

  lazy val node = SubstitutionNode(specName, specName, "foo @@@bar@@@", Map.empty)

  s"$specName" should {
    "handle a simple substitution" in nodeTest(node) { r: Response ⇒
      r.mediaType must beEqualTo(MediaTypes.`text/plain`)
      r.disposition.isSuccessful must beFalse
      r.isInstanceOf[UnimplementedResponse] must beTrue
      val sr = r.asInstanceOf[UnimplementedResponse]
      sr.what must beEqualTo( "SubstitutionNode" )
    }
    "resolve substitutions" in {
      val tags : Map[String,(Node,Response)] = Map("bar" → (StringNode("bar","bar","bar") → StringResponse("bar")))
      val resolved = node.resolve(context, tags)
      resolved.isInstanceOf[StringResponse] must beTrue
      // FIXME: Update this test when SubstitutionNode actually does substitutions
    }
  }
}
