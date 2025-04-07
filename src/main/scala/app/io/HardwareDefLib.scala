package ca.esri.capsim
package app.io

import engine.compute.HardwareDef

import java.time.Instant

case class HardwareDefLib(date: Instant, hardware: Map[String, HardwareDef]):

end HardwareDefLib

object HardwareDefLib:
  def empty: HardwareDefLib =
    HardwareDefLib(date = Instant.now, hardware = Map.empty)
end HardwareDefLib

