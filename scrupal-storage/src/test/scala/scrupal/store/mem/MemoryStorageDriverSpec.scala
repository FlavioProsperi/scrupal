package scrupal.store.mem

import scrupal.storage.api._


/** Title Of Thing.
  *
  * Description of thing
  */
class MemoryStorageDriverSpec extends StorageTestSuite("MemoryStorageDriver") {
  def driver: StorageDriver = MemoryStorageDriver

  def driverName : String = "Memory"

  def scheme: String = "scrupal-mem"

  def configFile: String = "scrupal-storage/src/test/resources/storage/config/mem_testing.conf"
}
