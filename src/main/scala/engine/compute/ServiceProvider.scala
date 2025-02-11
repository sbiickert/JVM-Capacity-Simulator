package ca.esri.capsim
package engine.compute

import engine.Described

import ca.esri.capsim.engine.compute.BalancingModel.{FAILOVER, SINGLE}

case class ServiceProvider(name:String, description:String,
                           service: Service,
                           primary: Option[ComputeNode],
                           nodes: Set[ComputeNode]) extends Described:

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

end ServiceProvider

