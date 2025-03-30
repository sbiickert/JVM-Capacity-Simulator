package ca.esri.capsim
package engine.queue

import engine.queue.WaitMode.QUEUEING
import engine.work.ClientRequest

import ca.esri.capsim.engine.network.Connection
import ca.esri.capsim.engine.{Described, PerformanceMetric, QueueMetric, RequestMetric}

import scala.collection.mutable

class MultiQueue(val serviceTimeCalculator: ServiceTimeCalculator,
                 var waitMode: WaitMode,
                 val channelCount: Int):

  private val channels = mutable.ArrayBuffer[Option[WaitingRequest]]()
  private val mainQueue = mutable.Queue[WaitingRequest]()

  def availableChannelCount:Int =
    // First check that the channels are initialized
    while channels.length < channelCount do
      channels.addOne(None)
    channels.count(_.isEmpty)

  private def firstAvailableChannel:Option[Int] =
    if availableChannelCount == 0 then
      None
    else
      val idx = channels.zipWithIndex
        .flatMap(reqIdx => {
        reqIdx._1 match
          case Some(_) => List()
          case None => List(reqIdx._2)
      }).head
      Some(idx)

  private def channelsWithFinishedRequests(clock:Int):List[Int] =
    channels.zipWithIndex
      .flatMap(reqIdx => {
        reqIdx._1 match
          case Some(_) => List(reqIdx._2)
          case None => List()
      })
      .filter(channels(_).get.waitEnd.get <= clock)
      .toList

  def requestCount:Int =
    channelCount - availableChannelCount + mainQueue.length

  def nextEventTime:Option[Int] =
    val processingRequests = channels.flatten
    processingRequests.flatMap(_.waitEnd).minOption

  def removeFinishedRequests(clock: Int): List[(ClientRequest, RequestMetric)] =
    val finishedIndexes = channelsWithFinishedRequests(clock)
    val finishedRequests = mutable.ArrayBuffer[(ClientRequest, RequestMetric)]()
    finishedIndexes.foreach(i => {
      val wr = channels(i).get
      finishedRequests.addOne(
        wr.request,
        RequestMetric(sourceName = name, clock = clock,
          requestName = wr.request.name,
          serviceTime = wr.serviceTime.get,
          queueTime = clock - wr.waitStart - wr.serviceTime.get - wr.latency.getOrElse(0),
          latencyTime = wr.latency.getOrElse(0)))
      if mainQueue.isEmpty then
        channels(i) = None
      else
        channels(i) = Some(mainQueue.dequeue())
    })
    finishedRequests.toList

  def enqueue(req:ClientRequest, clock:Int): Unit =
    assert(req.solution.currentStep.nonEmpty)
    val st = serviceTimeCalculator.calculateServiceTime(req)
    val lat = serviceTimeCalculator.calculateLatency(req)

    if availableChannelCount == 0 then
      mainQueue.enqueue(WaitingRequest(request = req, waitStart = clock,
        serviceTime = st, latency = lat, waitMode = QUEUEING))
    else
      // Find a channel and put it there
      val idx = firstAvailableChannel.get
      val wr = WaitingRequest(request = req, waitStart = clock,
        serviceTime = st, latency = lat, waitMode = waitMode)
      channels(idx) = Some(wr)

  def allWaitingRequests: List[WaitingRequest] =
    mainQueue.toList ++: channels.flatten.toList

  private val name:String = serviceTimeCalculator.asInstanceOf[Described].name

  def getPerformanceMetric(clock: Int): QueueMetric =
    QueueMetric(sourceName = name, clock = clock, channelCount = channelCount, requestCount = allWaitingRequests.length)

end MultiQueue
