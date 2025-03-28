package ca.esri.capsim
package engine.work

import engine.Described

case class WorkflowDefStep(name: String, description: String,
                           serviceType:String,
                           serviceTime: Int,
                           chatter: Int,
                           requestSizeKB: Int,
                           responseSizeKB: Int,
                           dataSourceType:DataSourceType,
                           cachePercent:Int) extends Described:

end WorkflowDefStep