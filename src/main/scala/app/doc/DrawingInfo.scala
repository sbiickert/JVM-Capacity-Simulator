package ca.esri.capsim
package app.doc

import ca.esri.capsim.engine.network.{Connection, Zone}

import java.awt.{Color, Image}
import java.awt.geom.{Point2D, Rectangle2D}

case class DrawingInfo():
  val zones: List[(Zone, RectangleInfo)] = List.empty
  val connections: List[(Connection, ConnectionInfo)] = List.empty
end DrawingInfo


case class RectangleInfo(rect: Rectangle2D, label:String, fgColor:Color, bgColor:Color, icon:Image)

case class ConnectionInfo(snap0: Point2D, snap1: Point2D, label: String, fgColor:Color, bgColor: Color, icon:Image)
