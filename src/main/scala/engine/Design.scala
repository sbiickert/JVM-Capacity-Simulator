package ca.esri.capsim
package engine

import engine.Design.updateWorkflowsWithUpdatedServiceProviders
import engine.compute.*
import engine.network.*
import engine.queue.MultiQueue
import engine.work.{TransactionalWorkflow, UserWorkflow, Workflow}

case class Design(name: String, description: String = "",
                  zones:List[Zone] = List(), 
                  network:List[Connection] = List(),
                  computeNodes:List[ComputeNode] = List(),
                  services:Map[String, Service] = Map(),
                  serviceProviders:List[ServiceProvider] = List(),
                  workflows:List[Workflow] = List()) extends Described:

  def isValid:Boolean =
    val allServiceProvidersValid = serviceProviders.forall(_.isValid)
    val allZonesAreConnected = zones.forall(_.isConnected(network))
    val allWorkflowsValid = workflows.forall(_.isValid)
    zones.nonEmpty
      && network.nonEmpty
      && computeNodes.nonEmpty
      && workflows.nonEmpty
      && services.nonEmpty
      && allServiceProvidersValid
      && allZonesAreConnected
      && allWorkflowsValid
    
  // ----------------------------------------------------------------
  // Zone Management
  // ----------------------------------------------------------------
  /** Creates a new zone if it doesn't already exist.
   *  Creates a local network connection to save a step. */
  def addZone(zone: Zone, localBandwidth:Int, localLatency:Int): Design =
    if zones.contains(zone) then
      this
    else
      val updatedZones = zone +: zones
      val internalConnection = zone.selfConnect(bandwidth = localBandwidth, latency = localLatency)
      val updatedConnections = internalConnection +: network
      this.copy(zones = updatedZones, network = updatedConnections)

  /**
   * Removes zone from the Design. Follow on effects:
   * - Removes any connections running to/from the zone
   * - Removes any compute nodes in the zone
   * - Updates the list of service providers affected by removal of compute nodes
   * - Updates any workflows that are impacted by the change in service providers
   * @param zone
   * @return the altered Design
   */
  def removeZone(zone: Zone): Design =
    val updatedZones = zones.filter(_ != zone)
    val updatedConnections = network.filter(conn => {
      conn.sourceZone != zone && conn.destinationZone != zone
    })
    val updatedNodes = computeNodes.filterNot(_.zone == zone)
    val updatedSPs = Design.updateServiceProvidersWithUpdatedNodes(serviceProviders, updatedNodes)
    val updatedWorkflows = updateWorkflowsWithUpdatedServiceProviders(workflows, updatedSPs)

    this.copy(zones = updatedZones,
      network = updatedConnections,
      computeNodes = updatedNodes,
      serviceProviders = updatedSPs,
      workflows = updatedWorkflows)

  /**
   * Replaces a zone with another in the Design. Follow on effects:
   * - Updates any connections running to/from the zone
   * - Updates any compute nodes in the zone
   * - Updates the list of service providers affected by change of compute nodes
   * - Updates any workflows that are impacted by the change in service providers
   * @param zone
   * @return the altered Design
   */
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
    val updatedSPs = Design.updateServiceProvidersWithUpdatedNodes(serviceProviders, updatedNodes)
    val updatedWorkflows = updateWorkflowsWithUpdatedServiceProviders(workflows, updatedSPs)

    this.copy(zones = updatedZones,
      network = updatedConnections,
      computeNodes = updatedNodes,
      serviceProviders = updatedSPs,
      workflows = updatedWorkflows)

  def getZone(name:String): Option[Zone] =
    zones.find(_.name == name)
    
  // ----------------------------------------------------------------
  // Network Management
  // ----------------------------------------------------------------
  /** Create a new connection if it doesn't exist.
   *  Adds a reciprocal connection if specified.
   *  Only alters the Design. */
  def addConnection(conn:Connection,
                    addReciprocalConnection:Boolean): Design =
    if network.contains(conn) then
      this
    else
      var updatedNetwork = conn +: network
      if addReciprocalConnection then
        val connR = conn.invert
        if !network.contains(connR) then
          updatedNetwork = conn.invert +: updatedNetwork
      this.copy(network = updatedNetwork)

  /** Removes a connection. Only alters the Design. */
  def removeConnection(connToRemove:Connection): Design =
    val updatedNetwork = network.filter(_ != connToRemove)
    this.copy(network = updatedNetwork)

  /**
   * Replaces one connection with another. Alters the Design.
   * @param original: the connection being replaced
   * @param updated: the replacement connection
   * @return The modified Design
   */
  def replaceConnection(original:Connection, updated:Connection): Design =
    val updatedNetwork = network.filter(_ != original).appended(updated)
    this.copy(network = updatedNetwork)

  // ----------------------------------------------------------------
  // Physical Host/Client Management
  // ----------------------------------------------------------------

  /**
   * Adds a physical host in a Zone.
   * @param host: the physical host being added.
   * @return The modified Design
   */
  def addHost(host:PhysicalHost): Design =
    addComputeNode(host)

  def addClient(client:Client): Design =
    addComputeNode(client)
    
  private def addComputeNode(node:ComputeNode): Design =
    if computeNodes.contains(node) then
      this
    else
      val updatedNodes = node +: computeNodes
      this.copy(computeNodes = updatedNodes)

  /**
   * Removes a physical host from a Zone. All VMs hosted on it are removed.
   * Updates service providers that referenced the removed compute nodes.
   * Updates workflows that referenced the updated service providers
   * @param host: the physical host being removed.
   * @return The modified Design
   */
  def removeHost(host:PhysicalHost): Design =
    val vHostsToRemove = computeNodes.filter(host.virtualHosts.contains(_))
    removeComputeNode(host +: vHostsToRemove)
    
  def removeClient(client:Client): Design =
    removeComputeNode(List(client))
    
  private def removeComputeNode(nodes: List[ComputeNode]): Design =
    val updatedNodes = computeNodes.filterNot(nodes.contains(_))
    val updatedSPs = Design.updateServiceProvidersWithUpdatedNodes(serviceProviders, updatedNodes)
    val updatedWorkflows = updateWorkflowsWithUpdatedServiceProviders(workflows, updatedSPs)
  
    this.copy(computeNodes = updatedNodes,
      serviceProviders = updatedSPs,
      workflows = updatedWorkflows)

  /**
   * Replaces a physical host in a Zone. All original VMs hosted on it are removed.
   * and replaced with new ones.
   * Updates service providers that referenced the removed compute nodes.
   * Updates workflows that referenced the updated service providers.
   * @param original: the original physical host
   * @param updated: the updated physical host
   * @return The modified Design
   */
  def replaceHost(original:PhysicalHost, updated:PhysicalHost): Design =
    val updatedNodes = computeNodes.filterNot(_ == original)
      .appended(updated)
      .filterNot(original.virtualHosts.contains(_))
      .appendedAll(updated.virtualHosts)
    val updatedSPs = Design.updateServiceProvidersWithUpdatedNodes(serviceProviders, updatedNodes)
    val updatedWorkflows = updateWorkflowsWithUpdatedServiceProviders(workflows, updatedSPs)

    this.copy(computeNodes = updatedNodes,
      serviceProviders = updatedSPs,
      workflows = updatedWorkflows)
    
  def replaceClient(original: Client, updated: Client): Design =
    val updatedNodes = computeNodes.filterNot(_ == original)
      .appended(updated)
    val updatedSPs = Design.updateServiceProvidersWithUpdatedNodes(serviceProviders, updatedNodes)
    val updatedWorkflows = updateWorkflowsWithUpdatedServiceProviders(workflows, updatedSPs)
  
    this.copy(computeNodes = updatedNodes,
      serviceProviders = updatedSPs,
      workflows = updatedWorkflows)


  def getComputeNode(name:String): Option[ComputeNode] =
    computeNodes.find(_.name == name)
    
  // ----------------------------------------------------------------
  // Service Management
  // ----------------------------------------------------------------
  /** Adds a new service type to the design. */
  def addService(service:Service): Design =
    val updatedMap = services.updated(service.serviceType, service)
    copy(services = updatedMap)

  /**
   * Removes a service type from the design.
   * Removes any service providers that were of that type.
   * Updates any workflows that referenced that service provider.
   * @param service
   * @return The modified Design
   */
  def removeService(service: Service): Design =
    val updatedMap = services.removed(service.serviceType)
    val updatedSPs = serviceProviders.filterNot(_.service == service)
    val updatedWorkflows = updateWorkflowsWithUpdatedServiceProviders(workflows, updatedSPs)
    this.copy(services = updatedMap,
      serviceProviders = updatedSPs,
      workflows = updatedWorkflows)

  /**
   * Replaces a service type in the design.
   * Updates any service providers that were of the original type
   * Updates any workflows that referenced the updated service providers.
   * @param original: the previous service type
   * @param updated: the updated service type
   * @return The modified Design
   */
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
    val updatedWorkflows = updateWorkflowsWithUpdatedServiceProviders(workflows, updatedSPs)
    this.copy(services = updatedMap,
      serviceProviders = updatedSPs,
      workflows = updatedWorkflows)

  // ----------------------------------------------------------------
  // Service Provider Management
  // ----------------------------------------------------------------
  /** Adds a new service provider to the design. */
  def addServiceProvider(sp:ServiceProvider): Design =
    if serviceProviders.contains(sp) then
      this
    else if serviceProviders.exists(_.name == sp.name) then
      this // Can't have two service providers with the same name.
    else
      val updatedSPs = sp +: serviceProviders
      this.copy(serviceProviders = updatedSPs)

  /**
   * Removes a service provider from the design.
   * Updates any workflows that referenced the service provider
   * @param sp
   * @return The modified Design
   */
  def removeServiceProvider(sp:ServiceProvider): Design =
    val updatedSPs = serviceProviders.filterNot(_ == sp)
    val updatedWorkflows = updateWorkflowsWithUpdatedServiceProviders(workflows, updatedSPs)
    this.copy(serviceProviders = updatedSPs,
      workflows = updatedWorkflows)

  /**
   * Replaces a service provider with an updated one.
   * Updates any workflows that referenced the service provider
   * @param original: the original service provider
   * @param updated: the updated service provider
   * @return The modified design
   */
  def replaceServiceProvider(original:ServiceProvider, updated:ServiceProvider): Design =
    val updatedSPs = serviceProviders.filter(_ != original).appended(updated)
    val updatedWorkflows = updateWorkflowsWithUpdatedServiceProviders(workflows, updatedSPs)
    this.copy(serviceProviders = updatedSPs,
      workflows = updatedWorkflows)
    

  // ----------------------------------------------------------------
  // Workflow Management
  // ----------------------------------------------------------------
    
  /** Adds a new workflow to the design */
  def addWorkflow(wf:Workflow): Design =
    if workflows.contains(wf) then
      this
    else
      val updatedWorkflows = wf +: workflows
      copy(workflows = updatedWorkflows)

  /**
   * Removes a workflow from the design
   * @param wf
   * @return The modified Design
   */
  def removeWorkflow(wf:Workflow): Design =
    val updatedWorkflows = workflows.filterNot(_ == wf)
    this.copy(workflows = updatedWorkflows)

  /**
   * Replaces a workflow in the design
   * @param original
   * @param updated
   * @return The modified design
   */
  def replaceWorkflow(original:Workflow, updated:Workflow): Design =
    val updatedWorkflows = workflows.filter(_ != original).appended(updated)
    this.copy(workflows = updatedWorkflows)

  // ----------------------------------------------------------------
  // Queues
  // ----------------------------------------------------------------
  def provideQueues():List[MultiQueue] =
    var queues = List[MultiQueue]()
    for conn <- network do
      queues = conn.provideQueue() +: queues

    for host <- computeNodes do
      queues = host.provideQueue() +: queues

    queues

end Design

object Design:
  var _nextID: Int = 0
  def nextID(): Int =
    _nextID += 1
    _nextID
  
  def nextName(): String = s"Design $_nextID"

  /** Assumes that ComputeNode names are unique and unchanged. */
  def updateServiceProvidersWithUpdatedNodes(serviceProviders:List[ServiceProvider],
                                             newNodes:List[ComputeNode]):List[ServiceProvider] =
    serviceProviders.map(sp => {
      val updatedNodes = sp.nodes.flatMap(n => {
        newNodes.find(_.name == n.name)
      })
      sp.copy(nodes = updatedNodes)
    })

  def updateWorkflowsWithUpdatedServiceProviders(workflows:List[Workflow],
                                                 updatedSPs:List[ServiceProvider]):List[Workflow] =
    workflows.map(w => {
      val uSPs = w.defaultServiceProviders.flatMap(sp => {
        updatedSPs.find(_.name == sp.name)
      })
      w match
        case uwf: UserWorkflow => uwf.copy(defaultServiceProviders = uSPs)
        case twf: TransactionalWorkflow => twf.copy(defaultServiceProviders = uSPs)
    })
end Design

  
