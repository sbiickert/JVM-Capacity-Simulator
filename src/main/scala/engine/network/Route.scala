package ca.esri.capsim
package engine.network

case class Route(connections: List[Connection]):
  
end Route

object Route:
  def zoneIsSource(zone: Zone, connections:List[Connection]): Boolean =
    connections.exists(_.sourceZone == zone)
    
  def zoneIsDestination(zone: Zone, connections:List[Connection]): Boolean =
    connections.exists(_.destinationZone == zone)
    
  def findRoute(fromZone: Zone, toZone: Zone, connections:List[Connection]): Option[Route] =
    if !zoneIsSource(fromZone, connections) || !zoneIsDestination(toZone, connections) then None
    else if fromZone.localConnection(connections).isEmpty then None
    else
      // Depth-first search
      val path = findRouteDFS(fromZone, toZone, Set(fromZone),
                              List(fromZone.localConnection(connections).get),
                              connections)
      if path.head.destinationZone == toZone then
        Some(Route(path.reverse))
      else None

  private def findRouteDFS(fromZone: Zone, toZone: Zone,
                           visited:Set[Zone],
                           path:List[Connection],
                           connections:List[Connection]): List[Connection] =
    if fromZone == toZone then
      path
    else
      val exits = fromZone.exitConnections(connections)
      // Path not starting with toZone is signal that no path was found
      var results = exits.filter(exit => !visited.contains(exit.destinationZone))
        .map(exit => {
          val extendedPath = exit +: path
          findRouteDFS(exit.destinationZone, toZone,
            visited + exit.destinationZone,
            extendedPath, connections) })
        .filter(result => {result.head.destinationZone == toZone})
        .sortBy({_.length})
      results.head


end Route

