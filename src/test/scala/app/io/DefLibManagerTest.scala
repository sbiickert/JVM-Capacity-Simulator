package ca.esri.capsim
package app.io

import org.scalatest.PrivateMethodTester
import org.scalatest.funsuite.AnyFunSuite

class DefLibManagerTest extends AnyFunSuite, PrivateMethodTester:
  test("read") {
    val privateDataFolder = PrivateMethod.apply[String](Symbol("dataFolder"))
    val dataFolder = DefLibManager invokePrivate privateDataFolder()
    assert(dataFolder.endsWith("JVM-Capacity-Simulator/data"))
    val privateGetFullPath = PrivateMethod.apply[String](Symbol("getFullPath"))
    val hwFullPath = DefLibManager invokePrivate privateGetFullPath("test.txt")
    assert(hwFullPath.endsWith("JVM-Capacity-Simulator/data/test.txt"))
  }

  test("read hardware") {
    val privateReadFileContent = PrivateMethod.apply[String](Symbol("readFileContent"))
    val jsonContent = DefLibManager invokePrivate privateReadFileContent("hardwaredef.json")
    assert(jsonContent.contains("\"hardware\": ["))
    val hwDefLib = DefLibManager.hardwareDefLib
    assert(hwDefLib.date.toString == "2024-04-04T16:14:04Z")
    assert(hwDefLib.hardware.size == 436)
  }
  
  test("read workflows") {
    val privateReadFileContent = PrivateMethod.apply[String](Symbol("readFileContent"))
    val jsonContent = DefLibManager invokePrivate privateReadFileContent("workflowdef.json")
    assert(jsonContent.contains("\"workflows\": ["))
    val wfDefLib = DefLibManager.workflowDefLib
    assert(wfDefLib.date.toString == "2024-04-04T16:14:04Z")

  }