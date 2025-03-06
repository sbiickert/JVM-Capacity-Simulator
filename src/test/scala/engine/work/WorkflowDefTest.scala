package ca.esri.capsim
package engine.work

import ca.esri.capsim.engine.work.WorkflowServiceTest.*
import ca.esri.capsim.engine.work.WorkflowChain
import ca.esri.capsim.engine.work.WorkflowDefTest.{sampleProBasemap, sampleProMapChain, sampleWebWorkflowDef}
import org.scalatest.funsuite.AnyFunSuite

class WorkflowDefTest extends AnyFunSuite:
  test("create") {
    val wf = sampleWebWorkflowDef
    assert(wf.thinkTime == 3)
    assert(wf.parallelServices.length == 2)
    assert(wf.parallelServices(0)(3).serviceType == "map")
    assert(wf.parallelServices(1)(3).serviceType == "map")
    assert(wf.parallelServices(0)(4).serviceType == "dbms")
  }

  test("addChain") {
    val original = sampleWebWorkflowDef
    val overlay = List(
      sampleBrowserWorkflowService,
      sampleWebWorkflowService,
      samplePortalWorkflowService,
      sampleHostedWorkflowService,
      sampleRelationalWorkflowService
    )
    val withOverlay = original.addChain(overlay)
    assert(withOverlay.parallelServices.length == 3)
    assert(withOverlay.parallelServices.head(4).serviceType == "relational")
  }

  test("removeChain") {
    val original = sampleWebWorkflowDef
    val justBasemap = original.removeChain(0)
    assert(justBasemap.parallelServices.length == 1)
    assert(justBasemap.parallelServices.head(4).serviceType == "file")
  }

object WorkflowDefTest:
  val sampleWebDynamicMapChain: WorkflowChain =
    List(
      sampleBrowserWorkflowService,
      sampleWebWorkflowService,
      samplePortalWorkflowService,
      sampleDynMapWorkflowService,
      sampleDBMSWorkflowService
    )
    
  val sampleWebCachedMapChain: WorkflowChain =
    List(
      sampleBrowserWorkflowService,
      sampleWebWorkflowService,
      samplePortalWorkflowService,
      sampleCachedMapWorkflowService,
      sampleFileWorkflowService
    )
    
  val sampleProMapChain: WorkflowChain =
    List(sampleProWorkflowService, sampleDBMSWorkflowService)
    
  val sampleProBasemap: WorkflowChain =
    List(
      sampleProWorkflowService,
      sampleWebWorkflowService,
      samplePortalWorkflowService,
      sampleCachedMapWorkflowService,
      sampleFileWorkflowService
    )

  val sampleWebWorkflowDef: WorkflowDef =
    WorkflowDef("Workflow Def 001", "Sample Web Map", 6, List(
      sampleWebDynamicMapChain, sampleWebCachedMapChain ))

  val sampleMobileWorkflowDef: WorkflowDef =
    WorkflowDef("Workflow Def 003", "Sample Mobile Map", 10, List(
      sampleWebDynamicMapChain, sampleWebCachedMapChain ))

  val sampleWorkstationWorkflowDef: WorkflowDef =
    WorkflowDef("Workflow Def 002", "Sample Pro Work", 3, List(
      sampleProMapChain, sampleProBasemap ))

  val sampleVDIWorkflowDef: WorkflowDef =
    WorkflowDef("Workflow Def 003", "Sample VDI Pro Work", 3, List(
      List(sampleVDIWorkflowService, sampleProWorkflowService, sampleDBMSWorkflowService),
      sampleProBasemap ))
