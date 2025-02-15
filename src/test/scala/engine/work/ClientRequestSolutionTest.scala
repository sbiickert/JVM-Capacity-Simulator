package ca.esri.capsim
package engine.work

import ca.esri.capsim.engine.compute.ServiceProviderTest
import ca.esri.capsim.engine.network.RouteTest
import org.scalatest.funsuite.AnyFunSuite

class ClientRequestSolutionTest extends AnyFunSuite:
  test("create") {
    val crsWebMapImage = ClientRequestSolution.create(
      chain = WorkflowDefTest.sampleWebDynamicMapChain,
      serviceProviders = ServiceProviderTest.sampleWebGISServiceProviders,
      network = RouteTest.sampleIntranet)
    assert(crsWebMapImage.steps.nonEmpty)
  }
end ClientRequestSolutionTest

object ClientRequestSolutionTest:

end ClientRequestSolutionTest

