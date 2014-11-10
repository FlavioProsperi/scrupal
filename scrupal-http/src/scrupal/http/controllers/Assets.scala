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

package scrupal.http.controllers

import java.io.File
import java.net.URL

import scrupal.core.Scrupal
import spray.routing.Route

/**
 * Asset controller for core assets. This one gets used by the templates
 */
object Assets extends BasicController('Assets, "assets",-1)
{
  def routes(scrupal: Scrupal) : Route = complete("Assets Not Implemented")

  def favicon() = "/assets/favicon.ico"
  def theme(provider: String, name: String) = "/assets/themes/default.css"
  def css(name: String) = s"/assets/css/$name"
  def css_s(name: String) = s"/assets/css/$name"

  /* FIXME: Implement Assets controller
  // Save the Play AssetBuilder object under a new name so we can refer to it without referring to ourself!
  val assetBuilder = controllers.Assets
  def fallback(path : String, file : String) : Action[AnyContent] = {
    assetBuilder.at(path, file)
  }

  val root = "/public"
  val javascripts = root + "/javascripts"
  val javascripts_min = root + "/javascripts_min"
  val stylesheets = root + "/stylesheets"
  val images = root + "/images"
  val themes = root + "/themes"
  val docs = root + "/docs"
  val chunks = root + "/chunks"
  val templates = root + "/templates"

  /** Resolve a path/file combination from either WebJars or the static/compiled resources Scrupal provides
    * Attempts to resolve the path/file combination using WebJars but if the file could not be located there then it
    * falls back to using Play's AssetBuilder to locate the resource inherent to Scrupal.
    * @param path The static path at which the resource might be located (if not found b WebJars)
    * @param file The basic file name with path and basename but without suffixes or versions
    * @return The Enumeratee of the resource
    */
  def resolve(path: String, file: String) : Action[AnyContent] = {
    try {
      resolve(file)
    }
    catch {
      case x: IllegalArgumentException => fallback(path, file)
    }
  }

  def resolve(file:String) : Action[AnyContent] = {
    val expanded_file_path = super.locate(file)
    super.at(expanded_file_path)
  }

  def misc(file: String) = resolve(root, file)

  /** Get a Javascript from a Jar file
    * Uses WebJarAssets to locate and return the Jar file corresponding to the argument which must end with .js. T
    * @param file
    * @return
    */
  def js(file: String, min : Boolean = true) = resolve("", minify(file, ".js", min))

  def requirejs() = resolve(javascripts, minify("require.js", ".js", true))

  /** Get a Javascript from assets/javascripts (static or compiled)
    * Just uses the Play AssetBuilder to extract the javascript file.
    * @param file The name of the script with partial path after "javascripts" and no version or suffix.
    * @param min Whether or not to minify the resulting file name (always off for Dev mode)
    * @return The Content of the file as an Action
    */
  def js_s(file: String, min : Boolean = true) = {
    if (file.endsWith(".js"))
      resolve(javascripts, minify(file, ".js", min))
    else
      fallback(javascripts,file)
  }
  def js_s_min(file: String) = js_s(file, true)

  /** Get a Stylesheet from a Jar file
    * Uses WebJarAssets to locate and return the `file` from within a ClassLoaded Jar file.
    *
    * @param file The name of the file without path prefix, version nor suffix, just the basename
    * @param min Whether or not to minify the resulting file name (always off for Dev mode)
    * @return The Content of the file as an Action
    */
  def css(file: String, min : Boolean = true) = resolve(stylesheets, minify(file, ".css", min))

  /** Get a Stylesheet from public/stylesheets (static or compiled)
    *
    * @param file The partial path with no suffix
    * @return The Content of the file as an Action
    */
  def css_s(file: String) = fallback(stylesheets, minify(file, ".css", min=false))

  /** Get a PNG (Portable Network Graphic) file with extension .png from the static assets
    *
    * @param file name of the file to fetch with any partial path (after /public/images) and without the suffix
    * @return
    */
  def img(file: String) = fallback(images, file)

  /** Get the correct favicon for the context
    * TODO: Defaulted for now to a static result :(
    * @return The /public/images/favicon.png file
    */
  def favicon = fallback(images, "viritude.ico")

	/**
	 * A way to obtain a theme css file just by the name of the theme
   * @param provider The web source for the theme
	 * @param name Name of the theme
	 * @return path to the theme's .css file
	 */
	def theme(provider: String, name: String, min: Boolean = true) : Action[AnyContent] =  {
    (Global.ScrupalIsConfigured && !CoreFeatures.DevMode) match {
      case true => {
        provider.toLowerCase() match {
          case "scrupal"    => {
            // TODO: Look it up in the database first and if that does not work forward on to static resolution
            fallback(themes, minify(name,".css", true))
          }
          case "bootswatch" =>  Action { request:RequestHeader =>
            // TODO: Deal with "NotModified" better here?
            MovedPermanently("http://bootswatch.com/" + name + "/" + minify("bootstrap", ".css", min))
          }
          case _ =>  fallback(stylesheets, "boostrap.min.css")
        }
      }
      case false => fallback(themes, "bootswatch/cyborg.min.css")
    }
  }

  /** Serve markdown fragments that provide the documentation
    *
    */
  def doc(path: String) = fallback(docs, path)

  def isValidDocAsset(path: String) : Boolean = {
    resourceNameAt(docs, path).exists { resourceName: String => Play.resource(resourceName).exists { u: URL => true } }
  }

  private def resourceNameAt(path: String, file: String): Option[String] = {
    val decodedFile = UriEncoding.decodePath(file, "utf-8")
    val resourceName = Option(path + "/" + decodedFile).map(name => if (name.startsWith("/")) name else ("/" + name)).get
    if (new File(resourceName).isDirectory || !new File(resourceName).getCanonicalPath.startsWith(new File(path).getCanonicalPath)) {
      None
    } else {
      Some(resourceName)
    }
  }


  /** Serve AngularJS Partial Chunks of HTML
   * An accessor for getting Angular's partial/fragment/chunks of HTML for composed views. We store the HTML files in
   * a directory named "chunks" underneath the javascript module's directory. But, we ask for it with a path like
   * /chunks/module/file.html and the router routes such requests HERE.
   * @param module The name of the Angular module
   * @param file The name of the file being requested
   * @return The content of the file
   */
  def chunk(module: String, file: String) = fallback(chunks + "/" + module, file)

  /** ng-ui-bootstrap requires template files to satisfy some of its directives.
    *
    * @param path Path to the template
    * @return
    */
  def template(path: String) = resolve(root, "/template/" + path)

  /** The pattern for extracting the suffix from a file name */
  private lazy val suffix_r = "(\\.[^.]*)$".r

  private def minify(file: String, suffix: String, min: Boolean ) = {
    (min && Play.mode != Mode.Dev, file.endsWith(suffix)) match {
      case (false, false) => file + suffix
      case (false, true) => file
      case (true, false) => file + ".min" + suffix
      case (true, true) => suffix_r.replaceFirstIn(file, ".min$1")
    }
  }
  */
}
