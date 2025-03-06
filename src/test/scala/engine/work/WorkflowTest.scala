package ca.esri.capsim
package engine.work

import ca.esri.capsim.engine.compute.ServiceProviderTest.*
import ca.esri.capsim.engine.network.ZoneTest
import ca.esri.capsim.engine.work.WorkflowDefTest.*
import org.scalatest.funsuite.AnyFunSuite

class WorkflowTest extends AnyFunSuite:
  test("create") {
    val wfPro = WorkflowTest.sampleWorkstationWorkflow
    assert(wfPro.missingServiceProviders.isEmpty)
    val wfVDI = WorkflowTest.sampleVDIWorkflow
    assert(wfVDI.missingServiceProviders.isEmpty)
    val wfWeb = WorkflowTest.sampleWebWorkflow
    assert(wfWeb.missingServiceProviders.isEmpty)
  }
end WorkflowTest


object WorkflowTest:
  val sampleWorkstationWorkflow: Workflow =
    UserWorkflow("Pro", "Local workstation",
      sampleWorkstationWorkflowDef,
      Set(sampleProServiceProvider,
        sampleWebServiceProvider,
        samplePortalServiceProvider,
        sampleMapServiceProvider,
        sampleDBMSServiceProvider,
        sampleFileServiceProvider),
      userCount = 5, productivity = 10)

  val sampleVDIWorkflow: Workflow =
    UserWorkflow("VDI", "VDI workstation",
      sampleVDIWorkflowDef,
      Set(sampleVDIServiceProvider,
        sampleProServiceProvider,
        sampleWebServiceProvider,
        samplePortalServiceProvider,
        sampleMapServiceProvider,
        sampleDBMSServiceProvider,
        sampleFileServiceProvider),
      userCount = 5, productivity = 10)
    
  val sampleWebWorkflow: Workflow =
    TransactionalWorkflow("Web", "Web Application",
      sampleWebWorkflowDef,
      Set(sampleBrowserServiceProvider,
        sampleWebServiceProvider,
        samplePortalServiceProvider,
        sampleMapServiceProvider,
        sampleDBMSServiceProvider,
        sampleFileServiceProvider),
      tph = 10000)
  