package ca.esri.capsim
package engine.compute

import engine.queue.{MultiQueue, QueueProvider, ServiceTimeCalculator}

import ca.esri.capsim.engine.Described
import ca.esri.capsim.engine.network.Zone
import ca.esri.capsim.engine.queue.WaitMode.WAITING
import ca.esri.capsim.engine.work.ClientRequest

sealed trait ComputeNode:
  val hardwareDef: HardwareDef



case class Client(val name: String, val description: String,
                  val hardwareDef: HardwareDef,
                  val zone: Zone)
  extends ComputeNode, Described, QueueProvider, ServiceTimeCalculator:

  override def calculateServiceTime(request: ClientRequest): Int = ???
  override def calculateLatency(request: ClientRequest): Int = ???

  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = WAITING, channelCount = hardwareDef.cores)
end Client



case class PhysicalHost(val name: String, val description: String,
                        val hardwareDef: HardwareDef,
                        val zone: Zone, val virtualHosts: List[VirtualHost])
  extends ComputeNode, Described, QueueProvider, ServiceTimeCalculator:

  def addVHost(vCPUs: Int, memoryGB: Int, threadingModel: ThreadingModel): PhysicalHost =
    val vHost = VirtualHost(name = (s"VH $name:" + virtualHosts.length), description = "",
      hardwareDef = hardwareDef, zone = zone,
      vCPUs = vCPUs, memoryGB = memoryGB, threadingModel = threadingModel)
    val list = vHost +: virtualHosts
    this.copy(virtualHosts = list)

  def removeVHost(virtualHost: VirtualHost): PhysicalHost =
    val list = virtualHosts.filter(_ != virtualHost)
    this.copy(virtualHosts = list)

  override def calculateServiceTime(request: ClientRequest): Int = ???
  override def calculateLatency(request: ClientRequest): Int = ???

  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = WAITING, channelCount = hardwareDef.cores)

end PhysicalHost



case class VirtualHost(val name: String, val description: String,
                       val hardwareDef: HardwareDef,
                       val zone: Zone,
                       val vCPUs:Int, val memoryGB:Int, val threadingModel:ThreadingModel)
  extends ComputeNode, Described, QueueProvider, ServiceTimeCalculator:

  override def calculateServiceTime(request: ClientRequest): Int = ???
  override def calculateLatency(request: ClientRequest): Int = ???

  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = WAITING, channelCount = hardwareDef.cores)

end VirtualHost
