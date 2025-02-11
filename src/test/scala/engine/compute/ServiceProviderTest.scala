package ca.esri.capsim
package engine.compute

import ca.esri.capsim.engine.compute.BalancingModel.ROUNDROBIN
import org.scalatest.funsuite.AnyFunSuite

class ServiceProviderTest extends AnyFunSuite:
  test("create") {
    val simpleMap:ServiceProvider = ServiceProviderTest.sampleMapServiceProvider
    assert(simpleMap.name == "GIS Site")
    val haMap = ServiceProviderTest.sampleHAMapServiceProvider
    assert(haMap.nodes.size == 2)
    assert(haMap.service.balancingModel == ROUNDROBIN)
  }

object ServiceProviderTest:
  private val vm = ComputeNodeTest.sampleVHost

  val sampleWebServiceProvider: ServiceProvider =
    val webVM = vm.copy(name = "Web 001")
    ServiceProvider("IIS", "Web Server", ServiceTest.sampleService("web"), None, Set(webVM))

  val samplePortalServiceProvider: ServiceProvider =
    val portalVM = vm.copy(name = "Portal 001")
    ServiceProvider("Portal", "Portal", ServiceTest.sampleService("portal"), Some(portalVM), Set(portalVM))

  val sampleMapServiceProvider: ServiceProvider =
    val gisVM = vm.copy(name = "GIS 001")
    ServiceProvider("GIS Site", "Map Server Site", ServiceTest.sampleService("map"), None, Set(gisVM))

  val sampleHAMapServiceProvider: ServiceProvider =
    val gis1 = vm.copy(name = "GIS 001")
    val gis2 = vm.copy(name = "GIS 002")
    ServiceProvider("GIS Site", "Map Server HA Site", ServiceTest.sampleService("map"), None, Set(gis1,gis2))

  val sampleDBMSServiceProvider: ServiceProvider =
    val dbVM = vm.copy(name = "SQL 001")
    ServiceProvider("SQL Server", "Geodatabase", ServiceTest.sampleService("dbms"), Some(dbVM), Set(dbVM))

  val sampleHADataStoreServiceProvider: ServiceProvider =
    val ds1 = vm.copy(name = "DS 001")
    val ds2 = vm.copy(name = "DS 002")
    ServiceProvider("Relational DS", "HA DataStore", ServiceTest.sampleService("relational"), None, Set(ds1, ds2))