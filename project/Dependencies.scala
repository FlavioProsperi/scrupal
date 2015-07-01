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


import sbt.Keys._
import sbt._

/** Build Dependencies
  * This trait can be mixed in to get all of Scrupals repository resolvers and dependent libraries.
  */
trait Dependencies
{
  // val scrupal_org_releases    = "Scrupal.org Releases" at "http://scrupal.github.org/mvn/releases"
  val google_sedis            = "Google Sedis" at "http://pk11-scratch.googlecode.com/svn/trunk/"
  val jcenter_repo            = "JCenter" at "http://jcenter.bintray.com/"

//val scala_lang              = "Scala Language" at "http://mvnrepository.com/artifact/org.scala-lang/"
//val geolocation             = "geolocation repository" at "http://blabluble.github.com/modules/releases/"

  val all_resolvers : Seq[MavenRepository] = Seq (
    google_sedis, jcenter_repo
  )

  object Ver {
    val play = "2.4.1"
    val akka = "2.3.9"
    val spray = "1.3.2"
    val akka_http = "1.0-RC3"
    val kamon = "0.4.0"
  }

  // Things we borrow from Play Framework
  val play_anorm              = "com.typesafe.play"         %% "anorm"                    % Ver.play
  val play_cache              = "com.typesafe.play"         %% "play-cache"               % Ver.play
  val play_docs               = "com.typesafe.play"         %% "play-docs"                % Ver.play
  val play_filters            = "com.typesafe.play"         %% "filters-helpers"          % Ver.play
  val play_iteratees          = "com.typesafe.play"         %% "play-iteratees"           % Ver.play
  val play_jdbc               = "com.typesafe.play"         %% "play-jdbc"                % Ver.play
  val play_json               = "com.typesafe.play"         %% "play-json"                % Ver.play
  val play_ws                 = "com.typesafe.play"         %% "play-ws"                  % Ver.play

  // Play Plugins
  val mail_plugin             = "com.typesafe.play.plugins" %% "play-plugins-mailer"      % "3.0.1"
  val silhouette              = "com.mohiva"                %% "play-silhouette"          % "3.0.0-RC1"
//val play_plugins_redis      = "com.typesafe.play.plugins" %% "play-plugins-redis"       % "2.3.1"

  // Spray Stuff
  val spray_can               = "io.spray"                  %%  "spray-can"               % Ver.spray
  val spray_routing           = "io.spray"                  %%  "spray-routing"           % Ver.spray
  val spray_http              = "io.spray"                  %%  "spray-http"              % Ver.spray
  val spray_httpx             = "io.spray"                  %%  "spray-httpx"             % Ver.spray
  val spray_caching           = "io.spray"                  %%  "spray-caching"           % Ver.spray

  // Akka Stuff
  val akka_actor              = "com.typesafe.akka"         %% "akka-actor"               % Ver.akka
  val akka_slf4j              = "com.typesafe.akka"         %% "akka-slf4j"               % Ver.akka
  val akka_http               = "com.typesafe.akka"         %% "akka-http-experimental"   % "1.0-RC4"

  // Fundamental Libraries
  val shapeless               = "com.chuusai"               %% "shapeless"                % "2.2.1"
  val scala_arm               = "com.jsuereth"              %% "scala-arm"                % "1.4"

  // Database, Caches, Serialization, Data Storage stuff
  val rxmongo                 = "com.reactific"             %% "rxmongo"                  % "0.1.0-SNAPSHOT"
  val reactivemongo           = "org.reactivemongo"         %% "reactivemongo"            % "0.11.0-SNAPSHOT"
  val livestream_scredis      = "com.livestream"            %% "scredis"                  % "2.0.6"
  val akka_kryo_serialization = "com.github.romix.akka"     %% "akka-kryo-serialization"  % "0.3.3"
  val scala_pickling          = "org.scala-lang.modules"    %% "scala-pickling"           % "0.10.1"
  val chill                   = "com.twitter"               %% "chill"                    % "0.6.0"

  // UI Based Stuff
  val scalatags               = "com.lihaoyi"               %% "scalatags"                % "0.5.2"

  // WebJars We Use
  val wj_bootstrap            = "org.webjars"               % "bootstrap"                 % "3.3.5"
  val wj_marked               = "org.webjars"               % "marked"                    % "0.3.2-1"
  val wj_requirejs            = "org.webjars"               % "requirejs"                 % "2.1.18"
  val wj_requirejs_domready   = "org.webjars"               % "requirejs-domready"        % "2.0.1-2"
  val wj_font_awesome         = "org.webjars"               % "font-awesome"              % "4.3.0"

  // Hashing Algorithms
  val pbkdf2                  = "io.github.nremond"         %% "pbkdf2-scala"             % "0.4"
  val bcrypt                  = "org.mindrot"               % "jbcrypt"                   % "0.3m"
  val scrypt                  = "com.lambdaworks"           % "scrypt"                    % "1.4.0"

  // Kamon Monitoring
  // TODO: Utilize Kamon Monitoring
  val kamon_core              = "io.kamon"                  %% "kamon-core"                % Ver.kamon
  val kamon_scala             = "io.kamon"                  %% "kamon-scala"               % Ver.kamon
  val kamon_akka              = "io.kamon"                  %% "kamon-akka"                % Ver.kamon
  val kamon_log_reporter      = "io.kamon"                  %% "kamon-log-reporter"        % Ver.kamon
  val kamon_play              = "io.kamon"                  %% "kamon-play"                % Ver.kamon
  val kamon_system_metrics    = "io.kamon"                  %% "kamon-system_metrics"      % Ver.kamon
  val kamon_annotation        = "io.kamon"                  %% "kamon-annotation"          % Ver.kamon

  // Logging
  val grizzled_slf4j          = "org.clapper"               %% "grizzled-slf4j"           % "1.0.2"
  val logback_classic         = "ch.qos.logback"            %  "logback-classic"          % "1.1.3"

  // Miscellaneous
  val osgi_core               = "org.osgi"                  % "org.osgi.core"             % "6.0.0"
  val config                  =  "com.typesafe"             %  "config"                   % "1.2.1"
  val commons_lang3           = "org.apache.commons"        % "commons-lang3"             % "3.3.2"
  val hsp                     = "com.reactific"             %% "hotspot-profiler"         % "0.1.0-SNAPSHOT"
  // Test Libraries

//val icu4j                   = "com.ibm.icu"          % "icu4j"                  % "51.1"
//val geolocation             =  "com.edulify"        %% "geolocation"            % "1.1.0"

/*  val all_dependencies : Seq[ModuleID] = Seq(
    play_cache, play_filters, play_test, play_docs, play_ws,
    mailer_plugin,
    reactivemongo,
    pbkdf2, bcrypt, scrypt,
    osgi_core, slf4j,
    webjars_play,
    requirejs, requirejs_domready,
    angularjs, angular_drag_drop, angular_multi_select,
    angular_ui, angular_ui_bootstrap, angular_ui_router, angular_ui_utils, angular_ui_calendar,
    marked, fontawesome
  )
  */

  object Test {
    val akka_testkit     = "com.typesafe.akka"        %% "akka-testkit"             % Ver.akka        % "test"
    val specs2           = "org.specs2"               %% "specs2-core"              % "3.6.1"         % "test"
    val commons_io       = "commons-io"                %  "commons-io"              % "2.4"           % "test"
    val nu_validator     = "nu.validator.htmlparser"   % "htmlparser"               % "1.4"           % "test"
    val play_specs2      = "com.typesafe.play"        %% "play-specs2"              % Ver.play        % "test"
    val play_test        = "com.typesafe.play"        %% "play-test"                % Ver.play        % "test"
    val silhouette_test  = "com.mohiva"               %% "play-silhouette-testkit"  % "3.0.0-RC1"     % "test"
  }

  val root_dependencies : Seq[ModuleID] = Seq(
  )

  val common_dependencies : Seq[ModuleID] = Seq(
    scala_pickling, grizzled_slf4j, akka_slf4j, logback_classic, commons_lang3,
    Test.specs2, Test.play_test, Test.play_specs2
  )

  val utils_dependencies : Seq[ModuleID] = Seq(
    scalatags, pbkdf2, bcrypt, scrypt, config, shapeless
  ) ++ common_dependencies

  val api_dependencies : Seq[ModuleID] = Seq(
    play_json, shapeless, akka_http, akka_actor,
    Test.nu_validator
  ) ++ common_dependencies

  val storage_dependencies : Seq[ModuleID] = Seq(
    play_json, scala_pickling, chill
  ) ++ common_dependencies

  val store_reactivemongo_dependencies : Seq[ModuleID] = Seq(
    reactivemongo
  )

  val store_rxmongo_dependencies : Seq[ModuleID] = Seq(
    rxmongo
  )

  val types_dependencies : Seq[ModuleID] = Seq(
    play_json, shapeless
  ) ++ common_dependencies

  val db_dependencies : Seq[ModuleID] = Seq(
    reactivemongo, rxmongo, play_iteratees, play_json
  ) ++ common_dependencies

  val ui_dependencies : Seq[ModuleID] = Seq (
    scalatags, spray_http, spray_httpx, spray_caching, spray_routing, spray_can, wj_marked, wj_font_awesome,
    commons_lang3, scala_arm, livestream_scredis, akka_actor, play_iteratees,
    Test.akka_testkit, Test.commons_io, Test.nu_validator
  ) ++ common_dependencies

  val bootswatch_themes: Seq[ModuleID] = Seq(
    "org.webjars" % "bootswatch-amelia" % "3.2.0-1",
    "org.webjars" % "bootswatch-cerulean" % "3.3.1+2",
    "org.webjars" % "bootswatch-cosmo" % "3.3.1+2",
    "org.webjars" % "bootswatch-cupid" % "3.1.0+1",
    "org.webjars" % "bootswatch-cyborg" % "3.3.1+2",
    "org.webjars" % "bootswatch-darkly" % "3.3.1+2",
    "org.webjars" % "bootswatch-default" % "3.3.1+2",
    "org.webjars" % "bootswatch-flatly" % "3.3.1+2",
    "org.webjars" % "bootswatch-journal" % "3.3.1+2",
    "org.webjars" % "bootswatch-lumen" % "3.3.1+2",
    "org.webjars" % "bootswatch-paper" % "3.3.1+2",
    "org.webjars" % "bootswatch-readable" % "3.3.1+2",
    "org.webjars" % "bootswatch-sandstone" % "3.3.1+2",
    "org.webjars" % "bootswatch-simplex" % "3.3.1+2",
    "org.webjars" % "bootswatch-slate" % "3.3.1+2",
    "org.webjars" % "bootswatch-spacelab" % "3.3.1+2",
    "org.webjars" % "bootswatch-superhero" % "3.3.1+2",
    "org.webjars" % "bootswatch-united" % "3.3.1+2",
    "org.webjars" % "bootswatch-yeti" % "3.3.1+2"
  )

  val bootswatch_theme_names : Seq[String] = bootswatch_themes.map { mid ⇒ mid.name.replace("bootswatch-","") }

  val core_dependencies : Seq[ModuleID] = Seq(
    commons_lang3, scala_arm, scala_pickling, livestream_scredis, akka_actor, play_iteratees, akka_http, shapeless,
    scalatags, wj_marked, wj_font_awesome, wj_bootstrap,
    Test.akka_testkit, Test.commons_io
  ) ++ common_dependencies ++ bootswatch_themes

  val config_dependencies : Seq[ModuleID] = Seq()

  val admin_dependencies : Seq[ModuleID] = Seq()

  val doc_dependencies: Seq[ModuleID] = Seq()
}
