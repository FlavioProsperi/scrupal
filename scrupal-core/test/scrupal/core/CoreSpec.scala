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

package scrupal.core

import org.specs2.mutable.Specification
import reactivemongo.bson.BSONDocument
import scrupal.core.api.{Entity, Instance, Module}
import scrupal.fakes.FakeModule
import scrupal.utils.Version

/** Top Level Test Suite for Core */
class CoreSpec extends Specification {


  class TestModule extends FakeModule('foo) {
    val thai = StringType('Thai, "Thai Foon", id, ".*".r)
    val buns = BundleType('Buns, "Buns Aye", id, Map('tie -> thai))
    val plun = Entity('Plun, "Plunderous Thundering Fun", buns, this)

    override val types = Seq(thai, buns)

    override val entities = Seq(plun)
  }

  "Module Type, Entity and Instance " should {
    "support CRUD" in {
      new FakeScrupal("CoreSpec") {
        withCoreSchema { schema : CoreSchema =>


          val foo = new TestModule

          foo.id must beEqualTo('foo)

          /*        val ty_id = schema.ypes.insert()
          val ty = Types.fetch(ty_id)
          val ty2 = ty.get
          ty2.id must beEqualTo('Thai)
          ty_id must beEqualTo(ty2.id)
          ty2.moduleId must beEqualTo( 'foo )

          val bun_id = Types.insert( new BundleType('Buns, "Buns Aye", m_id, HashMap('tie -> Type('Thai).get)))
          val bun = Types.fetch(bun_id)
          val bun2 = bun.get
          bun2.id must beEqualTo('Buns)
          bun_id must beEqualTo(bun2.id)
  */
          dbContext.get must beEqualTo(schema.dbc)

          val instance = Instance('Inst, "Instance", "Instigating Instance", foo.plun.id, BSONDocument())
          val wr = schema.instances.insertSync(instance)
          wr.hasErrors must beFalse
          val oi2 = schema.instances.fetchSync('Inst)
          oi2.isDefined must beTrue
          val i2 = oi2.get
          i2._id must beEqualTo('Inst)
          i2.name must beEqualTo("Instance")
          i2.entityId must beEqualTo('Plun)

          // FIXME: Do Update and Delete too!
        }
      }
    }
  }
}