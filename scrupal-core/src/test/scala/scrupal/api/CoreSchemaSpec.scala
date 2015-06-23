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

import scrupal.core.CoreSchemaDesign
import scrupal.test.ScrupalApiSpecification

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


/** One line sentence description here.
  * Further description here.
  */
class CoreSchemaSpec extends ScrupalApiSpecification("CoreSchema") {

  "CoreSchema" should {
    "Accumulate table names correctly" in {
      val future = withStoreContext { context ⇒
        context.addSchema(CoreSchemaDesign()).map { schema ⇒
          val names = schema.collectionNames.toSeq
          val required = Seq("instances", "sites", "nodes", "principals", "alias", "token")
          for (name ← required) {
            names.contains(name) must beTrue
          }
          names.size must beEqualTo(required.size)
        }
      }
      Await.result(future, 2.seconds)
    }
  }
}