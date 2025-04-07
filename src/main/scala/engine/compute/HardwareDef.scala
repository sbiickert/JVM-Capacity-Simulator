package ca.esri.capsim
package engine.compute

import ca.esri.capsim.engine.Described

case class HardwareDef(processor: String, cores: Int, specIntRate2017: Double,
                       architecture: ComputeArchitecture):
  val specIntRate2017PerCore: Double = specIntRate2017 / cores
end HardwareDef

object HardwareDef:
  val BASELINE_PER_CORE_SPEC_INT_RATE_2017:Double = 10.0

end HardwareDef
