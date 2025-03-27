package ca.esri.capsim
package engine.queue

import engine.work.ClientRequest

import ca.esri.capsim.engine.queue.WaitMode.QUEUEING

case class WaitingRequest(request: ClientRequest,
                          waitStart: Int,
                          serviceTime: Option[Int],
                          latency: Option[Int],
                          waitMode: WaitMode):

  val waitEnd: Option[Int] =
    if waitMode == QUEUEING || serviceTime.isEmpty then
      None
    else
      Some(waitStart + serviceTime.get + latency.getOrElse(0))
end WaitingRequest

object WaitingRequest:
 
end WaitingRequest
