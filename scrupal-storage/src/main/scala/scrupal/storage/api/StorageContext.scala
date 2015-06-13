/** ********************************************************************************************************************
  * This file is part of Scrupal, a Scalable Reactive Content Management System.                                       *
  *                                                                                                     *
  * Copyright © 2015 Reactific Software LLC                                                                            *
  *                                                                                                     *
  * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this file                         *
  * except in compliance with the License. You may obtain a copy of the License at                                     *
  *                                                                                                     *
  * http://www.apache.org/licenses/LICENSE-2.0                                                                  *
  *                                                                                                     *
  * Unless required by applicable law or agreed to in writing, software distributed under the                          *
  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,                          *
  * either express or implied. See the License for the specific language governing permissions                         *
  * and limitations under the License.                                                                                 *
  * ********************************************************************************************************************
  */

package scrupal.storage.api

import java.io.Closeable
import java.net.URI
import java.util.concurrent.atomic.AtomicInteger

import play.api.Configuration

import scrupal.storage.impl.StorageConfigHelper
import scrupal.utils.{ ConfigHelpers, Registry, ScrupalComponent, Registrable }

import scala.concurrent.ExecutionContext
import scala.util.{ Failure, Success, Try }

/** Context For Storage
  *
  * When connecting to a storage provider, subclasses of this instance provide all the details
  */
trait StorageContext extends Registrable[StorageContext] with Closeable with ScrupalComponent {
  implicit val executionContext : ExecutionContext = ExecutionContext.global
  def registry = StorageContext
  def driver : StorageDriver

  def withStore[T](uri : URI, create : Boolean = false)(f : (Store) ⇒ T) : T = {
    driver.open(uri, create) match {
      case Some(store) ⇒ f(store)
      case None ⇒ toss(s"Store not found for $uri")
    }
  }

  def withSchema[T](uri : URI, schema : String, create : Boolean = false)(f : (Schema) ⇒ T) : T = {
    driver.open(uri, create) match {
      case Some(storage) ⇒ storage.withSchema(schema)(f)
      case None ⇒ toss(s"Store not found for $uri")
    }
  }

  def withCollection[T, S <: Storable](
    uri : URI,
    schema : String,
    collection : String)(f : (Collection[S]) ⇒ T) : T = {
    driver.withStore(uri) { store ⇒
      store.withCollection[T, S](schema, collection)(f)
    }
  }

  def checkExists(storageNames : Seq[String]) : Seq[String] = {
    storageNames.filter { name ⇒ driver.storeExists(name) }
  }

  def close() = Try {
    log.debug("Closing storage driver:" + driver.name)
    driver.close()
  } match {
    case Success(x) ⇒ log.debug("Closed StorageContext '" + id.name + "' successfully.")
    case Failure(x) ⇒ log.error("Failed to close StorageContext '" + id.name + "': ", x)
  }
}

object StorageContext extends Registry[StorageContext] with ScrupalComponent {
  val registrantsName : String = "storageContext"
  val registryName : String = "StorageContexts"

  def apply(id : Symbol, url : URI) : StorageContext = {
    StorageDriver.apply(url).makeContext(id)
  }

  private case class State(driver : StorageDriver, counter : AtomicInteger = new AtomicInteger(1))
  private var state : Option[State] = None

  def fromConfiguration(id : Symbol, conf : Option[Configuration] = None) : StorageContext = {
    val topConfig = conf.getOrElse(ConfigHelpers.default)
    val helper = new StorageConfigHelper(topConfig)
    val config = helper.getStorageConfig
    config.getConfig("db.scrupal") match {
      case Some(cfg) ⇒ fromSpecificConfig(id, cfg)
      case None ⇒ fromURI(id, "scrupal_mem://localhost/scrupal")
    }
  }

  def fromSpecificConfig(id : Symbol, config : Configuration) : StorageContext = {
    config.getString("uri") match {
      case Some(uri) ⇒ fromURI(id, uri)
      case None ⇒ toss("Missing 'uri' in database configuration for '" + id.name + "'")
    }
  }

  def fromURI(id : Symbol, uri : String) : StorageContext = {
    getRegistrant(id) match {
      case Some(context) ⇒
        context
      case None ⇒
        StorageDriver.apply(new URI(uri)).makeContext(id)
    }
  }

  /*
  def numberOfStartups : Int = {
    state match {
      case None    ⇒ 0
      case Some(s) ⇒ s.counter.get()
    }
  }

  def isStartedUp : Boolean = {
    state match {
      case None    ⇒ false
      case Some(s) ⇒ !s.driver.system.isTerminated
    }
  }

  def startup() : Unit = Try {
    state match {
      case Some(s) ⇒
        val startCount = s.counter.incrementAndGet()
        log.debug("The mongoDB driver initialized " + startCount + " times.")
      case None ⇒
        val full_config = ConfigFactory.load()
        val driver = MongoDriver(full_config)
        val s = State(driver)
        state = Some(State(driver))
    }
  } match {
    case Success(x) ⇒ log.debug("Successful mongoDB startup.")
    case Failure(x) ⇒ log.error("Failed to start up mongoDB", x)
  }

  def shutdown() : Unit = Try {
    state match {
      case Some(s) ⇒
        s.counter.decrementAndGet() match {
          case 0 ⇒
            for (ctxt ← values) {
              ctxt.close()
              ctxt.unregister()
            }
            Try { s.driver.close(10.seconds) } match {
              case Success(x) ⇒ log.debug("Successfully closed ReactiveMongo Driver")
              case Failure(x) ⇒ log.error("Failed to close ReactiveMongo Driver", x)
            }
            for (connection ← s.driver.connections) {
              log.debug("Connection remains open:" + connection)
            }
            state = None
          case x : Int ⇒
            log.debug("The DBContext requires " + x + " more shutdowns before MongoDB driver shut down.")
        }
      case None ⇒
        log.debug("The MongoDB Driver has never been started up.")
    }
  } match {
    case Success(x) ⇒ log.debug("Successful DBContext shutdown.")
    case Failure(x) ⇒ log.error("Failed to shut down DBContext", x)
  }
  */
}

