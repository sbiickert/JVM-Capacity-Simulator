package ca.esri.capsim
package engine

import engine.compute.*
import engine.compute.ThreadingModel.HYPERTHREADED
import engine.network.ZoneType.{EDGE, INTERNET, SECURED}
import engine.network.{Connection, Zone}
import engine.work.*
import engine.work.WorkflowDefTest.{sampleMobileWorkflowDef, sampleWebWorkflowDef}

import org.scalatest.funsuite.AnyFunSuite

class DesignTest extends AnyFunSuite:
  test("updateWorkflowList") {
    val workflows = List(WorkflowTest.sampleVDIWorkflow, WorkflowTest.sampleWorkstationWorkflow)
    val oldSPs = workflows.head.defaultServiceProviders.toList
    val withRemoved = oldSPs.filterNot(_.name == "Pro")
    val updatedWithRemoved = Design.updateWorkflowsWithUpdatedServiceProviders(
      workflows = workflows, updatedSPs = withRemoved)
    assert(updatedWithRemoved.forall(w => !w.defaultServiceProviders.exists(_.name == "Pro")))
    val withAltered = oldSPs.map(sp => {
      sp.copy(description = "Altered")
    })
    val updatedWithAltered = Design.updateWorkflowsWithUpdatedServiceProviders(
      workflows = workflows, updatedSPs = withAltered)
    assert(updatedWithAltered.forall(w => w.defaultServiceProviders.forall(_.description == "Altered")))
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

  test("create") {
    val design = DesignTest.sampleDesign
    assert(design.zones.length == 4)
    assert(design.network.length == 10)
    assert(design.getZone("DMZ").nonEmpty)
    assert(design.getZone("DMZ").get.localConnection(design.network).nonEmpty)
    assert(design.getZone("DMZ").get.exitConnections(design.network).length == 2)
    assert(design.getZone("AGOL").nonEmpty)
    assert(design.getZone("AGOL").get.exitConnections(design.network).head.bandwidth == 1000)

    assert(design.computeNodes.length == 7)

    assert(design.serviceProviders.length == 13)

    assert(design.isValid)
  }
end DesignTest

object DesignTest:
  val sampleDesign:Design =
    var d = Design("Sample 01", "For testing")

    // Start with laying out Zones and Connections
    d = d.addZone(zone = Zone("LAN", "Local Network", SECURED), localBandwidth = 1000, localLatency = 0)
    d = d.addZone(zone = Zone("DMZ", "Edge Network", EDGE), localBandwidth = 1000, localLatency = 0)
    d = d.addZone(zone = Zone("Internet", "Internet", INTERNET), localBandwidth = 10000, localLatency = 10)
    d = d.addZone(zone = Zone("AGOL", "SaaS on AWS", EDGE), localBandwidth = 10000, localLatency = 0)

    d = d.addConnection(Connection(d.getZone("LAN").get, d.getZone("DMZ").get, 1000, 0), true)
    d = d.addConnection(Connection(d.getZone("DMZ").get, d.getZone("Internet").get, 300, 8), true)
    d = d.addConnection(Connection(d.getZone("Internet").get, d.getZone("AGOL").get, 1000, 10), true)

    // Then add physical hosts
    var localHost = PhysicalHost("SRV01", "Local Server",
      HardwareDefTest.sampleServerHWDef,
      d.getZone("LAN").get, List())

    val agolHost = PhysicalHost("AGOL01", "AWS Server",
      HardwareDefTest.sampleServerHWDef,
      d.getZone("AGOL").get, List())

    d = d.addHost(localHost)
    d = d.addHost(agolHost)

    // Virtual hosts
    val localVHost1 = VirtualHost(name = "VWEB01",
      description = "", hardwareDef = localHost.hardwareDef, zone = localHost.zone,
      vCPUs = 4, memoryGB = 16, threadingModel = HYPERTHREADED)
    localHost = localHost.addVHost(localVHost1)
    val localVHost2 = localVHost1.copy(name = "VGIS01")
    localHost = localHost.addVHost(localVHost2)
    val localVHost3 = localVHost1.copy(name = "VDB01")
    localHost = localHost.addVHost(localVHost3)

    d = d.replaceHost(d.getComputeNode(localHost.name).get.asInstanceOf[PhysicalHost],
      localHost)

    // Clients
    val localClient = Client(name = "PC01", description = "Client PC",
      HardwareDefTest.sampleClientHWDef, zone = d.getZone("LAN").get)
    d = d.addClient(localClient)

    val mobileClient = Client(name = "Phone01", description = "iPhone",
      hardwareDef = HardwareDefTest.sampleMobileHWDef, zone = d.getZone("Internet").get)
    d = d.addClient(mobileClient)

    // Services
    ServiceTest.sampleServiceTypes.foreach(st => {
      d = d.addService(ServiceTest.sampleService(st))
    })

    // Service Providers
    var spSet = Set[ServiceProvider]()
    val spBrowser = ServiceProvider("Web Browser", "Web Browser", d.services("browser"), Some(mobileClient), Set(mobileClient))
    d = d.addServiceProvider(spBrowser)
    spSet = spSet + spBrowser
    val spPro = ServiceProvider("Pro", "PC Workstation", d.services("pro"), Some(localClient), Set(localClient))
    d = d.addServiceProvider(spPro)
    spSet = spSet + spPro
    val spWeb = ServiceProvider("IIS", "Web Server", d.services("web"), Some(localVHost1), Set(localVHost1))
    d = d.addServiceProvider(spWeb)
    spSet = spSet + spWeb
    val spPortal = ServiceProvider("Portal", "Portal for ArcGIS", d.services("portal"), Some(localVHost1), Set(localVHost1))
    d = d.addServiceProvider(spPortal)
    spSet = spSet + spPortal
    val spGIS = ServiceProvider("GIS", "Map Server", d.services("map"), None, Set(localVHost2))
    d = d.addServiceProvider(spGIS)
    spSet = spSet + spGIS
    val spDB = ServiceProvider("SQL", "Geodatabase", d.services("dbms"), Some(localVHost3), Set(localVHost3))
    d = d.addServiceProvider(spDB)
    spSet = spSet + spDB
    val spFile = ServiceProvider("File", "File Server", d.services("file"), Some(localVHost2), Set(localVHost2))
    d = d.addServiceProvider(spFile)
    spSet = spSet + spFile

    var spSetAGO = Set[ServiceProvider]()
    spSetAGO = spSetAGO + spBrowser
    val agoWeb = ServiceProvider("AGO Edge", "Web Server", d.services("web"), None, Set(agolHost))
    d = d.addServiceProvider(agoWeb)
    spSetAGO = spSetAGO + agoWeb
    val agoPortal = ServiceProvider("AGO Portal", "Portal for ArcGIS", d.services("portal"), Some(agolHost), Set(agolHost))
    d = d.addServiceProvider(agoPortal)
    spSetAGO = spSetAGO + agoPortal
    val agoGIS = ServiceProvider("AGO GIS", "Feature Server", d.services("feature"), None, Set(agolHost))
    d = d.addServiceProvider(agoGIS)
    spSetAGO = spSetAGO + agoGIS
    val agoBaseMap = ServiceProvider("AGO Basemap", "Map Server", d.services("map"), None, Set(agolHost))
    d = d.addServiceProvider(agoBaseMap)
    spSetAGO = spSetAGO + agoBaseMap
    val agoDB = ServiceProvider("AGO SQL", "Hosted Datastore", d.services("dbms"), Some(agolHost), Set(agolHost))
    d = d.addServiceProvider(agoDB)
    spSetAGO = spSetAGO + agoDB
    val agoFile = ServiceProvider("AGO File", "File Server", d.services("file"), Some(agolHost), Set(agolHost))
    d = d.addServiceProvider(agoFile)
    spSetAGO = spSetAGO + agoFile

    // Workflow Definitions
    var webWF = TransactionalWorkflow("Web", "Web Application",
      sampleWebWorkflowDef, spSet, 1000).applyDefaultServiceProviders
      webWF = webWF.updateServiceProviders(1, spSetAGO) // dynamic map from on prem, basemap from AGO
    d = d.addWorkflow(webWF)
    
    // Workflows
    val mobileMapWF = UserWorkflow("Field Maps", "Mobile Data Collection",
      sampleMobileWorkflowDef, spSetAGO, userCount = 15, productivity = 6).applyDefaultServiceProviders
    d = d.addWorkflow(mobileMapWF)
    
//    println(d)
    d
end DesignTest
