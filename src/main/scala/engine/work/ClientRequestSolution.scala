package ca.esri.capsim
package engine.work

import engine.compute.ServiceProvider

import ca.esri.capsim.engine.network.Connection

case class ClientRequestSolution(steps: List[ClientRequestSolutionStep]):
  def currentStep: ClientRequestSolutionStep = steps.head
  def gotoNextStep: ClientRequestSolution =
    this.copy(steps = steps.tail)

end ClientRequestSolution

object ClientRequestSolution:
  def create(chain:WorkflowChain,
             serviceProviders:Set[ServiceProvider],
             network:List[Connection]): ClientRequestSolution =
    // Starting at the head of the chain (client), stop at each
    // service provider, traversing the network between each
    var sourceSP = serviceProviders.find(_.service.serviceType == chain.head.serviceType).get
    var sourceNode = sourceSP.handlerNode()
    var steps = List[ClientRequestSolutionStep](ClientRequestSolutionStep(
      serviceTimeCalculator = sourceNode, isResponse = false,
      dataSize = chain.head.requestSizeKB, chatter = 0, // Zero chatter for compute nodes
      serviceTime = chain.head.serviceTime))

    for i <- 1 until chain.length do
      val destSP = serviceProviders.find(_.service.serviceType == chain(i).serviceType).get
      val destNode = destSP.handlerNode()
      val conn = network.find(conn => {
        conn.sourceZone == sourceNode.zone && conn.destinationZone == destNode.zone
      }).get
      steps = ClientRequestSolutionStep(
        serviceTimeCalculator = conn, isResponse = false,
        dataSize = chain(i).requestSizeKB, chatter = chain(i).chatter,
        serviceTime = 0) +: steps   // Service time is derived from data size for connection nodes
      steps = ClientRequestSolutionStep(
        serviceTimeCalculator = destNode, isResponse = false,
        dataSize = chain(i).requestSizeKB, chatter = 0, // Zero chatter for compute nodes
        serviceTime = chain(i).serviceTime) +: steps 
      sourceSP = destSP
      sourceNode = destNode

    // Now, retrace our steps back to the client
    val chainR = chain.reverse
    for i <- 1 until chainR.length do
      val destSP = serviceProviders.find(_.service.serviceType == chainR(i).serviceType).get
      val destNode = destSP.handlerNode()
      val conn = network.find(conn => {
        conn.sourceZone == sourceNode.zone && conn.destinationZone == destNode.zone
      }).get
      steps = ClientRequestSolutionStep(
        serviceTimeCalculator = conn, isResponse = true,
        dataSize = chainR(i).responseSizeKB, chatter = chainR(i).chatter,
        serviceTime = 0) +: steps
      steps = ClientRequestSolutionStep(
        serviceTimeCalculator = destNode, isResponse = true,
        dataSize = chainR(i).responseSizeKB, chatter = 0,
        serviceTime = chainR(i).serviceTime) +: steps
      sourceSP = destSP
      sourceNode = destNode

    ClientRequestSolution(steps)

