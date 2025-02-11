package ca.esri.capsim
package engine.compute

import ca.esri.capsim.engine.compute.ComputeArchitecture.*
import org.scalatest.funsuite.AnyFunSuite

class HardwareDefTest extends AnyFunSuite:
  test("create") {
    val phone = HardwareDefTest.sampleMobileHWDef
    assert(phone.cores == 8)
    assert(phone.architecture == ARM64)
    assert(phone.specIntRate2017 == 500)
    val client = HardwareDefTest.sampleClientHWDef
    assert(client.cores == 4)
    assert(client.architecture == INTEL)
    assert(client.specIntRate2017 == 20)
    val server = HardwareDefTest.sampleServerHWDef
    assert(server.cores == 12)
    assert(server.architecture == INTEL)
    assert(server.specIntRate2017 == 67)
  }

object HardwareDefTest:
  val sampleMobileHWDef: HardwareDef =
    HardwareDef("Mobile 001", "sample phone", "Apple Silicon M1", 8, 500, ARM64)

  val sampleClientHWDef: HardwareDef =
    HardwareDef("Client 001", "sample client", "Intel Core i7-4770K", 4, 20, INTEL)

  val sampleServerHWDef: HardwareDef =
    HardwareDef("Server 001", "sample server", "Xeon E5-2643v3", 12, 67, INTEL)