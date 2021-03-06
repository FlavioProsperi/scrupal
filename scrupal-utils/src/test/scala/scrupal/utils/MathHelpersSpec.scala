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

package scrupal.utils

import org.specs2.mutable.Specification

/** One line sentence description here.
  * Further description here.
  */
class MathHelpersSpec extends Specification {

  "MathHelpers" should {
    "compute log2 sanely" in {
      val x : Long = 0x18000000000L // 2^40 + 2^39
      val log2_x = MathHelpers.log2(x)
      val y : Int = 0x18000 // 2^16 + 2^15
      val log2_y = MathHelpers.log2(y)
      log2_x must beEqualTo(40)
      log2_y must beEqualTo(16)
    }
  }

}
