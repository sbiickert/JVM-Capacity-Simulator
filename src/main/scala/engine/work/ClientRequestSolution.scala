package ca.esri.capsim
package engine.work

import engine.compute.ServiceProvider
import engine.network.{Connection, Route}

case class ClientRequestSolution(steps: List[ClientRequestSolutionStep]):
  def currentStep: Option[ClientRequestSolutionStep] = steps.headOption
  def gotoNextStep: ClientRequestSolution =
    this.copy(steps = steps.tail)

end ClientRequestSolution

object ClientRequestSolution:
  def create(chain:WorkflowChain,
             network:List[Connection]): ClientRequestSolution =
    // Starting at the head of the chain (client), stop at each
    // service provider, traversing the network between each
    var sourceSP = chain.serviceProvider(chain.steps.head)
    var sourceNode = sourceSP.handlerNode()
    var steps = List[ClientRequestSolutionStep](ClientRequestSolutionStep(
      serviceTimeCalculator = sourceNode, isResponse = false,
      dataSize = chain.steps.head.requestSizeKB, chatter = 0, // Zero chatter for compute nodes
      serviceTime = chain.steps.head.serviceTime))

    for i <- 1 until chain.steps.length do
      val destSP = chain.serviceProvider(i)
      val destNode = destSP.handlerNode()
      //println(s"Finding connection between $sourceNode.zone} and $destNode.zone}")
      val route = Route.findRoute(sourceNode.zone, destNode.zone, network)
      assert(route.nonEmpty)
      route.get.connections.foreach(connection => {
        steps = ClientRequestSolutionStep(
          serviceTimeCalculator = connection, isResponse = false,
          dataSize = chain.steps(i).requestSizeKB, chatter = chain.steps(i).chatter,
          serviceTime = 0) +: steps // Service time is derived from data size for connection nodes
      })
      steps = ClientRequestSolutionStep(
        serviceTimeCalculator = destNode, isResponse = false,
        dataSize = chain.steps(i).requestSizeKB, chatter = 0, // Zero chatter for compute nodes
        serviceTime = chain.steps(i).serviceTime) +: steps 
      sourceSP = destSP
      sourceNode = destNode

    // Now, retrace our steps back to the client
    val chainStepsR = chain.steps.reverse
    for i <- 1 until chainStepsR.length do
      val destSP = chain.serviceProvider(chainStepsR(i))
      val destNode = destSP.handlerNode()
      val route = Route.findRoute(sourceNode.zone, destNode.zone, network)
      assert(route.nonEmpty)
      route.get.connections.foreach(conn => {
        steps = ClientRequestSolutionStep(
          serviceTimeCalculator = conn, isResponse = true,
          dataSize = chainStepsR(i).responseSizeKB, chatter = chainStepsR(i).chatter,
          serviceTime = 0) +: steps
      })
      steps = ClientRequestSolutionStep(
        serviceTimeCalculator = destNode, isResponse = true,
        dataSize = chainStepsR(i).responseSizeKB, chatter = 0,
        serviceTime = chainStepsR(i).serviceTime) +: steps
      sourceSP = destSP
      sourceNode = destNode

    ClientRequestSolution(steps.reverse)

