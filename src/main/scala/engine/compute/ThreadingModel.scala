package ca.esri.capsim
package engine.compute

enum ThreadingModel(tm:String):
  case PHYSICAL extends ThreadingModel("PHYSICAL")
  case HYPERTHREADED extends ThreadingModel("HT")
  
  val factor: Double =
    this match
      case HYPERTHREADED => 0.51 // Ensure rounding up
      case _ => 1.0