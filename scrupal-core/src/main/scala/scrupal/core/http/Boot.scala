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

package scrupal.core.http

import java.util.concurrent.TimeUnit

import akka.actor.{ ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import play.api.Configuration
import scrupal.api.{ Scrupal, Site }
import scrupal.utils.{ ScrupalComponent, DateTimeHelpers }

import scala.compat.Platform
import scala.concurrent.duration._

/** Boot Main
  * This is the main entry point to Scrupal as it contains the "Main" function provided by the App Scrupal library class.
  * We don't override that class but instead just start whatever is necessary in the constructor of this object.
  * Since we are Spray based that only consists of creating the actor system, the top level Actor, and binding that
  * actor to the correct HTTP interface and port.
  */
case class Boot(scrupal : Scrupal, config : Configuration) extends ScrupalComponent {
  val executionStart = Platform.currentTime

  def run() = {
    // Check to make sure everything is ready to run.
    checkReady()

    // we need an ActorSystem to host our application in
    implicit val system = ActorSystem("Scrupal-Http")

    implicit val timeout = Timeout(config.getMilliseconds("scrupal.timeout").getOrElse(8000), TimeUnit.MILLISECONDS)

    // create and start our service actor
    val service = system.actorOf(Props(classOf[ScrupalServiceActor], scrupal, timeout), ScrupalServiceActor.name)

    val interface = config.getString("scrupal.http.interface").getOrElse("0.0.0.0")
    val port = config.getInt("scrupal.http.port").getOrElse(8888)

    log.info(s"Scrupal HTTP starting up. Interface=$interface, Port=$port, Timeout=${timeout.duration.toMillis}ms")

    // start a new HTTP server on port 8080 with our service actor as the handler
    // FIXME: Need to start the Play Server here, not spray
    // IO(Http) ? Http.bind(service, interface, port)

    scrupal.onStart()
  }

  def runDuration = {
    val run_time = Platform.currentTime - executionStart
    val duration = Duration(run_time, TimeUnit.MILLISECONDS)
    DateTimeHelpers.makeDurationReadable(duration)
  }

  def checkReady() = {
    for (site ← Site.values if site.isEnabled(scrupal)) {
      val app_names = for (app ← site.applications if app.isEnabled(site)) yield {
        for (mod ← app.modules if mod.isEnabled(app)) yield {
          val paths = for (ent ← mod.entities if ent.isEnabled(mod)) yield { ent.singularKey }
          val distinct_paths = paths.distinct
          if (paths.size != distinct_paths.size) {
            toss(
              s"Cowardly refusing to start with duplicate entity paths in module ${mod.label} in application ${
                mod
                  .label
              } in site ${site.label}")
          }
        }
        app.label
      }
      val distinct_app_names = app_names.distinct
      if (app_names.size != distinct_app_names.size) {
        toss(s"Cowardly refusing to start with duplicate application names in site ${site.label}")
      }
    }
  }
}
