package ca.esri.capsim
package app.io

import engine.compute.{ComputeArchitecture, HardwareDef}

import ca.esri.capsim.engine.work.{DataSourceType, WorkflowChain, WorkflowDef, WorkflowDefStep}
import ujson.Value

import java.time.Instant
import scala.io.Source

class DefLibManager:

end DefLibManager

object DefLibManager:
  private val sep = System.getProperty("file.separator")
  private def dataFolder: String =
    val components = List(System.getProperty("user.dir"), "data")
    components.mkString(sep)
  private def getFullPath(fileName:String): String =
    List(dataFolder, fileName).mkString(sep)

  private def readFileContent(fileName:String):String =
    val bufferedSource = Source.fromFile(getFullPath(fileName))
    bufferedSource.mkString

  
  // Hardware

  def hardwareDefLib: HardwareDefLib =
    val jsonString = readFileContent("hardwaredef.json")
    val json = ujson.read(jsonString)
    val version = json("version").num.toInt
    parseHW(version, json)

  private def parseHW(version:Int, json:Value): HardwareDefLib =
    version match
      case 1 => parseHWv1(json = json)
      case _ => HardwareDefLib.empty

  private def parseHWv1(json:Value): HardwareDefLib =
    val timestamp = Instant.parse(json("updatedOn").str)
    val hwList = json("hardware").arr
      .map(v => {
        val arch = v("arch").str match
          case "Intel" => ComputeArchitecture.INTEL
          case "aarm64" => ComputeArchitecture.ARM64
          case "armv7" => ComputeArchitecture.ARM64
          case _ => ComputeArchitecture.OTHER
        HardwareDef(
          processor = v("processor").str,
          cores = v("cores").num.toInt,
          specIntRate2017 = v("spec").num,
          architecture = arch)
      })
    val hwMap = hwList.map(hw => {hw.processor + " (" + hw.cores + ")" -> hw}).toMap

//    val dup = hwList
//      .map(hw => {hw.processor + " (" + hw.cores + ")"})
//      .groupBy(identity).collect { case (x,ys) if ys.lengthCompare(1) > 0 => x }

    HardwareDefLib(timestamp, hwMap)


  // Workflows
    
  def workflowDefLib: WorkflowDefLib =
    val jsonString = readFileContent("workflowdef.json")
    val json = ujson.read(jsonString)
    val version = json("version").num.toInt
    parseWF(version, json)

  private def parseWF(version: Int, json:Value): WorkflowDefLib =
    version match
      case 1 => parseWFv1(json)
      case _ => WorkflowDefLib.empty

  private def parseWFv1(json: Value): WorkflowDefLib =
    val timestamp = Instant.parse(json("updatedOn").str)
    val baseline = json("baselineSPECPerCore").num
    val stepsList = json("steps").arr
      .map(v => {
        val ds = v("ds").str match
          case "DBMS" => DataSourceType.DBMS
          case "FILE" => DataSourceType.FILE
          case "RELATIONAL" => DataSourceType.RELATIONAL
          case "NONE" => DataSourceType.NONE
          case "BIG" => DataSourceType.BIG
          case "OBJECT" => DataSourceType.OBJECT
          case _ => DataSourceType.OTHER
        WorkflowDefStep(
          name = v("name").str, 
          description = v("desc").str, 
          serviceType = v("type").str, 
          serviceTime = v("st").num.toInt,
          chatter = v("chatter").num.toInt, 
          requestSizeKB = v("reqKB").num.toInt, 
          responseSizeKB = v("respKB").num.toInt, 
          dataSourceType = ds,
          cachePercent = v("cache").num.toInt)
      })
    val stepsMap = stepsList.map(s => {s.name -> s}).toMap
    val chainsList = json("chains").arr
      .map(v => {
        val steps = v("steps").arr
          .map(name => stepsMap(name.str))
        WorkflowChain(
          name = v("name").str,
          description = v("desc").str,
          steps = steps.toList,
          serviceProviders = Set.empty)
      })
    val chainsMap = chainsList.map(c => {c.name -> c}).toMap
    val workflowsList = json("workflows").arr
      .map(v => {
        val chains = v("chains").arr
          .map(name => chainsMap(name.str))
        WorkflowDef(name = v("name").str,
          description = v("desc").str,
          thinkTime = v("think").num.toInt,
          parallelChains = chains.toList)
      })
    val workflowsMap = workflowsList.map(w => {w.name -> w}).toMap
    
    WorkflowDefLib(date = timestamp, baselineSpecPerCore = baseline, steps = stepsMap, chains = chainsMap, workflows = workflowsMap)
end DefLibManager

