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

package scrupal.models

import scala.slick.lifted.DDL
import scrupal.models.db._

/**
 * The basic schema for Scrupal. This is composed by merging together the various Components.
 */
class ScrupalSchema(sketch: Sketch) extends Schema (sketch)
  with CoreComponent with UserComponent  /* with AlertComponent */
{
  // Super class Schema requires us to provide the DDL from our tables
  override val ddl : DDL = {
    // CoreComponent tables
    Modules.ddl ++ Alerts.ddl ++
    // UserComponent tables
    Principals.ddl ++ Handles.ddl
    // Admin tables
    /* ++ Alerts.ddl */
  }

}
