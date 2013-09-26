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


/**
 * Some handy Json Reads[T] and Writes[T] for various things not otherswise provided.
import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import play.api.templates.Html

object MissingJSONReadsWrites
{

  implicit val BSONObjectIDReads : Reads[BSONObjectID] =
    ( __ \ '_id).read[String].map { v => BSONObjectID(v) }


  implicit val BSONObjectIDWrites : Writes[BSONObjectID] =
    ( __ \ '_id).write[String].contramap{ (a: BSONObjectID) => a.stringify }

  implicit val BSONObjectIDFormats = Format(BSONObjectIDReads, BSONObjectIDWrites)

  implicit val htmlReader : Reads[Html] = new Reads[Html] {
    def reads(jsValue : JsValue) : JsResult[Html] = {
      (jsValue \ "html" ).validate[String].map { h => Html(h) }
    }
  }

  implicit val htmlWriter : Writes[Html] = new Writes[Html] {
    def writes(h : Html) : JsValue = JsString(h.toString)
  }

  implicit val htmlFormat : Format[Html] = Format(htmlReader, htmlWriter)

}

 */

