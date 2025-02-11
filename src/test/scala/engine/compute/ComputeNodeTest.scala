package ca.esri.capsim
package engine.compute

import engine.compute.ThreadingModel.PHYSICAL

import ca.esri.capsim.engine.network.ZoneTest
import org.scalatest.funsuite.AnyFunSuite

class ComputeNodeTest extends AnyFunSuite:
  test("create") {
    val client = ComputeNodeTest.sampleClient
    assert(client.zone == ZoneTest.sampleIntranetZone)
    val host = ComputeNodeTest.sampleHost
    assert(host.hardwareDef.cores == 12)
    assert(host.virtualHosts.length == 1)
    assert(host.virtualHosts.head.vCPUs == 4)
  }


object ComputeNodeTest:
  val sampleClient: Client =
    Client("Client 001", "Sample PC", HardwareDefTest.sampleClientHWDef, ZoneTest.sampleIntranetZone)

  val sampleVHost: VirtualHost =
    VirtualHost("VHost 001", "Sample Virtual Machine", HardwareDefTest.sampleServerHWDef, ZoneTest.sampleIntranetZone,
      vCPUs = 4, memoryGB = 16, threadingModel = PHYSICAL)

  val sampleHost: PhysicalHost =
    PhysicalHost("Host 001", "Sample Host", HardwareDefTest.sampleServerHWDef, ZoneTest.sampleIntranetZone,
      List(sampleVHost))