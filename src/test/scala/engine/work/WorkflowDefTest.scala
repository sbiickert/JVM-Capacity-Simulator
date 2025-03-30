package ca.esri.capsim
package engine.work

import engine.work.WorkflowDefStepTest.*
import engine.work.WorkflowDefTest.sampleWebWorkflowDef

import org.scalatest.funsuite.AnyFunSuite

class WorkflowDefTest extends AnyFunSuite:
  test("create") {
    val wf = sampleWebWorkflowDef
    assert(wf.thinkTime == 6)
    assert(wf.parallelServices.length == 2)
    assert(wf.parallelServices.head.steps(3).serviceType == "map")
    assert(wf.parallelServices(1).steps(3).serviceType == "map")
    assert(wf.parallelServices.head.steps(4).serviceType == "dbms")
  }

  test("addChain") {
    val original = sampleWebWorkflowDef
    val overlay = WorkflowChain(name = "Hosted Features", description = "", 
      steps = List(
      sampleBrowserWorkflowService,
      sampleWebWorkflowService,
      samplePortalWorkflowService,
      sampleHostedWorkflowService,
      sampleRelationalWorkflowService
    ), serviceProviders = Set.empty)
    val withOverlay = original.addChain(overlay)
    assert(withOverlay.parallelServices.length == 3)
    assert(withOverlay.parallelServices.head.steps(4).serviceType == "relational")
  }

  test("removeChain") {
    val original = sampleWebWorkflowDef
    val justBasemap = original.removeChain(0)
    assert(justBasemap.parallelServices.length == 1)
    assert(justBasemap.parallelServices.head.steps(4).serviceType == "file")
  }

object WorkflowDefTest:
  val sampleWebDynamicMapChain: WorkflowChain =
    WorkflowChain(name = "Map Image", description = "", 
      steps = List(
      sampleBrowserWorkflowService,
      sampleWebWorkflowService,
      samplePortalWorkflowService,
      sampleDynMapWorkflowService,
      sampleDBMSWorkflowService
    ), serviceProviders = Set.empty)
    
  val sampleWebCachedMapChain: WorkflowChain =
    WorkflowChain(name = "Basemap", description = "", 
      steps = List(
      sampleBrowserWorkflowService,
      sampleWebWorkflowService,
      samplePortalWorkflowService,
      sampleCachedMapWorkflowService,
      sampleFileWorkflowService
    ), serviceProviders = Set.empty)
    
  val sampleProMapChain: WorkflowChain =
    WorkflowChain(name = "Pro Edit", description = "", 
      steps = List(sampleProWorkflowService, sampleDBMSWorkflowService), serviceProviders = Set.empty)
    
  val sampleProBasemap: WorkflowChain =
    WorkflowChain(name = "Basemap", description = "", 
      steps = List(
      sampleProWorkflowService,
      sampleWebWorkflowService,
      samplePortalWorkflowService,
      sampleCachedMapWorkflowService,
      sampleFileWorkflowService
    ), serviceProviders = Set.empty)

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
      WorkflowChain(name = "VDI Pro Edit", description = "", 
        steps = List(sampleVDIWorkflowService, sampleProWorkflowService, sampleDBMSWorkflowService), serviceProviders = Set.empty),
      sampleProBasemap ))
