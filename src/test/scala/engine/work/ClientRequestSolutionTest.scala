package ca.esri.capsim
package engine.work

import ca.esri.capsim.engine.compute.{ComputeNode, ServiceProviderTest}
import ca.esri.capsim.engine.network.{Connection, RouteTest}
import org.scalatest.funsuite.AnyFunSuite

class ClientRequestSolutionTest extends AnyFunSuite:
  test("createIntranet") {
    val crsWebMapImage = ClientRequestSolutionTest.sampleIntranetClientRequestSolution
    assert(crsWebMapImage.steps.length == 17)
    // Even indexes are compute, odd are network connection in this simple example
    crsWebMapImage.steps.zipWithIndex.filter(_._2 % 2 == 0).map(_._1)
      .foreach(step => assert(step.serviceTimeCalculator.isInstanceOf[ComputeNode]))
    crsWebMapImage.steps.zipWithIndex.filter(_._2 % 2 == 1).map(_._1)
      .foreach(step => assert(step.serviceTimeCalculator.isInstanceOf[Connection]))
  }
end ClientRequestSolutionTest

object ClientRequestSolutionTest:
  def sampleIntranetClientRequestSolution: ClientRequestSolution =
    ClientRequestSolution.create(
      chain = WorkflowDefTest.sampleWebDynamicMapChain.copy(serviceProviders = ServiceProviderTest.sampleWebGISServiceProviders),
      network = RouteTest.sampleIntranet)
end ClientRequestSolutionTest

