package ca.esri.capsim
package engine

import java.util.UUID

case class Design(name: String, description: String) extends Described:

end Design

object Design:
  var _nextID: Int = 0
  def nextID(): Int =
    _nextID += 1
    _nextID
    
    
  def nextName(): String = s"Design $_nextID"
end Design

  
