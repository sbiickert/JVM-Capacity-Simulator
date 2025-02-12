package ca.esri.capsim
package engine.network

import ca.esri.capsim.engine.network.RouteTest.sampleLoopingNetwork
import ca.esri.capsim.engine.network.ZoneType.{EDGE, SECURED}
import org.scalatest.funsuite.AnyFunSuite


class RouteTest extends AnyFunSuite:
  test("routing") {
    val network1 = RouteTest.sampleIntranet
    val intranet = ZoneTest.sampleIntranetZone
    val route1 = Route.findRoute(intranet, intranet, network1)
    assert(route1.nonEmpty)
    assert(route1.get == Route(List(intranet.connect(intranet, 1000, 0))))
    val network2 = RouteTest.sampleNetwork
    val dmz = ZoneTest.sampleEdgeZone
    val internet = ZoneTest.sampleInternetZone
    val route2 = Route.findRoute(intranet, internet, network2)
    assert(route2.nonEmpty)
    assert(route2.get == Route(List(
      intranet.selfConnect(),
      intranet.connect(dmz, 1000, 0),
      dmz.connect(internet, 100, 10)
    )))
    val network3 = RouteTest.sampleComplexNetwork
    val agol = ZoneTest.sampleAGOLZone
    val wan = intranet.copy(name = "WAN Site", "Second City Office")
    val route3 = Route.findRoute(wan, agol, network3)
    assert(route3.nonEmpty)
    assert(route3.get == Route(List(
      wan.selfConnect(),
      wan.connect(intranet, 300, 7),
      intranet.connect(dmz, 1000, 0),
      dmz.connect(internet, 100, 10),
      internet.connect(agol, 1000, 10)
    )))
    val route4 = Route.findRoute(agol, wan, network3)
    assert(route4.nonEmpty)
    assert(route4.get == Route(List(
      agol.selfConnect(),
      agol.connect(internet, 1000, 10),
      internet.connect(dmz, 100, 10),
      dmz.connect(intranet, 1000, 0),
      intranet.connect(wan, 300, 7)
    )))
  }

  test("looping network") {
    val network = sampleLoopingNetwork
    val zoneA = Zone("A", "Zone A", SECURED)
    val zoneB = Zone("B", "Zone B", SECURED)
    val zoneC = Zone("C", "Zone C", SECURED)

    val routeAB = Route.findRoute(zoneA, zoneB, network)
    assert(routeAB.nonEmpty)
    assert(routeAB.get == Route(List(
      zoneA.selfConnect(),
      zoneA.connect(zoneB, 100, 0)
    )))
    val routeAC = Route.findRoute(zoneA, zoneC, network)
    assert(routeAC.nonEmpty)
    assert(routeAC.get == Route(List(
      zoneA.selfConnect(),
      zoneA.connect(zoneC, 100, 0)
    )))
    val routeBC = Route.findRoute(zoneB, zoneC, network)
    assert(routeBC.nonEmpty)
    assert(routeBC.get == Route(List(
      zoneB.selfConnect(),
      zoneB.connect(zoneC, 100, 0)
    )))
  }
end RouteTest

object RouteTest:
  def sampleIntranet: List[Connection] =
    val intranet = ZoneTest.sampleIntranetZone
    val local = intranet.selfConnect()
    List(local)

  def sampleNetwork: List[Connection] =
    val intranet = ZoneTest.sampleIntranetZone
    var conns = List(intranet.connect(intranet, 1000, 0))
    val dmz = ZoneTest.sampleEdgeZone
    conns = dmz.selfConnect() +: conns
    conns = intranet.connect(dmz, 1000, 0) +: conns
    conns = dmz.connect(intranet, 1000, 0) +: conns
    val internet = ZoneTest.sampleInternetZone
    internet.selfConnect(1000, 10) +: conns
    conns = internet.connect(dmz, 500, 10) +: conns
    conns = dmz.connect(internet, 100, 10) +: conns
    conns

  def sampleComplexNetwork: List[Connection] =
    val intranet = ZoneTest.sampleIntranetZone
    var conns = List(intranet.selfConnect())

    val dmz = ZoneTest.sampleEdgeZone
    conns = dmz.selfConnect() +: conns
    conns = intranet.connect(dmz, 1000, 0) +: conns
    conns = dmz.connect(intranet, 1000, 0) +: conns

    val wan = intranet.copy(name = "WAN Site", "Second City Office")
    conns = wan.selfConnect() +: conns
    conns = intranet.connect(wan, 300, 7) +: conns
    conns = wan.connect(intranet, 300, 7) +: conns

    val internet = ZoneTest.sampleInternetZone
    conns = internet.selfConnect(latency =  10) +: conns
    conns = internet.connect(dmz, 500, 10) +: conns
    conns = dmz.connect(internet, 100, 10) +: conns
    conns

    val agol = ZoneTest.sampleAGOLZone
    conns = agol.selfConnect() +: conns
    conns = internet.connect(agol, 1000, 10) +: conns
    conns = agol.connect(internet, 1000, 10) +: conns

    conns

  def sampleLoopingNetwork: List[Connection] =
    val zoneA = Zone("A", "Zone A", SECURED)
    val zoneB = Zone("B", "Zone B", SECURED)
    val zoneC = Zone("C", "Zone C", SECURED)

    var conns = List(zoneA.selfConnect())
    conns = zoneB.selfConnect() +: conns
    conns = zoneC.selfConnect() +: conns
    conns = zoneA.connect(zoneB, 100, 0) +: conns
    conns = zoneA.connect(zoneC, 100, 0) +: conns
    conns = zoneB.connect(zoneA, 100, 0) +: conns
    conns = zoneB.connect(zoneC, 100, 0) +: conns
    conns = zoneC.connect(zoneA, 100, 0) +: conns
    conns = zoneC.connect(zoneB, 100, 0) +: conns

    conns
end RouteTest

