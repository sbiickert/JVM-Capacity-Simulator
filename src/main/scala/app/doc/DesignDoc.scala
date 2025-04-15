package ca.esri.capsim
package app.doc

import engine.Design

case class DesignDoc(design: Design):
  val version = 1
  val savePath: Option[String] = None
  val fileName: String = "Untitled"
  
  def name: String = design.name
  def description: String = design.description
  
  val drawingInfo: DrawingInfo = DrawingInfo()
  
end DesignDoc

