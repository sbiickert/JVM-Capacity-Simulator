package ca.esri.capsim
package app.ui

import app.doc.DesignDoc

import java.awt.Dimension
import scala.swing.*

class DesignFrame(doc:DesignDoc) extends Frame:
  title = doc.name
  menuBar = new DesignFrameMenuBar()

  private val panel = BorderPanel()
  panel.layout(DesignView()) = BorderPanel.Position.Center
  panel.layout(DesignStatusBar()) = BorderPanel.Position.South
  panel.layout(InspectorPanel()) = BorderPanel.Position.East
  contents = panel

  minimumSize = Dimension(500,500)
  preferredSize = Dimension(1000, 500)
  pack()

//  override val minimumSize: Dimension = Dimension(500,500)
//  override val preferredSize: Dimension =
//    Dimension(500, 500)
end DesignFrame

