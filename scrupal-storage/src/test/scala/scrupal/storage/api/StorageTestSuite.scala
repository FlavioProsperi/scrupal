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

package scrupal.storage.api

import org.specs2.execute.{Error, Result}
import org.specs2.mutable.Specification
import play.api.libs.json.Json
import scrupal.storage.impl.JsonFormatter
import scrupal.utils.ScrupalComponent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class DingBot(id : Long, ding : String, bot : Long) extends Storable

object DingBotFormatter$ extends JsonFormatter[DingBot](Json.format[DingBot])

object DingBotsSchema extends SchemaDesign {
  override def name : String = "dingbots"


  override def requiredNames : Seq[String] = Seq("dingbots")
}

/** TestSuite Pattern For Testing Storage Implementations
  *
  * Each implementation of the Storage API should create a testing specification that inherits from this class
  * and fills in the blanks. All test must pass these tests before the implementation is considered conforming.
  * Note that in addition to implementing the missing definitions, a number of configuration files will be needed
  * as well.
  * @param name The name of the test suite being run
  */
abstract class StorageTestSuite(name: String) extends Specification with ScrupalComponent {

  def driver: StorageDriver
  def driverName : String
  def scheme: String
  def configFile: String

  def getContext(name: String, create : Boolean=true)(func : StoreContext ⇒ Future[Result]) : Result = {
    val f = Storage.fromConfigFile(configFile, name, create) flatMap { context ⇒
      func(context)
    }
    val g = f.recover { case x: Throwable ⇒
      Error(s"Unexpected exception: ${x.getClass.getSimpleName}: ${x.getMessage}", x)
    }
    Await.result(g, 5.seconds)
  }

  sequential

  s"$name" should {

    sequential

    "have same name as this test" in {
      driver.name must beEqualTo(driverName)
    }

    "recognize its scheme" in {
      driver.scheme must beEqualTo(scheme)
    }

    "obtain a context to the main test database" in {
      getContext("testing") { context ⇒
        Future {
          success
        }
      }
    }

    "obtain a store from the context" in {
      getContext("testing") { context ⇒
        Future {
          context.withStore { store ⇒
            (store.name must beEqualTo(store.uri.getPath)).toResult
          }
        }
      }
    }

    "create the DingBotsSchema" in {
      getContext("testing", create=false)  { context ⇒
        if (context.hasSchema(DingBotsSchema.name)) {
          context.dropSchema(DingBotsSchema.name) flatMap { wr ⇒
            wr.tossOnError
            if (context.hasSchema(DingBotsSchema.name))
              toss("Dropping schema failed")
            else {
              context.addSchema(DingBotsSchema) map { schema ⇒
                if (!context.hasSchema(DingBotsSchema.name))
                  toss("Adding schema failed")
                success
              }
            }
          }
        } else {
          context.addSchema(DingBotsSchema) map { schema ⇒
            if (!context.hasSchema(DingBotsSchema.name))
              toss("Adding schema failed")
            success
          }
        }
      }
    }

    "not allow duplication of a collection" in {
      getContext("testing", create=false)  { context ⇒
        context.withSchema(DingBotsSchema.name) { schema ⇒
          schema.addCollection[DingBot]("dingbots") map { coll ⇒
            failure("schema.addCollection should have failed")
          } recover {
            case x: Throwable ⇒ success
          }
        }
      }
    }

    "insert a dingbot in a collection" in {
      getContext("testing", create=false)  { context ⇒
        context.withSchema(DingBotsSchema.name) { schema ⇒
          schema.withCollection[DingBot,Future[Result]]("dingbots") { coll ⇒
            coll.insert(new DingBot(1, "ping", 42)) map { wr: WriteResult ⇒
              (wr.isSuccess must beTrue).toResult
            }
          }
        }
      }
    }

    "find a dingbot in a collection" in {
      getContext("testing", create=false) { context ⇒
        context.withSchema(DingBotsSchema.name) { schema ⇒
          schema.withCollection[DingBot,Future[Result]]("dingbots") { coll ⇒
            coll.fetch(1).map { optDB : Option[DingBot] ⇒
              optDB.nonEmpty must beTrue
              optDB.get.getPrimaryId must beEqualTo(1)
            }
          }
        }
      }
    }

    "resolve a FastReference to a dingbot" in {
      getContext("testing", create=false) { implicit context: StoreContext ⇒
        context.withSchema(DingBotsSchema.name) { schema ⇒
          schema.withCollection[DingBot,Future[Result]]("dingbots") { coll ⇒
            coll.fetch(1).map { optDB : Option[DingBot] ⇒
              optDB.nonEmpty must beTrue
              val obj: DingBot = optDB.get
              val fr1 = FastReference(coll, obj)
              val fr2 = FastReference(coll, obj.getPrimaryId())
              fr1.collection must beEqualTo(coll)
              fr2.collection must beEqualTo(coll)
              fr1.id must beEqualTo(obj.getPrimaryId())
              fr2.id must beEqualTo(obj.getPrimaryId())
              val f1 = fr1.fetch.map { optObj ⇒
                optObj.nonEmpty must beTrue
                optObj.get must beEqualTo(obj)
              }
              val f2 = fr2.fetch.map { optObj ⇒
                optObj.nonEmpty must beTrue
                optObj.get must beEqualTo(obj)
              }
              val res = Await.result(Future sequence Seq(f1,f2), 2.seconds)
              res.head.isSuccess must beTrue
              res(1).isSuccess must beTrue
            }
          }
        }
      }

    }

    "find all dingbots in a collection" in {
      getContext("testing", create=false) { context ⇒
        context.withSchema(DingBotsSchema.name) { schema ⇒
          schema.withCollection[DingBot,Future[Result]]("dingbots") { coll ⇒
            coll.fetchAll.map { bots : Iterable[DingBot] ⇒
              val seq = bots.toSeq
              seq.nonEmpty must beTrue
              seq.size must beGreaterThanOrEqualTo(1)
            }
          }
        }
      }
    }

    "close the context" in {
      getContext("testing", create=false) { context ⇒
        context.close()
        Future.successful { success }
      }
    }
  }
}
