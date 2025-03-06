package ca.esri.capsim
package engine.compute

import ca.esri.capsim.engine.compute.BalancingModel.*
import org.scalatest.funsuite.AnyFunSuite

class ServiceTest extends AnyFunSuite:
  test("create") {
    val allServices = ServiceTest.sampleServiceTypes
      .map(t => { (t, ServiceTest.sampleService(t))})
      .toMap

    assert(allServices("map").serviceType == "map")
    assert(allServices("image").balancingModel == ROUNDROBIN)
    assert(allServices("portal").balancingModel == FAILOVER)
    assert(allServices("geoevent").balancingModel == SINGLE)
  }

object ServiceTest:
  val sampleServiceTypes: List[String] =
    List( "pro", "browser",
      "map", "feature", "image", "geocode", "geoevent", "geometry", "gp",
      "network", "scene", "sync", "stream", "ranalytics", "un",
      "custom", "vdi", "web", "portal", "dbms", "relational", "object", "stbds", "file"
    )

  def sampleService(forType: String): Service =
    forType match
      case "pro" => Service("Pro", "Sample Pro", forType, SINGLE)
      case "browser" =>  Service("Browser", "Sample Web Browser", forType, SINGLE)
      case "map" => Service("Map", "Sample Map", forType, ROUNDROBIN)
      case "feature" =>  Service("Feature", "Sample Feature Service", forType, ROUNDROBIN)
      case "image" =>  Service("Image", "Sample Image Service", forType, ROUNDROBIN)
      case "geocode" =>  Service("Geocode", "Sample Geocode", forType, ROUNDROBIN)
      case "geoevent" =>  Service("GeoEvent", "Sample GeoEvent", forType, SINGLE)
      case "geometry" =>  Service("Geometry", "Sample Geometry", forType, ROUNDROBIN)
      case "gp" =>  Service("GP", "Sample Geoprocessing", forType, ROUNDROBIN)
      case "network" =>  Service("Network", "Sample Network Routing", forType, ROUNDROBIN)
      case "scene" =>  Service("Scene", "Sample Scene Service", forType, ROUNDROBIN)
      case "sync" =>  Service("Sync", "Sample Sync Service", forType, ROUNDROBIN)
      case "stream" =>  Service("Stream", "Sample Stream Service", forType, ROUNDROBIN)
      case "ranalytics" =>  Service("Raster Analytics", "Sample Raster Analytics", forType, ROUNDROBIN)
      case "un" =>  Service("UN", "Sample Utility Network", forType, ROUNDROBIN)
      case "custom" =>  Service("Custom", "Sample Custom", forType, SINGLE)
      case "vdi" =>  Service("VDI", "Sample VDI", forType, FAILOVER)
      case "web" =>  Service("Web", "Sample Web Server", forType, ROUNDROBIN)
      case "portal" =>  Service("Portal", "Sample Portal", forType, FAILOVER)
      case "dbms" =>  Service("DBMS", "Sample DBMS", forType, FAILOVER)
      case "relational" =>  Service("Relational", "Sample Relational DS", forType, FAILOVER)
      case "object" =>  Service("Object", "Sample Object DS", forType, FAILOVER)
      case "stbds" =>  Service("Big Data", "Sample ST BDS", forType, ROUNDROBIN)
      case "file" =>  Service("File", "Sample File", forType, FAILOVER)
      case _ => Service("None", "Invalid", forType, SINGLE)