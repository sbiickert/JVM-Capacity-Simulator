package ca.esri.capsim
package engine.queue

import engine.work.ClientRequest

case class WaitingRequest(request: ClientRequest,
                          waitStart: Int, waitEnd: Int,
                          latency: Int, waitMode: WaitMode)
