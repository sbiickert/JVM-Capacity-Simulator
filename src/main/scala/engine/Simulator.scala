package ca.esri.capsim
package engine

import engine.work.ClientRequest

import ca.esri.capsim.engine.queue.MultiQueue
import ca.esri.capsim.engine.work.ClientRequest

class Simulator(override val name:String, override val description:String) extends Described:
  var design: Design = Design(Design.nextName())
  var clock: Int = 0
  var isRunning: Boolean = false
  var activeRequests:List[ClientRequest] = List[ClientRequest]()
  var finishedRequests:List[ClientRequest] = List[ClientRequest]()
  var queues:List[MultiQueue] = List[MultiQueue]()
  var metrics:List[PerformanceMetric] = List[PerformanceMetric]()
end Simulator

