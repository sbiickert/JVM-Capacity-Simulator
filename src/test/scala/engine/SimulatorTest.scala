package ca.esri.capsim
package engine

import engine.SimulatorTest.sampleSimulator

import org.scalatest.funsuite.AnyFunSuite

class SimulatorTest extends AnyFunSuite:
  test("create") {
    val sim = sampleSimulator
    assert(!sim.isRunning)
    assert(sim.clock == 0)
  }

  test("advancing time") {
    val sim = sampleSimulator
    assert(DesignTest.sampleDesign.isValid)
    sim.design = DesignTest.sampleDesign
    assert(sim.nextEventTime.isEmpty)
    sim.start()
    assert(sim.nextEventTime.nonEmpty)
    sim.advanceTimeTo(sim.nextEventTime.get)
    assert(sim.clock > 0)
    assert(sim.queues.exists(_.requestCount > 0))
    assert(sim.nextEventTime.nonEmpty)
    assert(sim.nextEventTime.get > sim.clock)
  }
end SimulatorTest

object SimulatorTest:
  def sampleSimulator:Simulator =
    Simulator("Test Simulator 01", "Unit testing Simulator")
end SimulatorTest
