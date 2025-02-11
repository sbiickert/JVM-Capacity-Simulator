package ca.esri.capsim
package engine.work

import engine.queue.ServiceTimeCalculator

case class ClientRequestSolutionStep(serviceTimeCalculator: ServiceTimeCalculator,
                                     isResponse: Boolean, queueName: String, dataSize: Int)
