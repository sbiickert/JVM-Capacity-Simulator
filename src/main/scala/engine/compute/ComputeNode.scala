package ca.esri.capsim
package engine.compute

import engine.Described
import engine.network.Zone
import engine.queue.WaitMode.*
import engine.queue.{MultiQueue, QueueProvider, ServiceTimeCalculator}
import engine.work.{ClientRequest, ClientRequestSolutionStep}

sealed trait ComputeNode extends Described, ServiceTimeCalculator:
  val hardwareDef: HardwareDef
  val zone: Zone

  private def adjustedServiceTime(serviceTime:Int):Int =
    val relative = HardwareDef.BASELINE_PER_CORE_SPEC_INT_RATE_2017 / hardwareDef.specIntRate2017PerCore
    (serviceTime * relative).toInt

  def provideQueue(): MultiQueue
  
  override def calculateServiceTime(request: ClientRequest): Option[Int] =
    request.solution.currentStep match
      case step: Some[ClientRequestSolutionStep] => Some(adjustedServiceTime(step.get.serviceTime))
      case _ => None

  override def calculateLatency(request: ClientRequest): Option[Int] = None

case class Client(name: String, description: String,
                  hardwareDef: HardwareDef,
                  zone: Zone)
  extends ComputeNode, QueueProvider:

  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = PROCESSING,
      channelCount = 1000) // Arbitrary large number. Clients represent a group, not a PC.
end Client



case class PhysicalHost(name: String, description: String,
                        hardwareDef: HardwareDef,
                        zone: Zone, virtualHosts: List[VirtualHost])
  extends ComputeNode, QueueProvider:

  def addSimpleVHost(vCPUs: Int, memoryGB: Int, threadingModel: ThreadingModel): PhysicalHost =
    addVHost(VirtualHost(name = s"VH $name:" + virtualHosts.length, description = "",
      hardwareDef = hardwareDef, zone = zone,
      vCPUs = vCPUs, memoryGB = memoryGB, threadingModel = threadingModel))

  def addVHost(virtualHost: VirtualHost): PhysicalHost =
    val list = virtualHost +: virtualHosts
    this.copy(virtualHosts = list)

  def removeVHost(virtualHost: VirtualHost): PhysicalHost =
    val list = virtualHosts.filter(_ != virtualHost)
    this.copy(virtualHosts = list)

  def migrateVHost(virtualHost: VirtualHost, toNewHost:PhysicalHost): (PhysicalHost, PhysicalHost) =
    val updatedVMs = virtualHosts.filter(_ != virtualHost)
    val updatedOtherVMs = 
      if toNewHost.virtualHosts.contains(virtualHost) then
        virtualHost +: toNewHost.virtualHosts
      else
        toNewHost.virtualHosts
    (this.copy(virtualHosts = updatedVMs), toNewHost.copy(virtualHosts = updatedOtherVMs))
    
  def totalCPUAllocation: Int =
    virtualHosts.map(vm => vm.vCPUs * vm.threadingModel.factor)
      .sum
      .round
      .toInt
    
  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = PROCESSING, channelCount = hardwareDef.cores)

end PhysicalHost



case class VirtualHost(name: String, description: String,
                       hardwareDef: HardwareDef,
                       zone: Zone,
                       vCPUs:Int, memoryGB:Int, threadingModel:ThreadingModel)
  extends ComputeNode, QueueProvider:

  override def provideQueue(): MultiQueue =
    MultiQueue(serviceTimeCalculator = this, waitMode = PROCESSING, channelCount = hardwareDef.cores)

end VirtualHost
