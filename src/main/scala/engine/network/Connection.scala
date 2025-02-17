package ca.esri.capsim
package engine.network

import engine.Described
import engine.queue.WaitMode.TRANSMITTING
import engine.queue.{MultiQueue, QueueProvider, ServiceTimeCalculator}
import engine.work.ClientRequest

case class Connection(sourceZone:Zone, destinationZone:Zone,
                      bandwidth:Int, latency: Int)
  extends Described, ServiceTimeCalculator, QueueProvider:
  
  val name: String =
    sourceZone.name + " to " + destinationZone.name

  val description: String =
    sourceZone.description + " to " + destinationZone.description

  def isLocal: Boolean =
    sourceZone.name == destinationZone.name

  def invert: Connection =
    Connection(destinationZone, sourceZone, bandwidth, latency)

  override def calculateServiceTime(request: ClientRequest): Int =
    val dataKb = request.solution.currentStep.dataSize * 8
    val bwKbpms = bandwidth * 1000 / 1000 // Mbps -> kbps -> kb per millisecond (which is the time scale of the simulation)
    dataKb / bwKbpms

  override def calculateLatency(request: ClientRequest): Int =
    request.solution.currentStep.chatter * latency

  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = TRANSMITTING, channelCount = 2)

end Connection

object Connection:
  
end Connection


