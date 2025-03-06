package ca.esri.capsim
package engine.work

import ca.esri.capsim.engine.work.DataSourceType.*
import ca.esri.capsim.engine.work.WorkflowServiceTest.*
import org.scalatest.funsuite.AnyFunSuite

class WorkflowServiceTest extends AnyFunSuite:
  test("create") {
    val vdi = sampleVDIWorkflowService
    assert(vdi.chatter == 10)
  }

object WorkflowServiceTest:
  val sampleProWorkflowService: WorkflowService =
    WorkflowService("Client Service 001", "Sample Pro Workflow Service", "pro", 831, 500, 1000, 13340, DBMS, 0)
  val sampleBrowserWorkflowService: WorkflowService =
    WorkflowService("Client Service 002", "Sample Web Browser Workflow Service", "browser", 20, 10, 100, 2134, NONE, 20)
  val sampleMobileWorkflowService: WorkflowService =
    WorkflowService("Client Service 003", "Sample Mobile Workflow Service", "mobile", 20, 10, 100, 2134, NONE, 20)
  val sampleVDIWorkflowService: WorkflowService =
    WorkflowService("VDI Service 001", "Sample VDI Workflow Service", "vdi", 831, 10, 100, 3691, DBMS, 0)

  val sampleWebWorkflowService: WorkflowService =
    WorkflowService("Web Service 001", "Sample Web Workflow Service", "web", 18, 10, 100, 2134, NONE, 0)
  val samplePortalWorkflowService: WorkflowService =
    WorkflowService("Portal Service 001", "Sample Portal Workflow Service", "portal", 19, 10, 100, 2134, FILE, 0)
  val sampleDynMapWorkflowService: WorkflowService =
    WorkflowService("Map Service 001", "Sample Map Workflow Service", "map", 141, 10, 100, 2134, DBMS, 0)
  val sampleCachedMapWorkflowService: WorkflowService =
    WorkflowService("Map Service 002", "Sample Cached Map Workflow Service", "map", 1, 10, 100, 2134, FILE, 100)
  val sampleHostedWorkflowService: WorkflowService =
    WorkflowService("Hosted Service 001", "Sample Hosted Workflow Service", "feature", 70, 10, 100, 4000, RELATIONAL, 0)
  val sampleDBMSWorkflowService: WorkflowService =
    WorkflowService("DBMS Service 001", "Sample DBMS Workflow Service", "dbms", 24, 500, 1000, 13340, FILE, 75)
  val sampleFileWorkflowService: WorkflowService =
    WorkflowService("File Service 001", "Sample File Workflow Service", "file", 24, 500, 1000, 13340, FILE, 0)
  val sampleRelationalWorkflowService: WorkflowService =
    WorkflowService("Relational Service 001", "Sample Relational DS Workflow Service", "relational", 24, 10, 1000, 13340, FILE, 0)
