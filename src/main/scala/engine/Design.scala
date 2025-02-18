package ca.esri.capsim
package engine

import ca.esri.capsim.engine.compute.{Client, ComputeNode, PhysicalHost, Service, ServiceProvider, VirtualHost}
import ca.esri.capsim.engine.network.*
import ca.esri.capsim.engine.work.Workflow

import java.util.UUID

case class Design(name: String, description: String = "",
                  zones:List[Zone] = List(), 
                  network:List[Connection] = List(),
                  computeNodes:List[ComputeNode] = List(),
                  services:Map[String, Service] = Map(),
                  serviceProviders:List[ServiceProvider] = List(),
                  workflows:List[Workflow] = List()) extends Described:

  // ----------------------------------------------------------------
  // Zone Management
  // ----------------------------------------------------------------
  def addZone(zone: Zone, localBandwidth:Int, localLatency:Int): Design =
    if zones.contains(zone) then
      this
    else
      val updatedZones = zone +: zones
      val internalConnection = zone.selfConnect(bandwidth = localBandwidth, latency = localLatency)
      val updatedConnections = internalConnection +: network
      this.copy(zones = updatedZones, network = updatedConnections)

  def removeZone(zone: Zone): Design =
    val updatedZones = zones.filter(_ != zone)
    val updatedConnections = network.filter(conn => {
      conn.sourceZone != zone && conn.destinationZone != zone
    })
    val updatedNodes = computeNodes.filterNot(_.zone == zone)
    this.copy(zones = updatedZones,
      network = updatedConnections,
      computeNodes = updatedNodes)

  def replaceZone(original: Zone, updated:Zone): Design =
    val updatedZones = zones.filter(_ != original).appended(updated)
    val updatedConnections = network.map(conn => {
      if conn.sourceZone == original then
        conn.copy(sourceZone = updated)
      else if conn.destinationZone == original then
        conn.copy(destinationZone = updated)
      else
        conn
    })
    val updatedNodes = computeNodes.map(node => {
      if node.zone == original then
        node match
          case client: Client => client.copy(zone = updated)
          case host: PhysicalHost => host.copy(zone = updated)
          case vm: VirtualHost => vm.copy(zone = updated)
      else
        node
    })
    this.copy(zones = updatedZones,
      network = updatedConnections,
      computeNodes = updatedNodes)

  // ----------------------------------------------------------------
  // Network Management
  // ----------------------------------------------------------------
  def addConnection(conn:Connection,
                    addReciprocalConnection:Boolean): Design =
    if network.contains(conn) then
      this
    else
      var updatedNetwork = conn +: network
      if addReciprocalConnection then
        updatedNetwork = conn.invert +: updatedNetwork
      this.copy(network = updatedNetwork)

  def removeConnection(connToRemove:Connection): Design =
    val updatedNetwork = network.filter(_ != connToRemove)
    this.copy(network = updatedNetwork)

  def replaceConnection(original:Connection, updated:Connection): Design =
    val updatedNetwork = network.filter(_ != original).appended(updated)
    this.copy(network = updatedNetwork)

  // ----------------------------------------------------------------
  // Physical Host Management
  // ----------------------------------------------------------------
  def addHost(host:PhysicalHost): Design =
    if computeNodes.contains(host) then
      this
    else
      val updatedNodes = host +: computeNodes
      this.copy(computeNodes = updatedNodes)

  def removeHost(host:PhysicalHost): Design =
    // TODO: Need to check Service Providers
    val updatedNodes = computeNodes.filter(node => {
      node != host && !host.virtualHosts.contains(node)
    })
    this.copy(computeNodes = updatedNodes)

  def replaceHost(original:PhysicalHost, updated:PhysicalHost): Design =
    val updatedNodes = computeNodes.filter(_ != original).appended(updated)
    this.copy(computeNodes = updatedNodes)

  // ----------------------------------------------------------------
  // Service Management
  // ----------------------------------------------------------------
  def addService(service:Service): Design =
    val updatedMap = services.updated(service.serviceType, service)
    copy(services = updatedMap)
    
  def removeService(service: Service): Design =
    val updatedMap = services.removed(service.serviceType)
    val updatedSPs = serviceProviders.filterNot(_.service == service)
    copy(services = updatedMap, serviceProviders = updatedSPs)
    
  def replaceService(original: Service, updated:Service): Design =
    val updatedMap = services.removed(original.serviceType)
      .updated(updated.serviceType, updated)
    val updatedSPs = 
      serviceProviders.map(sp => {
        if sp.service == original then
          sp.copy(service = updated)
        else
          sp
        })
    this.copy(services = updatedMap, serviceProviders = updatedSPs)

  // ----------------------------------------------------------------
  // Service Provider Management
  // ----------------------------------------------------------------
  def addServiceProvider(sp:ServiceProvider): Design =
    if serviceProviders.contains(sp) then
      this
    else
      val updatedSPs = sp +: serviceProviders
      this.copy(serviceProviders = updatedSPs)

  def removeServiceProvider(sp:ServiceProvider): Design =
    // TODO: Need to check workflows
    val updatedSPs = serviceProviders.filterNot(_ == sp)
    this.copy(serviceProviders = updatedSPs)

  def replaceServiceProvider(original:ServiceProvider, updated:ServiceProvider): Design =
    val updatedSPs = serviceProviders.filter(_ != original).appended(updated)
    this.copy(serviceProviders = updatedSPs)

  // ----------------------------------------------------------------
  // Workflow Management
  // ----------------------------------------------------------------
  def addWorkflow(wf:Workflow): Design =
    if workflows.contains(wf) then
      this
    else
      val updatedWorkflows = wf +: workflows
      copy(workflows = updatedWorkflows)

  def removeWorkflow(wf:Workflow): Design =
    val updatedWorkflows = workflows.filterNot(_ == wf)
    this.copy(workflows = updatedWorkflows)
    
  def replaceWorkflow(original:Workflow, updated:Workflow): Design =
    val updatedWorkflows = workflows.filter(_ != original).appended(updated)
    this.copy(workflows = updatedWorkflows)

end Design

object Design:
  var _nextID: Int = 0
  def nextID(): Int =
    _nextID += 1
    _nextID
  
  def nextName(): String = s"Design $_nextID"
end Design

  
