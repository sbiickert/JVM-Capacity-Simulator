package ca.esri.capsim
package engine.work

case class ClientRequestGroup(requestClock:Int, workflow: Workflow):
  val id = ClientRequestGroup.nextID
end ClientRequestGroup

object ClientRequestGroup:
  private var _nextID: Int = 0
  def nextID:Int =
    _nextID += 1
    _nextID