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
  }
end WorkflowTest


object WorkflowTest:
  val sampleWorkstationWorkflow: Workflow =
    UserWorkflow("Pro", "Local workstation",
      sampleWorkstationWorkflowDef,
      ZoneTest.sampleIntranetZone,
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
      ZoneTest.sampleIntranetZone,
      Set(sampleVDIServiceProvider,
        sampleProServiceProvider,
        sampleWebServiceProvider,
        samplePortalServiceProvider,
        sampleMapServiceProvider,
        sampleDBMSServiceProvider,
        sampleFileServiceProvider),
      userCount = 5, productivity = 10)