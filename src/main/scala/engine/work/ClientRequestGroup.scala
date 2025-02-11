package ca.esri.capsim
package engine.work

import engine.Described

case class ClientRequestGroup(name: String, description: String, requestTime:Int, workflow: Workflow) extends Described:

end ClientRequestGroup

