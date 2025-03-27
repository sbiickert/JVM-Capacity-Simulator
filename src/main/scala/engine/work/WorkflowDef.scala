package ca.esri.capsim
package engine.work

import engine.Described
import engine.compute.ServiceProvider

import scala.collection.mutable

case class WorkflowDef(name: String, description: String,
                      thinkTime: Int, parallelServices:List[WorkflowChain]) extends Described:


  def addChain(chain: WorkflowChain): WorkflowDef =
    val chains = chain +: parallelServices
    this.copy(parallelServices = chains)

  def removeChain(index: Int): WorkflowDef =
    val r = parallelServices.indices
    val chains = r.filter(_ != index)
      .map(i => parallelServices(i))
      .toList
    this.copy(parallelServices = chains)
  
  def allRequiredServiceTypes: Set[String] = 
    parallelServices.flatMap(_.allRequiredServiceTypes)
      .toSet

  def updateServiceProviders(index: Int, serviceProviders: Set[ServiceProvider]): WorkflowDef =
    assert(index >= 0 && index < parallelServices.size)
    val chains = mutable.ArrayBuffer.from(parallelServices)
    chains.update(index, chains(index).copy(serviceProviders = serviceProviders))
    this.copy(parallelServices = chains.toList)

end WorkflowDef

object WorkflowDef:

end WorkflowDef