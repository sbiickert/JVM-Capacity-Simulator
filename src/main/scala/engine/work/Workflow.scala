package ca.esri.capsim
package engine.work

import engine.Described

import ca.esri.capsim.engine.compute.ServiceProvider
import ca.esri.capsim.engine.network.Zone

sealed trait Workflow extends Described:
  override val name: String
  override val description: String
  val workflowDef: WorkflowDef
  val zone: Zone
  val serviceProviders: Set[ServiceProvider]
  
  def missingServiceProviders: List[String] =
    val allRequired = workflowDef.allRequiredServiceTypes
    val configured = serviceProviders.map(sp => sp.service.serviceType).toSet
    (allRequired -- configured).toList
    
  def createClientRequests(clock:Int): (ClientRequestGroup, List[ClientRequest]) =
    val group = ClientRequestGroup(clock, this)
    val requests = workflowDef.parallelServices.map(serv => {
      ClientRequest(ClientRequest.nextName, "", clock, solution, metrics, group.id, false)
    })
    (group, requests)
    

case class UserWorkflow(name:String, description:String,
                        workflowDef: WorkflowDef, zone: Zone,
                        serviceProviders: Set[ServiceProvider],
                        userCount: Int, productivity: Int) extends Workflow:

end UserWorkflow

case class TransactionalWorkflow(name:String, description:String,
                                 workflowDef: WorkflowDef, zone: Zone,
                                 serviceProviders: Set[ServiceProvider],
                                 tph: Int) extends Workflow:

end TransactionalWorkflow

