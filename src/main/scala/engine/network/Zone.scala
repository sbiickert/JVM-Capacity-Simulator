package ca.esri.capsim
package engine.network

import engine.{Described, Design}

import ca.esri.capsim.engine.compute.{Client, ComputeNode, PhysicalHost, VirtualHost}
import ca.esri.capsim.engine.work.Workflow

case class Zone(val name:String, val description:String,
                val zoneType: ZoneType) extends Described:
  
  def connect(other:Zone, bandwidth:Int, latency:Int): Connection =
      Connection(sourceZone = this, destinationZone = other,
                 bandwidth = bandwidth, latency = latency)
      
  def selfConnect(bandwidth:Int = 1000, latency:Int = 0): Connection =
      Connection(sourceZone = this, destinationZone = this, bandwidth = bandwidth, latency = latency)

  // Computed properties. Pass in lists of all Connections to return the ones assoc. with this Zone
  def connections(inConnections: List[Connection]): List[Connection] =
    inConnections.filter(conn => {conn.sourceZone == this || conn.destinationZone == this})

  def localConnection(inConnections: List[Connection]): Option[Connection] =
    connections(inConnections).find(_.isLocal)

  def exitConnections(inConnections: List[Connection]): List[Connection] =
    connections(inConnections).filter(conn => {conn.destinationZone != this})

  // Computed properties. Pass in lists of all Clients/ComputeNodes/Workflows
  // to return the ones assoc. with this Zone
  def clients(inComputeNodes: List[ComputeNode]): List[Client] =
    inComputeNodes.filter(node => {
      node match
        case client: Client => client.zone == this
        case _ => false
    }).map(_.asInstanceOf[Client])

  def servers(inComputeNodes: List[ComputeNode]): List[ComputeNode] =
    inComputeNodes.filter(node => {
        node match
        case ph: PhysicalHost => ph.zone == this
        case vh: VirtualHost => vh.zone == this
        case _ => false})

  def workflows(inWorkflows: List[Workflow]): List[Workflow] =
    inWorkflows.filter(w => {w.zone == this})


end Zone

