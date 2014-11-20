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

package scrupal.api

import org.specs2.mutable.Specification
import spray.http.ContentTypes

/** Test Suite for Disposition class */
class DispositionSpec extends Specification {

  "Successful" should {
    "use positive values for success" in {
      Successful.code must beGreaterThan(0)
    }
    "be successful" in {
      Successful.isSuccessful must beTrue
    }
  }

  "Indeterminate" should {
    "not be successful" in {
      Indeterminate.isSuccessful must beFalse
    }
    "not be failure" in {
      Indeterminate.isFailure must beFalse
    }
  }

  "Disposition" should {
    "convert to Result easily" in {
      val result = Unspecified(42,ContentTypes.NoContentType)
      result.disposition must beEqualTo(Unspecified)
      result.payload.isInstanceOf[Int] must beTrue
      result.payload must beEqualTo(42)
      result.contentType must beEqualTo(ContentTypes.NoContentType)
    }
  }
}
