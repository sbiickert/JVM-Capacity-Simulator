package ca.esri.capsim
package engine.work

import ca.esri.capsim.engine.Described

import java.util.UUID

case class ClientRequest(name: String, description: String, requestClock: Int,
                         solution: ClientRequestSolution, metrics: ClientRequestMetrics,
                         groupID: Option[UUID] = None,
                         isFinished: Boolean = false) extends Described:
end ClientRequest