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
import scrupal.api.Html._
import scrupal.api.Response
import scrupal.api._
import scrupal.test.{NodeTest, ScrupalSpecification}

import scalatags.Text.all._

/** Test Case For CommandNode */
class HtmlNodeSpec extends ScrupalSpecification("HtmlNode") with NodeTest {

  lazy val template = new Html.Template(Symbol(specName)) {
    val description = "Describe me"
    def apply(context: Context, args: ContentsArgs) : Contents = Seq(span("scrupal"))
  }

  lazy val node = HtmlNode(specName, specName, template)

  s"$specName" should {
    "handle a simple span element" in nodeTest(node) { r: Response ⇒
      r.mediaType must beEqualTo(MediaTypes.`text/html`)
      r.disposition.isSuccessful must beTrue
      r.isInstanceOf[HtmlResponse] must beTrue
      val sr = r.asInstanceOf[HtmlResponse]
      sr.content.contains(<span>scrupal</span>.toString()) must beTrue
    }
  }
}
