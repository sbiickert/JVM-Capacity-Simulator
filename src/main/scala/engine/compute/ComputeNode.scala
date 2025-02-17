package ca.esri.capsim
package engine.compute

import engine.Described
import engine.network.Zone
import engine.queue.WaitMode.*
import engine.queue.{MultiQueue, QueueProvider, ServiceTimeCalculator}
import engine.work.ClientRequest

sealed trait ComputeNode extends ServiceTimeCalculator:
  val hardwareDef: HardwareDef
  val zone: Zone

  private def adjustedServiceTime(serviceTime:Int):Int =
    serviceTime * (HardwareDef.BASELINE_SPEC_INT_RATE_2017 / hardwareDef.specIntRate2017)

  override def calculateServiceTime(request: ClientRequest): Int =
    adjustedServiceTime(request.solution.currentStep.serviceTime)

  override def calculateLatency(request: ClientRequest): Int = 0

case class Client(name: String, description: String,
                  hardwareDef: HardwareDef,
                  zone: Zone)
  extends ComputeNode, Described, QueueProvider:

  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = PROCESSING, channelCount = hardwareDef.cores)
end Client



case class PhysicalHost(name: String, description: String,
                        hardwareDef: HardwareDef,
                        zone: Zone, virtualHosts: List[VirtualHost])
  extends ComputeNode, Described, QueueProvider:

  def addVHost(vCPUs: Int, memoryGB: Int, threadingModel: ThreadingModel): PhysicalHost =
    val vHost = VirtualHost(name = s"VH $name:" + virtualHosts.length, description = "",
      hardwareDef = hardwareDef, zone = zone,
      vCPUs = vCPUs, memoryGB = memoryGB, threadingModel = threadingModel)
    val list = vHost +: virtualHosts
    this.copy(virtualHosts = list)

  def removeVHost(virtualHost: VirtualHost): PhysicalHost =
    val list = virtualHosts.filter(_ != virtualHost)
    this.copy(virtualHosts = list)

  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = PROCESSING, channelCount = hardwareDef.cores)

end PhysicalHost



case class VirtualHost(name: String, description: String,
                       hardwareDef: HardwareDef,
                       zone: Zone,
                       vCPUs:Int, memoryGB:Int, threadingModel:ThreadingModel)
  extends ComputeNode, Described, QueueProvider:

  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = PROCESSING, channelCount = hardwareDef.cores)

end VirtualHost
