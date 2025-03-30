package ca.esri.capsim
package engine

sealed trait PerformanceMetric:
  val sourceName: String
  val clock: Int

case class QueueMetric(sourceName:String, clock: Int,
                       channelCount:Int, requestCount:Int) extends PerformanceMetric:

end QueueMetric


case class RequestMetric(sourceName:String, clock: Int,
                         requestName: String,
                         serviceTime:Int, queueTime:Int, latencyTime:Int) extends PerformanceMetric:

end RequestMetric


