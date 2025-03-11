package ca.esri.capsim
package engine

import engine.queue.{MultiQueue, WaitingRequest}
import engine.work.{ClientRequest, Workflow}

import scala.collection.mutable

class Simulator(override val name:String, override val description:String) extends Described:
  var design: Design = Design(Design.nextName())
  var clock: Int = 0
  var isRunning: Boolean = false
  var finishedRequests:List[ClientRequest] = List[ClientRequest]()
  var queues:List[MultiQueue] = List[MultiQueue]()
  var nextEventTimeForWorkflows:mutable.Map[String, Int] = mutable.Map.empty
  var metrics:List[PerformanceMetric] = List[PerformanceMetric]()

  def start():Unit =
    if !design.isValid then
      isRunning = false
    else
      reset()

      // Calculate next event time for all workflows
      for wf <- design.workflows do
        nextEventTimeForWorkflows(wf.name) = wf.calculateNextEventTime(clock)
      isRunning = true

  def stop():Unit =
    isRunning = false
    // TODO: Summarize stuff

  def nextEventTime:Option[Int] =
    List(nextQEventTime, nextWFEventTime).min

  private def nextWFEventTime:Option[Int] =
    nextEventTimeForWorkflows.values.minOption

  private def nextQEventTime:Option[Int] =
    queues.flatMap(_.nextEventTime).minOption

  private def getNextWorkflow:Workflow =
    val nameTime = nextEventTimeForWorkflows.minBy(_._2)
    design.workflows.filter(_.name == nameTime._1).head

  private def getNextQueue:MultiQueue =
    queues.filter(_.nextEventTime.nonEmpty)
      .minBy(_.nextEventTime)

  def advanceTimeBy(milliseconds:Int): Int =
    assert(milliseconds > 0)
    advanceTimeTo(clock + milliseconds)

  def advanceTimeTo(newClock:Int): Int =
    assert(newClock > clock)

    while nextEventTime.nonEmpty && nextEventTime.get <= newClock do
      doTheNextTask()

    clock = newClock
    clock

  private def doTheNextTask(): Unit =
    val wf = nextWFEventTime.getOrElse(Int.MaxValue)
    val q = nextQEventTime.getOrElse(Int.MaxValue)
    val now = math.min(wf, q)
    val requests =
      if wf < q then
        val workflow = getNextWorkflow
        nextEventTimeForWorkflows(workflow.name) = workflow.calculateNextEventTime(wf)
        val (group, rList) = workflow.createClientRequests(network = design.network, clock = wf)
        rList
      else
        val queue = getNextQueue
        var rList = queue.removeFinishedRequests(q)
        rList = rList.map(req => {
          val updatedSolution = req.solution.gotoNextStep
          req.copy(solution = updatedSolution)
        })
        rList

    //Move request(s) on to their next step
    for req <- requests do
      if req.solution.currentStep.isEmpty then
        finishedRequests = req +: finishedRequests
      else
        val stCalc = req.solution.currentStep.get.serviceTimeCalculator
        val queue = queues.find(_.serviceTimeCalculator == stCalc)
        assert(queue.nonEmpty)
        queue.get.enqueue(req, now)

  def activeRequests:List[WaitingRequest] =
    var result = List[WaitingRequest]()
    for queue <- queues do
      result = result ++: queue.allWaitingRequests
    result

  private def reset():Unit =
    clock = 0
    metrics = List[PerformanceMetric]()
    queues = design.provideQueues()
    finishedRequests = List[ClientRequest]()
    nextEventTimeForWorkflows = mutable.Map.empty



end Simulator

object Simulator:

end Simulator


