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

package scrupal.core

import play.api.Configuration
import scrupal.core.impl.Scrupal
import scrupal.utils.ConfigHelpers

object Boot extends Scrupal with App {

  override val (config, sc) = open()

  val http = scrupal.core.http.Boot(this, config)

  http.run()

  override def open() = {
    // Make sure that we registered the CoreModule as 'Core just to make sure it is instantiated at this point
    require(CoreModule.id == 'Core)
    super.open()
  }

  override def onLoadConfig(config : Configuration) : Configuration = {
    val new_config = super.onLoadConfig(config)
    CoreModule.bootstrap(new_config)
    new_config
  }

}
