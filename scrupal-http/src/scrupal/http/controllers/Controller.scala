/**********************************************************************************************************************
 * Copyright © 2014 Reactific Software, Inc.                                                                          *
 *                                                                                                                    *
 * This file is part of Scrupal, an Opinionated Web Application Framework.                                            *
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

package scrupal.http.controllers

import scrupal.api._
import scrupal.http.ScrupalMarshallers
import scrupal.utils.{Registrable, Registry}
import shapeless.{::,HNil}
import spray.http.StatusCodes.NotFound
import spray.http.{HttpResponse, HttpRequest}
import spray.routing._
import spray.routing.directives.LogEntry
import spray.http._
import StatusCodes._

import akka.event.Logging._

/** Abstract Controller
  *
  * A controller in scrupal is a route provider and action generator. It provides a route object which
  * should separate valid from invalid routes and generate Actions for the valid ones.
  *
  * Created by reidspencer on 10/29/14.
  */
trait Controller extends Registrable[Controller]
                         with Directives with ScrupalMarshallers with RequestLoggers {

  /** The priority of this controller for routing
    * This affects the controller's placement in the list of controllers that form the route processing.
    * Lower numbers increase the priority of the controller and move it towards the start of the list.
    * Scrupal's own controllers work within a -100 to 100 range and it is recommended that other
    * controllers operate within that same range. Note that this just helps optimize the processing
    * of routes.
    */
  def priority: Int

  /** The routes that this controller provides
    * Note that this does not include checking of the context path. That will have already done before the
    * routes here are invoked. So, this should only check within that 'context"
    */
  def routes(implicit scrupal: Scrupal): Route

  /** Required method for registration */
  def registry: Registry[Controller] = Controller
  def asT: Controller = this

  def spaces2underscores(what: String) = what.replaceAll(" ","_")

  def modules = Module.values
  def moduleNames : Seq[String]  = Module.values map { module: Module => module.label }
  def moduleTypeNames(mod:Module)  : Seq[String] = mod.types map { typ => typ.label }

  def types       : Seq[Type]    = Module.values flatMap { module => module.types }
  def typeNames   : Seq[String]  = types map { typ : Type => typ.label }

  def request_context = extract( rc ⇒ rc )

  def context(scrupal: Scrupal): Directive1[Context] = {
    hostName.flatMap { host: String ⇒
      extract { ctxt: RequestContext ⇒
        val sites = Site.forHost(host)
        if (sites.isEmpty)
          Context(scrupal, ctxt)
        else {
          val site = sites.head
          if (site.isEnabled(scrupal)) {
            if ((ctxt.request.uri.scheme == "https") == site.requireHttps)
              scrupal.authenticate(ctxt) match {
                case Some(principal: Principal) ⇒ Context(scrupal, ctxt, site, principal)
                case None ⇒ Context(scrupal, ctxt, site)
              }
            else
              Context(scrupal, ctxt)
          } else {
            Context(scrupal, ctxt)
          }
        }
      }
    }
  }
}

abstract class BasicController(val id : Identifier, val priority: Int = 0) extends Controller

/** A Controller For ActionProviders
  *
  * Scrupal API module provides the ActionProvider that can convert a path
  */
abstract class ActionProviderController extends Controller {

  def provider : ActionProvider

  def providerToAction(unmatchedPath: Uri.Path, context: Context) : Option[Action] = {
    provider.matchingAction(unmatchedPath, context)
  }

  def action(key: String, unmatchedPath: Uri.Path, context: Context) : Directive1[Action] = {
    new Directive1[Action] {
      def happly(f: ::[Action,HNil] ⇒ Route) : Route = {
        providerToAction(unmatchedPath, context) match {
          case Some(action) ⇒ f(action :: HNil)
          case None ⇒ reject
        }
      }
    }
  }

  def routes(implicit scrupal: Scrupal) : Route = {
    context(scrupal) { ctxt : Context ⇒
      if (provider.key.nonEmpty) {
        pathPrefix(provider.singularKey ~ Slash) {
          action(provider.singularKey, ctxt.request.get.unmatchedPath, ctxt) {
            a: Action ⇒ complete {
              makeMarshallable {
                a.dispatch
              }
            }
          }
        } ~
        pathPrefix(provider.pluralKey ~ Slash) {
          action(provider.pluralKey, ctxt.request.get.unmatchedPath, ctxt) {
            a: Action ⇒ complete {
              makeMarshallable {
                a.dispatch
              }
            }
          }
        }
      } else {
        action("", ctxt.request.get.unmatchedPath, ctxt) {
          a: Action ⇒ complete {
            makeMarshallable {
              a.dispatch
            }
          }
        }
      }
    }
  }
}

/** Controller That Requires A Site.
  *
  * This is the controller superclass
  */
case class SiteController(
  id: Symbol,
  provider: Site,
  priority: Int = 0
) extends ActionProviderController {
}

case class ApplicationController(
  id: Symbol,
  provider: Application,
  priority: Int = 0
) extends ActionProviderController {
}

trait RequestLoggers {
  def showRequest(request: HttpRequest) = LogEntry(request.uri, InfoLevel)

  def showAllResponses(request: HttpRequest) : Any ⇒ Option[LogEntry] = {
    case x: HttpResponse => {
      println (s"Normal: $request")
      createLogEntry(request,   x.status + " " + x.toString())
    }
    case Rejected(rejections) => {
      println (s"Rejection: $request")
      createLogEntry(request,   " Rejection " + rejections.toString())
    }
    case x => {
      println (s"other: $request")
      createLogEntry(request,   x.toString())
    }
  }

  def createLogEntry(request: HttpRequest, text: String): Some[LogEntry] = {
    Some(LogEntry("#### Request " + request + " => " + text, DebugLevel))
  }

  def showErrorResponses(request: HttpRequest): Any ⇒ Option[LogEntry] = {
    case HttpResponse(OK | NotModified | PartialContent, _, _, _) ⇒ None
    case HttpResponse(NotFound, _, _, _)                          ⇒ Some(LogEntry("404: " + request.uri, WarningLevel))
    case r @ HttpResponse(Found | MovedPermanently, _, _, _) ⇒
      Some(LogEntry(s"${r.status.intValue}: ${request.uri} -> ${r.header[HttpHeaders.Location].map(_.uri.toString).getOrElse("")}", WarningLevel))
    case response ⇒ Some(
      LogEntry("Non-200 response for\n  Request : " + request + "\n  Response: " + response, WarningLevel))
  }

  def showRepoResponses(repo: String)(request: HttpRequest): HttpResponsePart ⇒ Option[LogEntry] = {
    case HttpResponse(s @ (OK | NotModified), _, _, _) ⇒ Some(LogEntry(s"$repo  ${s.intValue}: ${request.uri}", InfoLevel))
    case ChunkedResponseStart(HttpResponse(OK, _, _, _)) ⇒ Some(LogEntry(repo + " 200 (chunked): " + request.uri, InfoLevel))
    case HttpResponse(NotFound, _, _, _) ⇒ Some(LogEntry(repo + " 404: " + request.uri))
    case _ ⇒ None
  }
}


object Controller extends Registry[Controller] {
  override val registryName: String = "Controllers"
  override val registrantsName: String = "Controller"

}
