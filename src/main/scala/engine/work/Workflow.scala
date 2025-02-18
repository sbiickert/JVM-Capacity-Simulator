package ca.esri.capsim
package engine.work

import engine.Described
import engine.compute.ServiceProvider
import engine.network.{Connection, Zone}

sealed trait Workflow extends Described:
  override val name: String
  override val description: String
  val workflowDef: WorkflowDef
  val serviceProviders: Set[ServiceProvider]
  
  def missingServiceProviders: List[String] =
    val allRequired = workflowDef.allRequiredServiceTypes
    val configured = serviceProviders.map(sp => sp.service.serviceType).toSet
    (allRequired -- configured).toList
    
  def createClientRequests(network:List[Connection], clock:Int): (ClientRequestGroup, List[ClientRequest]) =
    val group = ClientRequestGroup(clock, this)
    val requests = workflowDef.parallelServices.map(chain => {
      val solution = ClientRequestSolution.create(chain, serviceProviders, network)
      ClientRequest(ClientRequest.nextName, "", clock, solution, group.id, false)
    })
    (group, requests)
    

case class UserWorkflow(name:String, description:String,
                        workflowDef: WorkflowDef,
                        serviceProviders: Set[ServiceProvider],
                        userCount: Int, productivity: Int) extends Workflow:

end UserWorkflow

case class TransactionalWorkflow(name:String, description:String,
                                 workflowDef: WorkflowDef,
                                 serviceProviders: Set[ServiceProvider],
                                 tph: Int) extends Workflow:

end TransactionalWorkflow

