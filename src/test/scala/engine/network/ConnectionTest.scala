package ca.esri.capsim
package engine.network

import org.scalatest.funsuite.AnyFunSuite


class ConnectionTest extends AnyFunSuite:
  test("create") {
    val local:Connection = ConnectionTest.sampleConnectionForIntranet
    assert(local.isLocal)
    assert(local.bandwidth == 1000)
    assert(local.name == "Intranet to Intranet")

    val toDMZ:Connection = ConnectionTest.sampleConnectionToDMZ
    assert(!toDMZ.isLocal)
    assert(toDMZ.name == "Intranet to DMZ")

    val toInternet:Connection = ConnectionTest.sampleConnectionToInternet
    assert(!toInternet.isLocal)
    assert(toInternet.description == "Edge Network to Open Web")
  }

  test("invert") {
    val local:Connection = ConnectionTest.sampleConnectionForIntranet
    val invLocal = local.invert
    assert(invLocal == local) // source and dest swapped, but they're the same Zone
    assert(invLocal.name == local.name)
    assert(invLocal.bandwidth == local.bandwidth)
    assert(invLocal.latency == local.latency)

    val toDMZ = ConnectionTest.sampleConnectionToDMZ
    val fromDMZ = toDMZ.invert
    assert(toDMZ != fromDMZ)
    assert(fromDMZ.name == "DMZ to Intranet")
  }
end ConnectionTest


object ConnectionTest:
  val sampleConnectionForIntranet: Connection =
    Connection(ZoneTest.sampleIntranetZone, ZoneTest.sampleIntranetZone, 1000, 1)
  val sampleConnectionToDMZ: Connection =
    Connection(ZoneTest.sampleIntranetZone, ZoneTest.sampleEdgeZone, 1000, 1)
  val sampleConnectionToInternet: Connection =
    Connection(ZoneTest.sampleEdgeZone, ZoneTest.sampleInternetZone, 500, 10)
end ConnectionTest
