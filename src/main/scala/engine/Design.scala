package ca.esri.capsim
package engine

import ca.esri.capsim.engine.compute.{ComputeNode, ServiceProvider}
import ca.esri.capsim.engine.network.*

import java.util.UUID

case class Design(name: String, description: String = "",
                  zones:List[Zone] = List(), 
                  network:List[Connection] = List(),
                  serviceProviders:List[ServiceProvider] = List(),
                  computeNodes:List[ComputeNode] = List()) extends Described:

end Design

object Design:
  var _nextID: Int = 0
  def nextID(): Int =
    _nextID += 1
    _nextID
  
  def nextName(): String = s"Design $_nextID"
end Design

  
