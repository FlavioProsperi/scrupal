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

package scrupal.utils

import play.api.{Logger, Configuration}
import scala.util.Try
import java.io.{PrintWriter, File}
import com.typesafe.config.{ConfigRenderOptions, ConfigFactory, Config}

/**
 * Provide some extentions to the Play Configuration class via the pimp-my-library pattern
 * Further description here.
 */
class ConfigHelper(config : Configuration) {

  import ClassHelpers._

  /** Convert any class name into an instance of that class, assuming it has an empty args constructor
    *
    * @param name The class name
    * @param m A manifest for the class
    * @tparam C The kind of class expected, a base class
    * @return An instance of C that is of class `name` or None if it couldn't be instantiated.
    */
  def getInstance[C<:AnyRef](name: String)(implicit m: Manifest[C]) : Option[C] = {
    try
    {
      Option( string2instance[C](name))
    }
    catch {
      case x: IllegalAccessException =>  { Logger.error("Cannot access class " + name + " while instantiating: ", x); None }
      case x: InstantiationException =>  { Logger.error("Cannot instantiate uninstantiable class " + name + ": ", x); None }
      case x: ExceptionInInitializerError =>  { Logger.error("Instance initialization of " + name + " failed: ", x); None }
      case x: SecurityException => { Logger.error("Security exception while instantiating " + name + ": ", x);  None }
      case x: LinkageError => { Logger.error("Linkage error while instantiating " + name + ": ", x); None }
      case x: ClassNotFoundException =>  { Logger.error("Cannot find class " + name + " to instantiate: ", x); None }
      case x: Throwable  => { throw x }
    }
  }

  type DBConfig = Map[String,Option[Configuration]]
  val emptyDBConfig = Map.empty[String,Option[Configuration]]

  def forEachDB(f: (String, Configuration) => Boolean ) : DBConfig = {
    val db_config = getDbConfig
    val root_config = db_config.getConfig("db")
    root_config map { rootConfig: Configuration => internalForEach(rootConfig)(f) }
  }.getOrElse(emptyDBConfig)

  private def internalForEach(rootConfig: Configuration)( f: (String, Configuration) => Boolean ) : DBConfig = {
    for (dbName <- rootConfig.subKeys) yield {
      val dbConf = rootConfig.getConfig(dbName)
      val resolvedConf = dbConf.getOrElse(Configuration.empty)
      if (f(dbName, resolvedConf)) (dbName,  dbConf) else (dbName,  None)
    }
  }.toMap

  private def getDbConfigFile : Option[File] = {
    config.getString(ConfigHelper.scrupal_database_config_file_key) map { db_config_file_name: String =>
      new File(db_config_file_name)
    }
  }

  def getDbConfig : Configuration = Configuration (
    {
      getDbConfigFile map { db_config_file: File =>
        if (db_config_file.isFile) {
          ConfigFactory.parseFile(db_config_file)
        } else {
          ConfigFactory.empty
        }
      }
    }.getOrElse(ConfigFactory.empty)
  )

  def setDbConfig(new_config: Configuration, writeTo: Option[File] = None) : Configuration = {
    val result = {
      val data: String = new_config.underlying.root.render (ConfigRenderOptions.concise()) // whew!
      val trimmed_data = data.substring(1, data.length-1)
      writeTo.orElse(getDbConfigFile) map { db_config_file : File =>
        val writer = new PrintWriter(db_config_file)
        try  { writer.println(trimmed_data) } finally { writer.close }
        new_config
      }
    }.getOrElse(Configuration.empty)
    Logger.debug("DB Config set to " + result)
    result
  }

  def addDbConfig(db_config: Configuration) : Configuration = {
    setDbConfig( getDbConfig ++ db_config )
  }

  def setDbConfig(new_config: Map[String,Any]) : Configuration = {
    import collection.JavaConversions._
    val cfg = Configuration(ConfigFactory.parseMap(new_config))
    setDbConfig(cfg)
  }

  def validateDBConfiguration : Try[Map[String,Option[Configuration]]] = {
    Try {
      val cfg = getDbConfig
      if (cfg.keys.size == 0)
        throw new Exception("The database configuration is completely empty.")
      val db_cfg = cfg.getConfig("db");
      {
        db_cfg map { the_config: Configuration =>
          if (the_config.getConfig("default").isDefined)
            throw new Throwable("The initial, default database configuration was detected.")
          internalForEach(db_cfg.get) { (db: String, db_config: Configuration ) =>
            val keys: Set[String] = db_config.subKeys
            // Whatever keys are there they must all be strings so validate that (getString will throw if its not a string)
            // and make sure they didn't provide a key with an empty value, also
            for ( key <- keys ) yield if (db_config.getString(key).getOrElse {
              throw new Exception("Configuration for '" + db + "' is missing a value for '" + key + "'.")
            }.isEmpty) { throw new Exception("Configuration for '" + db + "' has an empty value for '" + key + "'.") }
            // The config needs to at least have a url key
            if (!keys.contains("url")) {
              throw new Exception("Configuration for '" + db + "' must specify a value for 'url' key, at least.")
            } else if (db_config.getString("url").get.equals("jdbc:h2:mem:")) {
              throw new Exception("Configuration for '" + db + "' must not use a private memory-only database")
            }
            // Okay, looks good, include this in the results
            true
          }
        }
      }.getOrElse( { throw new Exception("The database configuration does not contain a top level 'db' key.")} )
    }
  }
}

object ConfigHelper
{
  implicit def helpYoConfig(config: Configuration) = new ConfigHelper(config)
  def apply(config: Configuration) = helpYoConfig(config)

  // The configuration key that says where to get the database configuration data.
  val scrupal_database_config_file_key = "scrupal.database.config.file"

}
