package ca.esri.capsim
package engine.work

import engine.queue.ServiceTimeCalculator

case class ClientRequestSolutionStep(serviceTimeCalculator: ServiceTimeCalculator,
                                     isResponse: Boolean, 
                                     dataSize: Int, 
                                     chatter:Int,
                                     serviceTime:Int)
