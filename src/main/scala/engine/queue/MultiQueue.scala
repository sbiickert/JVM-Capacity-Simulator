package ca.esri.capsim
package engine.queue

import ca.esri.capsim.engine.Described

import scala.collection.mutable.ArrayBuffer

class MultiQueue(val serviceTimeCalculator: ServiceTimeCalculator,
                 var waitMode: WaitMode,
                 val channelCount: Int):

  private val channels = ArrayBuffer[Option[WaitingRequest]]()
  private val waitingRequests = ArrayBuffer[WaitingRequest]()

end MultiQueue
