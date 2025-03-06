package ca.esri.capsim
package engine

import engine.work.ClientRequest
import scala.collection.mutable

import ca.esri.capsim.engine.queue.MultiQueue
import ca.esri.capsim.engine.work.ClientRequest

class Simulator(override val name:String, override val description:String) extends Described:
  var design: Design = Design(Design.nextName())
  var clock: Int = 0
  var isRunning: Boolean = false
  var activeRequests:List[ClientRequest] = List[ClientRequest]()
  var finishedRequests:List[ClientRequest] = List[ClientRequest]()
  var queues:List[MultiQueue] = List[MultiQueue]()
  var nextWorkflowEventTime:mutable.Map[String, Int] = mutable.Map.empty
  var metrics:List[PerformanceMetric] = List[PerformanceMetric]()

  def start():Unit =
    if !design.isValid then
      isRunning = false
    else
      // Calculate next event time for all workflows
      for wf <- design.workflows do
        nextWorkflowEventTime(wf.name) = wf.calculateNextEventTime(clock)
      isRunning = true

  def pause():Unit =
    isRunning = false

  def stop():Unit =
    isRunning = false
    reset()

  def advanceTimeBy(milliseconds:Int): Int =
    assert(milliseconds > 0)
    advanceTimeTo(clock + milliseconds)

  private def advanceTimeTo(newClock:Int): Int =
    assert(newClock > clock)

    // do stuff

    clock = newClock
    clock

  private def reset():Unit =
    clock = 0
    metrics = List[PerformanceMetric]()
    queues = design.provideQueues()
    activeRequests = List[ClientRequest]()
    finishedRequests = List[ClientRequest]()
    nextWorkflowEventTime = mutable.Map.empty



end Simulator

object Simulator:

end Simulator


