package ca.esri.capsim
package engine.network

import engine.Described
import engine.queue.WaitMode.TRANSMITTING
import engine.queue.{MultiQueue, QueueProvider, ServiceTimeCalculator}
import engine.work.{ClientRequest, ClientRequestSolutionStep}

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

  override def calculateServiceTime(request: ClientRequest): Option[Int] =
    request.solution.currentStep match
      case step: Some[ClientRequestSolutionStep] => {}
        val dataKb = step.get.dataSize * 8
        val bwKbpms = bandwidth * 1000 / 1000 // Mbps -> kbps -> kb per millisecond (which is the time scale of the simulation)
        Some(dataKb / bwKbpms)
      case _ => None

  override def calculateLatency(request: ClientRequest): Option[Int] =
    request.solution.currentStep match
      case step: Some[ClientRequestSolutionStep] => Some(step.get.chatter * latency)
      case _ => None

  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = TRANSMITTING, channelCount = 2)

end Connection

object Connection:
  
end Connection


