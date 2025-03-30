package ca.esri.capsim
package engine.compute

import engine.compute.BalancingModel.*
import engine.{Described, Validatable, ValidationMessage}

import scala.util.Random

case class ServiceProvider(name:String, description:String,
                           service: Service,
                           primary: Option[ComputeNode],
                           nodes: Set[ComputeNode]) 
  extends Described, Validatable:

  def addNode(node: ComputeNode): ServiceProvider =
    if service.balancingModel == SINGLE && nodes.nonEmpty then
      this
    else if service.balancingModel == FAILOVER && nodes.size >= 2 then
      this
    else
      val set = nodes + node
      this.copy(nodes = set)

  def removeNode(node: ComputeNode): ServiceProvider =
    val set = nodes - node
    this.copy(nodes = set)
    
  def handlerNode(): ComputeNode =
    service.balancingModel match
      case SINGLE => nodes.head
      case FAILOVER => primary.get
      case ROUNDROBIN => {
        val i = Random.nextInt(nodes.size)
        nodes.iterator.drop(i).next
      }

  override def validate: List[ValidationMessage] = 
    var eList = List[ValidationMessage]()
    val failoverNoPrimary = service.balancingModel == FAILOVER && primary.isEmpty
    if failoverNoPrimary then
      eList = ValidationMessage("Failover service type without primary node set", name) +: eList
    if nodes.isEmpty then
      eList = ValidationMessage("Service provider without any nodes", name) +: eList
    eList
  
end ServiceProvider

