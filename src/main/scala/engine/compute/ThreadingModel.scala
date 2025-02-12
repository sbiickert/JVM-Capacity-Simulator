package ca.esri.capsim
package engine.compute

enum ThreadingModel(tm:String):
  case PHYSICAL extends ThreadingModel("PHYSICAL")
  case HYPERTHREADED extends ThreadingModel("HT")