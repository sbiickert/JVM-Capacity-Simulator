package ca.esri.capsim
package engine.compute

import engine.Described

case class Service(name:String, description:String,
                   serviceType:String,
                   balancingModel: BalancingModel) extends Described:

end Service

