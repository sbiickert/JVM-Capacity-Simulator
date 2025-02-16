package ca.esri.capsim
package engine.network

import engine.Described

import ca.esri.capsim.engine.compute.ServiceProvider
import ca.esri.capsim.engine.queue.WaitMode.{TRANSMITTING, QUEUEING}
import ca.esri.capsim.engine.queue.{MultiQueue, QueueProvider, ServiceTimeCalculator}
import ca.esri.capsim.engine.work.ClientRequest

case class Connection(val sourceZone:Zone, val destinationZone:Zone,
                 val bandwidth:Int, val latency: Int)
  extends Described, ServiceTimeCalculator, QueueProvider:
  
  val name: String =
    sourceZone.name + " to " + destinationZone.name

  val description: String =
    sourceZone.description + " to " + destinationZone.description

  def isLocal: Boolean =
    sourceZone.name == destinationZone.name

  def invert: Connection =
    Connection(destinationZone, sourceZone, bandwidth, latency)

  override def calculateServiceTime(request: ClientRequest): Int = ???
  override def calculateLatency(request: ClientRequest): Int = ???

  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = TRANSMITTING, channelCount = 2)

end Connection

object Connection:
  
end Connection


