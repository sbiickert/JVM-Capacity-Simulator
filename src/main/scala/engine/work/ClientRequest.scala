package ca.esri.capsim
package engine.work

import ca.esri.capsim.engine.Described

import java.util.UUID

case class ClientRequest(name: String, description: String, requestClock: Int,
                         solution: ClientRequestSolution,
                         groupID: Int,
                         isFinished: Boolean = false) extends Described:
end ClientRequest

object ClientRequest:
  private var _nextID:Int = 0
  def nextID:Int =
    _nextID += 1
    nextID
    
  def nextName:String = s"CR-$nextID"