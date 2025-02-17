package ca.esri.capsim
package engine.compute

import ca.esri.capsim.engine.Described

case class HardwareDef(name: String, description:String,
                       processor: String, cores: Int, specIntRate2017: Int,
                       architecture: ComputeArchitecture) extends Described:
end HardwareDef

object HardwareDef:
  val BASELINE_SPEC_INT_RATE_2017:Int = 10

end HardwareDef
