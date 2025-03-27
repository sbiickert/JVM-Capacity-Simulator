package ca.esri.capsim
package engine.queue

import ca.esri.capsim.engine.compute.ComputeNodeTest
import ca.esri.capsim.engine.network.RouteTest
import ca.esri.capsim.engine.queue.MultiQueueTest.{sampleComputeCR, sampleComputeQ, sampleConnectionCR, sampleConnectionQ}
import ca.esri.capsim.engine.queue.WaitMode.{PROCESSING, TRANSMITTING}
import ca.esri.capsim.engine.work.{ClientRequest, ClientRequestGroup, ClientRequestSolution, ClientRequestSolutionStep}
import org.scalatest.funsuite.AnyFunSuite

class MultiQueueTest extends AnyFunSuite:
  test("create") {
    val connQ = sampleConnectionQ
    assert(connQ.serviceTimeCalculator == RouteTest.sampleNetwork.head)

    val vmQ = sampleComputeQ
    assert(vmQ.channelCount == ComputeNodeTest.sampleVHost.vCPUs)
  }

  test("networkEnqueue") {
    val connQ = sampleConnectionQ // Network connections have one (serial) channel
    connQ.enqueue(sampleConnectionCR, 13)
    assert(connQ.requestCount == 1)
    assert(connQ.availableChannelCount == 0)
    // 13 ms + 160 ms ST + 100 ms latency
    assert(connQ.nextEventTime.contains(273))

    // Yes, I know it's the same request. Just looking at queueing
    connQ.enqueue(sampleConnectionCR, 15)
    connQ.enqueue(sampleConnectionCR, 16)
    assert(connQ.requestCount == 3)

    val finished = connQ.removeFinishedRequests(273)
    assert(finished.length == 1)
    assert(connQ.requestCount == 2)
    assert(connQ.availableChannelCount == 0)
  }

  test("computeEnqueue") {
    val compQ = sampleComputeQ // 4 vCore server
    compQ.enqueue(sampleComputeCR, 13)
    assert(compQ.requestCount == 1)
    assert(compQ.nextEventTime.contains(265)) // 13 + service time, which is 141 adjusted slower hardware

    compQ.enqueue(sampleComputeCR, 23)
    compQ.enqueue(sampleComputeCR, 33)
    assert(compQ.requestCount == 3)
    assert(compQ.availableChannelCount == 1)
    compQ.enqueue(sampleComputeCR, 43)
    compQ.enqueue(sampleComputeCR, 53)
    assert(compQ.requestCount == 5)
    assert(compQ.availableChannelCount == 0)

    val finished = compQ.removeFinishedRequests(275) // Should be 2 finished requests
    assert(finished.length == 2)
    assert(compQ.requestCount == 3)
    assert(compQ.availableChannelCount == 1)
  }

end MultiQueueTest

object MultiQueueTest:
  val sampleConnectionQ =
    val network = RouteTest.sampleNetwork
    MultiQueue(network.head, TRANSMITTING, 1)

  val sampleComputeQ =
    val vm = ComputeNodeTest.sampleVHost
    MultiQueue(vm, PROCESSING, vm.vCPUs)

  val sampleConnectionCR =
    ClientRequest(ClientRequest.nextName, "", 10,
      ClientRequestSolution(List(
        ClientRequestSolutionStep(sampleConnectionQ.serviceTimeCalculator, true, 2000, 10, 0))),
      ClientRequestGroup.nextID, false)

  val sampleComputeCR =
    ClientRequest(ClientRequest.nextName, "", 10,
      ClientRequestSolution(List(
        ClientRequestSolutionStep(sampleComputeQ.serviceTimeCalculator, true, 2000, 10, 141))),
      ClientRequestGroup.nextID, false)