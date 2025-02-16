package ca.esri.capsim
package engine.queue

import engine.work.ClientRequest

import ca.esri.capsim.engine.queue.WaitMode.QUEUEING

case class WaitingRequest(request: ClientRequest,
                          waitStart: Int, waitEnd: Option[Int],
                          latency: Int, waitMode: WaitMode):

end WaitingRequest

object WaitingRequest:
  def create(request: ClientRequest, waitStart:Int, serviceTime:Option[Int],
             latency:Option[Int], waitMode: WaitMode):WaitingRequest =
    val wEnd = if waitMode == QUEUEING || serviceTime.isEmpty then
      None
    else
      Some(waitStart + serviceTime.get)
      
    val lat = if latency.isEmpty then
      0
    else
      latency.get
    
    WaitingRequest(
      request = request,
      waitStart = waitStart,
      waitEnd = wEnd,
      latency = lat,
      waitMode = waitMode
    )
end WaitingRequest
