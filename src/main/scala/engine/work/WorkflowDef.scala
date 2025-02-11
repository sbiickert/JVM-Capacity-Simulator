package ca.esri.capsim
package engine.work

import engine.Described

type WorkflowChain = List[WorkflowService]

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

end WorkflowDef

object WorkflowDef:

end WorkflowDef