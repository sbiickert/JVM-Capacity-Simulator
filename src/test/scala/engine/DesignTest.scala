package ca.esri.capsim
package engine

import engine.compute.{ServiceProviderTest, VirtualHost}
import engine.work.WorkflowTest

import org.scalatest.funsuite.AnyFunSuite

class DesignTest extends AnyFunSuite:
  test("updateWorkflowList") {
    val workflows = List(WorkflowTest.sampleVDIWorkflow, WorkflowTest.sampleWorkstationWorkflow)
    val oldSPs = workflows.head.serviceProviders.toList
    val withRemoved = oldSPs.filterNot(_.name == "Pro")
    val updatedWithRemoved = Design.updateWorkflowsWithUpdatedServiceProviders(
      workflows = workflows, updatedSPs = withRemoved)
    assert(updatedWithRemoved.forall(w => !w.serviceProviders.exists(_.name == "Pro")))
    val withAltered = oldSPs.map(sp => {
      sp.copy(description = "Altered")
    })
    val updatedWithAltered = Design.updateWorkflowsWithUpdatedServiceProviders(
      workflows = workflows, updatedSPs = withAltered)
    assert(updatedWithAltered.forall(w => w.serviceProviders.forall(_.description == "Altered")))
  }
  test("updateServiceProviderList") {
    val serviceProviders = ServiceProviderTest.sampleWebGISServiceProviders
    println(serviceProviders)
    val newNodes = serviceProviders.flatMap(_.nodes)
      .map(n => {
        if n.name == "SQL 001" then
          n.asInstanceOf[VirtualHost].copy(description = "Modified")
        else
          n
      }).toList

    val updatedWithModified = Design.updateServiceProvidersWithUpdatedNodes(
      serviceProviders = serviceProviders.toList, newNodes = newNodes)

    assert(updatedWithModified.forall(sp => {
      !sp.nodes.exists(n => {n.name == "SQL 001" && n.description == "Sample Virtual Machine"})}))
  }
end DesignTest

