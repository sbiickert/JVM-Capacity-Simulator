package ca.esri.capsim
package engine.work

import engine.Described
import engine.compute.ServiceProvider

import scala.collection.mutable

case class WorkflowDef(name: String, description: String,
                       thinkTime: Int, parallelChains:List[WorkflowChain]) extends Described:


  def addChain(chain: WorkflowChain): WorkflowDef =
    val chains = chain +: parallelChains
    this.copy(parallelChains = chains)

  def removeChain(index: Int): WorkflowDef =
    val r = parallelChains.indices
    val chains = r.filter(_ != index)
      .map(i => parallelChains(i))
      .toList
    this.copy(parallelChains = chains)
  
  def allRequiredServiceTypes: Set[String] = 
    parallelChains.flatMap(_.allRequiredServiceTypes)
      .toSet

  def updateServiceProviders(index: Int, serviceProviders: Set[ServiceProvider]): WorkflowDef =
    assert(index >= 0 && index < parallelChains.size)
    val chains = mutable.ArrayBuffer.from(parallelChains)
    chains.update(index, chains(index).copy(serviceProviders = serviceProviders))
    this.copy(parallelChains = chains.toList)

end WorkflowDef

object WorkflowDef:

end WorkflowDef