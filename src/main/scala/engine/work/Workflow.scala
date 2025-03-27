package ca.esri.capsim
package engine.work

import engine.Described
import engine.compute.ServiceProvider
import engine.network.{Connection, Zone}

import java.util.Random

sealed trait Workflow extends Described:
  override val name: String
  override val description: String
  val workflowDef: WorkflowDef
  val defaultServiceProviders: Set[ServiceProvider]

  def isValid:Boolean =
    val chainsValid = workflowDef.parallelServices.forall(_.isValid)
    transactionRate >= 0 && chainsValid

  def missingServiceProviders: List[String] =
    val allRequired = workflowDef.allRequiredServiceTypes
    val configured = defaultServiceProviders.map(sp => sp.service.serviceType).toSet
    (allRequired -- configured).toList

  def applyDefaultServiceProviders: Workflow

  def updateServiceProviders(index: Int, serviceProviders: Set[ServiceProvider]): Workflow
    
  def createClientRequests(network:List[Connection], clock:Int): (ClientRequestGroup, List[ClientRequest]) =
    val group = ClientRequestGroup(clock, this)
    val requests = workflowDef.parallelServices.map(chain => {
      val solution = ClientRequestSolution.create(chain, defaultServiceProviders, network)
      ClientRequest(ClientRequest.nextName, "", clock, solution, group.id, false)
    })
    (group, requests)

  def transactionRate:Int

  def calculateNextEventTime(clock:Int):Int =
    // transactionRate is transactions per hour
    // time between events (ms) is
    val msPerEvent = 3600000.0 / transactionRate.toDouble
    val r = Random(System.currentTimeMillis())
    // Gaussian (normal) distribution with std dev of 25% of the mean
    val randomized = r.nextGaussian(msPerEvent, msPerEvent * 0.25).round.toInt
    clock + randomized

case class UserWorkflow(name:String, description:String,
                        workflowDef: WorkflowDef,
                        defaultServiceProviders: Set[ServiceProvider],
                        userCount: Int, productivity: Int) extends Workflow:

  override def transactionRate: Int = userCount * productivity * 60

  override def applyDefaultServiceProviders: Workflow =
    val updatedChains = workflowDef.parallelServices.map(_.copy(serviceProviders = defaultServiceProviders))
    val wfd = workflowDef.copy(parallelServices = updatedChains)
    this.copy(workflowDef = wfd)

  override def updateServiceProviders(index: Int, serviceProviders: Set[ServiceProvider]): Workflow =
    val wfd = workflowDef.updateServiceProviders(index, serviceProviders)
    this.copy(workflowDef = wfd)


end UserWorkflow


case class TransactionalWorkflow(name:String, description:String,
                                 workflowDef: WorkflowDef,
                                 defaultServiceProviders: Set[ServiceProvider],
                                 tph: Int) extends Workflow:

  override def transactionRate: Int = tph

  override def applyDefaultServiceProviders: Workflow =
    val updatedChains = workflowDef.parallelServices.map(_.copy(serviceProviders = defaultServiceProviders))
    val wfd = workflowDef.copy(parallelServices = updatedChains)
    this.copy(workflowDef = wfd)

  override def updateServiceProviders(index: Int, serviceProviders: Set[ServiceProvider]): Workflow =
    val wfd = workflowDef.updateServiceProviders(index, serviceProviders)
    this.copy(workflowDef = wfd)

end TransactionalWorkflow

