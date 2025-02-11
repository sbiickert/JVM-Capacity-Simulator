package ca.esri.capsim
package engine.network

import ca.esri.capsim.engine.network.ZoneTest.sampleInternetZone
import ca.esri.capsim.engine.network.ZoneType.{EDGE, INTERNET, SECURED}
import org.scalatest.funsuite.AnyFunSuite

class ZoneTest extends AnyFunSuite:
  test("create") {
    val internet = sampleInternetZone
    assert(internet.zoneType == INTERNET)
  }

object ZoneTest:
  val sampleInternetZone: Zone =
    Zone("Internet", "Open Web", INTERNET)

  val sampleEdgeZone: Zone =
    Zone("DMZ", "Edge Network", EDGE)

  val sampleIntranetZone: Zone =
    Zone("Intranet", "Secure Network", SECURED)