package ca.esri.capsim
package engine.compute

enum ComputeArchitecture(ca:String):
  case INTEL extends ComputeArchitecture("INTEL")
  case ARM64 extends ComputeArchitecture("ARM64")
  case OTHER extends ComputeArchitecture("OTHER")
  