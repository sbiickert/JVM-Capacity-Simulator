package ca.esri.capsim
package engine.queue

import engine.queue.WaitMode.QUEUEING
import engine.work.ClientRequest

import scala.collection.mutable

class MultiQueue(val serviceTimeCalculator: ServiceTimeCalculator,
                 var waitMode: WaitMode,
                 val channelCount: Int):

  private val channels = mutable.ArrayBuffer[Option[WaitingRequest]]()
  private val mainQueue = mutable.Queue[WaitingRequest]()

  private def availableChannelCount:Int =
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
    processingRequests.map(_.waitEnd).min

  def removeFinishedRequests(clock: Int): List[ClientRequest] =
    val finishedIndexes = channelsWithFinishedRequests(clock)
    val finishedRequests = mutable.ArrayBuffer[ClientRequest]()
    finishedIndexes.foreach(i => {
      finishedRequests.addOne(channels(i).get.request)
      channels(i) = None
    })
    finishedRequests.toList

  def enqueue(req:ClientRequest, clock:Int): Unit =
    if availableChannelCount == 0 then
      mainQueue.enqueue(WaitingRequest.create(req, clock, None, None, QUEUEING))
    else
      // Find a channel and put it there
      val idx = firstAvailableChannel.get
      val st = serviceTimeCalculator.calculateServiceTime(req)
      val lat = serviceTimeCalculator.calculateLatency(req)
      val wr = WaitingRequest.create(req, clock, Some(st), Some(lat), waitMode)
      channels(idx) = Some(wr)


end MultiQueue
