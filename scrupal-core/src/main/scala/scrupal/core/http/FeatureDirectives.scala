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

import scrupal.core.api.Feature
import scrupal.utils.Enablement
import spray.routing.Directives._
import spray.routing._

/** Spray Directives For Scrupal Features
  *
  */
trait FeatureDirectives {

  def feature(theFeature : Feature, scope : Enablement[_]) : Directive0 = {
    if (theFeature.implemented) {
      if (theFeature.isEnabled(scope)) {
        pass
      } else {
        reject(ValidationRejection(s"Feature '${theFeature.name}' of module '${theFeature.moduleOf}' is not enabled."))
      }
    } else {
      reject(ValidationRejection(s"Feature '${theFeature.name}' of module '${theFeature.moduleOf}' is not implemented."))
    }
  }

}
